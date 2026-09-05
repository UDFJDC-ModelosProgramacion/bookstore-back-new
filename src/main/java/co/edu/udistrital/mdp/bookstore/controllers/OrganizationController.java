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

import co.edu.udistrital.mdp.bookstore.dto.OrganizationDTO;
import co.edu.udistrital.mdp.bookstore.entities.OrganizationEntity;
import co.edu.udistrital.mdp.bookstore.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.bookstore.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.bookstore.services.OrganizationService;
import lombok.RequiredArgsConstructor;

/**
 * Class implementing the "organizations" resource.
 *
 * @author Jose Bocanegra
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/organizations")
public class OrganizationController {

	private final OrganizationService organizationService;

	private final ModelMapper modelMapper;

	/**
	 * Searches for and returns all organizations existing in the application.
	 *
	 * @return JSONArray {@link OrganizationDTO} - The organizations found in the
	 *         application. If none exist, returns an empty list.
	 */
	@GetMapping
	@ResponseStatus(code = HttpStatus.OK)
	public List<OrganizationDTO> findAll() {
		List<OrganizationEntity> organizations = organizationService.getOrganizations();
		return modelMapper.map(organizations, new TypeToken<List<OrganizationDTO>>() {
		}.getType());
	}

	/**
	 * Searches for the organization with the associated ID received in the URL and
	 * returns it.
	 *
	 * @param id Identifier of the organization being searched. This must be a
	 *           string of digits.
	 * @return JSON {@link OrganizationDTO} - The requested organization
	 */
	@GetMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public OrganizationDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
		OrganizationEntity organizationEntity = organizationService.getOrganization(id);
		return modelMapper.map(organizationEntity, OrganizationDTO.class);
	}

	/**
	 * Creates a new organization with the information received in the request body
	 * and returns an identical object with an auto-generated ID from the database.
	 *
	 * @param organizationDTO {@link OrganizationDTO} - The organization to be
	 *                        saved.
	 * @return JSON {@link OrganizationDTO} - The saved organization with the
	 *         auto-generated
	 *         ID attribute.
	 */
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public OrganizationDTO create(@RequestBody OrganizationDTO organizationDTO) throws IllegalOperationException {
		OrganizationEntity organizationEntity = organizationService
				.createOrganization(modelMapper.map(organizationDTO, OrganizationEntity.class));
		return modelMapper.map(organizationEntity, OrganizationDTO.class);
	}

	/**
	 * Updates the organization with the ID received in the URL using the
	 * information
	 * received in the request body.
	 *
	 * @param id              Identifier of the organization to update. This must
	 *                        be a string of digits.
	 * @param organizationDTO {@link OrganizationDTO} The organization to be saved.
	 * @return JSON {@link OrganizationDTO} - The saved organization.
	 */
	@PutMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public OrganizationDTO update(@PathVariable Long id, @RequestBody OrganizationDTO organizationDTO)
			throws EntityNotFoundException {
		OrganizationEntity organizationEntity = organizationService.updateOrganization(id,
				modelMapper.map(organizationDTO, OrganizationEntity.class));
		return modelMapper.map(organizationEntity, OrganizationDTO.class);
	}

	/**
	 * Deletes the organization with the associated ID received in the URL.
	 *
	 * @param id Identifier of the organization to delete. This must be a
	 *           string of digits.
	 */
	@DeleteMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) throws EntityNotFoundException, IllegalOperationException {
		organizationService.deleteOrganization(id);
	}

}