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

import co.edu.udistrital.mdp.bookstore.dto.EditorialDTO;
import co.edu.udistrital.mdp.bookstore.dto.EditorialDetailDTO;
import co.edu.udistrital.mdp.bookstore.entities.EditorialEntity;
import co.edu.udistrital.mdp.bookstore.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.bookstore.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.bookstore.services.EditorialService;
import lombok.RequiredArgsConstructor;

/**
 * Class implementing the "editorials" resource.
 *
 * @author Jose Bocanegra
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/editorials")
public class EditorialController {

	private final EditorialService editorialService;

	private final ModelMapper modelMapper;

	/**
	 * Searches for the editorial with the associated ID received in the URL and
	 * returns it.
	 *
	 * @param id Identifier of the editorial being searched.
	 *           This must be a string of digits.
	 * @return JSON {@link EditorialDetailDTO} - The requested editorial
	 */
	@GetMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public EditorialDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
		EditorialEntity editorialEntity = editorialService.getEditorial(id);
		return modelMapper.map(editorialEntity, EditorialDetailDTO.class);
	}

	/**
	 * Searches for and returns all editorials existing in the application.
	 *
	 * @return JSONArray {@link EditorialDetailDTO} - The editorials
	 *         found in the application. If none exist, returns an empty list.
	 */
	@GetMapping
	@ResponseStatus(code = HttpStatus.OK)
	public List<EditorialDetailDTO> findAll() {
		List<EditorialEntity> editorials = editorialService.getEditorials();
		return modelMapper.map(editorials, new TypeToken<List<EditorialDetailDTO>>() {
		}.getType());
	}

	/**
	 * Creates a new editorial with the information received in the request body
	 * and returns an identical object with an auto-generated ID from the database.
	 *
	 * @param editorialDTO {@link EditorialDTO} - The editorial to be saved.
	 * @return JSON {@link EditorialDTO} - The saved editorial with the
	 *         auto-generated
	 *         ID attribute.
	 */
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public EditorialDTO create(@RequestBody EditorialDTO editorialDTO) throws IllegalOperationException {
		EditorialEntity editorialEntity = editorialService
				.createEditorial(modelMapper.map(editorialDTO, EditorialEntity.class));
		return modelMapper.map(editorialEntity, EditorialDTO.class);
	}

	/**
	 * Updates the editorial with the ID received in the URL using the information
	 * received in the request body.
	 *
	 * @param id           Identifier of the editorial to update.
	 *                     This must be a string of digits.
	 * @param editorialDTO {@link EditorialDTO} The editorial to be saved.
	 * @return JSON {@link EditorialDTO} - The saved editorial.
	 */

	@PutMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public EditorialDTO update(@PathVariable Long id, @RequestBody EditorialDTO editorialDTO)
			throws EntityNotFoundException {
		EditorialEntity editorialEntity = editorialService.updateEditorial(id,
				modelMapper.map(editorialDTO, EditorialEntity.class));
		return modelMapper.map(editorialEntity, EditorialDTO.class);
	}

	/**
	 * Deletes the editorial with the associated ID received in the URL.
	 *
	 * @param id Identifier of the editorial to delete.
	 *           This must be a string of digits.
	 */
	@DeleteMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) throws EntityNotFoundException, IllegalOperationException {
		editorialService.deleteEditorial(id);
	}
}