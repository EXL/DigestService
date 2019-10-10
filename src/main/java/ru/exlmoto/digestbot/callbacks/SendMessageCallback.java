package ru.exlmoto.digestbot.callbacks;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;

import ru.exlmoto.digestbot.DigestBot;

import java.io.Serializable;

public class SendMessageCallback<T extends Serializable> implements SentCallback<T> {
	private final DigestBot mDigestBot;

	public SendMessageCallback(final DigestBot aDigestBot) {
		mDigestBot = aDigestBot;
	}

	@Override
	public void onResult(final BotApiMethod<T> aMethod, final T aResponse) {
		// mDigestBot.loge(String.format("Message sent: '%s'.", aResponse.toString()));
	}

	@Override
	public void onError(final BotApiMethod<T> aMethod, final TelegramApiRequestException aApiException) {
		mDigestBot.loge(String.format("Error sending message: '%s'.", aApiException.toString()));
	}

	@Override
	public void onException(final BotApiMethod<T> aMethod, final Exception aException) {
		mDigestBot.loge(String.format("Exception on sending message: '%s'.", aException.toString()));
	}
}
