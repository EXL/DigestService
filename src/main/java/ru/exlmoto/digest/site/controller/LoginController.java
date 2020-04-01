package ru.exlmoto.digest.site.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

	/* TODO: Check this shit */
	@RequestMapping(path = "/ds-auth-logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
			securityContextLogoutHandler.setInvalidateHttpSession(true);
			securityContextLogoutHandler.setClearAuthentication(true);
			securityContextLogoutHandler.logout(request, response, auth);
		}

		return "redirect:/";
	}

	/*
	@RequestMapping(path = "/ds-auth-logout")
	public String dsAuthLogout(HttpServletRequest request, HttpServletResponse response) {

		if (error != null) {
			model.addAttribute("error", "error!");
		}

		return "login";
	}

	@RequestMapping(value="/logout", method = RequestMethod.GET)
	public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null){
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		return "redirect:/login?logout=true";
	}
	 */
}
