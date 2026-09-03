package co.edu.udistrital.mdp.ZZZ.entities;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@MappedSuperclass
public abstract class BaseEntity {

	@PodamExclude
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
}
