package ru.exlmoto.digest.repository;

import org.springframework.data.repository.CrudRepository;

import ru.exlmoto.digest.entity.BankRuEntity;

public interface BankRuRepository extends CrudRepository<BankRuEntity, Integer> {
	BankRuEntity getById(int id);

	default BankRuEntity getBankRu() {
		return getById(1);
	}
}
