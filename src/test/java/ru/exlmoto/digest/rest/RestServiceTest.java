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
	public void testRestService() {
		Answer answer = rest.getRawContent("https://exlmoto.ru");
		assertTrue(answer.status());
		assertThat(answer.answer()).isNotEmpty();

		answer = rest.getRawContent("https://exlmotor.ru");
		assertFalse(answer.status());
		assertThat(answer.answer()).isNotEmpty();
		System.out.println(answer.answer());
	}

	@Test
	public void testDropOnLargeData() {
		Answer answer = rest.getRawContent("https://mirror.yandex.ru/astra/current/orel/iso/orel-current.iso");
		assertFalse(answer.status());
		assertThat(answer.answer()).isNotEmpty();
		System.out.println(answer.answer());
	}
}
