package ru.exlmoto.digest.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.exlmoto.digest.bot.config.BotConfiguration;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotService extends TelegramLongPollingBot {
	private final BotConfiguration config;

	@Override
	public void onUpdateReceived(Update update) {
		if (config.isLogUpdates()) {
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
		return config.getUsername();
	}

	@Override
	public String getBotToken() {
		return config.getToken();
	}
}
