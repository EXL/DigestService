package ru.exlmoto.exchange.repository;

import org.springframework.data.repository.CrudRepository;

import ru.exlmoto.exchange.entity.BankByEntity;

public interface BankByRepository extends CrudRepository<BankByEntity, Integer> {
	BankByEntity getById(int id);

	default BankByEntity getBankBy() {
		return getById(1);
	}
}
