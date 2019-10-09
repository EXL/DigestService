package ru.exlmoto.digestbot.callbacks;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;

import ru.exlmoto.digestbot.DigestBot;

import java.io.Serializable;

public class SendMessageCallback<T extends Serializable> implements SentCallback<T> {
	private final DigestBot mDigestBot;

	public SendMessageCallback(DigestBot aDigestBot) {
		mDigestBot = aDigestBot;
	}

	@Override
	public void onResult(BotApiMethod<T> aMethod, T aResponse) {
		// mDigestBot.logi(String.format("Message sent: '%s'.", aResponse.toString()));
	}

	@Override
	public void onError(BotApiMethod<T> aMethod, TelegramApiRequestException aApiException) {
		mDigestBot.loge(String.format("Error sending message: '%s'.", aApiException.toString()));
	}

	@Override
	public void onException(BotApiMethod<T> aMethod, Exception aException) {
		mDigestBot.loge(String.format("Exception on sending message: '%s'.", aException.toString()));
	}
}
