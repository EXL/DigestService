package ru.exlmoto.digest.bot.ability.message.impl;

import com.pengrad.telegrambot.model.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import ru.exlmoto.digest.bot.ability.message.MessageAdminAbility;
import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.entity.BotDigestEntity;
import ru.exlmoto.digest.repository.BotDigestRepository;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

@Component
public class ShowCommand extends MessageAdminAbility {
	private final Logger log = LoggerFactory.getLogger(ShowCommand.class);

	private final BotConfiguration config;
	private final BotDigestRepository digestRepository;

	public ShowCommand(BotConfiguration config, BotDigestRepository digestRepository) {
		this.config = config;
		this.digestRepository = digestRepository;
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocalizationHelper locale, Message message) {
		String[] args = message.text().split(" ");
		String text = locale.i18n("bot.error.show.empty");
		int page = 0;
		if (args.length == 2) {
			page = getPageFromArgument(args[1]);
		}

		try {
			Page<BotDigestEntity> digestEntities = digestRepository.findAll(PageRequest.of(page,
				config.getShowPagePosts(), Sort.by(Sort.Order.desc("id"))));
			if (!digestEntities.isEmpty()) {
				text = generateShowReport(digestEntities, locale);
			}
		} catch (DataAccessException dae) {
			log.error("Cannot get BotDigestEntity objects from database.", dae);
			text = String.format(locale.i18n("bot.error.database"), dae.getLocalizedMessage());
		}

		sender.replyMessage(message.chat().id(), message.messageId(), text);
	}

	protected String generateShowReport(Page<BotDigestEntity> digestEntities, LocalizationHelper locale) {
		final int CHOP_NUMBER = 5;
		final int CHOP_USER = 7;
		final int CHOP_DIGEST = 25;
		String ellipsis = locale.i18n("bot.command.show.ellipsis");

		StringBuilder stringBuilder =
			new StringBuilder(String.format(locale.i18n("bot.command.show.header"),
				digestEntities.getTotalPages()));
		stringBuilder.append("\n\n");
		stringBuilder.append("```\n");
		stringBuilder.append(arrangeString("id", CHOP_NUMBER)).append(" ");
		stringBuilder.append(arrangeString("user", CHOP_USER)).append(" ");
		stringBuilder.append(arrangeString("chat", CHOP_NUMBER)).append(" ");
		stringBuilder.append("post").append("\n");
		stringBuilder.append("---------------------------------------------\n");
		for (BotDigestEntity entity : digestEntities) {
			stringBuilder.append(ellipsisString(String.valueOf(entity.getId()),
				CHOP_NUMBER, ellipsis, false)).append(" ");
			stringBuilder.append(ellipsisString(entity.getUser().getUsername(),
				CHOP_USER, ellipsis, true)).append(" ");
			stringBuilder.append(ellipsisString(String.valueOf(entity.getChat()),
				CHOP_NUMBER, ellipsis, false)).append(" ");
			stringBuilder.append(ellipsisString(entity.getDigest(),
				CHOP_DIGEST, ellipsis, true)).append("\n");
		}
		stringBuilder.append("\n```");
		return stringBuilder.toString();
	}

	protected int getPageFromArgument(String argument) {
		int page = 0;
		try {
			page = NumberUtils.parseNumber(argument, Integer.class) - 1;
		} catch (NumberFormatException nfe) {
			log.warn(String.format("Cannot parse show command argument: '%s' as Integer.", argument), nfe);
		}
		return Math.max(page, 0);
	}

	protected String arrangeString(String string, int length) {
		int stringLength = string.length();
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < length - stringLength; ++i) {
			stringBuilder.append(' ');
		}
		return string + stringBuilder.toString();
	}

	protected String ellipsisString(String string, int length, String ellipsis, boolean right) {
		if (length > 0) {
			int stringLength = string.length();
			if (stringLength < length) {
				return arrangeString(string, length);
			}
			return (right) ?
				string.substring(0, length - 1) + ellipsis :
				ellipsis + string.substring(stringLength - length + 1);
		}
		return string;
	}
}
