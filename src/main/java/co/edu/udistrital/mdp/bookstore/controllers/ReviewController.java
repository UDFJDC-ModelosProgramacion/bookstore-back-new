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
package co.edu.udistrital.mdp.bookstore.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.mdp.bookstore.dto.ReviewDTO;
import co.edu.udistrital.mdp.bookstore.entities.ReviewEntity;
import co.edu.udistrital.mdp.bookstore.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.bookstore.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.bookstore.services.ReviewService;
import lombok.RequiredArgsConstructor;

/**
 * Class implementing the "reviews" resource.
 *
 * @author Jose Bocanegra
 * @version 1.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class ReviewController {

	private final ReviewService reviewService;

	private final ModelMapper modelMapper;

	/**
	 * Creates a new review with the information received in the request body
	 * and returns an identical object with an auto-generated ID from the database.
	 *
	 * @param bookId    The ID of the book to which the review is added.
	 * @param reviewDTO {@link ReviewDTO} - The review to be saved.
	 * @return JSON {@link ReviewDTO} - The saved review with the auto-generated ID.
	 */
	@PostMapping(value = "/{bookId}/reviews")
	@ResponseStatus(code = HttpStatus.CREATED)
	public ReviewDTO createReview(@PathVariable Long bookId, @RequestBody ReviewDTO reviewDTO)
			throws EntityNotFoundException {
		ReviewEntity reviewEntity = modelMapper.map(reviewDTO, ReviewEntity.class);
		ReviewEntity newReview = reviewService.createReview(bookId, reviewEntity);
		return modelMapper.map(newReview, ReviewDTO.class);
	}

	/**
	 * Searches for and returns all reviews associated with a book.
	 *
	 * @param bookId The ID of the book for which reviews are searched.
	 * @return JSONArray {@link ReviewDTO} - The reviews found for the book. If none
	 *         exist,
	 *         returns an empty list.
	 */
	@GetMapping(value = "/{bookId}/reviews")
	@ResponseStatus(code = HttpStatus.OK)
	public List<ReviewDTO> getReviews(@PathVariable Long bookId) throws EntityNotFoundException {
		List<ReviewEntity> reviews = reviewService.getReviews(bookId);
		return modelMapper.map(reviews, new TypeToken<List<ReviewDTO>>() {
		}.getType());
	}

	/**
	 * Searches for and returns the review with the ID received in the URL, relative
	 * to a book.
	 *
	 * @param bookId   The ID of the book for which the review is searched.
	 * @param reviewId The ID of the review being searched.
	 * @return JSON {@link ReviewDTO} - The review found for the book.
	 */
	@GetMapping(value = "/{bookId}/reviews/{reviewId}")
	@ResponseStatus(code = HttpStatus.OK)
	public ReviewDTO getReview(@PathVariable Long bookId, @PathVariable Long reviewId)
			throws EntityNotFoundException {
		ReviewEntity entity = reviewService.getReview(bookId, reviewId);
		return modelMapper.map(entity, ReviewDTO.class);
	}

	/**
	 * Updates a review with the information received in the request body
	 * and returns the updated object.
	 *
	 * @param bookId    The ID of the book for which the review is saved.
	 * @param reviewId  The ID of the review to be updated.
	 * @param reviewDTO {@link ReviewDTO} - The review to be saved.
	 * @return JSON {@link ReviewDTO} - The updated review.
	 */
	@PutMapping(value = "/{bookId}/reviews/{reviewsId}")
	@ResponseStatus(code = HttpStatus.OK)
	public ReviewDTO updateReview(@PathVariable Long bookId, @PathVariable("reviewsId") Long reviewId,
			@RequestBody ReviewDTO reviewDTO) throws EntityNotFoundException {
		ReviewEntity reviewEntity = modelMapper.map(reviewDTO, ReviewEntity.class);
		ReviewEntity newEntity = reviewService.updateReview(bookId, reviewId, reviewEntity);
		return modelMapper.map(newEntity, ReviewDTO.class);
	}

	/**
	 * Deletes the review with the associated ID received in the URL.
	 *
	 * @param bookId   The ID of the book from which the review will be removed.
	 * @param reviewId The ID of the review to be removed.
	 */
	@DeleteMapping(value = "/{bookId}/reviews/{reviewId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void deleteReview(@PathVariable Long bookId, @PathVariable Long reviewId)
			throws EntityNotFoundException, IllegalOperationException {
		reviewService.deleteReview(bookId, reviewId);
	}

}