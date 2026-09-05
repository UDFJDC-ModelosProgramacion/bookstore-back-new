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
 * Class implementing persistence layer logic for the Review entity.
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
	 * Creates a Review in the database.
	 *
	 * @param reviewEntity ReviewEntity object with new data
	 * @param bookId       ID of the Book that will be the parent of the new Review.
	 * @return ReviewEntity object with the new data and its assigned ID.
	 * @throws EntityNotFoundException if the book does not exist.
	 *
	 */
	@Transactional
	public ReviewEntity createReview(Long bookId, ReviewEntity reviewEntity) throws EntityNotFoundException {
		log.info("Starting process to create review");
		Optional<BookEntity> bookOptional = bookRepository.findById(bookId);
		if (bookOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.BOOK_NOT_FOUND);

		BookEntity bookEntity = bookOptional.get();
		reviewEntity.setBook(bookEntity);

		log.info("Finished process to create review");
		return reviewRepository.save(reviewEntity);
	}

	/**
	 * Retrieves the list of Review records that belong to a Book.
	 *
	 * @param bookId ID of the Book that is parent to the Reviews.
	 * @return Collection of ReviewEntity objects.
	 */

	@Transactional
	public List<ReviewEntity> getReviews(Long bookId) throws EntityNotFoundException {
		log.info("Starting process to fetch reviews associated with book with id = {0}", bookId);
		Optional<BookEntity> bookOptional = bookRepository.findById(bookId);
		if (bookOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.BOOK_NOT_FOUND);

		log.info("Finished process to fetch reviews associated with book with id = {0}", bookId);

		BookEntity bookEntity = bookOptional.get();
		return bookEntity.getReviews();
	}

	/**
	 * Retrieves data for a Review instance by its ID. The existence of the parent
	 * Book element must be guaranteed.
	 *
	 * @param bookId   The ID of the requested Book
	 * @param reviewId Identifier of the Review to fetch
	 * @return ReviewEntity instance containing the data of the requested Review.
	 *
	 */
	@Transactional
	public ReviewEntity getReview(Long bookId, Long reviewId) throws EntityNotFoundException {
		log.info("Starting process to fetch review with id = {0} of book with id = " + bookId,
				reviewId);
		Optional<BookEntity> bookOptional = bookRepository.findById(bookId);
		if (bookOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.BOOK_NOT_FOUND);

		Optional<ReviewEntity> reviewOptional = reviewRepository.findById(reviewId);
		if (reviewOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.REVIEW_NOT_FOUND);

		log.info("Finished process to fetch review with id = {0} of book with id = " + bookId,
				reviewId);
		return reviewRepository.findByBookIdAndId(bookId, reviewId);
	}

	/**
	 * Updates the information of a Review instance.
	 *
	 * @param reviewEntity ReviewEntity instance with new data.
	 * @param bookId       ID of the Book that will be the parent of the updated
	 *                     Review.
	 * @param reviewId     ID of the review to be updated.
	 * @return ReviewEntity instance with updated data.
	 *
	 */
	@Transactional
	public ReviewEntity updateReview(Long bookId, Long reviewId, ReviewEntity review) throws EntityNotFoundException {
		log.info("Starting process to update review with id = {0} of book with id = " + bookId,
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
		log.info("Finished process to update review with id = {0} of book with id = " + bookId,
				reviewId);
		return reviewRepository.save(review);
	}

	/**
	 * Deletes a Review instance from the database.
	 *
	 * @param reviewId Identifier of the instance to delete.
	 * @param bookId   ID of the Book that is parent to the Review.
	 * @throws EntityNotFoundException   If the review is not associated with the
	 *                                   book.
	 * @throws IllegalOperationException
	 *
	 */
	@Transactional
	public void deleteReview(Long bookId, Long reviewId) throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to delete review with id = {0} of book with id = " + bookId,
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
		log.info("Finished process to delete review with id = {0} of book with id = " + bookId,
				reviewId);
	}
}