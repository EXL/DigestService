package ru.exlmoto.exchange.generator;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.mock.mockito.MockBean;

import ru.exlmoto.exchange.ExchangeConfiguration;
import ru.exlmoto.exchange.ExchangeConfigurationTest;
import ru.exlmoto.exchange.entity.BankByEntity;
import ru.exlmoto.exchange.entity.BankRuEntity;
import ru.exlmoto.exchange.entity.BankUaEntity;
import ru.exlmoto.exchange.entity.MetalRuEntity;
import ru.exlmoto.exchange.repository.BankByRepository;
import ru.exlmoto.exchange.repository.BankRuRepository;
import ru.exlmoto.exchange.repository.BankUaRepository;
import ru.exlmoto.exchange.repository.MetalRuRepository;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

public class MarkdownGeneratorTest extends ExchangeConfigurationTest {
	@Autowired
	private MarkdownGenerator generator;

	@MockBean private BankRuRepository bankRuRepository;
	@MockBean private BankUaRepository bankUaRepository;
	@MockBean private BankByRepository bankByRepository;
	@MockBean private MetalRuRepository metalRuRepository;

	@Test
	public void testBankRuMarkdownReport() {
		BankRuEntity bankRuEntity = new BankRuEntity(
			"08/01/2020",
			new BigDecimal("78"),
			new BigDecimal("79.5600"),
			new BigDecimal("1.1334231"),
			new BigDecimal("184.0"),
			new BigDecimal("1866.45565"),
			new BigDecimal("13456.45566"),
			new BigDecimal("88.100000000000000001")
		);
		when(bankRuRepository.getBankRu()).thenReturn(bankRuEntity);
		System.out.println(generator.bankRuReport());
	}

	@Test
	public void testBankUaMarkdownReport() {
		BankUaEntity bankUaEntity = new BankUaEntity(
			"08-JAN-2020",
			new BigDecimal("1235"),
			new BigDecimal("0.0013"),
			new BigDecimal("0.0"),
			new BigDecimal("1.00"),
			new BigDecimal("0"),
			null,
			new BigDecimal("1233")
		);
		when(bankUaRepository.getBankUa()).thenReturn(bankUaEntity);
		System.out.println(generator.bankUaReport());
	}

	@Test
	public void testBankByMarkdownReport() {
		BankByEntity bankByEntity = new BankByEntity(
			"10.01.2020",
			new BigDecimal("1235.0001"),
			new BigDecimal("013113"),
			new BigDecimal("-0.12"),
			new BigDecimal("1.234"),
			null,
			new BigDecimal("1000.1"),
			new BigDecimal("1235.0002")
		);
		when(bankByRepository.getBankBy()).thenReturn(bankByEntity);
		System.out.println(generator.bankByReport());
	}

	@Test
	public void testMetalRuMarkdownReport() {
		MetalRuEntity metalRuEntity = new MetalRuEntity(
			"08.01.2020",
			new BigDecimal("3031.25"),
			new BigDecimal("3821.40"),
			new BigDecimal("1932.59"),
			new BigDecimal("35.48"),
			new BigDecimal("3031.15")
		);
		when(metalRuRepository.getMetalRu()).thenReturn(metalRuEntity);
		System.out.println(generator.metalRuReport());
	}

	@SpringBootApplication(scanBasePackageClasses = { ExchangeConfiguration.class })
	public static class ExchangeConfigurationCommon {

	}
}
