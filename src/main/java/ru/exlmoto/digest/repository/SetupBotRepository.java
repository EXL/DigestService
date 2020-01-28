package ru.exlmoto.digest.repository;

import org.springframework.data.repository.CrudRepository;

import ru.exlmoto.digest.entity.SetupBotEntity;

public interface SetupBotRepository extends CrudRepository<SetupBotEntity, Integer> {
	SetupBotEntity getById(int id);

	default SetupBotEntity getSetupBot() {
		return getById(1);
	}
}
