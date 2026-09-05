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
import co.edu.udistrital.mdp.bookstore.entities.OrganizationEntity;
import co.edu.udistrital.mdp.bookstore.entities.PrizeEntity;
import co.edu.udistrital.mdp.bookstore.exceptions.EntityNotFoundException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * Logic tests for the Prize - Author relationship
 *
 * @author Jose Bocanegra
 */
@DataJpaTest
@Transactional
@Import(PrizeAuthorService.class)
class PrizeAuthorServiceTest {

	private PodamFactory factory = new PodamFactoryImpl();

	@Autowired
	private PrizeAuthorService prizeAuthorService;

	@Autowired
	private TestEntityManager entityManager;

	private List<AuthorEntity> authorsList = new ArrayList<>();
	private List<PrizeEntity> prizesList = new ArrayList<>();

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
		entityManager.getEntityManager().createQuery("delete from OrganizationEntity ").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from AuthorEntity").executeUpdate();
	}

	/**
	 * Inserts initial data required for tests to run properly.
	 */
	private void insertData() {
		for (int i = 0; i < 3; i++) {
			PrizeEntity prizes = factory.manufacturePojo(PrizeEntity.class);
			OrganizationEntity org = factory.manufacturePojo(OrganizationEntity.class);

			prizes.setOrganization(org);
			org.setPrize(prizes);

			entityManager.persist(org);
			entityManager.persist(prizes);
			prizesList.add(prizes);
		}
		for (int i = 0; i < 3; i++) {
			AuthorEntity entity = factory.manufacturePojo(AuthorEntity.class);
			entityManager.persist(entity);
			authorsList.add(entity);
			if (i == 0) {
				prizesList.get(i).setAuthor(entity);
			}
		}
	}

	/**
	 * Test to associate an existing Prize to an Author.
	 * 
	 * @throws EntityNotFoundException
	 */
	@Test
	void testAddAuthor() throws EntityNotFoundException {
		AuthorEntity entity = authorsList.get(0);
		PrizeEntity prizeEntity = prizesList.get(1);
		AuthorEntity response = prizeAuthorService.addAuthor(entity.getId(), prizeEntity.getId());

		assertNotNull(response);
		assertEquals(entity.getId(), response.getId());
	}

	/**
	 * Test to associate an existing Prize to a non-existent Author.
	 */
	@Test
	void testAddInvalidAuthor() {
		assertThrows(EntityNotFoundException.class, () -> {
			PrizeEntity prizeEntity = prizesList.get(1);
			prizeAuthorService.addAuthor(0L, prizeEntity.getId());
		});
	}

	/**
	 * Test to associate a non-existent Prize to an Author.
	 */
	@Test
	void testAddAuthorInvalidPrize() {
		assertThrows(EntityNotFoundException.class, () -> {
			AuthorEntity entity = authorsList.get(0);
			prizeAuthorService.addAuthor(entity.getId(), 0L);
		});
	}

	/**
	 * Test to retrieve an Author.
	 * 
	 * @throws EntityNotFoundException
	 */
	@Test
	void testGetAuthor() throws EntityNotFoundException {
		PrizeEntity entity = prizesList.get(0);
		AuthorEntity resultEntity = prizeAuthorService.getAuthor(entity.getId());
		assertNotNull(resultEntity);
		assertEquals(entity.getAuthor().getId(), resultEntity.getId());
	}

	/**
	 * Test to retrieve an Author for a non-existent Prize.
	 */
	@Test
	void testGetAuthorInvalidPrize() {
		assertThrows(EntityNotFoundException.class, () -> {
			prizeAuthorService.getAuthor(0L);
		});
	}

	/**
	 * Test to retrieve an Author for a prize without an assigned author.
	 */
	@Test
	void testGetAuthorNotPrize() {
		assertThrows(EntityNotFoundException.class, () -> {
			PrizeEntity prize = prizesList.get(1);
			prizeAuthorService.getAuthor(prize.getId());
		});
	}

	/**
	 * Test to replace the Author instance associated with a Prize instance.
	 * 
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceAuthor() throws EntityNotFoundException {
		AuthorEntity entity = authorsList.get(0);
		prizeAuthorService.replaceAuthor(prizesList.get(1).getId(), entity.getId());

		PrizeEntity prize = entityManager.find(PrizeEntity.class, prizesList.get(1).getId());
		assertEquals(prize.getAuthor(), entity);
	}

	/**
	 * Test to replace the Author instance associated with a Prize using a
	 * non-existent Author.
	 */
	@Test
	void testReplaceInvalidAuthor() {
		assertThrows(EntityNotFoundException.class, () -> {
			prizeAuthorService.replaceAuthor(prizesList.get(1).getId(), 0L);
		});
	}

	/**
	 * Test to replace the Author instance associated with a non-existent Prize.
	 */
	@Test
	void testReplaceAuthorInvalidPrize() {
		assertThrows(EntityNotFoundException.class, () -> {
			AuthorEntity entity = authorsList.get(0);
			prizeAuthorService.replaceAuthor(0L, entity.getId());

		});
	}

	/**
	 * Test to disassociate an existing Prize from an existing Author.
	 * 
	 * @throws EntityNotFoundException
	 */
	@Test
	void testRemovePrize() throws EntityNotFoundException {
		prizeAuthorService.removeAuthor(prizesList.get(0).getId());
		PrizeEntity prize = entityManager.find(PrizeEntity.class, prizesList.get(0).getId());
		assertNull(prize.getAuthor());
	}

	/**
	 * Test to disassociate a non-existent Prize.
	 */
	@Test
	void testRemoveInvalidPrize() {
		assertThrows(EntityNotFoundException.class, () -> {
			prizeAuthorService.removeAuthor(0L);
		});
	}

	/**
	 * Test to disassociate an Author from a Prize that does not have one
	 * associated.
	 */
	@Test
	void testRemoveAuthor() {
		assertThrows(EntityNotFoundException.class, () -> {
			prizeAuthorService.removeAuthor(prizesList.get(1).getId());
		});
	}

}