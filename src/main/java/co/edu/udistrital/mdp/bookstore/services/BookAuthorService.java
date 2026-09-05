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
import co.edu.udistrital.mdp.bookstore.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.bookstore.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.bookstore.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.bookstore.repositories.AuthorRepository;
import co.edu.udistrital.mdp.bookstore.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class BookAuthorService {

	private final BookRepository bookRepository;

	private final AuthorRepository authorRepository;

	/**
	 * Associates an existing Author to a Book
	 *
	 * @param bookId   Identifier of the Book instance
	 * @param authorId Identifier of the Author instance
	 * @return Instance of AuthorEntity that was associated with the Book
	 */
	@Transactional
	public AuthorEntity addAuthor(Long bookId, Long authorId) throws EntityNotFoundException {
		log.info("Starting process to associate an author with book id = {0}", bookId);
		Optional<AuthorEntity> authorOptional = authorRepository.findById(authorId);
		if (authorOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.AUTHOR_NOT_FOUND);

		Optional<BookEntity> bookOptional = bookRepository.findById(bookId);
		if (bookOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.BOOK_NOT_FOUND);

		BookEntity bookEntity = bookOptional.get();
		AuthorEntity authorEntity = authorOptional.get();
		bookEntity.getAuthors().add(authorEntity);
		log.info("Finished process to associate an author with book id = {0}", bookId);
		return authorEntity;
	}

	/**
	 * Retrieves a collection of AuthorEntity instances associated with a Book
	 * instance
	 *
	 * @param bookId Identifier of the Book instance
	 * @return Collection of AuthorEntity instances associated with the Book
	 *         instance
	 */
	@Transactional
	public List<AuthorEntity> getAuthors(Long bookId) throws EntityNotFoundException {
		log.info("Starting process to fetch all authors of book id = {0}", bookId);
		Optional<BookEntity> bookOptional = bookRepository.findById(bookId);
		if (bookOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.BOOK_NOT_FOUND);

		BookEntity bookEntity = bookOptional.get();
		log.info("Finished process to fetch all authors of book id = {0}", bookId);
		return bookEntity.getAuthors();
	}

	/**
	 * Retrieves an AuthorEntity instance associated with a Book instance
	 *
	 * @param bookId   Identifier of the Book instance
	 * @param authorId Identifier of the Author instance
	 * @return The Author entity associated with the book
	 */
	@Transactional
	public AuthorEntity getAuthor(Long bookId, Long authorId)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to fetch an author of book id = {0}", bookId);
		Optional<AuthorEntity> authorOptional = authorRepository.findById(authorId);
		Optional<BookEntity> bookOptional = bookRepository.findById(bookId);

		if (authorOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.AUTHOR_NOT_FOUND);

		if (bookOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.BOOK_NOT_FOUND);

		AuthorEntity authorEntity = authorOptional.get();
		BookEntity bookEntity = bookOptional.get();

		log.info("Finished process to fetch an author of book id = {0}", bookId);
		if (!bookEntity.getAuthors().contains(authorEntity))
			throw new IllegalOperationException(ErrorMessage.AUTHOR_NOT_ASSOCIATED_BOOK);

		return authorEntity;
	}

	@Transactional
	/**
	 * Replaces the Author instances associated with a Book instance
	 *
	 * @param bookId Identificador of the Book instance
	 * @param list   Collection of AuthorEntity instances to associate with the Book
	 *               instance
	 * @return New collection of AuthorEntity associated with the Book instance
	 */
	public List<AuthorEntity> replaceAuthors(Long bookId, List<AuthorEntity> list) throws EntityNotFoundException {
		log.info("Starting process to replace authors of book id = {0}", bookId);
		Optional<BookEntity> bookOptional = bookRepository.findById(bookId);
		if (bookOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.BOOK_NOT_FOUND);

		BookEntity bookEntity = bookOptional.get();

		for (AuthorEntity author : list) {
			Optional<AuthorEntity> authorOptional = authorRepository.findById(author.getId());
			if (authorOptional.isEmpty())
				throw new EntityNotFoundException(ErrorMessage.AUTHOR_NOT_FOUND);

			AuthorEntity authorEntity = authorOptional.get();

			if (!bookEntity.getAuthors().contains(authorEntity))
				bookEntity.getAuthors().add(authorEntity);
		}
		log.info("Finished process to replace authors of book id = {0}", bookId);
		return bookEntity.getAuthors();
	}

	@Transactional
	/**
	 * Disassociates an existing Author from an existing Book
	 *
	 * @param bookId   Identifier of the Book instance
	 * @param authorId Identifier of the Author instance
	 */
	public void removeAuthor(Long bookId, Long authorId) throws EntityNotFoundException {
		log.info("Starting process to remove an author from book id = {0}", bookId);
		Optional<AuthorEntity> authorOptional = authorRepository.findById(authorId);
		Optional<BookEntity> bookOptional = bookRepository.findById(bookId);

		if (authorOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.AUTHOR_NOT_FOUND);

		if (bookOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.BOOK_NOT_FOUND);

		BookEntity bookEntity = bookOptional.get();
		AuthorEntity authorEntity = authorOptional.get();

		bookEntity.getAuthors().remove(authorEntity);

		log.info("Finished process to remove an author from book id = {0}", bookId);
	}
}