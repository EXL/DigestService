package ru.exlmoto.rest.service;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ru.exlmoto.rest.RestConfigurationTest;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertNull;

public class RestServiceTest extends RestConfigurationTest {
	@Autowired
	private RestService service;

	@Test
	public void testRestServicePage() {
		assertThat(service.getRawContent("https://exlmoto.ru")).isNotEmpty();
		assertNull(service.getRawContent("https://exlmotor.ru"));
	}

	@Test
	public void testDropOnLargeData() {
		assertNull(service.getRawContent("https://mirror.yandex.ru/astra/current/orel/iso/orel-current.iso"));
	}

	@SpringBootApplication
	public static class RestConfigurationCommon {

	}
}
