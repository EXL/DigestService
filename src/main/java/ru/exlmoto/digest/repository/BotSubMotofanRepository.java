package ru.exlmoto.digest.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import ru.exlmoto.digest.entity.BotSubMotofanEntity;

import java.util.List;

public interface BotSubMotofanRepository extends CrudRepository<BotSubMotofanEntity, Integer> {
	List<BotSubMotofanEntity> findAll();

	BotSubMotofanEntity findSubMotofanEntityBySubscription(long subscription);

	@Transactional
	void deleteSubMotofanEntityBySubscription(long subscription);
}
