package ru.exlmoto.exchange.repository;

import org.springframework.data.repository.CrudRepository;

import ru.exlmoto.exchange.entity.BankRuEntity;

public interface BankRuRepository extends CrudRepository<BankRuEntity, Long> {
	BankRuEntity getById(Long id);

	default BankRuEntity getBankRu() {
		return getById(1L);
	}
}
