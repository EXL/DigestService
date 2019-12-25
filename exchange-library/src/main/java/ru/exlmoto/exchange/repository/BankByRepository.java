package ru.exlmoto.exchange.repository;

import org.springframework.data.repository.CrudRepository;

import ru.exlmoto.exchange.entity.BankByEntity;

public interface BankByRepository extends CrudRepository<BankByEntity, Long> {
	BankByEntity getById(Long id);

	default BankByEntity getBankBy() {
		return getById(1L);
	}
}
