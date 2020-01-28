package ru.exlmoto.digest.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import ru.exlmoto.digest.entity.SubMotofanEntity;

import java.util.List;

public interface SubMotofanRepository extends CrudRepository<SubMotofanEntity, Integer> {
	List<SubMotofanEntity> findAll();

	SubMotofanEntity findSubMotofanEntityBySubscription(long subscription);

	@Transactional
	void deleteSubMotofanEntityBySubscription(long subscription);
}
