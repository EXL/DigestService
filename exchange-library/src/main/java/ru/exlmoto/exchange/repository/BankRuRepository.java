package ru.exlmoto.exchange.repository;

import org.springframework.data.repository.CrudRepository;

import ru.exlmoto.exchange.entity.BankRuEntity;

public interface BankRuRepository extends CrudRepository<BankRuEntity, Integer> {
	BankRuEntity getById(int id);

	default BankRuEntity getBankRu() {
		return getById(1);
	}
}
