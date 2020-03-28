package ru.exlmoto.digest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import ru.exlmoto.digest.entity.BotSubCovidEntity;

public interface BotSubCovidRepository extends JpaRepository<BotSubCovidEntity, Long> {
	BotSubCovidEntity findBotSubCovidEntityBySubscription(long subscription);

	@Transactional
	void deleteBotSubCovidEntityBySubscription(long subscription);
}
