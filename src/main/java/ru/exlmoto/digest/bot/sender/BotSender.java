package ru.exlmoto.digest.bot.sender;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ru.exlmoto.digest.bot.BotService;
import ru.exlmoto.digest.bot.config.BotConfiguration;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

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

	public void replyMessageWithKeyboard(long chatId, int replyId, String text, InlineKeyboardMarkup keyboard) {
		sendMessage(chatId, replyId, text, ParseMode.MARKDOWN, keyboard);
	}

	public void replyMarkdownMessage(long chatId, int replyId, String text) {
		sendMessage(chatId, replyId, text, ParseMode.MARKDOWN, null);
	}

	public void sendHtmlMessage(long chatId, String text) {
		sendMessage(chatId, null, text, ParseMode.HTML, null);
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
		executeMethodAux(editMessageText);
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
		executeMethodAux(sendMessage);
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

	private void executeMethodAux(BotApiMethod<?> method) {
		if (config.isDebugLogSends()) {
			log.info(method.toString());
		}
		if (!config.isDebugSilent()) {
			executeMethod(method);
		}
	}

	public void executeMethod(BotApiMethod<?> method) {
		try {
			botService.execute(method);
		} catch (TelegramApiException tae) {
			log.error(String.format("Cannot execute BotApiMethod: '%s'.", method.toString()), tae);
		}
	}
}
