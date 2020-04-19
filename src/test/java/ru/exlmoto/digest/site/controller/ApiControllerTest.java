package ru.exlmoto.digest.site.controller;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ApiControllerTest {
	@Autowired
	private MockMvc mvc;

	private final ControllerHelper helper = new ControllerHelper();

	@Test
	public void testInfo() throws Exception {
		helper.checkPlainText(mvc, "/api/info", "Java Version");
	}

	@Test
	public void testRate() throws Exception {
		helper.validateJson(mvc, "/api/rate");
	}

	@Test
	public void testCovidRu() throws Exception {
		helper.validateJsonUtf8(mvc, "/api/covid/ru");
	}

	@Test
	public void testCovidUa() throws Exception {
		helper.validateJsonUtf8(mvc, "/api/covid/ua");
	}
}
