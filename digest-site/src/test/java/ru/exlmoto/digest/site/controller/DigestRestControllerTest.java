package ru.exlmoto.digest.site.controller;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ru.exlmoto.exchange.ExchangeConfiguration;
import ru.exlmoto.motofan.MotofanConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class DigestRestControllerTest {
	@Autowired
	private DigestRestController digestRestController;

	@Test
	public void contextLoads() {
		assertThat(digestRestController).isNotNull();
	}

	@SpringBootApplication(scanBasePackageClasses = {
		ExchangeConfiguration.class,
		MotofanConfiguration.class,
		DigestRestController.class
	})
	public static class DigestSiteConfigurationCommon {

	}
}
