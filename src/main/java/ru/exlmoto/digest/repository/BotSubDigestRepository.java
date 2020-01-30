package ru.exlmoto.digest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import ru.exlmoto.digest.entity.BotSubDigestEntity;

public interface BotSubDigestRepository extends JpaRepository<BotSubDigestEntity, Integer> {
	BotSubDigestEntity findBotSubDigestEntityBySubscription(long subscription);

	@Transactional
	void deleteBotSubDigestEntityBySubscription(long subscription);
}
