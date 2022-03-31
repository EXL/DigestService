/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 EXL <exlmotodev@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.exlmoto.digest.bot.sender;

import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.request.SendSticker;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.RestrictChatMember;
import com.pengrad.telegrambot.request.BanChatMember;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
import com.pengrad.telegrambot.response.GetChatAdministratorsResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.telegram.BotTelegram;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import java.io.File;

import java.util.List;

import static com.pengrad.telegrambot.model.request.ParseMode.HTML;
import static com.pengrad.telegrambot.model.request.ParseMode.Markdown;

import static ru.exlmoto.digest.util.Answer.Ok;
import static ru.exlmoto.digest.util.Answer.Error;

@Component
public class BotSender {
	private final Logger log = LoggerFactory.getLogger(BotSender.class);

	private final BotConfiguration config;
	private final BotTelegram telegram;
	private final LocaleHelper locale;

	@Value("${image.download-file}")
	private boolean downloadFile;

	public BotSender(BotConfiguration config, BotTelegram telegram, LocaleHelper locale) {
		this.config = config;
		this.telegram = telegram;
		this.locale = locale;
	}

	public void replySimple(long chatId, int replyId, String text) {
		sendMessage(chatId, replyId, text, null, null);
	}

	public void replyMarkdown(long chatId, int replyId, String text) {
		sendMessage(chatId, replyId, text, Markdown, null);
	}

	public void replyMarkdown(long chatId, int replyId, String text, InlineKeyboardMarkup keyboard) {
		sendMessage(chatId, replyId, text, Markdown, keyboard);
	}

	public Answer<String> replyHtml(long chatId, int replyId, String text, InlineKeyboardMarkup keyboard) {
		return sendMessage(chatId, replyId, text, HTML, keyboard);
	}

	public void sendHtml(long chatId, String text) {
		sendMessage(chatId, null, text, HTML, null);
	}

	public void sendMarkdown(long chatId, String text, InlineKeyboardMarkup keyboard) {
		sendMessage(chatId, null, text, Markdown, keyboard);
	}

	public void sendSimpleToChat(long chatId, String text) {
		sendSimpleToChat(chatId, text, null, null);
	}

	public void sendSimpleToChat(long chatId, String text, Long origChatId, Integer origReplyId) {
		Answer<String> res = executeRequestLog(new SendMessage(chatId, shrinkText(text)));
		if (!res.ok() && origChatId != null && origReplyId != null) {
			sendError(origChatId, origReplyId,
				shrinkText(String.format(locale.i18n("bot.error.send.message"), text, chatId, res.error())));
		}
	}

	private Answer<String> sendMessage(long chatId, Integer replyId, String text, ParseMode mode,
	                                   InlineKeyboardMarkup keyboard) {
		SendMessage sendMessage = new SendMessage(chatId, shrinkText(text)).disableWebPagePreview(downloadFile)
			.disableNotification(config.isDisableNotifications());
		if (mode != null) {
			sendMessage.parseMode(mode);
		}
		if (replyId != null) {
			sendMessage.replyToMessageId(replyId);
		}
		if (keyboard != null) {
			sendMessage.replyMarkup(keyboard);
		}
		return executeRequestLog(sendMessage);
	}

	public void editMarkdown(long chatId, int messageId, String text, InlineKeyboardMarkup keyboard) {
		editMessage(chatId, messageId, text, Markdown, keyboard);
	}

	public void editHtml(long chatId, int messageId, String text, InlineKeyboardMarkup keyboard) {
		editMessage(chatId, messageId, text, HTML, keyboard);
	}

	private void editMessage(long chatId, int messageId, String text, ParseMode mode, InlineKeyboardMarkup keyboard) {
		EditMessageText editMessageText = new EditMessageText(chatId, messageId, shrinkText(text))
			.parseMode(mode).disableWebPagePreview(downloadFile);
		if (keyboard != null) {
			editMessageText.replyMarkup(keyboard);
		}
		executeRequestLog(editMessageText);
	}

	public void replySticker(long chatId, int replyId, String stickerId) {
		executeRequestLog(new SendSticker(chatId, stickerId).replyToMessageId(replyId));
	}

	public void sendStickerToChat(long chatId, String stickerId) {
		sendStickerToChat(chatId, stickerId, null, null);
	}

