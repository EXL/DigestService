package ru.exlmoto.digest.bot;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@Service
public class BotService extends TelegramLongPollingBot {
	@Value("${bot.username}")
	private String botUsername;

	@Value("${bot.token}")
	private String botToken;

	@Value("${bot.log-updates}")
	private boolean botLogUpdates;

	@Override
	public void onUpdateReceived(Update update) {
		if (botLogUpdates) {
			log.info(update.toString());
		}
		Message message = checkMessage(update);
		if (message != null) {
			SendMessage sendMessage = new SendMessage();
			sendMessage.setChatId(message.getChatId());
			sendMessage.setText(message.getText());
			try {
				execute(sendMessage);
			} catch (TelegramApiException tae) {
				log.error(String.format("Cannot send message into chat '%s'.", message.getChatId()), tae);
			}
		}
	}

	private Message checkMessage(Update update) {
		return update.hasEditedMessage() ? update.getEditedMessage() : update.hasMessage() ? update.getMessage() : null;
	}

	@Override
	public void onUpdatesReceived(List<Update> updates) {
		// super;
		super.onUpdatesReceived(updates);
	}

	@Override
	public String getBotUsername() {
		return botUsername;
	}

	@Override
	public String getBotToken() {
		return botToken;
	}
}
