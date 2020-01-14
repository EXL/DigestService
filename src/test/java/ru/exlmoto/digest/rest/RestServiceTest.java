package ru.exlmoto.digest.rest;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.util.Answer;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

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

	@Test
	public void testGetRestFile() {
		System.out.println(rest.getRestFile("https://api.z-lab.me/charts/mmvb.png").answer());
		System.out.println(rest.getRestFile("https://www.linux.org.ru/tracker/").answer());
		System.out.println(rest.getRestFile("https://exlmoto.ru/").answer());
		assertNull(rest.getRestFile("https://exlmoto.ru/404").answer());
	}

	@Test
	public void testRestFileManagerLargeFileDrop() {
		Answer<String> res = rest.getRestFile("https://mirror.yandex.ru/astra/current/orel/iso/orel-current.iso");
		assertNull(res.answer());
		String error = res.error();
		assertThat(error).isNotEmpty();
		System.out.println(error);
	}
}
