package ru.exlmoto.digest.bot;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import ru.exlmoto.digest.bot.config.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;

import java.util.List;

@Slf4j
@Service
public class BotService extends TelegramLongPollingBot {
	private final BotConfiguration config;
	private BotSender sender;

	public BotService(BotConfiguration config) {
		this.config = config;
	}

	@Autowired
	public void setBotSender(BotSender sender) {
		this.sender = sender;
	}

	@Override
	public void onUpdatesReceived(List<Update> updates) {
		int updatesCount = updates.size();
		int maxUpdates = config.getMaxUpdates();
		if (updatesCount > maxUpdates) {
			log.info(String.format("Too many '%d' updates received, shrink updates array to '%d'.",
				updatesCount, maxUpdates));
			updates.subList(0, updatesCount - maxUpdates).clear();
		}
		super.onUpdatesReceived(updates);
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (config.isDebugLogUpdates()) {
			log.info(update.toString());
		}
		Message message = checkMessage(update);
		if (message != null) {
			handleMessage(message);
		} else if (update.hasCallbackQuery()) {
			// TODO: CallbackQuery
			// mCallbackQueryHandler.handle(this, aUpdate.getCallbackQuery());
		}
	}

	private void handleMessage(Message message) {
		if (message.isCommand()) {
			// onCommand(message);
		} else if (checkNewUsers(message)) {
			// onNewUsers(aMessage);
		} else if (checkLeftUser(message)) {
			// onLeftUser(aMessage);
		} else if (checkNewChatPhoto(message)) {
			// onNewChatPhoto(aMessage);
		} else if (checkOnHashTag(message)) {
			// onHashTag(aMessage);
		}

		sender.replyMarkdownMessage(message.getChatId(), message.getMessageId(), message.getText());
	}

	@Override
	public String getBotUsername() {
		return config.getUsername();
	}

	@Override
	public String getBotToken() {
		return config.getToken();
	}

	public Message checkMessage(Update update) {
		return update.hasEditedMessage() ? update.getEditedMessage() : update.hasMessage() ? update.getMessage() : null;
	}

	public boolean checkNewUsers(Message message) {
		return !ObjectUtils.isEmpty(message.getNewChatMembers());
	}

	public boolean checkNewChatPhoto(Message message) {
		return !ObjectUtils.isEmpty(message.getNewChatPhoto());
	}

	public boolean checkOnHashTag(Message message) {
		return !ObjectUtils.isEmpty(message.getEntities());
	}

	public boolean checkLeftUser(Message message) {
		return message.getLeftChatMember() != null;
	}
}
