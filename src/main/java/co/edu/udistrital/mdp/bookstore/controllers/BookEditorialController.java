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
package co.edu.udistrital.mdp.bookstore.controllers;

import org.modelmapper.ModelMapper;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.mdp.bookstore.dto.BookDetailDTO;
import co.edu.udistrital.mdp.bookstore.dto.EditorialDTO;
import co.edu.udistrital.mdp.bookstore.entities.BookEntity;
import co.edu.udistrital.mdp.bookstore.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.bookstore.services.BookEditorialService;
import lombok.RequiredArgsConstructor;

/**
 * Clase que implementa el recurso "books/{id}/editorial".
 *
 * @author ISIS2603
 * @version 1.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookEditorialController {

	private final BookEditorialService bookEditorialService;

	private final ModelMapper modelMapper;

	/**
	 * Remplaza la instancia de Editorial asociada a un Book.
	 *
	 * @param bookId    Identificador del libro que se esta actualizando. Este debe
	 *                  ser una cadena de dígitos.
	 * @param editorial La editorial que se será del libro.
	 * @return JSON {@link BookDetailDTO} - El arreglo de libros guardado en la
	 *         editorial.
	 */
	@PutMapping(value = "/{bookId}/editorial")
	@ResponseStatus(code = HttpStatus.OK)
	public BookDetailDTO replaceEditorial(@PathVariable Long bookId, @RequestBody EditorialDTO editorialDTO)
			throws EntityNotFoundException {
		BookEntity bookEntity = bookEditorialService.replaceEditorial(bookId, editorialDTO.getId());
		return modelMapper.map(bookEntity, BookDetailDTO.class);
	}

}
