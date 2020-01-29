package ru.exlmoto.digest.exchange.generator;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.when;

@SpringBootTest
class TgMarkdownGeneratorTest {
	@Autowired
	private TgMarkdownGenerator generator;

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
		String report = generator.bankRuReport();
		assertThat(report).isNotEmpty();
		System.out.println(report);
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
		String report = generator.bankUaReport();
		assertThat(report).isNotEmpty();
		System.out.println(report);
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
		String report = generator.bankByReport();
		assertThat(report).isNotEmpty();
		System.out.println(report);
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
		String report = generator.metalRuReport();
		assertThat(report).isNotEmpty();
		System.out.println(report);
	}
}
