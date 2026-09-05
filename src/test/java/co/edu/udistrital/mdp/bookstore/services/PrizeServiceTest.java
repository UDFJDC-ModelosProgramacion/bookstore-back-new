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
import co.edu.udistrital.mdp.bookstore.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * Logic tests for Prizes
 *
 * @author Jose Bocanegra
 */
@DataJpaTest
@Transactional
@Import({ PrizeService.class, OrganizationService.class })
class PrizeServiceTest {

	@Autowired
	private PrizeService prizeService;

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private List<OrganizationEntity> organizationList = new ArrayList<>();
	private List<PrizeEntity> prizeList = new ArrayList<PrizeEntity>();

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
	}

	/**
	 * Inserts initial data required for tests to run properly.
	 */
	private void insertData() {
		for (int i = 0; i < 3; i++) {
			PrizeEntity entity = factory.manufacturePojo(PrizeEntity.class);
			OrganizationEntity orgEntity = factory.manufacturePojo(OrganizationEntity.class);
			entityManager.persist(orgEntity);
			entity.setOrganization(orgEntity);
			orgEntity.setPrize(entity);
			entityManager.persist(entity);
			prizeList.add(entity);
			organizationList.add(orgEntity);
		}
	}

	/**
	 * Test to create a Prize.
	 *
	 * @throws IllegalOperationException
	 */
	@Test
	void testCreatePrize() throws IllegalOperationException {
		PrizeEntity newEntity = factory.manufacturePojo(PrizeEntity.class);
		OrganizationEntity newOrgEntity = factory.manufacturePojo(OrganizationEntity.class);

		newOrgEntity = organizationService.createOrganization(newOrgEntity);
		newEntity.setOrganization(newOrgEntity);
		PrizeEntity result = prizeService.createPrize(newEntity);
		assertNotNull(result);
		PrizeEntity entity = entityManager.find(PrizeEntity.class, result.getId());
		assertEquals(newEntity.getId(), entity.getId());
		assertEquals(newEntity.getName(), entity.getName());
		assertEquals(newEntity.getDescription(), entity.getDescription());
		assertEquals(newEntity.getPremiationDate(), entity.getPremiationDate());
	}

	/**
	 * Test to create a Prize with an invalid organization.
	 */
	@Test
	void testCreatePrizeWithNoValidOrganization() {
		assertThrows(IllegalOperationException.class, () -> {
			PrizeEntity newEntity = factory.manufacturePojo(PrizeEntity.class);
			newEntity.setOrganization(null);
			prizeService.createPrize(newEntity);
		});
	}

	/**
	 * Test to create a Prize with an invalid organization.
	 */
	@Test
	void testCreatePrizeWithNoValidOrganization2() {
		assertThrows(IllegalOperationException.class, () -> {
			PrizeEntity newEntity = factory.manufacturePojo(PrizeEntity.class);
			OrganizationEntity newOrgEntity = factory.manufacturePojo(OrganizationEntity.class);
			newOrgEntity.setId(0L);
			newEntity.setOrganization(newOrgEntity);
			prizeService.createPrize(newEntity);
		});
	}

	/**
	 * Test to create a Prize with an invalid organization.
	 */
	@Test
	void testCreatePrizeWithNoValidOrganization3() {
		assertThrows(IllegalOperationException.class, () -> {
			PrizeEntity newEntity = factory.manufacturePojo(PrizeEntity.class);
			newEntity.setOrganization(organizationList.get(0));
			prizeService.createPrize(newEntity);
		});
	}

	/**
	 * Test to retrieve the list of Prizes.
	 */
	@Test
	void testGetPrizes() {
		List<PrizeEntity> list = prizeService.getPrizes();
		assertEquals(prizeList.size(), list.size());
		for (PrizeEntity entity : list) {
			boolean found = false;
			for (PrizeEntity storedEntity : prizeList) {
				if (entity.getId().equals(storedEntity.getId())) {
					found = true;
				}
			}
			assertTrue(found);
		}
	}

	/**
	 * Test to retrieve a Prize.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testGetPrize() throws EntityNotFoundException {
		PrizeEntity entity = prizeList.get(0);
		PrizeEntity resultEntity = prizeService.getPrize(entity.getId());
		assertNotNull(resultEntity);
		assertEquals(entity.getName(), resultEntity.getName());
		assertEquals(entity.getId(), resultEntity.getId());
		assertEquals(entity.getDescription(), resultEntity.getDescription());
		assertEquals(entity.getPremiationDate(), resultEntity.getPremiationDate());
	}

	/**
	 * Test to retrieve a non-existent Prize.
	 */
	@Test
	void testGetInvalidPrize() {
		assertThrows(EntityNotFoundException.class, () -> {
			prizeService.getPrize(0L);
		});
	}

	/**
	 * Test to update a Prize.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testUpdatePrize() throws EntityNotFoundException {
		PrizeEntity entity = prizeList.get(0);
		PrizeEntity pojoEntity = factory.manufacturePojo(PrizeEntity.class);

		pojoEntity.setId(entity.getId());

		prizeService.updatePrize(entity.getId(), pojoEntity);

		PrizeEntity resp = entityManager.find(PrizeEntity.class, entity.getId());

		assertEquals(pojoEntity.getId(), resp.getId());
		assertEquals(pojoEntity.getName(), resp.getName());
		assertEquals(pojoEntity.getDescription(), resp.getDescription());
		assertEquals(pojoEntity.getPremiationDate(), resp.getPremiationDate());
	}

	/**
	 * Test to update a non-existent Prize.
	 */
	@Test
	void testUpdateInvalidPrize() {
		assertThrows(EntityNotFoundException.class, () -> {
			PrizeEntity pojoEntity = factory.manufacturePojo(PrizeEntity.class);
			pojoEntity.setId(0L);
			prizeService.updatePrize(0L, pojoEntity);
		});
	}

	/**
	 * Test to delete a Prize.
	 * 
	 * @throws EntityNotFoundException
	 * @throws IllegalOperationException
	 */
	@Test
	void testDeletePrize() throws EntityNotFoundException, IllegalOperationException {
		prizeService.deletePrize(prizeList.get(0).getId());
		PrizeEntity result = entityManager.find(PrizeEntity.class, prizeList.get(0).getId());
		assertNull(result);
	}

	/**
	 * Test to delete a Prize with an associated author.
	 */
	@Test
	void testDeletePrizeWithAuthor() {
		assertThrows(IllegalOperationException.class, () -> {

			AuthorEntity author = factory.manufacturePojo(AuthorEntity.class);
			entityManager.persist(author);

			PrizeEntity prizeEntity = prizeList.get(2);
			prizeEntity.setAuthor(author);

			prizeService.deletePrize(prizeEntity.getId());
		});
	}

	/**
	 * Test to delete a non-existent Prize.
	 */
	@Test
	void testDeleteInvalidPrize() {
		assertThrows(EntityNotFoundException.class, () -> {
			prizeService.deletePrize(0L);
		});
	}

}