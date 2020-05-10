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
				Collections.singleton(new SimpleGrantedAuthority(member.getRole().name()))
			);
		} else {
			return new User(
				"Administrator",
				new BCryptPasswordEncoder().encode(config.getObeyDebugPassword()),
				Collections.singleton(new SimpleGrantedAuthority(config.getObeyDebugRole()))
			);
		}
	}
}
