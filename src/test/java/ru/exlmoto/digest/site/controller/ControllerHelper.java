/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.exlmoto.digest.site.controller;

import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
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

	public void validateHtmlUtf8(MockMvc mvc, String path, String contains) throws Exception {
		validateHtmlAux(mvc, path, "text/html;charset=UTF-8", contains);
	}

	public void checkRedirect(MockMvc mvc, String path, String redirectPattern) throws Exception {
		mvc.perform(get(path).contentType(MediaType.TEXT_HTML))
			.andDo(print())
			.andExpect(redirectedUrlPattern(redirectPattern))
			.andExpect(status().isFound());
	}

	public void checkRedirectAndCookie(MockMvc mvc, String path, String redirectPattern,
	                                   String cookieName, String cookieValue) throws Exception {
		mvc.perform(get(path).contentType(MediaType.TEXT_HTML))
			.andExpect(cookie().value(cookieName, cookieValue))
			.andDo(print())
			.andExpect(redirectedUrlPattern(redirectPattern))
			.andExpect(status().isFound());
	}

	public void checkError4xx(MockMvc mvc, String path) throws Exception {
		mvc.perform(get(path).contentType(MediaType.TEXT_HTML))
			.andDo(print())
			.andExpect(status().is4xxClientError());
	}

	public void checkUnauthorized(MockMvc mvc, String path) throws Exception {
		mvc.perform(post(path).contentType(MediaType.TEXT_HTML)
			.param("username", "user")
			.param("password", "wrong-password")
			.with(unauthorizedUser()))
				.andDo(print())
				.andExpect(status().isForbidden());
	}

	public void checkAuthorizedWithoutCsrf(MockMvc mvc, String path) throws Exception {
		mvc.perform(post(path).contentType(MediaType.TEXT_HTML)
			.param("username", "admin")
			.param("password", "password")
			.with(authorizedUser()))
				.andDo(print())
				.andExpect(status().isForbidden());
	}

	public void checkAuthorizedWithCsrf(MockMvc mvc, String path) throws Exception {
		mvc.perform(post(path).contentType(MediaType.TEXT_HTML)
			.param("username", "admin")
			.param("password", "password")
			.with(authorizedUser())
			.with(csrf()))
				.andDo(print())
				.andExpect(status().isCreated());
	}

	public void checkAuthorizedWithCsrfRedirect(MockMvc mvc, String path, String redirectPattern) throws Exception {
		checkAuthorizedWithCsrfRedirectParam(mvc, path, redirectPattern,
			"username", "admin", "password", "password");
	}

	public void checkAuthorizedWithCsrfRedirectParam(MockMvc mvc, String path, String redirectPattern,
	                                                 String paramFirst, String valueFirst,
	                                                 String paramSecond, String valueSecond) throws Exception {
		authorizedAux(mvc, path, redirectPattern, paramFirst, valueFirst, paramSecond, valueSecond, authorizedUser());
	}

	public void checkAuthorizedWithCsrfRedirectWrongRole(MockMvc mvc, String path,
	                                                     String redirectPattern) throws Exception {
		authorizedAux(mvc, path, redirectPattern,
			"username", "admin", "password", "password",
			authorizedUserWrongRole());
	}

	private void authorizedAux(MockMvc mvc, String path, String redirectPattern,
	                           String paramFirst, String valueFirst, String paramSecond, String valueSecond,
	                           RequestPostProcessor user) throws Exception {
		mvc.perform(post(path).contentType(MediaType.TEXT_HTML)
			.param(paramFirst, valueFirst)
			.param(paramSecond, valueSecond)
			.with(user)
			.with(csrf()))
				.andDo(print())
				.andExpect(redirectedUrlPattern(redirectPattern))
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
		return user("user").password("wrong-password")
			.authorities(new SimpleGrantedAuthority(Role.Administrator.name()));
	}

	private RequestPostProcessor authorizedUser() {
		return user("admin").password("password")
			.authorities(new SimpleGrantedAuthority(Role.Owner.name()));
	}

	private RequestPostProcessor authorizedUserWrongRole() {
		return user("admin").password("password")
			.authorities(new SimpleGrantedAuthority(Role.Administrator.name()));
	}
}
