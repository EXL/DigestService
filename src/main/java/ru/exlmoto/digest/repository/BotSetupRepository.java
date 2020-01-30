package ru.exlmoto.digest.repository;

import org.springframework.data.repository.CrudRepository;

import ru.exlmoto.digest.entity.BotSetupEntity;

public interface BotSetupRepository extends CrudRepository<BotSetupEntity, Integer> {
	BotSetupEntity getById(int id);

	default BotSetupEntity getSetupBot() {
		return getById(BotSetupEntity.SETUP_ROW);
	}
}
