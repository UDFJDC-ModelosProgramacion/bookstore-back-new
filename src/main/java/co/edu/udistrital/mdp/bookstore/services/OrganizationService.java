/*
MIT License

Copyright (c) 2025 Universidad Distrital

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

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.bookstore.entities.OrganizationEntity;
import co.edu.udistrital.mdp.bookstore.entities.PrizeEntity;
import co.edu.udistrital.mdp.bookstore.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.bookstore.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.bookstore.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.bookstore.repositories.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Class implementing persistence layer logic for the Organization entity.
 *
 * @author Jose Bocanegra
 */

@RequiredArgsConstructor
@Slf4j
@Service
public class OrganizationService {

	final OrganizationRepository organizationRepository;

	/**
	 * Creates an organization in persistence.
	 *
	 * @param organizationEntity The entity representing the organization to
	 *                           persist.
	 * @return The organization entity after persisting it.
	 * @throws IllegalOperationException If the organization to persist already
	 *                                   exists.
	 */
	@Transactional
	public OrganizationEntity createOrganization(OrganizationEntity organizationEntity)
			throws IllegalOperationException {
		log.info("Starting process to create organization");
		if (!organizationRepository.findByName(organizationEntity.getName()).isEmpty()) {
			throw new IllegalOperationException(ErrorMessage.ORGANIZATION_EXISTS);
		}
		log.info("Finished process to create organization");
		return organizationRepository.save(organizationEntity);
	}

	/**
	 * Retrieves all existing organizations from the database.
	 *
	 * @return A list of organizations.
	 */
	@Transactional
	public List<OrganizationEntity> getOrganizations() {
		log.info("Starting process to fetch all organizations");
		return organizationRepository.findAll();
	}

	/**
	 * Retrieves an organization by its ID.
	 *
	 * @param organizationId: ID of the organization to search for.
	 * @return The requested organization matching the ID.
	 */
	@Transactional
	public OrganizationEntity getOrganization(Long organizationId) throws EntityNotFoundException {
		log.info("Starting process to fetch organization with id = {0}", organizationId);
		Optional<OrganizationEntity> organizationOptional = organizationRepository.findById(organizationId);

		if (organizationOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.ORGANIZATION_NOT_FOUND);

		log.info("Finished process to fetch organization with id = {0}", organizationId);

		OrganizationEntity organizationEntity = organizationOptional.get();

		return organizationEntity;
	}

	/**
	 * Updates an organization.
	 *
	 * @param organizationId: ID of the organization to search for in the database.
	 * @param organization:   Organization containing the changes to update, such as
	 *                        the name.
	 * @return The organization with updated changes saved in the database.
	 */
	@Transactional
	public OrganizationEntity updateOrganization(Long organizationId, OrganizationEntity organization)
			throws EntityNotFoundException {
		log.info("Starting process to update organization with id = {0}", organizationId);
		Optional<OrganizationEntity> organizationOptional = organizationRepository.findById(organizationId);
		if (organizationOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.ORGANIZATION_NOT_FOUND);

		organization.setId(organizationId);
		log.info("Finished process to update organization with id={0}", organizationId);
		return organizationRepository.save(organization);
	}

	/**
	 * Deletes an organization.
	 *
	 * @param organizationId: ID of the organization to delete.
	 * @throws IllegalOperationException If the organization has an associated
	 *                                   prize.
	 */
	@Transactional
	public void deleteOrganization(Long organizationId) throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to delete organization with id = {0}", organizationId);
		Optional<OrganizationEntity> organizationOptional = organizationRepository.findById(organizationId);
		if (organizationOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.ORGANIZATION_NOT_FOUND);

		OrganizationEntity organizationEntity = organizationOptional.get();

		PrizeEntity prize = organizationEntity.getPrize();
		if (prize != null)
			throw new IllegalOperationException(ErrorMessage.ORGANIZATION_ASSOCIATED_PRIZE);

		organizationRepository.deleteById(organizationId);
		log.info("Finished process to delete organization with id = {0}", organizationId);
	}
}