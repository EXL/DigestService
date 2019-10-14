package ru.exlmoto.digestbot.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import ru.exlmoto.digestbot.services.RestTextService;
import ru.exlmoto.digestbot.yaml.impl.YamlRatesIndexHelper;

@Service
public class BankService extends RestTextService {
	private final YamlRatesIndexHelper mYamlRatesIndexHelper;

	@Autowired
	public BankService(final RestTemplateBuilder aRestTemplateBuilder,
	                   final YamlRatesIndexHelper aYamlRatesIndexHelper,
	                   @Value("${digestbot.request.timeout}") final Integer aRequestTimeout) {
		super(aRestTemplateBuilder, aRequestTimeout);
		mYamlRatesIndexHelper = aYamlRatesIndexHelper;
	}

	public Pair<Boolean, String> receiveBankRuData() {
		return receiveObject(mYamlRatesIndexHelper.getBankLink("i.rate.ru"));
	}

	public Pair<Boolean, String> receiveBankUaData() {
		return receiveObject(mYamlRatesIndexHelper.getBankLink("i.rate.ua"));
	}

	public Pair<Boolean, String> receiveBankByData() {
		return receiveObject(mYamlRatesIndexHelper.getBankLink("i.rate.by"));
	}

	public Pair<Boolean, String> receiveBankKzData() {
		return receiveObject(mYamlRatesIndexHelper.getBankLink("i.rate.kz"));
	}

	public Pair<Boolean, String> receiveBankMetalData() {
		return receiveObject(mYamlRatesIndexHelper.getBankLink("i.rate.metal"));
	}

	public Pair<Boolean, String> receiveBankRuMirrorData() {
		return receiveObject(mYamlRatesIndexHelper.getSingleParameter("rate.ru.mirror"));
	}
}
