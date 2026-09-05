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
import co.edu.udistrital.mdp.bookstore.services.EditorialBookService;
import lombok.RequiredArgsConstructor;

/**
 * Class implementing the "editorials/{id}/books" resource.
 *
 * @author Jose Bocanegra
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/editorials")
public class EditorialBookController {

	private final EditorialBookService editorialBookService;

	private final ModelMapper modelMapper;

	/**
	 * Saves a book under an editorial using the information received in the URL.
	 * Returns the saved book in the editorial.
	 *
	 * @param editorialId Identifier of the editorial being updated. This must
	 *                    be a string of digits.
	 * @param bookId      Identifier of the book to save. This must be a
	 *                    string of digits.
	 * @return JSON {@link BookDTO} - The book saved in the editorial.
	 */
	@PostMapping(value = "/{editorialId}/books/{bookId}")
	@ResponseStatus(code = HttpStatus.OK)
	public BookDTO addBook(@PathVariable Long editorialId, @PathVariable("bookId") Long booklId)
			throws EntityNotFoundException {
		BookEntity bookEntity = editorialBookService.addBook(booklId, editorialId);
		return modelMapper.map(bookEntity, BookDTO.class);
	}

	/**
	 * Searches for and returns all books existing in the editorial.
	 *
	 * @param editorialId Identifier of the editorial being searched. This must
	 *                    be a string of digits.
	 * @return JSONArray {@link BookDetailDTO} - The books found in the editorial.
	 *         If none exist, returns an empty list.
	 */
	@GetMapping(value = "/{editorialId}/books")
	@ResponseStatus(code = HttpStatus.OK)
	public List<BookDetailDTO> getBooks(@PathVariable Long editorialId) throws EntityNotFoundException {
		List<BookEntity> bookList = editorialBookService.getBooks(editorialId);
		return modelMapper.map(bookList, new TypeToken<List<BookDetailDTO>>() {
		}.getType());
	}

	/**
	 * Searches for the book with the associated ID within the editorial with the
	 * associated ID.
	 *
	 * @param editorialId Identifier of the editorial being searched. This must
	 *                    be a string of digits.
	 * @param bookId      Identifier of the book being searched. This must be a
	 *                    string of digits.
	 * @return JSON {@link BookDetailDTO} - The requested book
	 */
	@GetMapping(value = "/{editorialId}/books/{bookId}")
	@ResponseStatus(code = HttpStatus.OK)
	public BookDetailDTO getBook(@PathVariable Long editorialId, @PathVariable Long bookId)
			throws EntityNotFoundException, IllegalOperationException {
		BookEntity bookEntity = editorialBookService.getBook(editorialId, bookId);
		return modelMapper.map(bookEntity, BookDetailDTO.class);
	}

	/**
	 * Replaces the Book instances associated with an Editorial instance.
	 *
	 * @param editorialId Identifier of the editorial being replaced. This must
	 *                    be a string of digits.
	 * @param books       JSONArray {@link BookDetailDTO} - The new list of books
	 *                    for the editorial.
	 * @return JSON {@link BookDetailDTO} - The array of books saved in the
	 *         editorial.
	 */
	@PutMapping(value = "/{editorialId}/books")
	@ResponseStatus(code = HttpStatus.OK)
	public List<BookDetailDTO> replaceBooks(@PathVariable("editorialId") Long editorialsId,
			@RequestBody List<BookDetailDTO> books) throws EntityNotFoundException {
		List<BookEntity> booksList = modelMapper.map(books, new TypeToken<List<BookEntity>>() {
		}.getType());
		List<BookEntity> result = editorialBookService.replaceBooks(editorialsId, booksList);
		return modelMapper.map(result, new TypeToken<List<BookDetailDTO>>() {
		}.getType());
	}
}