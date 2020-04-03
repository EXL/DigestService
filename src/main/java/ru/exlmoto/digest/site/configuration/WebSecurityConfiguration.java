package ru.exlmoto.digest.site.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.exlmoto.digest.site.test.AdministratorAuthenticationFilter;
import ru.exlmoto.digest.site.test.AdministratorDetailService;

@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
	private final AdministratorDetailService administratorDetailService;

	public WebSecurityConfiguration(AdministratorDetailService administratorDetailService) {
		this.administratorDetailService = administratorDetailService;
	}

	/*
	@Bean
	@Override
	protected UserDetailsService userDetailsService() {
		UserDetails user = User.withDefaultPasswordEncoder()
			.username("u")
			.password("p")
			.roles("ADMIN")
			.build();
		return new InMemoryUserDetailsManager(user);
	}*/

	public AdministratorAuthenticationFilter authenticationFilter() throws Exception {
		AdministratorAuthenticationFilter filter = new AdministratorAuthenticationFilter();
		filter.setAuthenticationManager(authenticationManagerBean());
		filter.setAuthenticationFailureHandler(failureHandler()); //???
		return filter;
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authProvider());
	}

	public AuthenticationProvider authProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(administratorDetailService);
		// provider.setPasswordEncoder(passwordEncoder()); //???
		return provider;
	}

	public SimpleUrlAuthenticationFailureHandler failureHandler() {
		return new SimpleUrlAuthenticationFailureHandler("/ds-auth-login?error=true");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.addFilterBefore(authenticationFilter(), AdministratorAuthenticationFilter.class)
			.authorizeRequests()
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

		/*
		http
			.authorizeRequests()
			.antMatchers("/", "/home").permitAll()
			.anyRequest().authenticated()
			.and()
			.formLogin()
			.loginPage("/login")
			.permitAll()
			.and()
			.logout()
			.permitAll();*/
	}
/*
	public SimpleAuthenticationFilter authenticationFilter() throws Exception {
		SimpleAuthenticationFilter filter = new SimpleAuthenticationFilter();
		filter.setAuthenticationManager(authenticationManagerBean());
		filter.setAuthenticationFailureHandler(failureHandler());
		return filter;
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authProvider());
	}


	public AuthenticationProvider authProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/*
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
			.antMatchers(
				"/",
				"/search/**", "/jump/**",
				"/icon/**", "/image/**", "/style/**").permitAll()
			.anyRequest().authenticated()
			.and()
			.formLogin()
			.loginPage("/ds-auth-login")
			.permitAll()
			.and()
			.logout()
			.permitAll();
	}
	 */
}
