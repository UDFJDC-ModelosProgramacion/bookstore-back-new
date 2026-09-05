/*
MIT License

Copyright (c) 2026 Universidad Distrital Francisco José de Caldas

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package co.edu.udistrital.mdp.bookstore.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import co.edu.udistrital.mdp.bookstore.entities.AuthorEntity;
import co.edu.udistrital.mdp.bookstore.entities.BookEntity;
import co.edu.udistrital.mdp.bookstore.entities.EditorialEntity;
import co.edu.udistrital.mdp.bookstore.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.bookstore.exceptions.IllegalOperationException;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * Logic tests for the Author - Books relationship.
 *
 * @author Jose Bocanegra
 */
@DataJpaTest
@Transactional
@Import({ AuthorBookService.class, BookService.class })
class AuthorBookServiceTest {

	@Autowired
	private AuthorBookService authorBookService;

	@Autowired
	private BookService bookService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private AuthorEntity author = new AuthorEntity();
	private EditorialEntity editorial = new EditorialEntity();
	private List<BookEntity> bookList = new ArrayList<>();

	/**
	 * Initial setup for the test.
	 */
	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}

	/**
	 * Clears the tables involved in the test.
	 */
	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from AuthorEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from BookEntity").executeUpdate();
	}

	/**
	 * Inserts initial data required for tests to run properly.
	 */
	private void insertData() {
		editorial = factory.manufacturePojo(EditorialEntity.class);
		entityManager.persist(editorial);

		author = factory.manufacturePojo(AuthorEntity.class);
		entityManager.persist(author);

		for (int i = 0; i < 3; i++) {
			BookEntity entity = factory.manufacturePojo(BookEntity.class);
			entity.setEditorial(editorial);
			entity.getAuthors().add(author);
			entityManager.persist(entity);
			bookList.add(entity);
			author.getBooks().add(entity);
		}
	}

	/**
	 * Test to associate a book with an author.
	 */
	@Test
	void testAddBook() throws EntityNotFoundException, IllegalOperationException {
		BookEntity newBook = factory.manufacturePojo(BookEntity.class);
		newBook.setEditorial(editorial);
		bookService.createBook(newBook);

		BookEntity bookEntity = authorBookService.addBook(author.getId(), newBook.getId());
		assertNotNull(bookEntity);

		assertEquals(bookEntity.getId(), newBook.getId());
		assertEquals(bookEntity.getName(), newBook.getName());
		assertEquals(bookEntity.getDescription(), newBook.getDescription());
		assertEquals(bookEntity.getIsbn(), newBook.getIsbn());
		assertEquals(bookEntity.getImage(), newBook.getImage());

		BookEntity lastBook = authorBookService.getBook(author.getId(), newBook.getId());

		assertEquals(lastBook.getId(), newBook.getId());
		assertEquals(lastBook.getName(), newBook.getName());
		assertEquals(lastBook.getDescription(), newBook.getDescription());
		assertEquals(lastBook.getIsbn(), newBook.getIsbn());
		assertEquals(lastBook.getImage(), newBook.getImage());
	}

	/**
	 * Test to associate a book with an author that does not exist.
	 */
	@Test
	void testAddBookInvalidAuthor() {
		assertThrows(EntityNotFoundException.class, () -> {
			BookEntity newBook = factory.manufacturePojo(BookEntity.class);
			newBook.setEditorial(editorial);
			entityManager.persist(newBook);
			authorBookService.addBook(0L, newBook.getId());
		});
	}

	/**
	 * Test to associate a book that does not exist with an author.
	 */
	@Test
	void testAddInvalidBook() {
		assertThrows(EntityNotFoundException.class, () -> {
			authorBookService.addBook(author.getId(), 0L);
		});
	}

	/**
	 * Test to retrieve the list of books of an author.
	 */
	@Test
	void testGetBooks() throws EntityNotFoundException {
		List<BookEntity> bookEntities = authorBookService.getBooks(author.getId());

		assertEquals(bookList.size(), bookEntities.size());

		for (int i = 0; i < bookList.size(); i++) {
			assertTrue(bookEntities.contains(bookList.get(0)));
		}
	}

	/**
	 * Test to retrieve the list of books of an author that does not exist.
	 */
	@Test
	void testGetBooksInvalidAuthor() {
		assertThrows(EntityNotFoundException.class, () -> {
			authorBookService.getBooks(0L);
		});
	}

	/**
	 * Test to retrieve a book of an author.
	 */
	@Test
	void testGetBook() throws EntityNotFoundException, IllegalOperationException {
		BookEntity bookEntity = bookList.get(0);
		BookEntity book = authorBookService.getBook(author.getId(), bookEntity.getId());
		assertNotNull(book);

		assertEquals(bookEntity.getId(), book.getId());
		assertEquals(bookEntity.getName(), book.getName());
		assertEquals(bookEntity.getDescription(), book.getDescription());
		assertEquals(bookEntity.getIsbn(), book.getIsbn());
		assertEquals(bookEntity.getImage(), book.getImage());
	}

	/**
	 * Test to retrieve a book of an author that does not exist.
	 */
	@Test
	void testGetBookInvalidAuthor() {
		assertThrows(EntityNotFoundException.class, () -> {
			BookEntity bookEntity = bookList.get(0);
			authorBookService.getBook(0L, bookEntity.getId());
		});
	}

	/**
	 * Test to retrieve a non-existent book of an author.
	 */
	@Test
	void testGetInvalidBook() {
		assertThrows(EntityNotFoundException.class, () -> {
			authorBookService.getBook(author.getId(), 0L);
		});
	}

	/**
	 * Test to retrieve a book that is not associated with an author.
	 */
	@Test
	void testGetBookNotAssociatedAuthor() {
		assertThrows(IllegalOperationException.class, () -> {
			AuthorEntity authorEntity = factory.manufacturePojo(AuthorEntity.class);
			entityManager.persist(authorEntity);

			BookEntity bookEntity = factory.manufacturePojo(BookEntity.class);
			bookEntity.setEditorial(editorial);
			entityManager.persist(bookEntity);

			authorBookService.getBook(authorEntity.getId(), bookEntity.getId());
		});
	}

	/**
	 * Test to update the books of an author.
	 */
	@Test
	void testReplaceBooks() throws EntityNotFoundException {
		List<BookEntity> newBookList = new ArrayList<>();

		for (int i = 0; i < 3; i++) {
			BookEntity entity = factory.manufacturePojo(BookEntity.class);
			entity.setEditorial(editorial);
			entityManager.persist(entity);
			newBookList.add(entity);
		}

		authorBookService.addBooks(author.getId(), newBookList);

		List<BookEntity> bookEntities = entityManager.find(AuthorEntity.class, author.getId()).getBooks();
		for (BookEntity item : newBookList) {
			assertTrue(bookEntities.contains(item));
		}
	}

	/**
	 * Test to update the books of an author that does not exist.
	 */
	@Test
	void testReplaceBooksInvalidAuthor() {
		assertThrows(EntityNotFoundException.class, () -> {
			List<BookEntity> newBookList = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				BookEntity entity = factory.manufacturePojo(BookEntity.class);
				entity.setEditorial(editorial);
				entityManager.persist(entity);
				newBookList.add(entity);
			}
			authorBookService.addBooks(0L, newBookList);
		});
	}

	/**
	 * Test to update non-existent books for an author.
	 */
	@Test
	void testReplaceInvalidBooks() {
		assertThrows(EntityNotFoundException.class, () -> {
			List<BookEntity> newBookList = new ArrayList<>();
			BookEntity entity = factory.manufacturePojo(BookEntity.class);
			entity.setEditorial(editorial);
			entity.setId(0L);
			newBookList.add(entity);
			authorBookService.addBooks(author.getId(), newBookList);
		});
	}

	/**
	 * Test to disassociate a book from an author.
	 */
	@Test
	void testRemoveBook() throws EntityNotFoundException {
		for (BookEntity book : bookList) {
			authorBookService.removeBook(author.getId(), book.getId());
		}
		assertTrue(authorBookService.getBooks(author.getId()).isEmpty());
	}

	/**
	 * Test to disassociate a book from an author that does not exist.
	 */
	@Test
	void testRemoveBookInvalidAuthor() {
		assertThrows(EntityNotFoundException.class, () -> {
			for (BookEntity book : bookList) {
				authorBookService.removeBook(0L, book.getId());
			}
		});
	}

	/**
	 * Test to disassociate a non-existent book from an author.
	 */
	@Test
	void testRemoveInvalidBook() {
		assertThrows(EntityNotFoundException.class, () -> {
			authorBookService.removeBook(author.getId(), 0L);
		});
	}
}