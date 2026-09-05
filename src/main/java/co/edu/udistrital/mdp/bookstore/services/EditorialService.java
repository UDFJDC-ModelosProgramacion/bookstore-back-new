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

import co.edu.udistrital.mdp.bookstore.entities.BookEntity;
import co.edu.udistrital.mdp.bookstore.entities.EditorialEntity;
import co.edu.udistrital.mdp.bookstore.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.bookstore.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.bookstore.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.bookstore.repositories.EditorialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Class implementing persistence layer logic for the Editorial entity.
 *
 * @author Jose Bocanegra
 */

@RequiredArgsConstructor
@Slf4j
@Service
public class EditorialService {

	final EditorialRepository editorialRepository;

	/**
	 * Creates an editorial in persistence.
	 *
	 * @param editorialEntity The entity representing the editorial to persist.
	 * @return The editorial entity after persisting it.
	 * @throws IllegalOperationException If the editorial to persist already exists.
	 */
	@Transactional
	public EditorialEntity createEditorial(EditorialEntity editorialEntity) throws IllegalOperationException {
		log.info("Starting process to create editorial");
		if (!editorialRepository.findByName(editorialEntity.getName()).isEmpty()) {
			throw new IllegalOperationException(ErrorMessage.EDITORIAL_EXISTS);
		}
		log.info("Finished process to create editorial");
		return editorialRepository.save(editorialEntity);
	}

	/**
	 *
	 * Retrieves all existing editorials from the database.
	 *
	 * @return A list of editorials.
	 */
	@Transactional
	public List<EditorialEntity> getEditorials() {
		log.info("Starting process to fetch all editorials");
		return editorialRepository.findAll();
	}

	/**
	 *
	 * Retrieves an editorial by its ID.
	 *
	 * @param editorialId: ID of the editorial to search for.
	 * @return The requested editorial matching the ID.
	 */
	@Transactional
	public EditorialEntity getEditorial(Long editorialId) throws EntityNotFoundException {
		log.info("Starting process to fetch editorial with id = {0}", editorialId);
		Optional<EditorialEntity> editorialOptional = editorialRepository.findById(editorialId);
		if (editorialOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.EDITORIAL_NOT_FOUND);
		log.info("Finished process to fetch editorial with id = {0}", editorialId);
		EditorialEntity editorialEntity = editorialOptional.get();
		return editorialEntity;
	}

	/**
	 *
	 * Updates an editorial.
	 *
	 * @param editorialId: ID of the editorial to find in the database.
	 * @param editorial:   Editorial containing the changes to update.
	 * @return The editorial with updated changes saved in the database.
	 */
	@Transactional
	public EditorialEntity updateEditorial(Long editorialId, EditorialEntity editorial) throws EntityNotFoundException {
		log.info("Starting process to update editorial with id = {0}", editorialId);
		Optional<EditorialEntity> editorialOptional = editorialRepository.findById(editorialId);
		if (editorialOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.EDITORIAL_NOT_FOUND);

		editorial.setId(editorialId);
		log.info("Finished process to update editorial with id = {0}", editorialId);
		return editorialRepository.save(editorial);
	}

	/**
	 * Deletes an editorial
	 *
	 * @param editorialId: ID of the editorial to delete
	 * @throws IllegalOperationException If the editorial to delete has associated
	 *                                   books.
	 */
	@Transactional
	public void deleteEditorial(Long editorialId) throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to delete editorial with id = {0}", editorialId);
		Optional<EditorialEntity> editorialOptional = editorialRepository.findById(editorialId);
		if (editorialOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.EDITORIAL_NOT_FOUND);

		EditorialEntity editorialEntity = editorialOptional.get();

		List<BookEntity> books = editorialEntity.getBooks();

		if (!books.isEmpty()) {
			throw new IllegalOperationException(ErrorMessage.EDITORIAL_ASSOCIATED_BOOKS);
		}

		editorialRepository.deleteById(editorialId);
		log.info("Finished process to delete editorial with id = {0}", editorialId);
	}
}