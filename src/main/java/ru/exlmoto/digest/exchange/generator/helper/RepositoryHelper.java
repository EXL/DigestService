package ru.exlmoto.digest.exchange.generator.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.entity.BankRuEntity;
import ru.exlmoto.digest.entity.BankUaEntity;
import ru.exlmoto.digest.entity.BankByEntity;
import ru.exlmoto.digest.entity.BankKzEntity;
import ru.exlmoto.digest.entity.MetalRuEntity;
import ru.exlmoto.digest.repository.BankRuRepository;
import ru.exlmoto.digest.repository.BankUaRepository;
import ru.exlmoto.digest.repository.BankByRepository;
import ru.exlmoto.digest.repository.BankKzRepository;
import ru.exlmoto.digest.repository.MetalRuRepository;

@Component
public class RepositoryHelper {
	private final Logger log = LoggerFactory.getLogger(RepositoryHelper.class);

	private final BankRuRepository bankRuRepository;
	private final BankUaRepository bankUaRepository;
	private final BankByRepository bankByRepository;
	private final BankKzRepository bankKzRepository;
	private final MetalRuRepository metalRuRepository;

	public RepositoryHelper(BankRuRepository bankRuRepository,
	                        BankUaRepository bankUaRepository,
	                        BankByRepository bankByRepository,
	                        BankKzRepository bankKzRepository,
	                        MetalRuRepository metalRuRepository) {
		this.bankRuRepository = bankRuRepository;
		this.bankUaRepository = bankUaRepository;
		this.bankByRepository = bankByRepository;
		this.bankKzRepository = bankKzRepository;
		this.metalRuRepository = metalRuRepository;
	}

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
