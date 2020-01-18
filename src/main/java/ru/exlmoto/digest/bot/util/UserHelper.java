package ru.exlmoto.digest.bot.util;

import org.springframework.stereotype.Component;

import org.telegram.telegrambots.meta.api.objects.User;

@Component
public class UserHelper {
	public String getValidUsername(User user) {
		return (user.getUserName() != null) ? "@" + user.getUserName() :
			(user.getLastName() != null) ? user.getFirstName() + " " + user.getLastName() :
				user.getFirstName();
	}
}
