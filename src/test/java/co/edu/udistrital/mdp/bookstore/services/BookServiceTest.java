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
 * Logic tests for Books
 *
 * @author Jose Bocanegra
 */
@DataJpaTest
@Transactional
@Import(BookService.class)
class BookServiceTest {

	@Autowired
	private BookService bookService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private List<BookEntity> bookList = new ArrayList<>();
	private EditorialEntity editorialEntity;

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
		entityManager.getEntityManager().createQuery("delete from BookEntity");
		entityManager.getEntityManager().createQuery("delete from EditorialEntity");
		entityManager.getEntityManager().createQuery("delete from AuthorEntity");
	}

	/**
	 * Inserts initial data required for tests to run properly.
	 */
	private void insertData() {

		editorialEntity = factory.manufacturePojo(EditorialEntity.class);
		entityManager.persist(editorialEntity);

		for (int i = 0; i < 3; i++) {
			BookEntity bookEntity = factory.manufacturePojo(BookEntity.class);
			bookEntity.setEditorial(editorialEntity);
			entityManager.persist(bookEntity);
			bookList.add(bookEntity);
		}

		AuthorEntity authorEntity = factory.manufacturePojo(AuthorEntity.class);
		entityManager.persist(authorEntity);
		authorEntity.getBooks().add(bookList.get(0));
		bookList.get(0).getAuthors().add(authorEntity);
	}

	/**
	 * Test to create a Book.
	 */
	@Test
	void testCreateBook() throws EntityNotFoundException, IllegalOperationException {
		BookEntity newEntity = factory.manufacturePojo(BookEntity.class);
		newEntity.setEditorial(editorialEntity);
		newEntity.setIsbn("1-4028-9462-7");
		BookEntity result = bookService.createBook(newEntity);
		assertNotNull(result);
		BookEntity entity = entityManager.find(BookEntity.class, result.getId());
		assertEquals(newEntity.getId(), entity.getId());
		assertEquals(newEntity.getName(), entity.getName());
		assertEquals(newEntity.getDescription(), entity.getDescription());
		assertEquals(newEntity.getImage(), entity.getImage());
		assertEquals(newEntity.getPublishingDate(), entity.getPublishingDate());
		assertEquals(newEntity.getIsbn(), entity.getIsbn());
	}

	/**
	 * Test to create a Book with an invalid ISBN (empty string).
	 */
	@Test
	void testCreateBookWithNoValidISBN() {
		assertThrows(IllegalOperationException.class, () -> {
			BookEntity newEntity = factory.manufacturePojo(BookEntity.class);
			newEntity.setEditorial(editorialEntity);
			newEntity.setIsbn("");
			bookService.createBook(newEntity);
		});
	}

	/**
	 * Test to create a Book with an invalid ISBN (null).
	 */
	@Test
	void testCreateBookWithNoValidISBN2() {
		assertThrows(IllegalOperationException.class, () -> {
			BookEntity newEntity = factory.manufacturePojo(BookEntity.class);
			newEntity.setEditorial(editorialEntity);
			newEntity.setIsbn(null);
			bookService.createBook(newEntity);
		});
	}

	/**
	 * Test to create a Book with an already existing ISBN.
	 */
	@Test
	void testCreateBookWithStoredISBN() {
		assertThrows(IllegalOperationException.class, () -> {
			BookEntity newEntity = factory.manufacturePojo(BookEntity.class);
			newEntity.setEditorial(editorialEntity);
			newEntity.setIsbn(bookList.get(0).getIsbn());
			bookService.createBook(newEntity);
		});
	}

	/**
	 * Test to create a Book with an editorial that does not exist.
	 */
	@Test
	void testCreateBookWithInvalidEditorial() {
		assertThrows(IllegalOperationException.class, () -> {
			BookEntity newEntity = factory.manufacturePojo(BookEntity.class);
			newEntity.setIsbn("1-4028-9462-7");
			EditorialEntity newEditorialEntity = new EditorialEntity();
			newEditorialEntity.setId(0L);
			newEntity.setEditorial(newEditorialEntity);
			bookService.createBook(newEntity);
		});
	}

	/**
	 * Test to create a Book with a null editorial.
	 */
	@Test
	void testCreateBookWithNullEditorial() {
		assertThrows(IllegalOperationException.class, () -> {
			BookEntity newEntity = factory.manufacturePojo(BookEntity.class);
			newEntity.setIsbn("1-4028-9462-7");
			newEntity.setEditorial(null);
			bookService.createBook(newEntity);
		});
	}

	/**
	 * Test to retrieve the list of Books.
	 */
	@Test
	void testGetBooks() {
		List<BookEntity> list = bookService.getBooks();
		assertEquals(bookList.size(), list.size());
		for (BookEntity entity : list) {
			boolean found = false;
			for (BookEntity storedEntity : bookList) {
				if (entity.getId().equals(storedEntity.getId())) {
					found = true;
				}
			}
			assertTrue(found);
		}
	}

	/**
	 * Test to retrieve a Book.
	 */
	@Test
	void testGetBook() throws EntityNotFoundException {
		BookEntity entity = bookList.get(0);
		BookEntity resultEntity = bookService.getBook(entity.getId());
		assertNotNull(resultEntity);
		assertEquals(entity.getId(), resultEntity.getId());
		assertEquals(entity.getName(), resultEntity.getName());
		assertEquals(entity.getDescription(), resultEntity.getDescription());
		assertEquals(entity.getIsbn(), resultEntity.getIsbn());
		assertEquals(entity.getImage(), resultEntity.getImage());
	}

	/**
	 * Test to retrieve a non-existent Book.
	 */
	@Test
	void testGetInvalidBook() {
		assertThrows(EntityNotFoundException.class, () -> {
			bookService.getBook(0L);
		});
	}

	/**
	 * Test to update a Book.
	 */
	@Test
	void testUpdateBook() throws EntityNotFoundException, IllegalOperationException {
		BookEntity entity = bookList.get(0);
		BookEntity pojoEntity = factory.manufacturePojo(BookEntity.class);
		pojoEntity.setId(entity.getId());
		bookService.updateBook(entity.getId(), pojoEntity);

		BookEntity resp = entityManager.find(BookEntity.class, entity.getId());
		assertEquals(pojoEntity.getId(), resp.getId());
		assertEquals(pojoEntity.getName(), resp.getName());
		assertEquals(pojoEntity.getDescription(), resp.getDescription());
		assertEquals(pojoEntity.getIsbn(), resp.getIsbn());
		assertEquals(pojoEntity.getImage(), resp.getImage());
		assertEquals(pojoEntity.getPublishingDate(), resp.getPublishingDate());
	}

	/**
	 * Test to update a non-existent Book.
	 */
	@Test
	void testUpdateBookInvalid() {
		assertThrows(EntityNotFoundException.class, () -> {
			BookEntity pojoEntity = factory.manufacturePojo(BookEntity.class);
			pojoEntity.setId(0L);
			bookService.updateBook(0L, pojoEntity);
		});
	}

	/**
	 * Test to update a Book with an invalid ISBN (empty string).
	 */
	@Test
	void testUpdateBookWithNoValidISBN() {
		assertThrows(IllegalOperationException.class, () -> {
			BookEntity entity = bookList.get(0);
			BookEntity pojoEntity = factory.manufacturePojo(BookEntity.class);
			pojoEntity.setIsbn("");
			pojoEntity.setId(entity.getId());
			bookService.updateBook(entity.getId(), pojoEntity);
		});
	}

	/**
	 * Test to update a Book with an invalid ISBN (null).
	 */
	@Test
	void testUpdateBookWithNoValidISBN2() {
		assertThrows(IllegalOperationException.class, () -> {
			BookEntity entity = bookList.get(0);
			BookEntity pojoEntity = factory.manufacturePojo(BookEntity.class);
			pojoEntity.setIsbn(null);
			pojoEntity.setId(entity.getId());
			bookService.updateBook(entity.getId(), pojoEntity);
		});
	}

	/**
	 * Test to delete a Book.
	 */
	@Test
	void testDeleteBook() throws EntityNotFoundException, IllegalOperationException {
		BookEntity entity = bookList.get(1);
		bookService.deleteBook(entity.getId());
		BookEntity deleted = entityManager.find(BookEntity.class, entity.getId());
		assertNull(deleted);
	}

	/**
	 * Test to delete a non-existent Book.
	 */
	@Test
	void testDeleteInvalidBook() {
		assertThrows(EntityNotFoundException.class, () -> {
			bookService.deleteBook(0L);
		});
	}

	/**
	 * Test to delete a Book that has an associated author.
	 */
	@Test
	void testDeleteBookWithAuthor() {
		assertThrows(IllegalOperationException.class, () -> {
			BookEntity entity = bookList.get(0);
			bookService.deleteBook(entity.getId());
		});
	}

}