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

import co.edu.udistrital.mdp.bookstore.dto.AuthorDTO;
import co.edu.udistrital.mdp.bookstore.dto.AuthorDetailDTO;
import co.edu.udistrital.mdp.bookstore.entities.AuthorEntity;
import co.edu.udistrital.mdp.bookstore.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.bookstore.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.bookstore.services.BookAuthorService;
import lombok.RequiredArgsConstructor;

/**
 * Class implementing the "books/{id}/authors" resource.
 *
 * @author Jose Bocanegra
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookAuthorController {

	private final BookAuthorService bookAuthorService;

	private final ModelMapper modelMapper;

	/**
	 * Associates an existing author with an existing book.
	 *
	 * @param authorId The ID of the author to associate
	 * @param bookId   The ID of the book to associate the author with
	 * @return JSON {@link AuthorDetailDTO} - The associated author.
	 */
	@PostMapping(value = "/{bookId}/authors/{authorId}")
	@ResponseStatus(code = HttpStatus.OK)
	public AuthorDetailDTO addAuthor(@PathVariable Long authorId, @PathVariable Long bookId)
			throws EntityNotFoundException {
		AuthorEntity authorEntity = bookAuthorService.addAuthor(bookId, authorId);
		return modelMapper.map(authorEntity, AuthorDetailDTO.class);
	}

	/**
	 * Searches for and returns the author with the ID received in the URL, relative
	 * to a book.
	 *
	 * @param authorId The ID of the author being searched
	 * @param bookId   The ID of the book whose author is being searched
	 * @return {@link AuthorDetailDTO} - The author found for the book.
	 */
	@GetMapping(value = "/{bookId}/authors/{authorId}")
	@ResponseStatus(code = HttpStatus.OK)
	public AuthorDetailDTO getAuthor(@PathVariable Long authorId, @PathVariable Long bookId)
			throws EntityNotFoundException, IllegalOperationException {
		AuthorEntity authorEntity = bookAuthorService.getAuthor(bookId, authorId);
		return modelMapper.map(authorEntity, AuthorDetailDTO.class);
	}

	/**
	 * Updates the list of authors for a book with the list received in the request
	 * body.
	 *
	 * @param bookId  The ID of the book to associate with the list of authors
	 * @param authors JSONArray {@link AuthorDTO} - The list of authors to save.
	 * @return JSONArray {@link AuthorDetailDTO} - The updated list.
	 */
	@PutMapping(value = "/{bookId}/authors")
	@ResponseStatus(code = HttpStatus.OK)
	public List<AuthorDetailDTO> addAuthors(@PathVariable Long bookId, @RequestBody List<AuthorDTO> authors)
			throws EntityNotFoundException {
		List<AuthorEntity> entities = modelMapper.map(authors, new TypeToken<List<AuthorEntity>>() {
		}.getType());
		List<AuthorEntity> authorsList = bookAuthorService.replaceAuthors(bookId, entities);
		return modelMapper.map(authorsList, new TypeToken<List<AuthorDetailDTO>>() {
		}.getType());
	}

	/**
	 * Searches for and returns all authors associated with a book.
	 *
	 * @param bookId The ID of the book whose authors are being searched
	 * @return JSONArray {@link AuthorDetailDTO} - The authors found for the book.
	 *         If none exist, returns an empty list.
	 */
	@GetMapping(value = "/{bookId}/authors")
	@ResponseStatus(code = HttpStatus.OK)
	public List<AuthorDetailDTO> getAuthors(@PathVariable Long bookId) throws EntityNotFoundException {
		List<AuthorEntity> authorEntity = bookAuthorService.getAuthors(bookId);
		return modelMapper.map(authorEntity, new TypeToken<List<AuthorDetailDTO>>() {
		}.getType());
	}

	/**
	 * Removes the association between the author and book received in the URL.
	 *
	 * @param bookId   The ID of the book to disassociate the author from
	 * @param authorId The ID of the author being disassociated
	 */
	@DeleteMapping(value = "/{bookId}/authors/{authorId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void removeAuthor(@PathVariable Long authorId, @PathVariable Long bookId)
			throws EntityNotFoundException {
		bookAuthorService.removeAuthor(bookId, authorId);
	}
}