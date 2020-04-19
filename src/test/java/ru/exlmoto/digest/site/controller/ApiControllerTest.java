package ru.exlmoto.digest.site.controller;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.endsWith;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiControllerTest {
	@Autowired
	private MockMvc mvc;

	@Test
	public void testInfo() throws Exception {
		mvc.perform(get("/api/info"))
			.andDo(print())
			.andExpect(header().string("content-type", containsString("text/plain")))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Java Version")));
	}

	@Test
	public void testRate() throws Exception {
		validateJson("/api/rate", "application/json");
	}

	@Test
	public void testCovidRu() throws Exception {
		validateJson("/api/covid/ru", "application/json;charset=UTF-8");
	}

	@Test
	public void testCovidUa() throws Exception {
		validateJson("/api/covid/ua", "application/json;charset=UTF-8");
	}

	private void validateJson(String path, String produces) throws Exception {
		mvc.perform(get(path))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(header().string("content-type", containsString(produces)))
			.andExpect(content().string(startsWith("{")))
			.andExpect(content().string(endsWith("}")));
	}
}
