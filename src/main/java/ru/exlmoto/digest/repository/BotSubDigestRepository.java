package ru.exlmoto.digest.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import ru.exlmoto.digest.entity.BotSubDigestEntity;

import java.util.List;

public interface BotSubDigestRepository extends CrudRepository<BotSubDigestEntity, Integer> {
	List<BotSubDigestEntity> findAll();

	BotSubDigestEntity findSubDigestEntityBySubscription(long subscription);

	@Transactional
	void deleteSubDigestEntityBySubscription(long subscription);
}
