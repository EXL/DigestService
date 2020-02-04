package ru.exlmoto.digest.bot.ability.message.impl;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.message.MessageAbility;
import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.bot.worker.AvatarWorker;
import ru.exlmoto.digest.bot.worker.DigestWorker;
import ru.exlmoto.digest.entity.BotDigestEntity;
import ru.exlmoto.digest.entity.BotDigestUserEntity;
import ru.exlmoto.digest.repository.BotDigestRepository;
import ru.exlmoto.digest.repository.BotDigestUserRepository;
import ru.exlmoto.digest.util.i18n.LocaleHelper;
import ru.exlmoto.digest.util.filter.FilterHelper;

@Component
public class DigestHashTag extends MessageAbility {
	private final Logger log = LoggerFactory.getLogger(DigestHashTag.class);

	private final BotConfiguration config;
	private final FilterHelper filter;
	private final BotDigestRepository digestRepository;
	private final BotDigestUserRepository digestUserRepository;
	private final AvatarWorker avatarWorker;
	private final DigestWorker digestWorker;

	public DigestHashTag(BotConfiguration config,
	                     FilterHelper filter,
	                     BotDigestRepository digestRepository,
	                     BotDigestUserRepository digestUserRepository,
	                     AvatarWorker avatarWorker,
	                     DigestWorker digestWorker) {
		this.config = config;
		this.filter = filter;
		this.digestRepository = digestRepository;
		this.digestUserRepository = digestUserRepository;
		this.avatarWorker = avatarWorker;
		this.digestWorker = digestWorker;
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocaleHelper locale, Message message) {
		long chatId = message.chat().id();
		int messageId = message.messageId();
		User user = message.from();
		long userId = user.id();

		String messageText = isolateMessageText(message.text());
		if (!messageText.isEmpty()) {
			if (messageText.length() <= config.getMaxDigestLength()) {
				sender.replySimple(chatId, messageId,
					locale.i18nRU("bot.hashtag.digest.ok", helper.getValidUsername(user)));

				try {
					BotDigestUserEntity digestUserEntity =
						digestUserRepository.findById(userId).orElseGet(() -> new BotDigestUserEntity(userId));
					digestUserEntity.setAvatar(avatarWorker.getAvatarLink(user));
					digestUserEntity.setUsername(helper.getValidUsername(user));
					digestUserRepository.save(digestUserEntity);

					digestRepository.save(new BotDigestEntity(chatId,
						message.date(), messageId, messageText, digestUserEntity));
				} catch (DataAccessException dae) {
					log.error("Cannot save digest entity to database.", dae);
				}

				if (chatId == config.getMotofanChatId()) {
					digestWorker.sendDigestToSubscribers(sender, message, messageText);
				}
			} else {
				sender.replySimple(chatId, messageId, locale.i18n("bot.hashtag.digest.error.length"));
			}
		} else {
			sender.replySimple(chatId, messageId, locale.i18n("bot.hashtag.digest.error.empty"));
		}
	}

	protected String isolateMessageText(String message) {
		return filter.removeHtmlTags(message.replaceAll("#digest|#news", "")).trim();
	}
}
