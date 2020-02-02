package ru.exlmoto.digest.repository;

import org.springframework.data.repository.CrudRepository;

import ru.exlmoto.digest.entity.BotSetupEntity;

import java.util.Optional;

public interface BotSetupRepository extends CrudRepository<BotSetupEntity, Integer> {
	default Optional<BotSetupEntity> getSetupBot() {
		return findById(BotSetupEntity.SETUP_ROW);
	}
}
