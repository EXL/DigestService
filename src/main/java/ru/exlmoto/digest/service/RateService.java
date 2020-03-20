package ru.exlmoto.digest.service;

import org.springframework.stereotype.Service;

import ru.exlmoto.digest.entity.ExchangeRateEntity;
import ru.exlmoto.digest.repository.ExchangeRateRepository;

import java.util.Optional;

@Service
public class RateService {
	private final ExchangeRateRepository repository;

	public RateService(ExchangeRateRepository repository) {
		this.repository = repository;
	}

	public Optional<ExchangeRateEntity> getBankRu() {
		return repository.findById(ExchangeRateEntity.BANK_RU_ROW);
	}

	public Optional<ExchangeRateEntity> getBankUa() {
		return repository.findById(ExchangeRateEntity.BANK_UA_ROW);
	}

	public Optional<ExchangeRateEntity> getBankBy() {
		return repository.findById(ExchangeRateEntity.BANK_BY_ROW);
	}

	public Optional<ExchangeRateEntity> getBankKz() {
		return repository.findById(ExchangeRateEntity.BANK_KZ_ROW);
	}

	public Optional<ExchangeRateEntity> getMetalRu() {
		return repository.findById(ExchangeRateEntity.METAL_RU_ROW);
	}

	public void save(ExchangeRateEntity entity) {
		repository.save(entity);
	}
}
