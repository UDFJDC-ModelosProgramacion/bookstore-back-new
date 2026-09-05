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
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * Logic tests for the Book - Editorial relationship.
 *
 * @author Jose Bocanegra
 */
@DataJpaTest
@Transactional
@Import({ BookService.class, BookEditorialService.class })
class BookEditorialServiceTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private BookEditorialService bookEditorialService;

	@Autowired
	private BookService bookService;

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
			BookEntity books = factory.manufacturePojo(BookEntity.class);
			entityManager.persist(books);
			booksList.add(books);
		}
		for (int i = 0; i < 3; i++) {
			EditorialEntity entity = factory.manufacturePojo(EditorialEntity.class);
			entityManager.persist(entity);
			editorialsList.add(entity);
			if (i == 0) {
				booksList.get(i).setEditorial(entity);
			}
		}
	}

	/**
	 * Test to replace the Editorial instance associated with a Book instance.
	 * 
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceEditorial() throws EntityNotFoundException {
		BookEntity entity = booksList.get(0);
		bookEditorialService.replaceEditorial(entity.getId(), editorialsList.get(1).getId());
		entity = bookService.getBook(entity.getId());
		assertEquals(entity.getEditorial(), editorialsList.get(1));
	}

	/**
	 * Test to replace the Editorial instance associated with a non-existent Book
	 * instance.
	 * 
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceEditorialInvalidBook() {
		assertThrows(EntityNotFoundException.class, () -> {
			bookEditorialService.replaceEditorial(0L, editorialsList.get(1).getId());
		});
	}

	/**
	 * Test to replace a non-existent Editorial instance associated with a Book
	 * instance.
	 * 
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceInvalidEditorial() {
		assertThrows(EntityNotFoundException.class, () -> {
			BookEntity entity = booksList.get(0);
			bookEditorialService.replaceEditorial(entity.getId(), 0L);
		});
	}

	/**
	 * Test to disassociate an existing Book from an existing Editorial.
	 * 
	 * @throws EntityNotFoundException
	 */
	@Test
	void testRemoveEditorial() throws EntityNotFoundException {
		bookEditorialService.removeEditorial(booksList.get(0).getId());
		BookEntity response = bookService.getBook(booksList.get(0).getId());
		assertNull(response.getEditorial());
	}

	/**
	 * Test to disassociate a non-existent Book from an Editorial.
	 * 
	 * @throws EntityNotFoundException
	 */
	@Test
	void testRemoveEditorialInvalidBook() {
		assertThrows(EntityNotFoundException.class, () -> {
			bookEditorialService.removeEditorial(0L);
		});
	}

}