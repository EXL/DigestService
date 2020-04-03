package ru.exlmoto.digest.site.test;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service("userDe")
public class AdministratorDetailService implements UserDetailsService {
	private final AdministratorRepository repository;

	public AdministratorDetailService(AdministratorRepository repository) {
		this.repository = repository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, NumberFormatException {
		System.out.println("loadUserByUsername");

		String[] usernameAndKey = StringUtils.split(username, String.valueOf(Character.LINE_SEPARATOR));
		if (usernameAndKey == null || usernameAndKey.length != 2) {
			throw new UsernameNotFoundException("Username and key must be provided");
		}

		String clearUsername = usernameAndKey[0];
		int clearKey = Integer.parseInt(usernameAndKey[1]);

		Administrator administrator = repository.findUser(clearUsername, clearKey);
		if (administrator == null) {
			throw new UsernameNotFoundException(String.format("Username '%s' with key '%d' not found.",
				clearUsername, clearKey));
		}
		return administrator;
	}
}
