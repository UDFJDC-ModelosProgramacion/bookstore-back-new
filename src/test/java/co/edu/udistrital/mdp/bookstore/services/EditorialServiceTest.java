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
 * Logic tests for Editorials
 *
 * @author Jose Bocanegra
 */
@DataJpaTest
@Transactional
@Import(EditorialService.class)
class EditorialServiceTest {

	@Autowired
	private EditorialService editorialService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private List<EditorialEntity> editorialList = new ArrayList<>();

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
	}

	/**
	 * Inserts initial data required for tests to run properly.
	 */
	private void insertData() {

		for (int i = 0; i < 3; i++) {
			EditorialEntity editorialEntity = factory.manufacturePojo(EditorialEntity.class);
			entityManager.persist(editorialEntity);
			editorialList.add(editorialEntity);
		}
	}

	/**
	 * Test to create an Editorial.
	 *
	 * @throws IllegalOperationException
	 */
	@Test
	void testCreateEditorial() throws IllegalOperationException {
		EditorialEntity newEntity = factory.manufacturePojo(EditorialEntity.class);
		EditorialEntity result = editorialService.createEditorial(newEntity);
		assertNotNull(result);

		EditorialEntity entity = entityManager.find(EditorialEntity.class, result.getId());
		assertEquals(newEntity.getId(), entity.getId());
		assertEquals(newEntity.getName(), entity.getName());
	}

	/**
	 * Test to create an Editorial with the same name as an existing Editorial.
	 */
	@Test
	void testCreateEditorialWithSameName() {
		assertThrows(IllegalOperationException.class, () -> {
			EditorialEntity newEntity = factory.manufacturePojo(EditorialEntity.class);
			newEntity.setName(editorialList.get(0).getName());
			editorialService.createEditorial(newEntity);
		});
	}

	/**
	 * Test to retrieve the list of Editorials.
	 */
	@Test
	void testGetEditorials() {
		List<EditorialEntity> list = editorialService.getEditorials();
		assertEquals(editorialList.size(), list.size());
		for (EditorialEntity entity : list) {
			boolean found = false;
			for (EditorialEntity storedEntity : editorialList) {
				if (entity.getId().equals(storedEntity.getId())) {
					found = true;
				}
			}
			assertTrue(found);
		}
	}

	/**
	 * Test to retrieve an Editorial.
	 * 
	 * @throws EntityNotFoundException
	 */
	@Test
	void testGetEditorial() throws EntityNotFoundException {
		EditorialEntity entity = editorialList.get(0);
		EditorialEntity resultEntity = editorialService.getEditorial(entity.getId());
		assertNotNull(resultEntity);
		assertEquals(entity.getId(), resultEntity.getId());
		assertEquals(entity.getName(), resultEntity.getName());
	}

	/**
	 * Test to retrieve a non-existent Editorial.
	 */
	@Test
	void testGetEditorialInvalid() {
		assertThrows(EntityNotFoundException.class, () -> {
			editorialService.getEditorial(0L);
		});
	}

	/**
	 * Test to update an Editorial.
	 */
	@Test
	void testUpdateEditorial() throws EntityNotFoundException {
		EditorialEntity entity = editorialList.get(0);
		EditorialEntity pojoEntity = factory.manufacturePojo(EditorialEntity.class);
		pojoEntity.setId(entity.getId());
		editorialService.updateEditorial(entity.getId(), pojoEntity);
		EditorialEntity resp = entityManager.find(EditorialEntity.class, entity.getId());
		assertEquals(pojoEntity.getId(), resp.getId());
		assertEquals(pojoEntity.getName(), resp.getName());
	}

	/**
	 * Test to update a non-existent Editorial.
	 */
	@Test
	void testUpdateEditorialInvalid() {
		assertThrows(EntityNotFoundException.class, () -> {
			EditorialEntity pojoEntity = factory.manufacturePojo(EditorialEntity.class);
			pojoEntity.setId(0L);
			editorialService.updateEditorial(0L, pojoEntity);
		});
	}

	/**
	 * Test to delete an Editorial.
	 */
	@Test
	void testDeleteEditorial() throws EntityNotFoundException, IllegalOperationException {
		EditorialEntity entity = editorialList.get(1);
		editorialService.deleteEditorial(entity.getId());
		EditorialEntity deleted = entityManager.find(EditorialEntity.class, entity.getId());
		assertNull(deleted);
	}

	/**
	 * Test to delete a non-existent Editorial.
	 */
	@Test
	void testDeleteEditorialInvalid() {
		assertThrows(EntityNotFoundException.class, () -> {
			editorialService.deleteEditorial(0L);
		});
	}

	/**
	 * Test to delete an Editorial with associated books.
	 */
	@Test
	void testDeleteEditorialWithBooks() {
		assertThrows(IllegalOperationException.class, () -> {

			EditorialEntity editorialEntity = editorialList.get(0);
			BookEntity bookEntity = factory.manufacturePojo(BookEntity.class);
			entityManager.persist(bookEntity);
			editorialEntity.getBooks().add(bookEntity);
			editorialService.deleteEditorial(editorialEntity.getId());
		});
	}
}