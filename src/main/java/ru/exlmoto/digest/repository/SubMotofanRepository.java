package ru.exlmoto.digest.repository;

import org.springframework.data.repository.CrudRepository;

import ru.exlmoto.digest.entity.SubMotofanEntity;

public interface SubMotofanRepository extends CrudRepository<SubMotofanEntity, Integer> {
	SubMotofanEntity findSubMotofanEntityBySubscription(long subscription);

	void deleteSubMotofanEntityBySubscription(long subscription);
}
