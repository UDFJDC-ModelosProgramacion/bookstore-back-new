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

import co.edu.udistrital.mdp.bookstore.entities.BookEntity;
import co.edu.udistrital.mdp.bookstore.entities.EditorialEntity;
import co.edu.udistrital.mdp.bookstore.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.bookstore.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * Logic tests for the Editorial - Books relationship.
 *
 * @author Jose Bocanegra
 */
@DataJpaTest
@Transactional
@Import({ EditorialService.class, EditorialBookService.class })
class EditorialBookServiceTest {

	@Autowired
	private EditorialBookService editorialBookService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private List<EditorialEntity> editorialsList = new ArrayList<>();
	private List<BookEntity> booksList = new ArrayList<>();

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
		entityManager.getEntityManager().createQuery("delete from BookEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from EditorialEntity").executeUpdate();
	}

	/**
	 * Inserts initial data required for tests to run properly.
	 */
	private void insertData() {
		for (int i = 0; i < 3; i++) {
			BookEntity book = factory.manufacturePojo(BookEntity.class);
			entityManager.persist(book);
			booksList.add(book);
		}

		for (int i = 0; i < 3; i++) {
			EditorialEntity entity = factory.manufacturePojo(EditorialEntity.class);
			entityManager.persist(entity);
			editorialsList.add(entity);
			if (i == 0) {
				booksList.get(i).setEditorial(entity);
				entity.getBooks().add(booksList.get(i));
			}
		}
	}

	/**
	 * Test to associate an existing Book with an Editorial.
	 * 
	 * @throws EntityNotFoundException
	 */
	@Test
	void testAddBook() throws EntityNotFoundException {
		EditorialEntity entity = editorialsList.get(0);
		BookEntity bookEntity = booksList.get(1);
		BookEntity response = editorialBookService.addBook(bookEntity.getId(), entity.getId());

		assertNotNull(response);
		assertEquals(bookEntity.getId(), response.getId());
	}

	/**
	 * Test to associate a non-existent Book with an Editorial.
	 * 
	 * @throws EntityNotFoundException
	 */
	@Test
	void testAddInvalidBook() {
		assertThrows(EntityNotFoundException.class, () -> {
			EditorialEntity entity = editorialsList.get(0);
			editorialBookService.addBook(0L, entity.getId());
		});
	}

	/**
	 * Test to associate a Book with a non-existent Editorial.
	 * 
	 * @throws EntityNotFoundException
	 */
	@Test
	void testAddBookInvalidEditorial() {
		assertThrows(EntityNotFoundException.class, () -> {
			BookEntity bookEntity = booksList.get(1);
			editorialBookService.addBook(bookEntity.getId(), 0L);
		});
	}

	/**
	 * Test to retrieve a collection of Book instances associated with an Editorial
	 * instance.
	 * 
	 * @throws EntityNotFoundException
	 */
	@Test
	void testGetBooks() throws EntityNotFoundException {
		List<BookEntity> list = editorialBookService.getBooks(editorialsList.get(0).getId());
		assertEquals(1, list.size());
	}

	/**
	 * Test to retrieve a collection of Book instances associated with a
	 * non-existent Editorial instance.
	 * 
	 * @throws EntityNotFoundException
	 */
	@Test
	void testGetBooksInvalidEditorial() {
		assertThrows(EntityNotFoundException.class, () -> {
			editorialBookService.getBooks(0L);
		});
	}

	/**
	 * Test to retrieve a Book instance associated with an Editorial instance.
	 * 
	 * @throws IllegalOperationException
	 * @throws EntityNotFoundException
	 */
	@Test
	void testGetBook() throws EntityNotFoundException, IllegalOperationException {
		EditorialEntity entity = editorialsList.get(0);
		BookEntity bookEntity = booksList.get(0);
		BookEntity response = editorialBookService.getBook(entity.getId(), bookEntity.getId());

		assertEquals(bookEntity.getId(), response.getId());
		assertEquals(bookEntity.getName(), response.getName());
		assertEquals(bookEntity.getDescription(), response.getDescription());
		assertEquals(bookEntity.getIsbn(), response.getIsbn());
		assertEquals(bookEntity.getImage(), response.getImage());
	}

	/**
	 * Test to retrieve a Book instance associated with a non-existent Editorial
	 * instance.
	 * 
	 * @throws EntityNotFoundException
	 */
	@Test
	void testGetBookInvalidEditorial() {
		assertThrows(EntityNotFoundException.class, () -> {
			BookEntity bookEntity = booksList.get(0);
			editorialBookService.getBook(0L, bookEntity.getId());
		});
	}

	/**
	 * Test to retrieve a non-existent Book instance associated with an Editorial
	 * instance.
	 * 
	 * @throws EntityNotFoundException
	 */
	@Test
	void testGetInvalidBook() {
		assertThrows(EntityNotFoundException.class, () -> {
			EditorialEntity entity = editorialsList.get(0);
			editorialBookService.getBook(entity.getId(), 0L);
		});
	}

	/**
	 * Test to retrieve a Book instance associated with an Editorial instance it
	 * does not belong to.
	 *
	 * @throws IllegalOperationException
	 */
	@Test
	void testGetNotAssociatedBook() {
		assertThrows(IllegalOperationException.class, () -> {
			EditorialEntity entity = editorialsList.get(0);
			BookEntity bookEntity = booksList.get(1);
			editorialBookService.getBook(entity.getId(), bookEntity.getId());
		});
	}

	/**
	 * Test to replace the Book instances associated with an Editorial instance.
	 */
	@Test
	void testReplaceBooks() throws EntityNotFoundException {
		EditorialEntity entity = editorialsList.get(0);
		List<BookEntity> list = booksList.subList(1, 3);
		editorialBookService.replaceBooks(entity.getId(), list);

		for (BookEntity book : list) {
			BookEntity b = entityManager.find(BookEntity.class, book.getId());
			assertEquals(b.getEditorial(), entity);
		}
	}

	/**
	 * Test to replace non-existent Book instances associated with an Editorial
	 * instance.
	 */
	@Test
	void testReplaceInvalidBooks() {
		assertThrows(EntityNotFoundException.class, () -> {
			EditorialEntity entity = editorialsList.get(0);

			List<BookEntity> books = new ArrayList<>();
			BookEntity newBook = factory.manufacturePojo(BookEntity.class);
			newBook.setId(0L);
			books.add(newBook);

			editorialBookService.replaceBooks(entity.getId(), books);
		});
	}

	/**
	 * Test to replace Book instances associated with a non-existent Editorial
	 * instance.
	 */
	@Test
	void testReplaceBooksInvalidEditorial() {
		assertThrows(EntityNotFoundException.class, () -> {
			List<BookEntity> list = booksList.subList(1, 3);
			editorialBookService.replaceBooks(0L, list);
		});
	}
}