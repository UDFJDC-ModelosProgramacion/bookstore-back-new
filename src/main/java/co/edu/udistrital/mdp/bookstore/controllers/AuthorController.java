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
import co.edu.udistrital.mdp.bookstore.services.AuthorService;
import lombok.RequiredArgsConstructor;

/**
 * Class implementing the "authors" resource.
 *
 * @author Jose Bocanegra
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/authors")
public class AuthorController {

	private final AuthorService authorService;

	private final ModelMapper modelMapper;

	/**
	 * Searches for and returns all authors existing in the application.
	 *
	 * @return JSONArray {@link AuthorDetailDTO} - The authors found in the
	 *         application. If none exist, returns an empty list.
	 */
	@GetMapping
	@ResponseStatus(code = HttpStatus.OK)
	public List<AuthorDetailDTO> findAll() {
		List<AuthorEntity> authors = authorService.getAuthors();
		return modelMapper.map(authors, new TypeToken<List<AuthorDetailDTO>>() {
		}.getType());
	}

	/**
	 * Searches for the author with the associated ID received in the URL and
	 * returns it.
	 *
	 * @param id Identifier of the author being searched. This must be a
	 *           string of digits.
	 * @return JSON {@link AuthorDetailDTO} - The requested author
	 */
	@GetMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public AuthorDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
		AuthorEntity authorEntity = authorService.getAuthor(id);
		return modelMapper.map(authorEntity, AuthorDetailDTO.class);
	}

	/**
	 * Creates a new author with the information received in the request body
	 * and returns an identical object with an auto-generated ID from the database.
	 *
	 * @param authorDTO {@link AuthorDTO} - The author to be saved.
	 * @return JSON {@link AuthorDTO} - The saved author with the auto-generated
	 *         ID attribute.
	 * @throws IllegalOperationException
	 */
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public AuthorDTO create(@RequestBody AuthorDTO authorDTO) throws IllegalOperationException {
		AuthorEntity authorEntity = authorService.createAuthor(modelMapper.map(authorDTO, AuthorEntity.class));
		return modelMapper.map(authorEntity, AuthorDTO.class);
	}

	/**
	 * Updates the author with the ID received in the URL using the information
	 * received in the request body.
	 *
	 * @param id        Identifier of the author to update. This must be a
	 *                  string of digits.
	 * @param authorDTO {@link AuthorDTO} The author to be saved.
	 */
	@PutMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public AuthorDTO update(@PathVariable Long id, @RequestBody AuthorDTO authorDTO)
			throws EntityNotFoundException {
		AuthorEntity authorEntity = authorService.updateAuthor(id, modelMapper.map(authorDTO, AuthorEntity.class));
		return modelMapper.map(authorEntity, AuthorDTO.class);
	}

	/**
	 * Deletes the author with the associated ID received in the URL.
	 *
	 * @param id Identifier of the author to delete. This must be a
	 *           string of digits.
	 */
	@DeleteMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) throws EntityNotFoundException, IllegalOperationException {
		authorService.deleteAuthor(id);
	}

}