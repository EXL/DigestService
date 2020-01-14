package ru.exlmoto.digest.exchange.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.exchange.manager.impl.BankByManager;
import ru.exlmoto.digest.exchange.manager.impl.BankKzManager;
import ru.exlmoto.digest.exchange.manager.impl.BankRuManager;
import ru.exlmoto.digest.exchange.manager.impl.BankUaManager;
import ru.exlmoto.digest.exchange.manager.impl.MetalRuManager;

@Slf4j
@RequiredArgsConstructor
@Component
public class RateGeneralManager {
	@Value("${exchange.bank-ru}")
	private String bankRu;

	@Value("${exchange.bank-ru-mirror}")
	private String bankRuMirror;

	@Value("${exchange.bank-ua}")
	private String bankUa;

	@Value("${exchange.bank-ua-mirror}")
	private String bankUaMirror;

	@Value("${exchange.bank-by}")
	private String bankBy;

	@Value("${exchange.bank-kz}")
	private String bankKz;

	@Value("${exchange.metal-ru}")
	private String metalRu;

	@Value("${exchange.metal-ru-mirror}")
	private String metalRuMirror;

	private final BankRuManager bankRuManager;
	private final BankUaManager bankUaManager;
	private final BankByManager bankByManager;
	private final BankKzManager bankKzManager;
	private final MetalRuManager metalRuManager;

	public void commitAllRates() {
		log.info("=> Start update exchanging rates.");
		bankRuManager.commitRates(bankRu, bankRuMirror);
		bankUaManager.commitRates(bankUa, bankUaMirror);
		bankByManager.commitRates(bankBy);
		bankKzManager.commitRates(bankKz);
		metalRuManager.commitRates(metalRu, metalRuMirror);
		log.info("=> End update exchanging rates.");
	}
}
