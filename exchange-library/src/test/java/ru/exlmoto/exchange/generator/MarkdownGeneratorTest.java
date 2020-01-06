package ru.exlmoto.exchange.generator;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@SpringBootApplication(scanBasePackages = "ru.exlmoto.exchange")
public class MarkdownGeneratorTest {
	@Autowired
	private MarkdownGenerator markdownGenerator;

	@Test
	public void testBankRuReport() {
		assertThat(markdownGenerator.bankRuReport()).isNotBlank();
	}

	@Test
	public void testBankUaReport() {
		assertThat(markdownGenerator.bankUaReport()).isNotBlank();
	}

	@Test
	public void testBankByReport() {
		assertThat(markdownGenerator.bankByReport()).isNotBlank();
	}

	@Test
	public void testBankKzReport() {
		assertThat(markdownGenerator.bankKzReport()).isNotBlank();
	}

	@Test
	public void testMetalRuReport() {
		assertThat(markdownGenerator.metalRuReport()).isNotBlank();
	}
}
