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

package ru.exlmoto.digest.site.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import ru.exlmoto.digest.service.impl.UserDetailsServiceImpl;

@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
	private final UserDetailsServiceImpl userDetailsService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public WebSecurityConfiguration(UserDetailsServiceImpl userDetailsService) {
		this.userDetailsService = userDetailsService;
		this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers("/obey/**")
				.authenticated()
			.anyRequest()
				.permitAll()
			.and()
				.formLogin()
				.loginPage("/ds-auth-login")
				.defaultSuccessUrl("/obey")
				.failureForwardUrl("/ds-auth-login?error=true")
			.and()
				.logout()
				.logoutUrl("/ds-auth-logout")
				.logoutSuccessUrl("/")
				.clearAuthentication(true)
				.invalidateHttpSession(true)
				.deleteCookies("JSESSIONID");
	}
}
