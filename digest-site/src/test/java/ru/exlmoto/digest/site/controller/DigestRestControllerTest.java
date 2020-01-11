package ru.exlmoto.digest.site.controller;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DigestRestControllerTest {
	@Autowired
	private DigestRestController digestRestController;

	@Test
	public void contextLoads() {
		assertThat(digestRestController).isNotNull();
	}

	@SpringBootApplication
	public static class DigestSiteConfigurationCommon {

	}
}
