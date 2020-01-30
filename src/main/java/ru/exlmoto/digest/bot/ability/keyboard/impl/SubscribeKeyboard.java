package ru.exlmoto.digest.bot.ability.keyboard.impl;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Chat.Type;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.keyboard.Keyboard;
import ru.exlmoto.digest.bot.ability.keyboard.KeyboardAbility;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.entity.BotSubDigestEntity;
import ru.exlmoto.digest.entity.BotSubMotofanEntity;
import ru.exlmoto.digest.repository.BotSubDigestRepository;
import ru.exlmoto.digest.repository.BotSubMotofanRepository;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

import javax.annotation.PostConstruct;

import static ru.exlmoto.digest.util.Answer.Ok;
import static ru.exlmoto.digest.util.Answer.Error;

@Component
public class SubscribeKeyboard extends KeyboardAbility {
	private final Logger log = LoggerFactory.getLogger(SubscribeKeyboard.class);

	private enum Subscription {
		digest_subscribe,
		digest_unsubscribe,
		motofan_subscribe,
		motofan_unsubscribe
	}

	private final LocalizationHelper locale;
	private final BotSubDigestRepository digestRepository;
	private final BotSubMotofanRepository motofanRepository;
	private final BotHelper helper;

	private InlineKeyboardMarkup markup = null;

	public SubscribeKeyboard(BotSubDigestRepository digestRepository,
	                         BotSubMotofanRepository motofanRepository,
	                         BotHelper helper,
	                         LocalizationHelper locale) {
		this.digestRepository = digestRepository;
		this.motofanRepository = motofanRepository;
		this.helper = helper;
		this.locale = locale;
	}

	@PostConstruct
	private void generateKeyboard() {
		markup = new InlineKeyboardMarkup(
			new InlineKeyboardButton[] {
				new InlineKeyboardButton(locale.i18n("bot.command.subscribe.button.subscribe.motofan"))
					.callbackData(Keyboard.subscribe.withName() + Subscription.motofan_subscribe),
				new InlineKeyboardButton(locale.i18n("bot.command.subscribe.button.unsubscribe.motofan"))
					.callbackData(Keyboard.subscribe.withName() + Subscription.motofan_unsubscribe)
			},
			new InlineKeyboardButton[] {
				new InlineKeyboardButton(locale.i18n("bot.command.subscribe.button.subscribe.digest"))
					.callbackData(Keyboard.subscribe.withName() + Subscription.digest_subscribe),
				new InlineKeyboardButton(locale.i18n("bot.command.subscribe.button.unsubscribe.digest"))
					.callbackData(Keyboard.subscribe.withName() + Subscription.digest_unsubscribe)
			}
		);
	}

	@Override
	public InlineKeyboardMarkup getMarkup() {
		return markup;
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocalizationHelper locale, CallbackQuery callback) {
		Message message = callback.message();
		int messageId = message.messageId();
		Chat chat = message.chat();
		long chatId = chat.id();

		String callbackId = callback.id();
		String key = Keyboard.chopKeyboardNameLeft(callback.data());

		if (!chat.type().equals(Type.Private)) {
			if (helper.isUserAdmin(callback.from().username())) {
				handleSubscription(chatId, messageId, chat, callbackId, sender, checkSubscription(key));
			} else {
				sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.error.subscribe.admin"));
			}
		} else {
			handleSubscription(chatId, messageId, chat, callbackId, sender, checkSubscription(key));
		}
	}

	public void processSubscribeStatusMessage(long chatId, int messageId, Chat chat, boolean edit, BotSender sender) {
		Answer<String> res = generateSubscribeStatusMessage(chat);
		String answer = res.ok() ? res.answer() : res.error();
		InlineKeyboardMarkup markup = res.ok() ? getMarkup() : null;
		if (edit) {
			sender.editMessage(chatId, messageId, answer, markup);
		} else {
			sender.replyKeyboard(chatId, messageId, answer, markup);
		}
	}

