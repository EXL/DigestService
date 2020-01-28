package ru.exlmoto.digest.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import ru.exlmoto.digest.entity.SubDigestEntity;

public interface SubDigestRepository extends CrudRepository<SubDigestEntity, Integer> {
	SubDigestEntity findSubDigestEntityBySubscription(long subscription);

	@Transactional
	void deleteSubDigestEntityBySubscription(long subscription);
}
