package ru.exlmoto.exchange.repository;

import org.springframework.data.repository.CrudRepository;

import ru.exlmoto.exchange.domain.BankKzEntity;

public interface BankKzRepository extends CrudRepository<BankKzEntity, Integer> {
	BankKzEntity getById(int id);

	default BankKzEntity getBankKz() {
		return getById(1);
	}
}
