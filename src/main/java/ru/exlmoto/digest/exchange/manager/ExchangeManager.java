/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 EXL <exlmotodev@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.exlmoto.digest.exchange.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.exchange.configuration.ExchangeConfiguration;
import ru.exlmoto.digest.exchange.parser.additional.RateRbcParser;
import ru.exlmoto.digest.exchange.parser.additional.RateAliParser;
import ru.exlmoto.digest.exchange.parser.impl.BankRuParser;
import ru.exlmoto.digest.exchange.parser.impl.BankUaParser;
import ru.exlmoto.digest.exchange.parser.impl.BankUaMirrorParser;
import ru.exlmoto.digest.exchange.parser.impl.BankByParser;
import ru.exlmoto.digest.exchange.parser.impl.BankKzParser;
import ru.exlmoto.digest.exchange.parser.impl.MetalRuParser;
import ru.exlmoto.digest.exchange.parser.impl.MetalRuMirrorParser;
import ru.exlmoto.digest.exchange.parser.impl.BitcoinParser;
import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.util.rest.RestHelper;

@Component
public class ExchangeManager {
	private final Logger log = LoggerFactory.getLogger(ExchangeManager.class);

	private final ExchangeConfiguration config;
	private final DatabaseService service;
	private final RestHelper rest;

	public ExchangeManager(ExchangeConfiguration config, DatabaseService service, RestHelper rest) {
		this.config = config;
		this.service = service;
		this.rest = rest;
	}

	public void commitAllRates() {
		log.info("=> Start update exchanging rates.");
		commitBankRu(config.getBankRu(), config.getBankRuMirror());
		commitBankUa(config.getBankUa(), config.getBankUaMirror());
		commitBankBy(config.getBankBy());
		commitBankKz(config.getBankKz());
		commitMetalRu(config.getMetalRu(), config.getMetalRuMirror());
		commitBitcoin(config.getBitcoin());
		commitRbc(config.getRbc());
		commitAliexpress(config.getAliexpress());
		log.info("=> End update exchanging rates.");
	}

	public void commitBankRu(String url, String mirror) {
		if (!new BankRuParser().commitRates(url, service, rest)) {
			new BankRuParser().commitRatesMirror(mirror, service, rest);
		}
	}

	public void commitBankUa(String url, String mirror) {
		if (!new BankUaParser().commitRates(url, service, rest)) {
			new BankUaMirrorParser().commitRates(mirror, service, rest);
		}
	}

	public void commitBankBy(String url) {
		new BankByParser().commitRates(url, service, rest);
	}

	public void commitBankKz(String url) {
		new BankKzParser().commitRates(url, service, rest);
	}

	public void commitMetalRu(String url, String mirror) {
		if (!new MetalRuParser().commitRates(url, service, rest)) {
			new MetalRuMirrorParser().commitRates(mirror, service, rest);
		}
	}

	public void commitBitcoin(String url) {
		new BitcoinParser().commitRates(url, service, rest);
	}

	public void commitRbc(String url) {
		new RateRbcParser().commitRates(url, service, rest);
	}

	public void commitAliexpress(String url) {
		new RateAliParser().commitRates(url, service, rest);
	}
}
