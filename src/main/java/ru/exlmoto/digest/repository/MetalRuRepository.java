package ru.exlmoto.digest.repository;

import org.springframework.data.repository.CrudRepository;

import ru.exlmoto.digest.entity.MetalRuEntity;

public interface MetalRuRepository extends CrudRepository<MetalRuEntity, Integer> {
	MetalRuEntity getById(int id);

	default MetalRuEntity getMetalRu() {
		return getById(1);
	}
}
