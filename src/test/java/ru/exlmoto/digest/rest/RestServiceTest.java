package ru.exlmoto.digest.rest;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.util.Answer;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RestServiceTest {
	@Autowired
	private RestService rest;

	@Test
	public void testGetRestContent() {
		Answer<String> res = rest.getRestResponse("https://exlmoto.ru");
		assertTrue(res.ok());
		assertThat(res.answer()).isNotEmpty();
		assertThat(res.error()).isEmpty();

		res = rest.getRestResponse("https://exlmotor.ru");
		assertFalse(res.ok());
		assertThat(res.error()).isNotEmpty();
		System.out.println(res.error());
	}

	@Test
	public void testDropLargeRestContent() {
		Answer<String> res = rest.getRestResponse("https://mirror.yandex.ru/astra/current/orel/iso/orel-current.iso");
		assertFalse(res.ok());
		assertThat(res.error()).isNotEmpty();
		System.out.println(res.error());
	}
}
