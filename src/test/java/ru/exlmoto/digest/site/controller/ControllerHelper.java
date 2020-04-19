package ru.exlmoto.digest.site.controller;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import ru.exlmoto.digest.util.Role;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.endsWith;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

public class ControllerHelper {
	public void checkPlainText(MockMvc mvc, String path, String contains) throws Exception {
		mvc.perform(get(path).contentType(MediaType.TEXT_PLAIN))
			.andDo(print())
			.andExpect(header().string("content-type", containsString("text/plain")))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString(contains)));
	}

	public void validateJson(MockMvc mvc, String path) throws Exception {
		validateJsonAux(mvc, path, "application/json");
	}

	public void validateJsonUtf8(MockMvc mvc, String path) throws Exception {
		validateJsonAux(mvc, path, "application/json;charset=UTF-8");
	}

	public void validateHtml(MockMvc mvc, String path, String contains) throws Exception {
		validateHtmlAux(mvc, path, "text/html", contains);
	}

	public void checkRedirect(MockMvc mvc, String path, String redirectPath) throws Exception {
		mvc.perform(get(path).contentType(MediaType.TEXT_HTML))
			.andExpect(redirectedUrl(redirectPath))
			.andExpect(status().isFound());
	}

	public void validateHtmlUtf8(MockMvc mvc, String path, String contains) throws Exception {
		validateHtmlAux(mvc, path, "text/html;charset=UTF-8", contains);
	}

	public void checkUnauthorized(MockMvc mvc, String path) throws Exception {
		mvc.perform(post(path).contentType(MediaType.TEXT_HTML)
			.param("username", "user")
			.param("password", "wrong-password")
			.with(unauthorizedUser()))
				.andExpect(status().isForbidden());
	}

	public void checkAuthorizedWithoutCsrf(MockMvc mvc, String path) throws Exception {
		mvc.perform(post(path).contentType(MediaType.TEXT_HTML)
			.param("username", "admin")
			.param("password", "password")
			.with(authorizedUser()))
				.andExpect(status().isForbidden());
	}

	public void checkAuthorizedWithCsrf(MockMvc mvc, String path) throws Exception {
		mvc.perform(post(path).contentType(MediaType.TEXT_HTML)
			.param("username", "admin")
			.param("password", "password")
			.with(authorizedUser())
			.with(csrf()))
			.andExpect(status().isCreated());
	}

	public void checkAuthorizedWithCsrfRedirect(MockMvc mvc, String path, String redirectPath) throws Exception {
		mvc.perform(post(path).contentType(MediaType.TEXT_HTML)
			.param("username", "admin")
			.param("password", "password")
			.with(authorizedUser())
			.with(csrf()))
			.andExpect(redirectedUrl(redirectPath))
			.andExpect(status().isFound());
	}

	private void validateHtmlAux(MockMvc mvc, String path, String produces, String contains) throws Exception {
		mvc.perform(get(path).contentType(MediaType.TEXT_HTML))
			.andDo(print())
			.andExpect(header().string("content-type", containsString(produces)))
			.andExpect(status().isOk())
			.andExpect(content().string(startsWith("<!")))
			.andExpect(content().string(endsWith(">\n")))
			.andExpect(content().string(containsString(contains)));
	}

	private void validateJsonAux(MockMvc mvc, String path, String produces) throws Exception {
		mvc.perform(get(path).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(header().string("content-type", containsString(produces)))
			.andExpect(content().string(startsWith("{")))
			.andExpect(content().string(endsWith("}")));
	}

	private RequestPostProcessor unauthorizedUser() {
		return user("user").password("wrong-password").roles(Role.Administrator.name());
	}

	private RequestPostProcessor authorizedUser() {
		return user("admin").password("password").roles(Role.Owner.name());
	}
}
