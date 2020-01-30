package ru.exlmoto.digest.repository;

import org.springframework.data.repository.CrudRepository;

import ru.exlmoto.digest.entity.ExchangeRateEntity;

public interface ExchangeRateRepository extends CrudRepository<ExchangeRateEntity, Integer> {
	ExchangeRateEntity getById(int id);

	default ExchangeRateEntity getBankRu() {
		return getById(ExchangeRateEntity.BANK_RU_ROW);
	}

	default ExchangeRateEntity getBankUa() {
		return getById(ExchangeRateEntity.BANK_UA_ROW);
	}

	default ExchangeRateEntity getBankBy() {
		return getById(ExchangeRateEntity.BANK_BY_ROW);
	}

	default ExchangeRateEntity getBankKz() {
		return getById(ExchangeRateEntity.BANK_KZ_ROW);
	}

	default ExchangeRateEntity getMetalRu() {
		return getById(ExchangeRateEntity.METAL_RU_ROW);
	}
}
