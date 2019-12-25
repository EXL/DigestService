package ru.exlmoto.exchange.repository;

import org.springframework.data.repository.CrudRepository;

import ru.exlmoto.exchange.entity.BankKzEntity;

public interface BankKzRepository extends CrudRepository<BankKzEntity, Long> {
	BankKzEntity getById(Long id);

	default BankKzEntity getBankKz() {
		return getById(1L);
	}
}
