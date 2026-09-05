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
import co.edu.udistrital.mdp.bookstore.repositories.PrizeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Class implementing persistence layer logic for the Prize entity.
 *
 * @author Jose Bocanegra
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class PrizeService {

	final PrizeRepository prizeRepository;

	final OrganizationRepository organizationRepository;

	/**
	 * Save a new prize
	 *
	 * @param prizeEntity The prize entity of the new prize to persist.
	 * @return The entity after persisting it
	 * @throws IllegalOperationException If the organization does not exist or
	 *                                   already holds a prize.
	 */

	@Transactional
	public PrizeEntity createPrize(PrizeEntity prizeEntity) throws IllegalOperationException {
		log.info("Starting process to create prize");
		if (prizeEntity.getOrganization() == null)
			throw new IllegalOperationException(ErrorMessage.ORGANIZATION_NOT_VALID);

		Optional<OrganizationEntity> organizationOptional = organizationRepository
				.findById(prizeEntity.getOrganization().getId());
		if (organizationOptional.isEmpty())
			throw new IllegalOperationException(ErrorMessage.ORGANIZATION_NOT_VALID);

		OrganizationEntity organizationEntity = organizationOptional.get();

		if (organizationEntity.getPrize() != null)
			throw new IllegalOperationException(ErrorMessage.ORGANIZATION_HOLDS_PRIZE);

		log.info("Finished process to create prize");
		return prizeRepository.save(prizeEntity);
	}

	/**
	 * Returns all prizes available in the database.
	 *
	 * @return List of prize entities.
	 */
	@Transactional
	public List<PrizeEntity> getPrizes() {
		log.info("Starting process to fetch all prizes");
		return prizeRepository.findAll();
	}

	/**
	 * Searches for a prize by ID
	 *
	 * @param prizeId The ID of the prize to search for
	 * @return The found prize
	 * @throws EntityNotFoundException If the prize is not found
	 */
	@Transactional
	public PrizeEntity getPrize(Long prizeId) throws EntityNotFoundException {
		log.info("Starting process to fetch prize with id = {0}", prizeId);
		Optional<PrizeEntity> prizeOptional = prizeRepository.findById(prizeId);
		if (prizeOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PRIZE_NOT_FOUND);

		log.info("Finished process to fetch prize with id = {0}", prizeId);
		PrizeEntity prizeEntity = prizeOptional.get();
		return prizeEntity;
	}

	/**
	 * Update a prize by ID
	 *
	 * @param prizeId The ID of the prize to update
	 * @param prize   The prize entity containing the desired changes
	 * @return The prize entity after updating it
	 */
	@Transactional
	public PrizeEntity updatePrize(Long prizeId, PrizeEntity prize) throws EntityNotFoundException {
		log.info("Starting process to update prize with id = {0}", prizeId);
		Optional<PrizeEntity> prizeOptional = prizeRepository.findById(prizeId);
		if (prizeOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PRIZE_NOT_FOUND);

		prize.setId(prizeId);

		log.info("Finished process to update prize with id = {0}", prizeId);
		return prizeRepository.save(prize);
	}

	/**
	 * Delete a prize by ID
	 *
	 * @param prizeId The ID of the prize to delete
	 * @throws IllegalOperationException If the prize has an associated author.
	 */

	@Transactional
	public void deletePrize(Long prizeId) throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to delete prize with id = {0}", prizeId);
		Optional<PrizeEntity> prizeOptional = prizeRepository.findById(prizeId);
		if (prizeOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PRIZE_NOT_FOUND);

		PrizeEntity prizeEntity = prizeOptional.get();

		if (prizeEntity.getAuthor() != null) {
			throw new IllegalOperationException(ErrorMessage.PRIZE_ASSOCIATED_AUTHOR);
		}

		if (prizeEntity.getOrganization() != null) {
			OrganizationEntity organizationEntity = prizeEntity.getOrganization();
			organizationEntity.setPrize(null);
			prizeEntity.setOrganization(null);
		}
		prizeRepository.delete(prizeEntity);
		log.info("Finished process to delete prize with id = {0}", prizeId);
	}
}