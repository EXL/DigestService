package ru.exlmoto.digest.repository;

import org.springframework.data.repository.CrudRepository;

import ru.exlmoto.digest.entity.ExchangeRateEntity;

import java.util.Optional;

public interface ExchangeRateRepository extends CrudRepository<ExchangeRateEntity, Integer> {
	default Optional<ExchangeRateEntity> getBankRu() {
		return findById(ExchangeRateEntity.BANK_RU_ROW);
	}

	default Optional<ExchangeRateEntity> getBankUa() {
		return findById(ExchangeRateEntity.BANK_UA_ROW);
	}

	default Optional<ExchangeRateEntity> getBankBy() {
		return findById(ExchangeRateEntity.BANK_BY_ROW);
	}

	default Optional<ExchangeRateEntity> getBankKz() {
		return findById(ExchangeRateEntity.BANK_KZ_ROW);
	}

	default Optional<ExchangeRateEntity> getMetalRu() {
		return findById(ExchangeRateEntity.METAL_RU_ROW);
	}
}
