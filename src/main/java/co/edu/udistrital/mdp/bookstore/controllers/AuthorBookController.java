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

import co.edu.udistrital.mdp.bookstore.dto.BookDTO;
import co.edu.udistrital.mdp.bookstore.dto.BookDetailDTO;
import co.edu.udistrital.mdp.bookstore.entities.BookEntity;
import co.edu.udistrital.mdp.bookstore.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.bookstore.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.bookstore.services.AuthorBookService;
import lombok.RequiredArgsConstructor;

/**
 * Class implementing the "authors/{id}/books" resource.
 *
 * @author Jose Bocanegra
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/authors")
public class AuthorBookController {

	private final AuthorBookService authorBookService;

	private final ModelMapper modelMapper;

	/**
	 * Searches for and returns the book with the ID received in the URL, relative
	 * to an author.
	 *
	 * @param authorId The ID of the author whose book is being searched
	 * @param bookId   The ID of the book being searched
	 * @return {@link BookDetailDTO} - The book found for the author.
	 */
	@GetMapping(value = "/{authorId}/books/{bookId}")
	@ResponseStatus(code = HttpStatus.OK)
	public BookDetailDTO getBook(@PathVariable Long authorId, @PathVariable Long bookId)
			throws EntityNotFoundException, IllegalOperationException {
		BookEntity bookEntity = authorBookService.getBook(authorId, bookId);
		return modelMapper.map(bookEntity, BookDetailDTO.class);
	}

	/**
	 * Searches for and returns all books belonging to an author.
	 *
	 * @param authorId The ID of the author whose books are being searched
	 * @return JSONArray {@link BookDetailDTO} - The books found for the author.
	 *         If none are found, returns an empty list.
	 */
	@GetMapping(value = "/{authorId}/books")
	@ResponseStatus(code = HttpStatus.OK)
	public List<BookDetailDTO> getBooks(@PathVariable Long authorId) throws EntityNotFoundException {
		List<BookEntity> bookEntity = authorBookService.getBooks(authorId);
		return modelMapper.map(bookEntity, new TypeToken<List<BookDetailDTO>>() {
		}.getType());
	}

	/**
	 * Associates an existing book with an existing author
	 *
	 * @param authorId The ID of the author to associate with the book
	 * @param bookId   The ID of the book being associated
	 * @return JSON {@link BookDetailDTO} - The associated book.
	 */
	@PostMapping(value = "/{authorId}/books/{bookId}")
	@ResponseStatus(code = HttpStatus.OK)
	public BookDetailDTO addBook(@PathVariable Long authorId, @PathVariable Long bookId)
			throws EntityNotFoundException {
		BookEntity bookEntity = authorBookService.addBook(authorId, bookId);
		return modelMapper.map(bookEntity, BookDetailDTO.class);
	}

	/**
	 * Updates an author's list of books with the list received in the request body
	 *
	 * @param authorId The ID of the author to associate with the books
	 * @param books    JSONArray {@link BookDTO} - The list of books to save.
	 * @return JSONArray {@link BookDetailDTO} - The updated list.
	 */
	@PutMapping(value = "/{authorId}/books")
	@ResponseStatus(code = HttpStatus.OK)
	public List<BookDetailDTO> replaceBooks(@PathVariable Long authorId, @RequestBody List<BookDTO> books)
			throws EntityNotFoundException {
		List<BookEntity> entities = modelMapper.map(books, new TypeToken<List<BookEntity>>() {
		}.getType());
		List<BookEntity> booksList = authorBookService.addBooks(authorId, entities);
		return modelMapper.map(booksList, new TypeToken<List<BookDetailDTO>>() {
		}.getType());

	}

	/**
	 * Removes the association between the book and author received in the URL.
	 *
	 * @param authorId The ID of the author to disassociate from the book
	 * @param bookId   The ID of the book being disassociated
	 */
	@DeleteMapping(value = "/{authorId}/books/{bookId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void removeBook(@PathVariable Long authorId, @PathVariable Long bookId)
			throws EntityNotFoundException {
		authorBookService.removeBook(authorId, bookId);
	}
}