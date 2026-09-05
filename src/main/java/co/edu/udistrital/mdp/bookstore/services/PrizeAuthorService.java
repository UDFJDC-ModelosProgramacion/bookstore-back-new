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

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.bookstore.entities.AuthorEntity;
import co.edu.udistrital.mdp.bookstore.entities.PrizeEntity;
import co.edu.udistrital.mdp.bookstore.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.bookstore.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.bookstore.repositories.AuthorRepository;
import co.edu.udistrital.mdp.bookstore.repositories.PrizeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Class that implements the persistence connection for the relationship between
 * the Prize and Author entities.
 *
 * @author Jose Bocanegra
 */
@RequiredArgsConstructor
@Slf4j
@Service

public class PrizeAuthorService {

	private final AuthorRepository authorRepository;

	private final PrizeRepository prizeRepository;

	/**
	 * Adds an author to a prize
	 *
	 * @param prizeId  The ID of the prize to save
	 * @param authorId The ID of the author to whom the prize will be assigned.
	 * @return The author that was associated with the prize.
	 * @throws EntityNotFoundException
	 */
	@Transactional
	public AuthorEntity addAuthor(Long authorId, Long prizeId) throws EntityNotFoundException {
		log.info("Starting process to associate author with id = {0} to prize with id = " + prizeId, authorId);
		Optional<AuthorEntity> autorOptional = authorRepository.findById(authorId);
		if (autorOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.AUTHOR_NOT_FOUND);

		Optional<PrizeEntity> prizeOptional = prizeRepository.findById(prizeId);
		if (prizeOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PRIZE_NOT_FOUND);

		AuthorEntity autorEntity = autorOptional.get();
		PrizeEntity prizeEntity = prizeOptional.get();

		prizeEntity.setAuthor(autorEntity);
		log.info("Finished process to associate author with id = {0} to prize with id = {1}", authorId, prizeId);
		return autorEntity;
	}

	/**
	 *
	 * Retrieves an author by using the prize ID.
	 *
	 * @param prizeId ID of the prize to search for.
	 * @return The requested author.
	 * @throws EntityNotFoundException
	 */

	@Transactional
	public AuthorEntity getAuthor(Long prizeId) throws EntityNotFoundException {
		log.info("Starting process to fetch the author of the prize with id = {0}", prizeId);
		Optional<PrizeEntity> prizeOptional = prizeRepository.findById(prizeId);
		if (prizeOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PRIZE_NOT_FOUND);

		PrizeEntity prizeEntity = prizeOptional.get();
		AuthorEntity authorEntity = prizeEntity.getAuthor();

		if (authorEntity == null)
			throw new EntityNotFoundException(ErrorMessage.AUTHOR_NOT_FOUND);

		log.info("Finished process to fetch the author of the prize with id = {0}", prizeId);
		return authorEntity;
	}

	/**
	 * Replaces the author of a prize
	 *
	 * @param prizeId  The ID of the prize to update.
	 * @param authorId The ID of the new author associated with the prize.
	 * @return The newly associated author.
	 * @throws EntityNotFoundException
	 */

	@Transactional
	public AuthorEntity replaceAuthor(Long prizeId, Long authorId) throws EntityNotFoundException {
		log.info("Starting process to update the author of the prize with id = {0}", prizeId);
		Optional<AuthorEntity> autorOptional = authorRepository.findById(authorId);
		if (autorOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.AUTHOR_NOT_FOUND);

		Optional<PrizeEntity> prizeOptional = prizeRepository.findById(prizeId);
		if (prizeOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PRIZE_NOT_FOUND);

		AuthorEntity autorEntity = autorOptional.get();
		PrizeEntity prizeEntity = prizeOptional.get();

		prizeEntity.setAuthor(autorEntity);
		log.info("Finished process to associate author with id = {0} to prize with id = " + prizeId, authorId);
		return autorEntity;
	}

	/**
	 * Deletes the author of a prize
	 *
	 * @param prizeId The prize whose author relationship is to be deleted.
	 * @throws EntityNotFoundException If the prize does not have an author
	 */

	@Transactional
	public void removeAuthor(Long prizeId) throws EntityNotFoundException {
		log.info("Starting process to delete the author of the prize with id = {0}", prizeId);
		Optional<PrizeEntity> prizeOptional = prizeRepository.findById(prizeId);
		if (prizeOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PRIZE_NOT_FOUND);

		PrizeEntity prizeEntity = prizeOptional.get();

		if (prizeEntity.getAuthor() == null) {
			throw new EntityNotFoundException(ErrorMessage.PRIZE_NOT_ASSOCIATED_AUTHOR);
		}
		Optional<AuthorEntity> authorOptional = authorRepository.findById(prizeEntity.getAuthor().getId());

		authorOptional.ifPresent(author -> {
			prizeEntity.setAuthor(null);
			author.getPrizes().remove(prizeEntity);
		});

		log.info("Finished process to delete the author of the prize with id = " + prizeId);
	}
}