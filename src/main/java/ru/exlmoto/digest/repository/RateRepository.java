package ru.exlmoto.digest.repository;

import org.springframework.data.repository.CrudRepository;

import ru.exlmoto.digest.entity.RateEntity;

public interface RateRepository extends CrudRepository<RateEntity, Integer> {
	RateEntity getById(int id);

	default RateEntity getBankRu() {
		return getById(RateEntity.BANK_RU_ROW);
	}

	default RateEntity getBankUa() {
		return getById(RateEntity.BANK_UA_ROW);
	}

	default RateEntity getBankBy() {
		return getById(RateEntity.BANK_BY_ROW);
	}

	default RateEntity getBankKz() {
		return getById(RateEntity.BANK_KZ_ROW);
	}

	default RateEntity getMetalRu() {
		return getById(RateEntity.METAL_RU_ROW);
	}
}
