package ru.exlmoto.digest.service.impl;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import ru.exlmoto.digest.entity.MemberEntity;
import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.site.configuration.SiteConfiguration;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	private final DatabaseService databaseService;
	private final SiteConfiguration config;

	public UserDetailsServiceImpl(DatabaseService databaseService, SiteConfiguration config) {
		this.databaseService = databaseService;
		this.config = config;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (config.isObeyProtection()) {
			MemberEntity member = databaseService.getMember(username);
			if (member == null) {
				throw new UsernameNotFoundException(username);
			}
			return new User(
				member.getUsername(),
				member.getPassword(),
				member.isEnable(),
				true,
				true,
				true,
				Collections.singleton(new SimpleGrantedAuthority(member.getRole().name())));
		} else {
			return new User(
				"Administrator",
				new BCryptPasswordEncoder().encode(config.getObeyDebugPassword()),
				Collections.singleton(new SimpleGrantedAuthority(config.getObeyDebugRole()))
			);
		}
	}
}
