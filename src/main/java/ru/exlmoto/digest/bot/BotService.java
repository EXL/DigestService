package ru.exlmoto.digest.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import ru.exlmoto.digest.bot.config.BotConfiguration;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class BotService extends TelegramLongPollingBot {
	private final BotConfiguration config;

	@Override
	public void onUpdateReceived(Update update) {
		if (config.isLogUpdates()) {
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
	}

	@Override
	public void onUpdatesReceived(List<Update> updates) {
		int updatesCount = updates.size();
		int maxUpdates = config.getMaxUpdates();
		if (updatesCount > maxUpdates) {
			updates.subList(0, updatesCount - maxUpdates).clear();
		}
		super.onUpdatesReceived(updates);
	}

	@Override
	public String getBotUsername() {
		return config.getUsername();
	}

	@Override
	public String getBotToken() {
		return config.getToken();
	}

	private Message checkMessage(Update update) {
		return update.hasEditedMessage() ? update.getEditedMessage() : update.hasMessage() ? update.getMessage() : null;
	}

	private boolean checkNewUsers(Message message) {
		return !ObjectUtils.isEmpty(message.getNewChatMembers());
	}

	private boolean checkNewChatPhoto(Message message) {
		return !ObjectUtils.isEmpty(message.getNewChatPhoto());
	}

	private boolean checkOnHashTag(Message message) {
		return !ObjectUtils.isEmpty(message.getEntities());
	}

	private boolean checkLeftUser(Message message) {
		return message.getLeftChatMember() != null;
	}
}
