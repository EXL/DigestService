package ru.exlmoto.digest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import ru.exlmoto.digest.entity.BotSubMotofanEntity;

public interface BotSubMotofanRepository extends JpaRepository<BotSubMotofanEntity, Long> {
	BotSubMotofanEntity findBotSubMotofanEntityBySubscription(long subscription);

	@Transactional
	void deleteBotSubMotofanEntityBySubscription(long subscription);
}
