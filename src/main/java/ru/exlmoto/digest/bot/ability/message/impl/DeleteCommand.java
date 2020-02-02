package ru.exlmoto.digest.bot.ability.message.impl;

import com.pengrad.telegrambot.model.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import ru.exlmoto.digest.bot.ability.message.MessageAdminAbility;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.repository.BotDigestRepository;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

@Component
public class DeleteCommand extends MessageAdminAbility {
	private final Logger log = LoggerFactory.getLogger(DeleteCommand.class);

	private final BotDigestRepository digestRepository;

	public DeleteCommand(BotDigestRepository digestRepository) {
		this.digestRepository = digestRepository;
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocalizationHelper locale, Message message) {
		String[] args = message.text().split(" ");
		String text = locale.i18n("bot.error.delete.format");

		if (args.length == 2) {
			long digestId = 0;
			try {
				digestId = NumberUtils.parseNumber(args[1], Long.class);
				digestRepository.deleteById(digestId);
				text = locale.i18n("bot.command.delete.ok");
			} catch (NumberFormatException nfe) {
				log.warn(String.format("Cannot parse delete command argument: '%s' as Long.", args[1]), nfe);
				text = String.format(locale.i18n("bot.error.delete.id"), args[1]);
			} catch (DataAccessException dae) {
				log.error("Cannot delete BotDigestEntity object from database.", dae);
				text = String.format(locale.i18n("bot.error.delete.entry"), digestId, dae.getLocalizedMessage());
			}
		}

		sender.replyMessage(message.chat().id(), message.messageId(), text);
	}
}
