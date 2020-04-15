package ru.exlmoto.digest.service.impl;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ru.exlmoto.digest.entity.MemberEntity;
import ru.exlmoto.digest.service.DatabaseService;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	private final DatabaseService databaseService;

	public UserDetailsServiceImpl(DatabaseService databaseService) {
		this.databaseService = databaseService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		MemberEntity member = databaseService.getMember(username);
		if (member == null) {
			throw new UsernameNotFoundException(username);
		}

		Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
		grantedAuthorities.add(new SimpleGrantedAuthority(member.getRole().name()));

		return new User(member.getUsername(), member.getPassword(), member.isEnable(),
			true, true, true,
			grantedAuthorities);
	}
}
