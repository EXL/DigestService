package ru.exlmoto.digest.flat.manager;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.util.StringUtils;

import ru.exlmoto.digest.flat.model.Flat;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.file.ResourceHelper;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import static ru.exlmoto.digest.util.Answer.Ok;

@SpringBootTest
class FlatManagerTest {
	@Autowired
	private FlatManager manager;

	@MockBean
	private RestHelper rest;

	@Autowired
	private ResourceHelper helper;

	@Test
	void testGetCianFlatList() {
		when(rest.getRestFile(anyString()))
			.thenReturn(Ok(helper.getResourceFilePath("classpath:flat/cian.xlsx")));

		checkGoodAnswer(manager.getCianFlatList("https://example.com"));
	}

	@Test
	void testGetCianFlatListOnError() {
		when(rest.getRestFile(anyString()))
			.thenReturn(Ok(helper.getResourceFilePath("classpath:flat/cian-broken.xlsx")));

		checkBadAnswer(manager.getCianFlatList("https://example.com"));
	}

	@Test
	void testGetCianFlatListOnEmpty() {
		when(rest.getRestFile(anyString()))
			.thenReturn(Ok(helper.getResourceFilePath("classpath:flat/cian-empty.xlsx")));

		checkBadAnswer(manager.getCianFlatList("https://example.com"));
	}

	@Test
	void testGetN1FlatList() {
		when(rest.getRestResponse(anyString()))
			.thenReturn(Ok(helper.readFileToString("classpath:flat/n1.json")));

		checkGoodAnswer(manager.getN1FlatList("https://example.com"));
	}

	@Test
	void testGetN1FlatListOnError() {
		when(rest.getRestResponse(anyString()))
			.thenReturn(Ok(helper.readFileToString("classpath:flat/n1-broken.json")));

		checkBadAnswer(manager.getN1FlatList("https://example.com"));
	}

	@Test
	void testGetN1FlatListOnEmpty() {
		when(rest.getRestResponse(anyString()))
			.thenReturn(Ok(helper.readFileToString("classpath:flat/n1-empty.json")));

		checkBadAnswer(manager.getN1FlatList("https://example.com"));
	}

	private void checkGoodAnswer(Answer<List<Flat>> answer) {
		assertTrue(answer.ok());
		assertFalse(answer.answer().isEmpty());
	}

	private void checkBadAnswer(Answer<List<Flat>> answer) {
		assertFalse(answer.ok());
		String error = answer.error();
		assertTrue(StringUtils.hasText(error));
		System.out.println(error);
	}
}
