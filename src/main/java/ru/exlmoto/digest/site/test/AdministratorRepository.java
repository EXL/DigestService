package ru.exlmoto.digest.site.test;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

@Repository
public class AdministratorRepository {
	public Administrator findUser(String username, int key) {
		System.out.println("findUser");
		if (StringUtils.isEmpty(username)) {
			return null;
		} else {
			return new Administrator(username, "pass",
				key,
				true, true, true, true,
				new ArrayList<>());
		}
	}
}
