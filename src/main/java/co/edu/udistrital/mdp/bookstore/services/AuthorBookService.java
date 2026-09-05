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

/**
 * Class that implements the persistence connection for the relationship between
 * the Author and Book entities.
 *
 * @author Jose Bocanegra
 */

@RequiredArgsConstructor
@Slf4j
@Service
public class AuthorBookService {

	private final BookRepository bookRepository;

	private final AuthorRepository authorRepository;

	/**
	 * Associates an existing Book to an Author
	 *
	 * @param authorId Identifier of the Author instance
	 * @param bookId   Identifier of the Book instance
	 * @return Instance of BookEntity that was associated with the Author
	 */
	@Transactional
	public BookEntity addBook(Long authorId, Long bookId) throws EntityNotFoundException {
		log.info("Starting process to associate a book to the author with id = {0}", authorId);
		Optional<AuthorEntity> authorOptional = authorRepository.findById(authorId);
		Optional<BookEntity> bookOptional = bookRepository.findById(bookId);

		if (authorOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.AUTHOR_NOT_FOUND);

		if (bookOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.BOOK_NOT_FOUND);

		BookEntity bookEntity = bookOptional.get();
		AuthorEntity authorEntity = authorOptional.get();

		bookEntity.getAuthors().add(authorEntity);
		log.info("Finished process to associate a book to the author with id = {0}", authorId);
		return bookEntity;
	}

	/**
	 * Retrieves a collection of BookEntity instances associated with an
	 * Author instance
	 *
	 * @param authorsId Identifier of the Author instance
	 * @return Collection of BookEntity instances associated with the Author
	 *         instance
	 */
	@Transactional
	public List<BookEntity> getBooks(Long authorId) throws EntityNotFoundException {
		log.info("Starting process to fetch all books of the author with id = {0}", authorId);
		Optional<AuthorEntity> authorOptional = authorRepository.findById(authorId);
		if (authorOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.AUTHOR_NOT_FOUND);

		AuthorEntity authorEntity = authorOptional.get();
		log.info("Finished process to fetch all books of the author with id = {0}", authorId);
		return authorEntity.getBooks();
	}

	/**
	 * Retrieves a BookEntity instance associated with an Author instance
	 *
	 * @param authorsId Identifier of the Author instance
	 * @param booksId   Identifier of the Book instance
	 * @return The Book entity of the author
	 */
	@Transactional
	public BookEntity getBook(Long authorId, Long bookId) throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to fetch book with id = {0} of author with id = " + authorId, bookId);
		Optional<AuthorEntity> authorOptional = authorRepository.findById(authorId);
		Optional<BookEntity> bookOptional = bookRepository.findById(bookId);

		if (authorOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.AUTHOR_NOT_FOUND);

		if (bookOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.BOOK_NOT_FOUND);

		BookEntity bookEntity = bookOptional.get();
		AuthorEntity authorEntity = authorOptional.get();

		log.info("Finished process to fetch book with id = {0} of author with id = " + authorId, bookId);
		if (!bookEntity.getAuthors().contains(authorEntity))
			throw new IllegalOperationException("The book is not associated to the author");

		return bookEntity;
	}

	/**
	 * Replaces the Book instances associated with an Author instance
	 *
	 * @param authorId Identifier of the Author instance
	 * @param books    Collection of BookEntity instances to associate with the
	 *                 Author instance
	 * @return New collection of BookEntity associated with the Author instance
	 */
	@Transactional
	public List<BookEntity> addBooks(Long authorId, List<BookEntity> books) throws EntityNotFoundException {
		log.info("Starting process to replace books associated with author with id = {0}", authorId);
		Optional<AuthorEntity> authorOptional = authorRepository.findById(authorId);
		if (authorOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.AUTHOR_NOT_FOUND);

		for (BookEntity book : books) {
			Optional<BookEntity> bookOptional = bookRepository.findById(book.getId());
			if (bookOptional.isEmpty())
				throw new EntityNotFoundException(ErrorMessage.BOOK_NOT_FOUND);

		}

		AuthorEntity authorEntity = authorOptional.get();
		log.info("Finished process to replace books associated with author with id = {0}", authorId);
		authorEntity.setBooks(books);
		return authorEntity.getBooks();
	}

	/**
	 * Disassociates an existing Book from an existing Author
	 *
	 * @param authorsId Identifier of the Author instance
	 * @param booksId   Identifier of the Book instance
	 */
	@Transactional
	public void removeBook(Long authorId, Long bookId) throws EntityNotFoundException {
		log.info("Starting process to remove a book from author with id = {0}", authorId);
		Optional<AuthorEntity> authorOptional = authorRepository.findById(authorId);
		if (authorOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.AUTHOR_NOT_FOUND);

		Optional<BookEntity> bookOptional = bookRepository.findById(bookId);
		if (bookOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.BOOK_NOT_FOUND);

		AuthorEntity authorEntity = authorOptional.get();
		BookEntity bookEntity = bookOptional.get();

		bookEntity.getAuthors().remove(authorEntity);
		authorEntity.getBooks().remove(bookEntity);
		log.info("Finished process to remove a book from author with id = {0}", authorId);
	}
}