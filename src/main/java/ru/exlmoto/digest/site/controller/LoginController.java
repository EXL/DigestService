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
