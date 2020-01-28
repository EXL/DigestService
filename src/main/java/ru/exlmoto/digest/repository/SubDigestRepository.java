package ru.exlmoto.digest.repository;

import org.springframework.data.repository.CrudRepository;

import ru.exlmoto.digest.entity.SubDigestEntity;

public interface SubDigestRepository extends CrudRepository<SubDigestEntity, Integer> {
	SubDigestEntity findSubDigestEntityBySubscription(long subscription);

	void deleteSubDigestEntityBySubscription(long subscription);
}
