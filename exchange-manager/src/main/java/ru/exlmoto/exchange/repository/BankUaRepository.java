package ru.exlmoto.exchange.repository;

import org.springframework.data.repository.CrudRepository;

import ru.exlmoto.exchange.entity.BankUaEntity;

public interface BankUaRepository extends CrudRepository<BankUaEntity, Integer> {
	BankUaEntity getById(int id);

	default BankUaEntity getBankUa() {
		return getById(1);
	}
}
