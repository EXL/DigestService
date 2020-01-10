package ru.exlmoto.digest.controller;

import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.exchange.ExchangeConfiguration;

@SpringBootTest
public class DigestRestControllerTest {
	@Test
	public void contextLoads() {

	}

	@SpringBootApplication(scanBasePackageClasses = { ExchangeConfiguration.class })
	public static class ExchangeConfigurationCommon {

	}
}
