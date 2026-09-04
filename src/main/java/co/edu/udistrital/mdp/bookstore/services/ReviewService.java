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
import co.edu.udistrital.mdp.bookstore.entities.ReviewEntity;
import co.edu.udistrital.mdp.bookstore.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.bookstore.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.bookstore.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.bookstore.repositories.BookRepository;
import co.edu.udistrital.mdp.bookstore.repositories.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase que implementa la conexion con la persistencia para la entidad de
 * Reseña(Review).
 *
 * @author Jose Bocanegra
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class ReviewService {

	final ReviewRepository reviewRepository;

	final BookRepository bookRepository;

	/**
	 * Se encarga de crear un Review en la base de datos.
	 *
	 * @param reviewEntity Objeto de ReviewEntity con los datos nuevos
	 * @param bookId       id del Book el cual sera padre del nuevo Review.
	 * @return Objeto de ReviewEntity con los datos nuevos y su ID.
	 * @throws EntityNotFoundException si el book no existe.
	 *
	 */
	@Transactional
	public ReviewEntity createReview(Long bookId, ReviewEntity reviewEntity) throws EntityNotFoundException {
		log.info("Inicia proceso de crear review");
		Optional<BookEntity> bookOptional = bookRepository.findById(bookId);
		if (bookOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.BOOK_NOT_FOUND);

		BookEntity bookEntity = bookOptional.get();
		reviewEntity.setBook(bookEntity);

		log.info("Termina proceso de creación del review");
		return reviewRepository.save(reviewEntity);
	}

	/**
	 * Obtiene la lista de los registros de Review que pertenecen a un Book.
	 *
	 * @param bookId id del Book el cual es padre de los Reviews.
	 * @return Colección de objetos de ReviewEntity.
	 */

	@Transactional
	public List<ReviewEntity> getReviews(Long bookId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar los reviews asociados al book con id = {0}", bookId);
		Optional<BookEntity> bookOptional = bookRepository.findById(bookId);
		if (bookOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.BOOK_NOT_FOUND);

		log.info("Termina proceso de consultar los reviews asociados al book con id = {0}", bookId);

		BookEntity bookEntity = bookOptional.get();
		return bookEntity.getReviews();
	}

	/**
	 * Obtiene los datos de una instancia de Review a partir de su ID. La existencia
	 * del elemento padre Book se debe garantizar.
	 *
	 * @param bookId   El id del Libro buscado
	 * @param reviewId Identificador de la Reseña a consultar
	 * @return Instancia de ReviewEntity con los datos del Review consultado.
	 *
	 */
	@Transactional
	public ReviewEntity getReview(Long bookId, Long reviewId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar el review con id = {0} del libro con id = " + bookId,
				reviewId);
		Optional<BookEntity> bookOptional = bookRepository.findById(bookId);
		if (bookOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.BOOK_NOT_FOUND);

		Optional<ReviewEntity> reviewOptional = reviewRepository.findById(reviewId);
		if (reviewOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.REVIEW_NOT_FOUND);

		log.info("Termina proceso de consultar el review con id = {0} del libro con id = " + bookId,
				reviewId);
		return reviewRepository.findByBookIdAndId(bookId, reviewId);
	}

	/**
	 * Actualiza la información de una instancia de Review.
	 *
	 * @param reviewEntity Instancia de ReviewEntity con los nuevos datos.
	 * @param bookId       id del Book el cual sera padre del Review actualizado.
	 * @param reviewId     id de la review que será actualizada.
	 * @return Instancia de ReviewEntity con los datos actualizados.
	 *
	 */
	@Transactional
	public ReviewEntity updateReview(Long bookId, Long reviewId, ReviewEntity review) throws EntityNotFoundException {
		log.info("Inicia proceso de actualizar el review con id = {0} del libro con id = " + bookId,
				reviewId);
		Optional<BookEntity> bookOptional = bookRepository.findById(bookId);
		if (bookOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.BOOK_NOT_FOUND);

		Optional<ReviewEntity> reviewOptional = reviewRepository.findById(reviewId);
		if (reviewOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.REVIEW_NOT_FOUND);

		BookEntity bookEntity = bookOptional.get();
		review.setId(reviewId);
		review.setBook(bookEntity);
		log.info("Termina proceso de actualizar el review con id = {0} del libro con id = " + bookId,
				reviewId);
		return reviewRepository.save(review);
	}

	/**
	 * Elimina una instancia de Review de la base de datos.
	 *
	 * @param reviewId Identificador de la instancia a eliminar.
	 * @param bookId   id del Book el cual es padre del Review.
	 * @throws EntityNotFoundException   Si la reseña no esta asociada al libro.
	 * @throws IllegalOperationException
	 *
	 */
	@Transactional
	public void deleteReview(Long bookId, Long reviewId) throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de borrar el review con id = {0} del libro con id = " + bookId,
				reviewId);
		Optional<BookEntity> bookOptional = bookRepository.findById(bookId);
		if (bookOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.BOOK_NOT_FOUND);

		Optional<ReviewEntity> reviewOptional = reviewRepository.findById(reviewId);
		if (reviewOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.REVIEW_NOT_FOUND);

		ReviewEntity reviewEntity = reviewOptional.get();

		if (!reviewEntity.getBook().getId().equals(bookId))
			throw new IllegalOperationException(ErrorMessage.REVIEW_NOT_ASSOCIATED_TO_BOOK);

		reviewRepository.deleteById(reviewId);
		log.info("Termina proceso de borrar el review con id = {0} del libro con id = " + bookId,
				reviewId);
	}
}