	public void sendStickerToChat(long chatId, String stickerId, Long origChatId, Integer origReplyId) {
		Answer<String> res = executeRequestLog(new SendSticker(chatId, stickerId));
		if (!res.ok() && origChatId != null && origReplyId != null) {
			sendError(origChatId, origReplyId,
				shrinkText(String.format(locale.i18n("bot.error.send.sticker"), stickerId, chatId, res.error())));
		}
	}

	public void replyPhoto(long chatId, int replyId, String uri, String title) {
		sendPhoto(chatId, replyId, uri, title, chatId, replyId);
	}

	public void sendPhotoToChat(long chatId, String uri) {
		sendPhoto(chatId, null, uri, null, null, null);
	}

	public void sendLocalPhotoToChat(long chatId, File photo) {
		sendLocalPhotoToChat(chatId, photo, null);
	}

	public File sendLocalPhotoToChat(long chatId, File photo, String title) {
		SendPhoto sendPhoto = new SendPhoto(chatId, photo);
		if (title != null) {
			sendPhoto.parseMode(HTML);
			sendPhoto.caption(title);
		}
		Answer<String> res = executeRequestLog(sendPhoto);
		if (!res.ok()) {
			log.error(String.format("Cannot execute sendPhoto() request from sendLocalPhotoToChat() method, error: %s.",
				res.error()));
			return null;
		} else {
			return photo;
		}
	}

	public void sendPhotoToChat(long chatId, String uri, long origChatId, int origReplyId) {
		sendPhoto(chatId, null, uri, null, origChatId, origReplyId);
	}

	private void sendPhoto(long chatId, Integer replyId, String uri, String title,
	                       Long origChatId, Integer origReplyId) {
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
		if (!res.ok() && origChatId != null && origReplyId != null) {
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

	public boolean isUserChatAdministrator(long chatId, long userId) {
		GetChatAdministratorsResponse response = telegram.chatAdministrators(chatId);
		if (response != null && response.isOk()) {
			List<ChatMember> administrators = response.administrators();
			for (ChatMember admin : administrators) {
				if (admin.user().id() == userId) {
					return true;
				}
			}
		}
		return false;
	}

	private Answer<String> executeRequestLog(BaseRequest<?, ?> request) {
		String error = request.getMethod() + " " + request.getParameters();
		if (config.isSilent()) {
			log.info(String.format("Silent mode is activated. Cannot execute request: '%s'.", error));
			return Ok("Ok!");
		}
		Answer<String> res;
		try {
			res = getResponse(telegram.getBot().execute(request));
		} catch (RuntimeException re) {
			error = String.format("Exception while execute request: '%s'.", re.getLocalizedMessage());
			log.error(error, re);
			return Error(error);
		}
		if (!res.ok()) {
			log.error(res.error());
		}
		return res;
	}

	private Answer<String> getResponse(BaseResponse response) {
		if (!response.isOk()) {
			return Error(String.format("Response error: '%d, %s'.", response.errorCode(), response.description()));
		}
		if (response instanceof SendResponse) {
			SendResponse sendResponse = (SendResponse) response;
			Message message = sendResponse.message();
			return Ok(String.valueOf(message.messageId()));
		}
		return Ok("Ok!");
	}

	public String shrinkText(String text) {
		int maxSendLength = config.getMaxSendLength();
		int textLength = text.length();
		if (textLength > maxSendLength) {
			log.warn(String.format(
				"Unexpectedly large response size: '%d', the text will be truncated to the '%d' characters.",
				textLength, maxSendLength
			));
			return text.substring(0, maxSendLength) + "\n" + locale.i18n("bot.warn.send.long");
		}
		return text;
	}

	private void sendError(long chatId, int replyId, String error) {
		log.error(error);
		replyMarkdown(chatId, replyId, error);
	}

	public void restrictUserInChat(long chatId, long userId) {
		executeRequestLog(new RestrictChatMember(chatId, userId));
	}

	public void allowUserInChat(long chatId, long userId) {
		executeRequestLog(new RestrictChatMember(chatId, userId)
			.canSendMessages(true)
			.canSendMediaMessages(true)
			.canAddWebPagePreviews(true)
			.canSendOtherMessages(true));
	}

	public void banUserInChat(long chatId, long userId, long seconds) {
		executeRequestLog(new BanChatMember(chatId, userId).untilDate(Math.toIntExact(seconds)));
	}

	public void deleteMessageInChat(long chatId, long messageId) {
		executeRequestLog(new DeleteMessage(chatId, Math.toIntExact(messageId)));
	}
}
