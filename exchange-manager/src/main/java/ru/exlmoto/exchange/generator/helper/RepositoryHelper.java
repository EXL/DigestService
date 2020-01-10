package ru.exlmoto.exchange.generator.helper;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import ru.exlmoto.exchange.entity.BankRuEntity;
import ru.exlmoto.exchange.entity.BankUaEntity;
import ru.exlmoto.exchange.entity.BankByEntity;
import ru.exlmoto.exchange.entity.BankKzEntity;
import ru.exlmoto.exchange.entity.MetalRuEntity;
import ru.exlmoto.exchange.repository.BankRuRepository;
import ru.exlmoto.exchange.repository.BankUaRepository;
import ru.exlmoto.exchange.repository.BankByRepository;
import ru.exlmoto.exchange.repository.BankKzRepository;
import ru.exlmoto.exchange.repository.MetalRuRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class RepositoryHelper {
	private final BankRuRepository bankRuRepository;
	private final BankUaRepository bankUaRepository;
	private final BankByRepository bankByRepository;
	private final BankKzRepository bankKzRepository;
	private final MetalRuRepository metalRuRepository;

	public BankRuEntity getBankRu() {
		try {
			BankRuEntity bankRuEntity = bankRuRepository.getBankRu();
			if (bankRuEntity != null && bankRuEntity.checkAllValues()) {
				return bankRuEntity;
			}
		} catch (DataAccessException dae) {
			log.error("Cannot get BankRuEntity from database.", dae);
		}
		return null;
	}

	public BankUaEntity getBankUa() {
		try {
			BankUaEntity bankUaEntity = bankUaRepository.getBankUa();
			if (bankUaEntity != null && bankUaEntity.checkAllValues()) {
				return bankUaEntity;
			}
		} catch (DataAccessException dae) {
			log.error("Cannot get BankUaEntity from database.", dae);
		}
		return null;
	}

	public BankByEntity getBankBy() {
		try {
			BankByEntity bankByEntity = bankByRepository.getBankBy();
			if (bankByEntity != null && bankByEntity.checkAllValues()) {
				return bankByEntity;
			}
		} catch (DataAccessException dae) {
			log.error("Cannot get BankByEntity from database.", dae);
		}
		return null;
	}

	public BankKzEntity getBankKz() {
		try {
			BankKzEntity bankKzEntity = bankKzRepository.getBankKz();
			if (bankKzEntity != null && bankKzEntity.checkAllValues()) {
				return bankKzEntity;
			}
		} catch (DataAccessException dae) {
			log.error("Cannot get BankKzEntity from database.", dae);
		}
		return null;
	}

	public MetalRuEntity getMetalRu() {
		try {
			MetalRuEntity metalRuEntity = metalRuRepository.getMetalRu();
			if (metalRuEntity != null && metalRuEntity.checkAllValues()) {
				return metalRuEntity;
			}
		} catch (DataAccessException dae) {
			log.error("Cannot get MetalRuEntity from database.", dae);
		}
		return null;
	}
}
