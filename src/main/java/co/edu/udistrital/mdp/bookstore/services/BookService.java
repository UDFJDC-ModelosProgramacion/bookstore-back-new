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

import co.edu.udistrital.mdp.bookstore.entities.AuthorEntity;
import co.edu.udistrital.mdp.bookstore.entities.BookEntity;
import co.edu.udistrital.mdp.bookstore.entities.EditorialEntity;
import co.edu.udistrital.mdp.bookstore.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.bookstore.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.bookstore.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.bookstore.repositories.BookRepository;
import co.edu.udistrital.mdp.bookstore.repositories.EditorialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class BookService {

	final BookRepository bookRepository;

	final EditorialRepository editorialRepository;

	/**
	 * Save a new book
	 *
	 * @param bookEntity The book entity of the new book to persist.
	 * @return The entity after persisting it
	 * @throws IllegalOperationException If the ISBN is invalid or already exists in
	 *                                   persistence, or if the publisher is invalid
	 */
	@Transactional
	public BookEntity createBook(BookEntity bookEntity) throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to create book");

		if (bookEntity.getEditorial() == null)
			throw new IllegalOperationException(ErrorMessage.EDITORIAL_NOT_VALID);

		Optional<EditorialEntity> editorialOptional = editorialRepository.findById(bookEntity.getEditorial().getId());
		if (editorialOptional.isEmpty())
			throw new IllegalOperationException(ErrorMessage.EDITORIAL_NOT_VALID);

		if (!validateISBN(bookEntity.getIsbn()))
			throw new IllegalOperationException(ErrorMessage.ISBN_NOT_VALID);

		if (!bookRepository.findByIsbn(bookEntity.getIsbn()).isEmpty())
			throw new IllegalOperationException(ErrorMessage.ISBN_ALREADY_EXISTS);

		EditorialEntity editorialEntity = editorialOptional.get();

		bookEntity.setEditorial(editorialEntity);
		log.info("Finished process to create book");
		return bookRepository.save(bookEntity);
	}

	/**
	 * Returns all books available in the database.
	 *
	 * @return List of book entities.
	 */
	@Transactional
	public List<BookEntity> getBooks() {
		log.info("Starting process to fetch all books");
		return bookRepository.findAll();
	}

	/**
	 * Searches for a book by ID
	 *
	 * @param bookId The ID of the book to search for
	 * @return The found book
	 * @throws EntityNotFoundException If the book is not found
	 */
	@Transactional
	public BookEntity getBook(Long bookId) throws EntityNotFoundException {
		log.info("Starting process to fetch book with id = {0}", bookId);
		Optional<BookEntity> bookOptional = bookRepository.findById(bookId);
		if (bookOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.BOOK_NOT_FOUND);

		BookEntity bookEntity = bookOptional.get();
		log.info("Finished process to fetch book with id = {0}", bookId);
		return bookEntity;
	}

	/**
	 * Update a book by ID
	 *
	 * @param bookId The ID of the book to update
	 * @param book   The book entity containing the desired changes
	 * @return The book entity after updating it
	 * @throws IllegalOperationException If the updated ISBN is invalid
	 * @throws EntityNotFoundException   If the book is not found
	 */
	@Transactional
	public BookEntity updateBook(Long bookId, BookEntity book)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to update book with id = {0}", bookId);
		Optional<BookEntity> bookOptional = bookRepository.findById(bookId);
		if (bookOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.BOOK_NOT_FOUND);

		if (!validateISBN(book.getIsbn()))
			throw new IllegalOperationException(ErrorMessage.ISBN_NOT_VALID);

		book.setId(bookId);
		log.info("Finished process to update book with id = {0}", bookId);
		return bookRepository.save(book);
	}

	/**
	 * Delete a book by ID
	 *
	 * @param bookId The ID of the book to delete
	 * @throws IllegalOperationException If the book has associated authors
	 * @throws EntityNotFoundException   If the book does not exist
	 */
	@Transactional
	public void deleteBook(Long bookId) throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to delete book with id = {0}", bookId);
		Optional<BookEntity> bookOptional = bookRepository.findById(bookId);
		if (bookOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.BOOK_NOT_FOUND);

		BookEntity bookEntity = bookOptional.get();
		List<AuthorEntity> authors = bookEntity.getAuthors();

		if (!authors.isEmpty())
			throw new IllegalOperationException(ErrorMessage.BOOK_ASSOCIATED_AUTHORS);

		bookRepository.deleteById(bookId);
		log.info("Finished process to delete book with id = {0}", bookId);
	}

	/**
	 * Verifies that the ISBN is not invalid.
	 *
	 * @param isbn to verify
	 * @return true if the ISBN is valid.
	 */
	private boolean validateISBN(String isbn) {
		return !(isbn == null || isbn.isEmpty());
	}
}