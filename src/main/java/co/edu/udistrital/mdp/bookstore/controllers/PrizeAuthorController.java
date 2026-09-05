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

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.mdp.bookstore.dto.AuthorDTO;
import co.edu.udistrital.mdp.bookstore.dto.AuthorDetailDTO;
import co.edu.udistrital.mdp.bookstore.entities.AuthorEntity;
import co.edu.udistrital.mdp.bookstore.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.bookstore.services.PrizeAuthorService;
import lombok.RequiredArgsConstructor;

/**
 * Class implementing the "prizes/{id}/author" resource.
 *
 * @author Jose Bocanegra
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/prizes")
public class PrizeAuthorController {

	private final PrizeAuthorService prizeAuthorService;

	private final ModelMapper modelMapper;

	/**
	 * Saves an author under a prize using the information received in the URL.
	 *
	 * @param prizeId  Identifier of the prize being updated. This must
	 *                 be a string of digits.
	 * @param authorId Identifier of the author to save. This must be a
	 *                 string of digits.
	 * @return JSON {@link AuthorDTO} - The saved author associated with the prize.
	 */
	@PostMapping(value = "/{prizeId}/author/{authorId}")
	@ResponseStatus(code = HttpStatus.OK)
	public AuthorDTO addAuthor(@PathVariable Long prizeId, @PathVariable Long authorId)
			throws EntityNotFoundException {
		AuthorEntity authorEntity = prizeAuthorService.addAuthor(authorId, prizeId);
		return modelMapper.map(authorEntity, AuthorDTO.class);
	}

	/**
	 * Searches for the author associated with the specified prize ID.
	 *
	 * @param prizeId Identifier of the prize being searched. This must be a
	 *                string of digits.
	 * @return JSON {@link AuthorDetailDTO} - The requested author.
	 */
	@GetMapping(value = "/{prizeId}/author")
	@ResponseStatus(code = HttpStatus.OK)
	public AuthorDetailDTO getAuthor(@PathVariable Long prizeId) throws EntityNotFoundException {
		AuthorEntity authorEntity = prizeAuthorService.getAuthor(prizeId);
		return modelMapper.map(authorEntity, AuthorDetailDTO.class);
	}

	/**
	 * Replaces the Author instance associated with a Prize instance.
	 *
	 * @param prizeId  Identifier of the prize being updated. This must
	 *                 be a string of digits.
	 * @param authorId Identifier of the replacing author. This must be a
	 *                 string of digits.
	 * @return JSON {@link AuthorDetailDTO} - The updated author.
	 */
	@PutMapping(value = "/{prizeId}/author/{authorId}")
	@ResponseStatus(code = HttpStatus.OK)
	public AuthorDetailDTO replaceAuthor(@PathVariable Long prizeId, @PathVariable Long authorId)
			throws EntityNotFoundException {
		AuthorEntity authorEntity = prizeAuthorService.replaceAuthor(prizeId, authorId);
		return modelMapper.map(authorEntity, AuthorDetailDTO.class);
	}

	/**
	 * Removes the association between the author and the prize received in the URL.
	 *
	 * @param prizeId ID of the prize from which to disassociate the author.
	 */
	@DeleteMapping(value = "/{prizeId}/author")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void removeAuthor(@PathVariable Long prizeId) throws EntityNotFoundException {
		prizeAuthorService.removeAuthor(prizeId);
	}

}