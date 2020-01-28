package ru.exlmoto.digest.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import ru.exlmoto.digest.entity.SubMotofanEntity;

public interface SubMotofanRepository extends CrudRepository<SubMotofanEntity, Integer> {
	SubMotofanEntity findSubMotofanEntityBySubscription(long subscription);

	@Transactional
	void deleteSubMotofanEntityBySubscription(long subscription);
}
