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
import java.util.Calendar;
import java.util.Date;
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
import co.edu.udistrital.mdp.bookstore.entities.PrizeEntity;
import co.edu.udistrital.mdp.bookstore.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.bookstore.exceptions.IllegalOperationException;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * Logic tests for Authors
 *
 * @author Jose Bocanegra
 */
@DataJpaTest
@Transactional
@Import(AuthorService.class)
class AuthorServiceTest {

	@Autowired
	private AuthorService authorService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

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
		entityManager.getEntityManager().createQuery("delete from PrizeEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from BookEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from AuthorEntity").executeUpdate();
	}

	/**
	 * Inserts initial data required for tests to run properly.
	 */
	private void insertData() {
		for (int i = 0; i < 3; i++) {
			AuthorEntity authorEntity = factory.manufacturePojo(AuthorEntity.class);
			entityManager.persist(authorEntity);
			authorList.add(authorEntity);
		}
	}

	/**
	 * Test to create an Author.
	 * 
	 * @throws IllegalOperationException
	 */
	@Test
	void testCreateAuthor() throws IllegalOperationException {
		AuthorEntity newEntity = factory.manufacturePojo(AuthorEntity.class);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, -15);
		newEntity.setBirthDate(calendar.getTime());
		AuthorEntity result = authorService.createAuthor(newEntity);
		assertNotNull(result);

		AuthorEntity entity = entityManager.find(AuthorEntity.class, result.getId());

		assertEquals(newEntity.getId(), entity.getId());
		assertEquals(newEntity.getName(), entity.getName());
		assertEquals(newEntity.getBirthDate(), entity.getBirthDate());
		assertEquals(newEntity.getDescription(), entity.getDescription());
	}

	/**
	 * Test to create an Author with a birth date greater than the current date.
	 * 
	 * @throws IllegalOperationException
	 */
	@Test
	void testCreateAuthorInvalidBirthDate() {
		assertThrows(IllegalOperationException.class, () -> {
			AuthorEntity newEntity = factory.manufacturePojo(AuthorEntity.class);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.DATE, 15);
			newEntity.setBirthDate(calendar.getTime());
			authorService.createAuthor(newEntity);
		});
	}

	/**
	 * Test to retrieve the list of Authors.
	 */
	@Test
	void testGetAuthors() {
		List<AuthorEntity> authorsList = authorService.getAuthors();
		assertEquals(authorList.size(), authorsList.size());

		for (AuthorEntity authorEntity : authorsList) {
			boolean found = false;
			for (AuthorEntity storedEntity : authorList) {
				if (authorEntity.getId().equals(storedEntity.getId())) {
					found = true;
				}
			}
			assertTrue(found);
		}
	}

	/**
	 * Test to retrieve an Author.
	 */
	@Test
	void testGetAuthor() throws EntityNotFoundException {
		AuthorEntity authorEntity = authorList.get(0);

		AuthorEntity resultEntity = authorService.getAuthor(authorEntity.getId());
		assertNotNull(resultEntity);

		assertEquals(authorEntity.getId(), resultEntity.getId());
		assertEquals(authorEntity.getName(), resultEntity.getName());
		assertEquals(authorEntity.getBirthDate(), resultEntity.getBirthDate());
		assertEquals(authorEntity.getDescription(), resultEntity.getDescription());
	}

	/**
	 * Test to retrieve an Author that does not exist.
	 */
	@Test
	void testGetInvalidAuthor() {
		assertThrows(EntityNotFoundException.class, () -> {
			authorService.getAuthor(0L);
		});
	}

	/**
	 * Test to update an Author.
	 */
	@Test
	void testUpdateAuthor() throws EntityNotFoundException {
		AuthorEntity authorEntity = authorList.get(0);
		AuthorEntity pojoEntity = factory.manufacturePojo(AuthorEntity.class);

		pojoEntity.setId(authorEntity.getId());

		authorService.updateAuthor(authorEntity.getId(), pojoEntity);

		AuthorEntity response = entityManager.find(AuthorEntity.class, authorEntity.getId());

		assertEquals(pojoEntity.getId(), response.getId());
		assertEquals(pojoEntity.getName(), response.getName());
		assertEquals(pojoEntity.getBirthDate(), response.getBirthDate());
		assertEquals(pojoEntity.getDescription(), response.getDescription());
	}

	/**
	 * Test to update an Author that does not exist.
	 */
	@Test
	void testUpdateInvalidAuthor() {
		assertThrows(EntityNotFoundException.class, () -> {
			AuthorEntity pojoEntity = factory.manufacturePojo(AuthorEntity.class);
			authorService.updateAuthor(0L, pojoEntity);
		});
	}

	/**
	 * Test to delete an Author.
	 */
	@Test
	void testDeleteAuthor() throws EntityNotFoundException, IllegalOperationException {
		AuthorEntity authorEntity = authorList.get(0);
		authorService.deleteAuthor(authorEntity.getId());
		AuthorEntity deleted = entityManager.find(AuthorEntity.class, authorEntity.getId());
		assertNull(deleted);
	}

	/**
	 * Test to delete an Author that does not exist.
	 */
	@Test
	void testDeleteInvalidAuthor() {
		assertThrows(EntityNotFoundException.class, () -> {
			authorService.deleteAuthor(0L);
		});
	}

	/**
	 * Test to delete an Author associated with a book.
	 */
	@Test
	void testDeleteAuthorWithBooks() {
		assertThrows(IllegalOperationException.class, () -> {
			AuthorEntity authorEntity = authorList.get(2);

			BookEntity bookEntity = factory.manufacturePojo(BookEntity.class);
			bookEntity.getAuthors().add(authorEntity);
			entityManager.persist(bookEntity);
			authorEntity.getBooks().add(bookEntity);

			authorService.deleteAuthor(authorList.get(2).getId());
		});
	}

	/**
	 * Test to delete an Author associated with a prize.
	 */
	@Test
	void testDeleteAuthorWithPrize() {
		assertThrows(IllegalOperationException.class, () -> {
			PrizeEntity prize = factory.manufacturePojo(PrizeEntity.class);
			prize.setAuthor(authorList.get(1));
			entityManager.persist(prize);
			authorList.get(1).getPrizes().add(prize);

			authorService.deleteAuthor(authorList.get(1).getId());
		});
	}

}