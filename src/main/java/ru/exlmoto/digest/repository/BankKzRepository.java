package ru.exlmoto.digest.repository;

import org.springframework.data.repository.CrudRepository;

import ru.exlmoto.digest.entity.BankKzEntity;

public interface BankKzRepository extends CrudRepository<BankKzEntity, Integer> {
	BankKzEntity getById(int id);

	default BankKzEntity getBankKz() {
		return getById(1);
	}
}
