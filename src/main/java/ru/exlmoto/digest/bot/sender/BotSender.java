package ru.exlmoto.digest.bot.sender;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ru.exlmoto.digest.bot.BotService;
import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

import java.io.File;

import static ru.exlmoto.digest.util.Answer.Ok;
import static ru.exlmoto.digest.util.Answer.Error;

@Slf4j
@Component
public class BotSender {
	private final BotConfiguration config;
	private final LocalizationHelper locale;
	private BotService botService;

	@Value("${image.download-file}")
	private boolean downloadFile;

	public BotSender(BotConfiguration config, LocalizationHelper locale) {
		this.config = config;
		this.locale = locale;
	}

	@Autowired
	public void setBotService(BotService botService) {
		this.botService = botService;
	}

	public void replyMessage(long chatId, int replyId, String text) {
		sendMessage(chatId, replyId, text, ParseMode.MARKDOWN, null);
	}

	public void replyMessageWithKeyboard(long chatId, int replyId, String text, InlineKeyboardMarkup keyboard) {
		sendMessage(chatId, replyId, text, ParseMode.MARKDOWN, keyboard);
	}

	public void sendHtmlMessage(long chatId, String text) {
		sendMessage(chatId, null, text, ParseMode.HTML, null);
	}

	public void sendMessageToChat(long chatId, String text, long origChatId, int origReplyId) {
		Answer<String> res =
			executeGeneralMethod(new SendMessage().setChatId(chatId).setParseMode(ParseMode.MARKDOWN).setText(text));
		if (!res.ok()) {
			replyMessage(origChatId, origReplyId,
				shrinkText(String.format(locale.i18n("bot.error.send.message"), text, chatId, res.error())));
		}
	}

	private void sendMessage(long chatId, Integer replyId, String text, String mode, InlineKeyboardMarkup keyboard) {
		SendMessage sendMessage =
			new SendMessage()
				.setChatId(chatId)
				.setReplyToMessageId(replyId)
				.setText(shrinkText(text))
				.setParseMode(mode)
				.setReplyMarkup(keyboard);
		if (config.isEnableNotifications()) {
			sendMessage.enableNotification();
		} else {
			sendMessage.disableNotification();
		}
		if (downloadFile) {
			sendMessage.disableWebPagePreview();
		}
		executeGeneralMethod(sendMessage);
	}

	public void editMessage(long chatId, int messageId, String text, InlineKeyboardMarkup keyboard) {
		EditMessageText editMessageText =
			new EditMessageText()
				.setChatId(chatId)
				.setMessageId(messageId)
				.setText(shrinkText(text))
				.setParseMode(ParseMode.MARKDOWN)
				.setReplyMarkup(keyboard);
		if (downloadFile) {
			editMessageText.disableWebPagePreview();
		}
		executeGeneralMethod(editMessageText);
	}

	public void sendStickerToChat(long chatId, String stickerId, long origChatId, int origReplyId) {
		Answer<String> res = executeStickerMethod(new SendSticker().setChatId(chatId).setSticker(stickerId));
		if (!res.ok()) {
			replyMessage(origChatId, origReplyId,
				shrinkText(String.format(locale.i18n("bot.error.send.sticker"), stickerId, chatId, res.error())));
		}
	}

	public void replySticker(long chatId, int replyId, String stickerId) {
		executeStickerMethod(new SendSticker().setChatId(chatId).setReplyToMessageId(replyId).setSticker(stickerId));
	}

	public void sendPhotoToChat(long chatId, String uri, String title, long origChatId, int origReplyId) {
		sendPhoto(new SendPhoto().setChatId(chatId).setCaption(title), uri, origChatId, origReplyId);
	}

	public void replyPhoto(long chatId, int replyId, String uri, String title) {
		sendPhoto(new SendPhoto().setChatId(chatId).setReplyToMessageId(replyId).setCaption(title),
			uri, chatId, replyId);
	}

	private void sendPhoto(SendPhoto sendPhoto, String uri, long origChatId, int origReplyId) {
		File photo = null;
		if (downloadFile) {
			photo = new File(uri);
			sendPhoto.setPhoto(photo);
		} else {
			sendPhoto.setPhoto(uri);
		}
		Answer<String> res = executePhotoMethod(sendPhoto);
		if (!res.ok()) {
			replyMessage(origChatId, origReplyId, shrinkText(String.format(locale.i18n("bot.error.send.image"),
					uri, sendPhoto.getChatId(), res.error())));
		}
		if (photo != null && !photo.delete()) {
			log.error(String.format("Cannot delete temporary photo file '%s'.", uri));
		}
	}

	public void sendAnswer(String callbackId, String text) {
		executeGeneralMethod(
			new AnswerCallbackQuery().setCallbackQueryId(callbackId).setText(text).setShowAlert(false));
	}

	public String shrinkText(String text) {
		String botAnswer = text;
		int maxSendLength = config.getMaxSendLength();
		if (botAnswer.length() > config.getMaxSendLength()) {
			log.warn(String.format(
				"Unexpectedly large response size! The message will be truncated to the '%d' characters.", maxSendLength
			));
			botAnswer = botAnswer.substring(0, maxSendLength);
			botAnswer += "\n" + locale.i18n("bot.warn.send.long");
		}
		return botAnswer;
	}

	private Answer<String> executePhotoMethod(SendPhoto sendPhoto) {
		Answer<String> res = logExecuteMethod(sendPhoto);
		if (res.ok()) {
			try {
				botService.execute(sendPhoto);
			} catch (TelegramApiException tae) {
				return logError(String.format("Cannot execute SendSticker method: '%s'.", sendPhoto.toString()));
			}
		}
		return res;
	}

	public Answer<String> executeStickerMethod(SendSticker sendSticker) {
		Answer<String> res = logExecuteMethod(sendSticker);
		if (res.ok()) {
			try {
				botService.execute(sendSticker);
			} catch (TelegramApiException tae) {
				return logError(String.format("Cannot execute SendSticker method: '%s'.", sendSticker.toString()));
			}
		}
		return res;
	}

	public Answer<String> executeGeneralMethod(BotApiMethod<?> method) {
		Answer<String> res = logExecuteMethod(method);
		if (res.ok()) {
			try {
				botService.execute(method);
			} catch (TelegramApiException tae) {
				return logError(String.format("Cannot execute BotApiMethod: '%s'.", method.toString()));
			}
		}
		return res;
	}

	private Answer<String> logExecuteMethod(PartialBotApiMethod<?> method) {
		if (config.isDebugLogSends()) {
			log.info(method.toString());
		}
		if (config.isDebugSilent()) {
			return Error(String.format("Silent mode is activated. Cannot execute method: '%s'.", method.toString()));
		}
		return Ok("Ok!");
	}

	private Answer<String> logError(String errorText) {
		log.error(errorText);
		return Error(errorText);
	}
}
