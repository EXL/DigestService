/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL <exlmotodev@gmail.com>
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
