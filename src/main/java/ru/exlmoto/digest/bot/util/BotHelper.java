package ru.exlmoto.digest.bot.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.meta.api.objects.User;

import org.thymeleaf.util.ArrayUtils;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

@Component
public class BotHelper {
	private final BotConfiguration config;
	private final LocalizationHelper locale;
	private BotSender sender;

	public BotHelper(BotConfiguration config, LocalizationHelper locale) {
		this.config = config;
		this.locale = locale;
	}

	@Autowired
	public void setBotSender(BotSender sender) {
		this.sender = sender;
	}

	public BotSender getSender() {
		return sender;
	}

	public LocalizationHelper getLocale() {
		return locale;
	}

	public String getValidUsername(User user) {
		return (user.getUserName() != null) ? "@" + user.getUserName() :
			(user.getLastName() != null) ? user.getFirstName() + " " + user.getLastName() :
				user.getFirstName();
	}

	public long getCurrentUnixTime() {
		return System.currentTimeMillis() / 1000L;
	}

	public boolean isUserAdmin(String username) {
		return username != null && ArrayUtils.contains(config.getAdmins(), username);
	}
}
