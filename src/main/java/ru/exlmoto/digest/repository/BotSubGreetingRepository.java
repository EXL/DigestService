package ru.exlmoto.digest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import ru.exlmoto.digest.entity.BotSubGreetingEntity;

public interface BotSubGreetingRepository extends JpaRepository<BotSubGreetingEntity, Long> {
	BotSubGreetingEntity findBotSubGreetingEntityByIgnored(long chatId);

	@Transactional
	void deleteBotSubGreetingEntityByIgnored(long chatId);
}
