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

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import co.edu.udistrital.mdp.bookstore.entities.BookEntity;
import co.edu.udistrital.mdp.bookstore.entities.EditorialEntity;
import co.edu.udistrital.mdp.bookstore.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.bookstore.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.bookstore.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.bookstore.repositories.BookRepository;
import co.edu.udistrital.mdp.bookstore.repositories.EditorialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Class that implements the persistence connection for the relationship between
 * the Editorial and Book entities.
 *
 * @author Jose Bocanegra
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class EditorialBookService {

	private final BookRepository bookRepository;

	private final EditorialRepository editorialRepository;

	/**
	 * Adds a book to the editorial
	 *
	 * @param bookId      The ID of the book to save
	 * @param editorialId The ID of the editorial where the book will be saved.
	 * @return The created book.
	 * @throws EntityNotFoundException
	 */

	@Transactional
	public BookEntity addBook(Long bookId, Long editorialId) throws EntityNotFoundException {
		log.info("Starting process to add a book to the editorial with id = {0}", editorialId);

		Optional<BookEntity> bookOptional = bookRepository.findById(bookId);
		if (bookOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.BOOK_NOT_FOUND);

		Optional<EditorialEntity> editorialOptional = editorialRepository.findById(editorialId);
		if (editorialOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.EDITORIAL_NOT_FOUND);

		BookEntity bookEntity = bookOptional.get();
		EditorialEntity editorialEntity = editorialOptional.get();

		bookEntity.setEditorial(editorialEntity);
		log.info("Finished process to add a book to the editorial with id = {0}", editorialId);
		return bookEntity;
	}

	/**
	 * Returns all books associated with an editorial
	 *
	 * @param editorialId The ID of the target editorial
	 * @return The list of books belonging to the editorial
	 * @throws EntityNotFoundException if the editorial does not exist
	 */
	@Transactional
	public List<BookEntity> getBooks(Long editorialId) throws EntityNotFoundException {
		log.info("Starting process to fetch books associated with editorial with id = {0}", editorialId);
		Optional<EditorialEntity> editorialOptional = editorialRepository.findById(editorialId);
		if (editorialOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.EDITORIAL_NOT_FOUND);

		EditorialEntity editorialEntity = editorialOptional.get();
		return editorialEntity.getBooks();
	}

	/**
	 * Returns a book associated with an editorial
	 *
	 * @param editorialId The ID of the editorial to search.
	 * @param bookId      The ID of the book to search
	 * @return The book found within the editorial.
	 * @throws EntityNotFoundException   If the book is not found in the editorial
	 * @throws IllegalOperationException If the book is not associated with the
	 *                                   editorial
	 */
	@Transactional
	public BookEntity getBook(Long editorialId, Long bookId) throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to fetch book with id = {0} of editorial with id = " + editorialId, bookId);

		Optional<EditorialEntity> editorialOptional = editorialRepository.findById(editorialId);
		if (editorialOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.EDITORIAL_NOT_FOUND);

		Optional<BookEntity> bookOptional = bookRepository.findById(bookId);
		if (bookOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.BOOK_NOT_FOUND);

		log.info("Finished process to fetch book with id = {0} of editorial with id = " + editorialId, bookId);

		EditorialEntity editorialEntity = editorialOptional.get();
		BookEntity bookEntity = bookOptional.get();

		if (!editorialEntity.getBooks().contains(bookEntity))
			throw new IllegalOperationException(ErrorMessage.BOOK_NOT_ASSOCIATED_EDITORIAL);

		return bookEntity;
	}

	/**
	 * Replaces books of an editorial
	 *
	 * @param books       List of books that will belong to the editorial.
	 * @param editorialId The ID of the editorial to update.
	 * @return The updated list of books.
	 * @throws EntityNotFoundException If the editorial or a book in the list is not
	 *                                 found
	 */
	@Transactional
	public List<BookEntity> replaceBooks(Long editorialId, List<BookEntity> books) throws EntityNotFoundException {
		log.info("Starting process to update editorial with id = {0}", editorialId);
		Optional<EditorialEntity> editorialOptional = editorialRepository.findById(editorialId);
		if (editorialOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.EDITORIAL_NOT_FOUND);

		EditorialEntity editorialEntity = editorialOptional.get();

		for (BookEntity book : books) {
			Optional<BookEntity> bookOptional = bookRepository.findById(book.getId());
			if (bookOptional.isEmpty())
				throw new EntityNotFoundException(ErrorMessage.BOOK_NOT_FOUND);
			BookEntity bookEntity = bookOptional.get();
			bookEntity.setEditorial(editorialEntity);
		}
		return books;
	}
}