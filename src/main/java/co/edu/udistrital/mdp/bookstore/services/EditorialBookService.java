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
 * Clase que implementa la conexión con la persistencia para la relación entre
 * la entidad Editorial y Book.
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
	 * Agregar un book a la editorial
	 *
	 * @param bookId      El id libro a guardar
	 * @param editorialId El id de la editorial en la cual se va a guardar el libro.
	 * @return El libro creado.
	 * @throws EntityNotFoundException
	 */

	@Transactional
	public BookEntity addBook(Long bookId, Long editorialId) throws EntityNotFoundException {
		log.info("Inicia proceso de agregarle un libro a la editorial con id = {0}", editorialId);

		Optional<BookEntity> bookOptional = bookRepository.findById(bookId);
		if (bookOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.BOOK_NOT_FOUND);

		Optional<EditorialEntity> editorialOptional = editorialRepository.findById(editorialId);
		if (editorialOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.EDITORIAL_NOT_FOUND);

		BookEntity bookEntity = bookOptional.get();
		EditorialEntity editorialEntity = editorialOptional.get();

		bookEntity.setEditorial(editorialEntity);
		log.info("Termina proceso de agregarle un libro a la editorial con id = {0}", editorialId);
		return bookEntity;
	}

	/**
	 * Retorna todos los books asociados a una editorial
	 *
	 * @param editorialId El ID de la editorial buscada
	 * @return La lista de libros de la editorial
	 * @throws EntityNotFoundException si la editorial no existe
	 */
	@Transactional
	public List<BookEntity> getBooks(Long editorialId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar los libros asociados a la editorial con id = {0}", editorialId);
		Optional<EditorialEntity> editorialOptional = editorialRepository.findById(editorialId);
		if (editorialOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.EDITORIAL_NOT_FOUND);

		EditorialEntity editorialEntity = editorialOptional.get();
		return editorialEntity.getBooks();
	}

	/**
	 * Retorna un book asociado a una editorial
	 *
	 * @param editorialId El id de la editorial a buscar.
	 * @param bookId      El id del libro a buscar
	 * @return El libro encontrado dentro de la editorial.
	 * @throws EntityNotFoundException   Si el libro no se encuentra en la editorial
	 * @throws IllegalOperationException Si el libro no está asociado a la editorial
	 */
	@Transactional
	public BookEntity getBook(Long editorialId, Long bookId) throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de consultar el libro con id = {0} de la editorial con id = " + editorialId, bookId);

		Optional<EditorialEntity> editorialOptional = editorialRepository.findById(editorialId);
		if (editorialOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.EDITORIAL_NOT_FOUND);

		Optional<BookEntity> bookOptional = bookRepository.findById(bookId);
		if (bookOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.BOOK_NOT_FOUND);

		log.info("Termina proceso de consultar el libro con id = {0} de la editorial con id = " + editorialId, bookId);

		EditorialEntity editorialEntity = editorialOptional.get();
		BookEntity bookEntity = bookOptional.get();

		if (!editorialEntity.getBooks().contains(bookEntity))
			throw new IllegalOperationException("The book is not associated to the editorial");

		return bookEntity;
	}

	/**
	 * Remplazar books de una editorial
	 *
	 * @param books       Lista de libros que serán los de la editorial.
	 * @param editorialId El id de la editorial que se quiere actualizar.
	 * @return La lista de libros actualizada.
	 * @throws EntityNotFoundException Si la editorial o un libro de la lista no se
	 *                                 encuentran
	 */
	@Transactional
	public List<BookEntity> replaceBooks(Long editorialId, List<BookEntity> books) throws EntityNotFoundException {
		log.info("Inicia proceso de actualizar la editorial con id = {0}", editorialId);
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