package ru.exlmoto.digest.site.controller;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;

import ru.exlmoto.digest.service.DatabaseService;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class SiteControllerTest {
	@Autowired
	private MockMvc mvc;

	@SpyBean
	private DatabaseService service;

	private final ControllerHelper helper = new ControllerHelper();

	@Test
	public void testIndex() throws Exception {
		helper.validateHtmlUtf8(mvc, "/", "!DOCTYPE");
	}

	@Test
	public void testJump() throws Exception {
		when(service.getDigestIndex(anyLong(), anyLong())).thenReturn(99);

		helper.checkError4xx(mvc, "/jump");
		helper.checkRedirect(mvc, "/jump?id=", "/**");
		helper.checkRedirect(mvc, "/jump?id=100", "/?page=*&post=*#*");
	}

	@Test
	public void testSearch() throws Exception {
		helper.validateHtmlUtf8(mvc, "/search", "!DOCTYPE");
	}

	@Test
	public void testLanguage() throws Exception {
		helper.checkRedirectAndCookie(mvc, "/language", "/**", "lang", "ru");
		helper.checkRedirectAndCookie(mvc, "/language?tag=en", "/**",
			"lang", "ru");
	}

	@Test
	public void testUsers() throws Exception {
		helper.validateHtmlUtf8(mvc, "/users", "!DOCTYPE");
	}

	@Test
	public void testHelp() throws Exception {
		helper.validateHtmlUtf8(mvc, "/help", "!DOCTYPE");
	}
}
