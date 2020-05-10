/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL <exlmotodev@gmail.com>
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

package ru.exlmoto.digest.exchange.generator;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import ru.exlmoto.digest.entity.ExchangeRateEntity;
import ru.exlmoto.digest.exchange.key.ExchangeKey;
import ru.exlmoto.digest.service.DatabaseService;

import java.math.BigDecimal;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.when;

@SpringBootTest
class RateTgMarkdownGeneratorTest {
	@Autowired
	private RateTgMarkdownGenerator generator;

	@MockBean
	private DatabaseService service;

	@Test
	public void testBankRuMarkdownReport() {
		ExchangeRateEntity bankRuEntity = new ExchangeRateEntity();
		bankRuEntity.setDate("08/01/2020");
		bankRuEntity.setUsd(new BigDecimal("78"));
		bankRuEntity.setEur(new BigDecimal("79.5600"));
		bankRuEntity.setKzt(new BigDecimal("1.1334231"));
		bankRuEntity.setUah(new BigDecimal("1866.45565"));
		bankRuEntity.setByn(new BigDecimal("184.0"));
		bankRuEntity.setCny(new BigDecimal("423.0"));
		bankRuEntity.setGbp(new BigDecimal("13456.45566"));
		bankRuEntity.setPrevUsd(new BigDecimal("88.100000000000000001"));
		bankRuEntity.setPrevEur(new BigDecimal("9.5600"));
		bankRuEntity.setPrevKzt(new BigDecimal("0.1334231"));
		bankRuEntity.setPrevUah(new BigDecimal("866.45565"));
		bankRuEntity.setPrevByn(new BigDecimal("84.0"));
		bankRuEntity.setPrevCny(new BigDecimal("23.0"));
		bankRuEntity.setPrevGbp(new BigDecimal("3456.45566"));

		when(service.getBankRu()).thenReturn(Optional.of(bankRuEntity));
		String report = generator.rateReportByKey(ExchangeKey.bank_ru.name());
		assertThat(report).isNotEmpty();
		System.out.println(report);
	}

	@Test
	public void testBankUaMarkdownReport() {
		ExchangeRateEntity bankUaEntity = new ExchangeRateEntity();
		bankUaEntity.setDate("08-JAN-2020");
		bankUaEntity.setUsd(new BigDecimal("1235"));
		bankUaEntity.setEur(new BigDecimal("0.0013"));
		bankUaEntity.setKzt(new BigDecimal("0.0"));
		bankUaEntity.setByn(new BigDecimal("0"));
		bankUaEntity.setRub(new BigDecimal("1.00"));
		bankUaEntity.setCny(new BigDecimal("1111.00"));
		bankUaEntity.setGbp(null);
		bankUaEntity.setPrevUsd(new BigDecimal("1233"));
		bankUaEntity.setPrevEur(new BigDecimal("1.0013"));
		bankUaEntity.setPrevKzt(new BigDecimal("1.0"));
		bankUaEntity.setPrevByn(new BigDecimal("1"));
		bankUaEntity.setPrevRub(new BigDecimal("2.00"));
		bankUaEntity.setPrevCny(new BigDecimal("111.00"));
		bankUaEntity.setPrevGbp(null);

		when(service.getBankUa()).thenReturn(Optional.of(bankUaEntity));
		String report = generator.rateReportByKey(ExchangeKey.bank_ua.name());
		assertThat(report).isNotEmpty();
		System.out.println(report);
	}

	@Test
	public void testBankByMarkdownReport() {
		ExchangeRateEntity bankByEntity = new ExchangeRateEntity();
		bankByEntity.setDate("10.01.2020");
		bankByEntity.setUsd(new BigDecimal("1235.0001"));
		bankByEntity.setEur(new BigDecimal("013113"));
		bankByEntity.setKzt(new BigDecimal("-0.12"));
		bankByEntity.setRub(new BigDecimal("1.234"));
		bankByEntity.setUah(null);
		bankByEntity.setCny(new BigDecimal("11"));
		bankByEntity.setGbp(new BigDecimal("1000.1"));
		bankByEntity.setPrevUsd(new BigDecimal("235.0001"));
		bankByEntity.setPrevEur(new BigDecimal("023113"));
		bankByEntity.setPrevKzt(new BigDecimal("-1.12"));
		bankByEntity.setPrevRub(new BigDecimal("0.234"));
		bankByEntity.setPrevUah(null);
		bankByEntity.setPrevCny(new BigDecimal("21"));
		bankByEntity.setPrevGbp(new BigDecimal("2000.1"));

		when(service.getBankBy()).thenReturn(Optional.of(bankByEntity));
		String report = generator.rateReportByKey(ExchangeKey.bank_by.name());
		assertThat(report).isNotEmpty();
		System.out.println(report);
	}

	@Test
	public void testMetalRuMarkdownReport() {
		ExchangeRateEntity metalRuEntity = new ExchangeRateEntity();
		metalRuEntity.setDate("08.01.2020");
		metalRuEntity.setGold(new BigDecimal("3031.25"));
		metalRuEntity.setSilver(new BigDecimal("3821.40"));
		metalRuEntity.setPlatinum(new BigDecimal("1932.59"));
		metalRuEntity.setPalladium(new BigDecimal("35.48"));
		metalRuEntity.setPrevGold(new BigDecimal("2031.25"));
		metalRuEntity.setPrevSilver(new BigDecimal("2821.40"));
		metalRuEntity.setPrevPlatinum(new BigDecimal("932.59"));
		metalRuEntity.setPrevPalladium(new BigDecimal("25.48"));

		when(service.getMetalRu()).thenReturn(Optional.of(metalRuEntity));
		String report = generator.rateReportByKey(ExchangeKey.metal_ru.name());
		assertThat(report).isNotEmpty();
		System.out.println(report);
	}
}
