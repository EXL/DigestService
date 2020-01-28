package ru.exlmoto.digest.bot.util;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Chat.Type;
import com.pengrad.telegrambot.model.User;

import org.springframework.stereotype.Component;

import org.thymeleaf.util.ArrayUtils;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;

@Component
public class BotHelper {
	private final BotConfiguration config;

	public BotHelper(BotConfiguration config) {
		this.config = config;
	}

	public long getCurrentUnixTime() {
		return System.currentTimeMillis() / 1000L;
	}

	public String getValidUsername(User user) {
		String username = user.username();
		String lastName = user.lastName();
		String firstName = user.firstName();
		return (username != null) ? "@" + username : (lastName != null) ? firstName + " " + lastName : firstName;
	}

	public boolean isUserAdmin(String username) {
		return username != null && ArrayUtils.contains(config.getAdmins(), username);
	}

	public String getValidChatName(Chat chat, String botUsername) {
		return (chat.type().equals(Type.Private)) ? botUsername : chat.title();
	}
}
