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

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Optional;

@Controller
public class LoginController {
	@RequestMapping(path = "/ds-auth-login")
	public String dsAuthLogin(@RequestParam(value = "error", required = false) String error,
	                          Model model) {
		if (error != null) {
			model.addAttribute("error", error);
		}

		return "login";
	}

	@RequestMapping(path = "/ds-auth-logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		Optional.of(SecurityContextHolder.getContext().getAuthentication()).ifPresent(auth -> {
			SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
			securityContextLogoutHandler.setInvalidateHttpSession(true);
			securityContextLogoutHandler.setClearAuthentication(true);
			securityContextLogoutHandler.logout(request, response, auth);

			clearCookieSession(request, response);
		});

		return "redirect:/";
	}

	/*
	 * Source: https://stackoverflow.com/a/41135587
	 */
	private void clearCookieSession(HttpServletRequest request, HttpServletResponse response) {
		Cookie cookieWithSlash = new Cookie("JSESSIONID", null);
		// Tomcat adds extra slash at the end of context path (e.g. "/foo/").
		cookieWithSlash.setPath(request.getContextPath() + "/");
		cookieWithSlash.setMaxAge(0);

		Cookie cookieWithoutSlash = new Cookie("JSESSIONID", null);
		// JBoss doesn't add extra slash at the end of context path (e.g. "/foo").
		cookieWithoutSlash.setPath(request.getContextPath());
		cookieWithoutSlash.setMaxAge(0);

		// Remove cookies on logout so that invalidSessionURL (session timeout) is not displayed on proper logout event.
		response.addCookie(cookieWithSlash); // For Tomcat.
		response.addCookie(cookieWithoutSlash); // For JBoss.
	}
}
