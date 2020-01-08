package ru.exlmoto.exchange.generator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import ru.exlmoto.exchange.entity.BankRuEntity;
import ru.exlmoto.exchange.entity.BankUaEntity;
import ru.exlmoto.exchange.entity.MetalRuEntity;
import ru.exlmoto.exchange.repository.BankRuRepository;
import ru.exlmoto.exchange.repository.BankUaRepository;
import ru.exlmoto.exchange.repository.MetalRuRepository;

import java.math.BigDecimal;

@SpringBootTest
@ActiveProfiles("test")
@SpringBootApplication(scanBasePackages = "ru.exlmoto.exchange")
public class MarkdownGeneratorTest {
	@Autowired
	private MarkdownGenerator generator;

	@MockBean private BankRuRepository bankRuRepository;
	@MockBean private BankUaRepository bankUaRepository;
	@MockBean private MetalRuRepository metalRuRepository;

	@BeforeEach
	public void setUpFakeData() {
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

		MetalRuEntity metalRuEntity = new MetalRuEntity(
			"08.01.2020",
			new BigDecimal("3031.25"),
			new BigDecimal("3821.40"),
			new BigDecimal("1932.59"),
			new BigDecimal("35.48"),
			new BigDecimal("3031.15")
		);

		Mockito.when(bankRuRepository.getBankRu()).thenReturn(bankRuEntity);
		Mockito.when(bankUaRepository.getBankUa()).thenReturn(bankUaEntity);
		Mockito.when(metalRuRepository.getMetalRu()).thenReturn(metalRuEntity);
	}

	@Test
	public void testBankRuMarkdownReport() {
		System.out.println(generator.bankRuReport());
	}

	@Test
	public void testBankUaMarkdownReport() {
		System.out.println(generator.bankUaReport());
	}

	@Test
	public void testMetalRuMarkdownReport() {
		System.out.println(generator.metalRuReport());
	}
}
