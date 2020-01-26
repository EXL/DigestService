package ru.exlmoto.digest.bot.sender;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.request.SendSticker;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.response.BaseResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.telegram.BotTelegram;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

import java.io.File;

import static com.pengrad.telegrambot.model.request.ParseMode.HTML;
import static com.pengrad.telegrambot.model.request.ParseMode.Markdown;

import static ru.exlmoto.digest.util.Answer.Ok;
import static ru.exlmoto.digest.util.Answer.Error;

@Component
public class BotSender {
	private final Logger log = LoggerFactory.getLogger(BotSender.class);

	private final BotConfiguration config;
	private final BotTelegram telegram;
	private final LocalizationHelper locale;

	@Value("${image.download-file}")
	private boolean downloadFile;

	public BotSender(BotConfiguration config, BotTelegram telegram, LocalizationHelper locale) {
		this.config = config;
		this.telegram = telegram;
		this.locale = locale;
	}

	public void replyMessage(long chatId, int replyId, String text) {
		sendMessage(chatId, replyId, text, Markdown, null);
	}

	public void replyKeyboard(long chatId, int replyId, String text, InlineKeyboardMarkup keyboard) {
		sendMessage(chatId, replyId, text, Markdown, keyboard);
	}

	public void sendHtmlMessage(long chatId, String text) {
		sendMessage(chatId, null, text, HTML, null);
	}

	public void sendMessageToChat(long chatId, String text, long origChatId, int origReplyId) {
		Answer<String> res = executeRequestLog(new SendMessage(chatId, shrinkText(text)).parseMode(Markdown));
		if (!res.ok()) {
			sendError(origChatId, origReplyId,
				shrinkText(String.format(locale.i18n("bot.error.send.message"), text, chatId, res.error())));
		}
	}

	private void sendMessage(long chatId, Integer replyId, String text, ParseMode mode, InlineKeyboardMarkup keyboard) {
		SendMessage sendMessage = new SendMessage(chatId, shrinkText(text)).parseMode(mode)
			.disableWebPagePreview(downloadFile)
			.disableNotification(config.isDisableNotifications());
		if (replyId != null) {
			sendMessage.replyToMessageId(replyId);
		}
		if (keyboard != null) {
			sendMessage.replyMarkup(keyboard);
		}
		executeRequestLog(sendMessage);
	}

	public void editMessage(long chatId, int messageId, String text, InlineKeyboardMarkup keyboard) {
		executeRequestLog(
			new EditMessageText(chatId, messageId, shrinkText(text))
				.parseMode(Markdown).disableWebPagePreview(downloadFile).replyMarkup(keyboard)
		);
	}

	public void replySticker(long chatId, int replyId, String stickerId) {
		executeRequestLog(new SendSticker(chatId, stickerId).replyToMessageId(replyId));
	}

	public void sendStickerToChat(long chatId, String stickerId, long origChatId, int origReplyId) {
		Answer<String> res = executeRequestLog(new SendSticker(chatId, stickerId));
		if (!res.ok()) {
			sendError(origChatId, origReplyId,
				shrinkText(String.format(locale.i18n("bot.error.send.sticker"), stickerId, chatId, res.error())));
		}
	}

	public void replyPhoto(long chatId, int replyId, String uri, String title) {
		sendPhoto(chatId, replyId, uri, title, chatId, replyId);
	}

	public void sendPhotoToChat(long chatId, String uri, long origChatId, int origReplyId) {
		sendPhoto(chatId, null, uri, null, origChatId, origReplyId);
	}

	private void sendPhoto(long chatId, Integer replyId, String uri, String title, long origChatId, int origReplyId) {
		SendPhoto sendPhoto;
		File photo = null;
		if (downloadFile) {
			photo = new File(uri);
			sendPhoto = new SendPhoto(chatId, photo);
		} else {
			sendPhoto = new SendPhoto(chatId, uri);
		}
		if (replyId != null) {
			sendPhoto.replyToMessageId(replyId);
		}
		if (title != null) {
			sendPhoto.caption(title);
		}
		Answer<String> res = executeRequestLog(sendPhoto);
		if (!res.ok()) {
			sendError(origChatId, origReplyId,
				shrinkText(String.format(locale.i18n("bot.error.send.image"), uri, chatId, res.error())));
		}
		if (photo != null && !photo.delete()) {
			log.error(String.format("Cannot delete temporary photo file '%s'.", uri));
		}
	}

	public void sendCallbackQueryAnswer(String callbackId, String text) {
		executeRequestLog(new AnswerCallbackQuery(callbackId).text(text).showAlert(false));
	}

	private Answer<String> executeRequestLog(BaseRequest<?, ?> request) {
		final String error = request.getMethod() + " " + request.getParameters();
		if (config.isSilent()) {
			log.info(String.format("Silent mode is activated. Cannot execute request: '%s'.", error));
			return Ok("Ok!");
		}
		Answer<String> res = getResponse(telegram.getBot().execute(request));
		if (!res.ok()) {
			log.error(res.error());
		}
		return res;
	}

	private Answer<String> getResponse(BaseResponse response) {
		if (!response.isOk()) {
			return Error(String.format("Response error: %d, %s.", response.errorCode(), response.description()));
		}
		return Ok("Ok!");
	}

	public String shrinkText(String text) {
		int maxSendLength = config.getMaxSendLength();
		if (text.length() > maxSendLength) {
			log.warn(String.format(
				"Unexpectedly large response size! The text will be truncated to the '%d' characters.", maxSendLength
			));
			return text.substring(0, maxSendLength) + "\n" + locale.i18n("bot.warn.send.long");
		}
		return text;
	}

	private void sendError(long chatId, int replyId, String error) {
		log.error(error);
		replyMessage(chatId, replyId, error);
	}
}
