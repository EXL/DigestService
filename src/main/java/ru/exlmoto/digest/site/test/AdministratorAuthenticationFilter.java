package ru.exlmoto.digest.site.test;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdministratorAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
		throws AuthenticationException {

		System.out.println("attemptAuthentication!");

		if (!request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		}

		UsernamePasswordAuthenticationToken authRequest = getAuthRequest(request);
		setDetails(request, authRequest);
		return this.getAuthenticationManager().authenticate(authRequest);
	}

	private UsernamePasswordAuthenticationToken getAuthRequest(HttpServletRequest request) {
		System.out.println("getAuthRequest");


		String username = obtainUsername(request);
		String password = obtainPassword(request);
		int key = obtainKey(request);

		System.out.println(username);
		System.out.println(password);
		System.out.println(key);

		if (username == null) {
			username = "";
		}
		if (password == null) {
			password = "";
		}
		//if (domain == null) {
		//	domain = "";
		//}

		return new UsernamePasswordAuthenticationToken(
			String.format("%s%s%d", username.trim(), String.valueOf(Character.LINE_SEPARATOR), key),
			password
		);
	}

	private int obtainKey(HttpServletRequest request) {

		return 0;
		// return request.getParameter(SPRING_SECURITY_FORM_DOMAIN_KEY);
	}
}
