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
import co.edu.udistrital.mdp.bookstore.services.BookService;
import lombok.RequiredArgsConstructor;

/**
 * Class implementing the "books" resource.
 *
 * @author Jose Bocanegra
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookController {

	private final BookService bookService;

	private final ModelMapper modelMapper;

	/**
	 * Searches for and returns all books existing in the application.
	 *
	 * @return JSONArray {@link BookDetailDTO} - The books found in the
	 *         application. If none exist, returns an empty list.
	 */
	@GetMapping
	@ResponseStatus(code = HttpStatus.OK)
	public List<BookDetailDTO> findAll() {
		List<BookEntity> books = bookService.getBooks();
		return modelMapper.map(books, new TypeToken<List<BookDetailDTO>>() {
		}.getType());
	}

	/**
	 * Searches for the book with the associated ID received in the URL and returns
	 * it.
	 *
	 * @param id Identifier of the book being searched. This must be a
	 *           string of digits.
	 * @return JSON {@link BookDetailDTO} - The requested book
	 */
	@GetMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public BookDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
		BookEntity bookEntity = bookService.getBook(id);
		return modelMapper.map(bookEntity, BookDetailDTO.class);
	}

	/**
	 * Creates a new book with the information received in the request body
	 * and returns an identical object with an auto-generated ID from the database.
	 *
	 * @param bookDTO {@link BookDTO} - The book to be saved.
	 * @return JSON {@link BookDTO} - The saved book with the auto-generated
	 *         ID attribute.
	 */
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public BookDTO create(@RequestBody BookDTO bookDTO) throws IllegalOperationException, EntityNotFoundException {
		BookEntity bookEntity = bookService.createBook(modelMapper.map(bookDTO, BookEntity.class));
		return modelMapper.map(bookEntity, BookDTO.class);
	}

	/**
	 * Updates the book with the ID received in the URL using the information
	 * received in the request body.
	 *
	 * @param id      Identifier of the book to update. This must be
	 *                a string of digits.
	 * @param bookDTO {@link BookDTO} The book to be saved.
	 * @return JSON {@link BookDTO} - The saved book.
	 */
	@PutMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public BookDTO update(@PathVariable Long id, @RequestBody BookDTO bookDTO)
			throws EntityNotFoundException, IllegalOperationException {
		BookEntity bookEntity = bookService.updateBook(id, modelMapper.map(bookDTO, BookEntity.class));
		return modelMapper.map(bookEntity, BookDTO.class);
	}

	/**
	 * Deletes the book with the associated ID received in the URL.
	 *
	 * @param id Identifier of the book to delete. This must be a
	 *           string of digits.
	 */
	@DeleteMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) throws EntityNotFoundException, IllegalOperationException {
		bookService.deleteBook(id);
	}
}