	private Answer<String> generateSubscribeStatusMessage(Chat chat) {
		long chatId = chat.id();
		String chatTitle = helper.getValidChatName(chat);
		try {
			return Ok(String.format(locale.i18n("bot.command.subscribe"), chatTitle, chatId,
				getSubscribeStatus(motofanRepository.findBotSubMotofanEntityBySubscription(chatId) != null),
				getSubscribeStatus(digestRepository.findBotSubDigestEntityBySubscription(chatId) != null)));
		} catch (DataAccessException dae) {
			log.error("Cannot get subscribe object from database.", dae);
			return Error(String.format(locale.i18n("bot.error.database"), dae.getLocalizedMessage()));
		}
	}

	private String getSubscribeStatus(boolean status) {
		return (status) ?
			locale.i18n("bot.command.subscribe.subscribed") :
			locale.i18n("bot.command.subscribe.unsubscribed");
	}

	private Subscription checkSubscription(String key) {
		try {
			return Subscription.valueOf(key);
		} catch (IllegalArgumentException iae) {
			log.error(String.format("Wrong subscribe key: '%s', return default '%s'.",
				key, Subscription.motofan_subscribe), iae);
		}
		return Subscription.motofan_subscribe;
	}

	private void handleSubscription(long chatId, int messageId, Chat chat, String callbackId, BotSender sender,
	                                Subscription subscription) {
		try {
			switch (subscription) {
				case motofan_subscribe: {
					motofanSubscribe(chatId, messageId, chat, callbackId, sender);
					break;
				}
				case motofan_unsubscribe: {
					motofanUnsubscribe(chatId, messageId, chat, callbackId, sender);
					break;
				}
				case digest_subscribe: {
					digestSubscribe(chatId, messageId, chat, callbackId, sender);
					break;
				}
				case digest_unsubscribe: {
					digestUnsubscribe(chatId, messageId, chat, callbackId, sender);
					break;
				}
			}
		} catch (DataAccessException dae) {
			log.error("Cannot save or delete subscribe object from database.", dae);
			sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.error.database"));
		}
	}

	private void motofanSubscribe(long chatId, int messageId, Chat chat, String callbackId, BotSender sender) {
		if (motofanRepository.findBotSubMotofanEntityBySubscription(chatId) != null) {
			sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.error.subscribe.exist"));
		} else {
			motofanRepository.save(new BotSubMotofanEntity(chatId, helper.getValidChatName(chat)));
			sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.subscribe.subscribed"));
			processSubscribeStatusMessage(chatId, messageId, chat, true, sender);
		}
	}

	private void motofanUnsubscribe(long chatId, int messageId, Chat chat, String callbackId, BotSender sender) {
		if (motofanRepository.findBotSubMotofanEntityBySubscription(chatId) == null) {
			sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.error.unsubscribe.exist"));
		} else {
			motofanRepository.deleteBotSubMotofanEntityBySubscription(chatId);
			sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.subscribe.unsubscribed"));
			processSubscribeStatusMessage(chatId, messageId, chat, true, sender);
		}
	}

	private void digestSubscribe(long chatId, int messageId, Chat chat, String callbackId, BotSender sender) {
		if (digestRepository.findBotSubDigestEntityBySubscription(chatId) != null) {
			sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.error.subscribe.exist"));
		} else {
			digestRepository.save(new BotSubDigestEntity(chatId, helper.getValidChatName(chat)));
			sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.subscribe.subscribed"));
			processSubscribeStatusMessage(chatId, messageId, chat, true, sender);
		}
	}

	private void digestUnsubscribe(long chatId, int messageId, Chat chat, String callbackId, BotSender sender) {
		if (digestRepository.findBotSubDigestEntityBySubscription(chatId) == null) {
			sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.error.unsubscribe.exist"));
		} else {
			digestRepository.deleteBotSubDigestEntityBySubscription(chatId);
			sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.subscribe.unsubscribed"));
			processSubscribeStatusMessage(chatId, messageId, chat, true, sender);
		}
	}
}
