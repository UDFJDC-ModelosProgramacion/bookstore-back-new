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

package co.edu.udistrital.mdp.bookstore.services;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.bookstore.entities.AuthorEntity;
import co.edu.udistrital.mdp.bookstore.entities.PrizeEntity;
import co.edu.udistrital.mdp.bookstore.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.bookstore.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.bookstore.repositories.AuthorRepository;
import co.edu.udistrital.mdp.bookstore.repositories.PrizeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase que implementa la conexion con la persistencia para la relación entre
 * la entidad Prize y Author
 *
 * @author Jose Bocanegra
 */
@RequiredArgsConstructor
@Slf4j
@Service

public class PrizeAuthorService {

	private final AuthorRepository authorRepository;

	private final PrizeRepository prizeRepository;

	/**
	 * Agregar un autor a un premio
	 *
	 * @param prizeId  El id premio a guardar
	 * @param authorId El id del autor al cual se le va a guardar el premio.
	 * @return El premio que fue agregado al autor.
	 * @throws EntityNotFoundException
	 */
	@Transactional
	public AuthorEntity addAuthor(Long authorId, Long prizeId) throws EntityNotFoundException {
		log.info("Inicia proceso de asociar el autor con id = {0} al premio con id = " + prizeId, authorId);
		Optional<AuthorEntity> autorOptional = authorRepository.findById(authorId);
		if (autorOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.AUTHOR_NOT_FOUND);

		Optional<PrizeEntity> prizeOptional = prizeRepository.findById(prizeId);
		if (prizeOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PRIZE_NOT_FOUND);

		AuthorEntity autorEntity = autorOptional.get();
		PrizeEntity prizeEntity = prizeOptional.get();

		prizeEntity.setAuthor(autorEntity);
		log.info("Termina proceso de asociar el autor con id = {0} al premio con id = {1}", authorId, prizeId);
		return autorEntity;
	}

	/**
	 *
	 * Obtener un author por medio del id del premio.
	 *
	 * @param prizeId id del premio a ser buscado.
	 * @return el autor solicitado.
	 * @throws EntityNotFoundException
	 */

	@Transactional
	public AuthorEntity getAuthor(Long prizeId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar el autor del premio con id = {0}", prizeId);
		Optional<PrizeEntity> prizeOptional = prizeRepository.findById(prizeId);
		if (prizeOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PRIZE_NOT_FOUND);

		PrizeEntity prizeEntity = prizeOptional.get();
		AuthorEntity authorEntity = prizeEntity.getAuthor();

		if (authorEntity == null)
			throw new EntityNotFoundException("The author was not found");

		log.info("Termina proceso de consultar el autor del premio con id = {0}", prizeId);
		return authorEntity;
	}

	/**
	 * Remplazar autor de un premio
	 *
	 * @param prizeId  el id del premio que se quiere actualizar.
	 * @param authorId El id del nuebo autor asociado al premio.
	 * @return el nuevo autor asociado.
	 * @throws EntityNotFoundException
	 */

	@Transactional
	public AuthorEntity replaceAuthor(Long prizeId, Long authorId) throws EntityNotFoundException {
		log.info("Inicia proceso de actualizar el autor del premio premio con id = {0}", prizeId);
		Optional<AuthorEntity> autorOptional = authorRepository.findById(authorId);
		if (autorOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.AUTHOR_NOT_FOUND);

		Optional<PrizeEntity> prizeOptional = prizeRepository.findById(prizeId);
		if (prizeOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PRIZE_NOT_FOUND);

		AuthorEntity autorEntity = autorOptional.get();
		PrizeEntity prizeEntity = prizeOptional.get();

		prizeEntity.setAuthor(autorEntity);
		log.info("Termina proceso de asociar el autor con id = {0} al premio con id = " + prizeId, authorId);
		return autorEntity;
	}

	/**
	 * Borrar el autor de un premio
	 *
	 * @param prizeId El premio que se desea borrar del autor.
	 * @throws EntityNotFoundException si el premio no tiene autor
	 */

	@Transactional
	public void removeAuthor(Long prizeId) throws EntityNotFoundException {
		log.info("Inicia proceso de borrar el autor del premio con id = {0}", prizeId);
		Optional<PrizeEntity> prizeOptional = prizeRepository.findById(prizeId);
		if (prizeOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PRIZE_NOT_FOUND);

		PrizeEntity prizeEntity = prizeOptional.get();

		if (prizeEntity.getAuthor() == null) {
			throw new EntityNotFoundException("El premio no tiene autor");
		}
		Optional<AuthorEntity> authorOptional = authorRepository.findById(prizeEntity.getAuthor().getId());

		authorOptional.ifPresent(author -> {
			prizeEntity.setAuthor(null);
			author.getPrizes().remove(prizeEntity);
		});

		log.info("Termina proceso de borrar el autor del premio con id = " + prizeId);
	}
}
