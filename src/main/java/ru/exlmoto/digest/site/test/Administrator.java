package ru.exlmoto.digest.site.test;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class Administrator extends User {
	private int key;

	public Administrator(String username, String password,
	                     int key,
	                     boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked,
	                     Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		System.out.println("Administrator");
		this.key = key;
	}

	public int getKey() {
		return key;
	}
}
