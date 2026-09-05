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

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.bookstore.entities.AuthorEntity;
import co.edu.udistrital.mdp.bookstore.entities.BookEntity;
import co.edu.udistrital.mdp.bookstore.entities.PrizeEntity;
import co.edu.udistrital.mdp.bookstore.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.bookstore.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.bookstore.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.bookstore.repositories.AuthorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Class that implements the persistence connection for the
 * Author entity.
 *
 * @author Jose Bocanegra
 */

@RequiredArgsConstructor
@Slf4j
@Service
public class AuthorService {

	final AuthorRepository authorRepository;

	/**
	 * Handles creating an Author in the database.
	 *
	 * @param author AuthorEntity object with new data
	 * @return AuthorEntity object with new data and its ID.
	 * @throws IllegalOperationException
	 */
	@Transactional
	public AuthorEntity createAuthor(AuthorEntity author) throws IllegalOperationException {
		log.info("Starting process to create author");
		Calendar calendar = Calendar.getInstance();
		if (author.getBirthDate().compareTo(calendar.getTime()) > 0) {
			throw new IllegalOperationException(ErrorMessage.BIRHT_DATE_AFTER);
		}

		return authorRepository.save(author);
	}

	/**
	 * Retrieves the list of Author records.
	 *
	 * @return Collection of AuthorEntity objects.
	 */
	@Transactional
	public List<AuthorEntity> getAuthors() {
		log.info("Starting process to fetch all authors");
		return authorRepository.findAll();
	}

	/**
	 * Retrieves the data of an Author instance by its ID.
	 *
	 * @param authorId Identifier of the instance to retrieve
	 * @return AuthorEntity instance with the queried Author data.
	 */
	@Transactional
	public AuthorEntity getAuthor(Long authorId) throws EntityNotFoundException {
		log.info("Starting process to fetch author with id = {0}", authorId);
		Optional<AuthorEntity> authorOptional = authorRepository.findById(authorId);

		if (authorOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.AUTHOR_NOT_FOUND);

		AuthorEntity authorEntity = authorOptional.get();
		log.info("Finished process to fetch author with id = {0}", authorId);
		return authorEntity;
	}

	/**
	 * Updates the information of an Author instance.
	 *
	 * @param authorId     Identifier of the instance to update
	 * @param authorEntity AuthorEntity instance with the new data.
	 * @return AuthorEntity instance with updated data.
	 */
	@Transactional
	public AuthorEntity updateAuthor(Long authorId, AuthorEntity author) throws EntityNotFoundException {
		log.info("Starting process to update author with id = {0}", authorId);
		Optional<AuthorEntity> authorOptional = authorRepository.findById(authorId);
		if (authorOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.AUTHOR_NOT_FOUND);
		log.info("Finished process to update author with id = {0}", authorId);
		author.setId(authorId);
		return authorRepository.save(author);
	}

	/**
	 * Deletes an Author instance from the database.
	 *
	 * @param authorId Identifier of the instance to delete.
	 * @throws BusinessLogicException if the author has associated books.
	 */
	@Transactional
	public void deleteAuthor(Long authorId) throws IllegalOperationException, EntityNotFoundException {
		log.info("Starting process to delete author with id = {0}", authorId);
		Optional<AuthorEntity> authorOptional = authorRepository.findById(authorId);
		if (authorOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.AUTHOR_NOT_FOUND);

		AuthorEntity authorEntity = authorOptional.get();

		List<BookEntity> books = authorEntity.getBooks();
		if (!books.isEmpty())
			throw new IllegalOperationException(ErrorMessage.AUTHOR_ASSOCIATED_BOOKS);

		List<PrizeEntity> prizes = authorEntity.getPrizes();
		if (!prizes.isEmpty())
			throw new IllegalOperationException(ErrorMessage.AUTHOR_ASSOCIATED_PRIZES);

		authorRepository.deleteById(authorId);
		log.info("Finished process to delete author with id = {0}", authorId);
	}
}