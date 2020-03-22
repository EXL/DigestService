package ru.exlmoto.digest.exchange.generator;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import ru.exlmoto.digest.entity.ExchangeRateEntity;
import ru.exlmoto.digest.exchange.key.ExchangeKey;
import ru.exlmoto.digest.repository.ExchangeRateRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.when;

@SpringBootTest
class RateTgMarkdownGeneratorTest {
	@Autowired
	private RateTgMarkdownGenerator generator;

	@MockBean
	private ExchangeRateRepository repository;

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
		bankRuEntity.setPrev(new BigDecimal("88.100000000000000001"));

		when(repository.getBankRu()).thenReturn(Optional.of(bankRuEntity));
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
		bankUaEntity.setPrev(new BigDecimal("1233"));

		when(repository.getBankUa()).thenReturn(Optional.of(bankUaEntity));
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
		bankByEntity.setPrev(new BigDecimal("1235.0002"));

		when(repository.getBankBy()).thenReturn(Optional.of(bankByEntity));
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
		metalRuEntity.setPrev(new BigDecimal("3031.15"));

		when(repository.getMetalRu()).thenReturn(Optional.of(metalRuEntity));
		String report = generator.rateReportByKey(ExchangeKey.metal_ru.name());
		assertThat(report).isNotEmpty();
		System.out.println(report);
	}
}
