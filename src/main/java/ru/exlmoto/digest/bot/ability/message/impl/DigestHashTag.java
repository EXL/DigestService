package ru.exlmoto.digest.bot.ability.message.impl;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.message.MessageAbility;
import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.entity.BotDigestEntity;
import ru.exlmoto.digest.entity.BotDigestUserEntity;
import ru.exlmoto.digest.repository.BotDigestRepository;
import ru.exlmoto.digest.repository.BotDigestUserRepository;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;
import ru.exlmoto.digest.util.text.FilterTextHelper;

@Component
public class DigestHashTag extends MessageAbility {
	private final BotConfiguration config;
	private final FilterTextHelper filterText;
	private final BotDigestRepository digestRepository;
	private final BotDigestUserRepository digestUserRepository;

	public DigestHashTag(BotConfiguration config,
	                     FilterTextHelper filterText,
	                     BotDigestRepository digestRepository,
	                     BotDigestUserRepository digestUserRepository) {
		this.config = config;
		this.filterText = filterText;
		this.digestRepository = digestRepository;
		this.digestUserRepository = digestUserRepository;
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocalizationHelper locale, Message message) {
		long chatId = message.chat().id();
		int messageId = message.messageId();
		User user = message.from();
		long userId = user.id();

		String messageText = isolateMessageText(message.text());
		// TODO: Check length of digest message

		if (!messageText.isEmpty()) {
			sender.replyMessage(chatId, messageId,
				locale.i18nRU("bot.hashtag.digest.ok", helper.getValidUsername(user)));

			BotDigestUserEntity digestUserEntity =
				digestUserRepository.findById(userId).orElseGet(() -> new BotDigestUserEntity(userId));
			digestUserEntity.setAvatar("avatar");
			digestUserEntity.setUsername(helper.getValidUsername(user));
			digestUserRepository.save(digestUserEntity);

			digestRepository.save(new BotDigestEntity(chatId,
				message.date(), messageId, messageText, digestUserEntity));

			if (chatId == config.getMotofanChatId()) {
				// Sends message to subs
			}
		}
	}

	protected String isolateMessageText(String message) {
		return filterText.removeHtmlTags(message.replaceAll("#digest|#news", "")).trim();
	}
}
