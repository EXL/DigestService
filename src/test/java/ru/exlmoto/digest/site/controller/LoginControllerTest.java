package ru.exlmoto.digest.site.controller;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "site.obey-protection=false")
@AutoConfigureMockMvc
class LoginControllerTest {
	@Autowired
	private MockMvc mvc;

	private final ControllerHelper helper = new ControllerHelper();

	@Test
	public void testDsAuthLogin() throws Exception {
		helper.validateHtmlUtf8(mvc, "/ds-auth-login", "!DOCTYPE");
	}

	@Test
	public void testDsAuthLoginAuthorize() throws Exception {
		helper.checkUnauthorized(mvc, "/ds-auth-login");
		helper.checkAuthorizedWithoutCsrf(mvc, "/ds-auth-login");
		// helper.checkAuthorizedWithCsrf(mvc, "/ds-auth-login");
		helper.checkAuthorizedWithCsrfRedirect(mvc, "/ds-auth-login", "/**/obey");
	}

	@Test
	public void testLogout() throws Exception {
		helper.checkRedirect(mvc, "/ds-auth-logout", "/**");
	}
}
