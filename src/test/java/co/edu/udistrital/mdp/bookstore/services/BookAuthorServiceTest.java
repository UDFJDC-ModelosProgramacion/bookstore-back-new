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
 * Logic tests for the Book - Author relationship.
 *
 * @author Jose Bocanegra
 */
@DataJpaTest
@Transactional
@Import(BookAuthorService.class)
class BookAuthorServiceTest {

	@Autowired
	private BookAuthorService bookAuthorService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private BookEntity book = new BookEntity();
	private EditorialEntity editorial = new EditorialEntity();
	private List<AuthorEntity> authorList = new ArrayList<>();

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

		book = factory.manufacturePojo(BookEntity.class);
		book.setEditorial(editorial);
		entityManager.persist(book);

		for (int i = 0; i < 3; i++) {
			AuthorEntity entity = factory.manufacturePojo(AuthorEntity.class);
			entityManager.persist(entity);
			entity.getBooks().add(book);
			authorList.add(entity);
			book.getAuthors().add(entity);
		}
	}

	/**
	 * Test to associate an author with a book.
	 */
	@Test
	void testAddAuthor() throws EntityNotFoundException, IllegalOperationException {
		BookEntity newBook = factory.manufacturePojo(BookEntity.class);
		newBook.setEditorial(editorial);
		entityManager.persist(newBook);

		AuthorEntity author = factory.manufacturePojo(AuthorEntity.class);
		entityManager.persist(author);

		bookAuthorService.addAuthor(newBook.getId(), author.getId());

		AuthorEntity lastAuthor = bookAuthorService.getAuthor(newBook.getId(), author.getId());
		assertEquals(author.getId(), lastAuthor.getId());
		assertEquals(author.getBirthDate(), lastAuthor.getBirthDate());
		assertEquals(author.getDescription(), lastAuthor.getDescription());
		assertEquals(author.getImage(), lastAuthor.getImage());
		assertEquals(author.getName(), lastAuthor.getName());
	}

	/**
	 * Test to associate an author that does not exist with a book.
	 */
	@Test
	void testAddInvalidAuthor() {
		assertThrows(EntityNotFoundException.class, () -> {
			BookEntity newBook = factory.manufacturePojo(BookEntity.class);
			newBook.setEditorial(editorial);
			entityManager.persist(newBook);
			bookAuthorService.addAuthor(newBook.getId(), 0L);
		});
	}

	/**
	 * Test to associate an author with a book that does not exist.
	 */
	@Test
	void testAddAuthorInvalidBook() {
		assertThrows(EntityNotFoundException.class, () -> {
			AuthorEntity author = factory.manufacturePojo(AuthorEntity.class);
			entityManager.persist(author);
			bookAuthorService.addAuthor(0L, author.getId());
		});
	}

	/**
	 * Test to retrieve the list of authors of a book.
	 */
	@Test
	void testGetAuthors() throws EntityNotFoundException {
		List<AuthorEntity> authorEntities = bookAuthorService.getAuthors(book.getId());

		assertEquals(authorList.size(), authorEntities.size());

		for (int i = 0; i < authorList.size(); i++) {
			assertTrue(authorEntities.contains(authorList.get(0)));
		}
	}

	/**
	 * Test to retrieve the list of authors of a book that does not exist.
	 */
	@Test
	void testGetAuthorsInvalidBook() {
		assertThrows(EntityNotFoundException.class, () -> {
			bookAuthorService.getAuthors(0L);
		});
	}

	/**
	 * Test to retrieve an author of a book.
	 */
	@Test
	void testGetAuthor() throws EntityNotFoundException, IllegalOperationException {
		AuthorEntity authorEntity = authorList.get(0);
		AuthorEntity author = bookAuthorService.getAuthor(book.getId(), authorEntity.getId());
		assertNotNull(author);

		assertEquals(authorEntity.getId(), author.getId());
		assertEquals(authorEntity.getName(), author.getName());
		assertEquals(authorEntity.getDescription(), author.getDescription());
		assertEquals(authorEntity.getImage(), author.getImage());
		assertEquals(authorEntity.getBirthDate(), author.getBirthDate());
	}

	/**
	 * Test to retrieve an author that does not exist from a book.
	 */
	@Test
	void testGetInvalidAuthor() {
		assertThrows(EntityNotFoundException.class, () -> {
			bookAuthorService.getAuthor(book.getId(), 0L);
		});
	}

	/**
	 * Test to retrieve an author from a book that does not exist.
	 */
	@Test
	void testGetAuthorInvalidBook() {
		assertThrows(EntityNotFoundException.class, () -> {
			AuthorEntity authorEntity = authorList.get(0);
			bookAuthorService.getAuthor(0L, authorEntity.getId());
		});
	}

	/**
	 * Test to retrieve an author not associated with a book.
	 */
	@Test
	void testGetNotAssociatedAuthor() {
		assertThrows(IllegalOperationException.class, () -> {
			BookEntity newBook = factory.manufacturePojo(BookEntity.class);
			newBook.setEditorial(editorial);
			entityManager.persist(newBook);
			AuthorEntity author = factory.manufacturePojo(AuthorEntity.class);
			entityManager.persist(author);
			bookAuthorService.getAuthor(newBook.getId(), author.getId());
		});
	}

	/**
	 * Test to update the authors of a book.
	 */
	@Test
	void testReplaceAuthors() throws EntityNotFoundException {
		List<AuthorEntity> newList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			AuthorEntity entity = factory.manufacturePojo(AuthorEntity.class);
			entityManager.persist(entity);
			book.getAuthors().add(entity);
			newList.add(entity);
		}
		bookAuthorService.replaceAuthors(book.getId(), newList);

		List<AuthorEntity> authorEntities = bookAuthorService.getAuthors(book.getId());
		for (AuthorEntity aNewList : newList) {
			assertTrue(authorEntities.contains(aNewList));
		}
	}

	/**
	 * Test to update the authors of a book with unassociated entities.
	 */
	@Test
	void testReplaceAuthors2() throws EntityNotFoundException {
		List<AuthorEntity> newList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			AuthorEntity entity = factory.manufacturePojo(AuthorEntity.class);
			entityManager.persist(entity);
			newList.add(entity);
		}
		bookAuthorService.replaceAuthors(book.getId(), newList);

		List<AuthorEntity> authorEntities = bookAuthorService.getAuthors(book.getId());
		for (AuthorEntity aNewList : newList) {
			assertTrue(authorEntities.contains(aNewList));
		}
	}

	/**
	 * Test to update the authors of a book that does not exist.
	 */
	@Test
	void testReplaceAuthorsInvalidBook() {
		assertThrows(EntityNotFoundException.class, () -> {
			List<AuthorEntity> newList = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				AuthorEntity entity = factory.manufacturePojo(AuthorEntity.class);
				entity.getBooks().add(book);
				entityManager.persist(entity);
				newList.add(entity);
			}
			bookAuthorService.replaceAuthors(0L, newList);
		});
	}

	/**
	 * Test to update non-existent authors for a book.
	 */
	@Test
	void testReplaceInvalidAuthors() {
		assertThrows(EntityNotFoundException.class, () -> {
			List<AuthorEntity> newList = new ArrayList<>();
			AuthorEntity entity = factory.manufacturePojo(AuthorEntity.class);
			entity.setId(0L);
			newList.add(entity);
			bookAuthorService.replaceAuthors(book.getId(), newList);
		});
	}

	/**
	 * Test to update an author of a book that does not exist.
	 */
	@Test
	void testReplaceAuthorsInvalidAuthor() {
		assertThrows(EntityNotFoundException.class, () -> {
			List<AuthorEntity> newList = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				AuthorEntity entity = factory.manufacturePojo(AuthorEntity.class);
				entity.getBooks().add(book);
				entityManager.persist(entity);
				newList.add(entity);
			}
			bookAuthorService.replaceAuthors(0L, newList);
		});
	}

	/**
	 * Test to disassociate an author from a book.
	 */
	@Test
	void testRemoveAuthor() throws EntityNotFoundException {
		for (AuthorEntity author : authorList) {
			bookAuthorService.removeAuthor(book.getId(), author.getId());
		}
		assertTrue(bookAuthorService.getAuthors(book.getId()).isEmpty());
	}

	/**
	 * Test to disassociate an author that does not exist from a book.
	 */
	@Test
	void testRemoveInvalidAuthor() {
		assertThrows(EntityNotFoundException.class, () -> {
			bookAuthorService.removeAuthor(book.getId(), 0L);
		});
	}

	/**
	 * Test to disassociate an author from a book that does not exist.
	 */
	@Test
	void testRemoveAuthorInvalidBook() {
		assertThrows(EntityNotFoundException.class, () -> {
			bookAuthorService.removeAuthor(0L, authorList.get(0).getId());
		});
	}

}