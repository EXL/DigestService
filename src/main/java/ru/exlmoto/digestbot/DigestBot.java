package ru.exlmoto.digestbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ru.exlmoto.digestbot.callbacks.SendMessageCallback;
import ru.exlmoto.digestbot.commands.BotCommandFactory;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.utils.LocalizationHelper;

import java.util.List;

@Component
public class DigestBot extends TelegramLongPollingBot {
	private final String mBotUsername;
	private final String mBotToken;
	private final String[] mBotAdmins;
	private final int mBotMaxUpdates;

	private final Logger mBotLogger;
	private final BotCommandFactory mBotCommandFactory;

	private final LocalizationHelper mLocalizationHelper;

	private final String K_BOT_COMMAND_ENTITY = "bot_command";

	private enum MessageMode {
		MESSAGE_SIMPLE,
		MESSAGE_HTML,
		MESSAGE_MARKDOWN
	}

	@Autowired
	public DigestBot(@Value("${digestbot.name}") String aBotUsername,
	                 @Value("${digestbot.token}") String aBotToken,
	                 @Value("${digestbot.admins}") String[] aBotAdmins,
	                 @Value("${digestbot.max_updates}") int aBotMaxUpdates,
	                 BotCommandFactory aBotCommandFactory,
	                 LocalizationHelper aLocalizationHelper) {
		mBotUsername = aBotUsername;
		mBotToken = aBotToken;
		mBotAdmins = aBotAdmins;
		mBotMaxUpdates = aBotMaxUpdates;

		mBotLogger = LoggerFactory.getLogger(DigestBot.class);
		mBotCommandFactory = aBotCommandFactory;
		mLocalizationHelper = aLocalizationHelper;
	}

	@Override
	public void onUpdateReceived(Update aUpdate) {
		if (aUpdate.hasMessage() && aUpdate.getMessage().isCommand()) {
			onCommand(aUpdate);
		}
	}

	/**
	 * Receive only a few recent updates for handling.
	 * See "digestbot.max_updates" setting in properties file.
	 * Default is 30.
	 */
	@Override
	public void onUpdatesReceived(List<Update> aUpdates) {
		final int lListSize = aUpdates.size();
		if (lListSize > mBotMaxUpdates) {
			aUpdates.subList(0, lListSize - mBotMaxUpdates).clear();
		}
		aUpdates.forEach(this::onUpdateReceived);
	}

	@Override
	public String getBotUsername() {
		return mBotUsername;
	}

	@Override
	public String getBotToken() {
		return mBotToken;
	}

	private void sendMessage(Long aChatId, Integer aMessageId, String aMessage, MessageMode aMessageMode) {
		try {
			SendMessage lSendMessage = new SendMessage();
			lSendMessage.setChatId(aChatId);
			lSendMessage.setReplyToMessageId(aMessageId);
			switch (aMessageMode) {
				case MESSAGE_SIMPLE:
				default: {
					break;
				}
				case MESSAGE_HTML: {
					lSendMessage.setParseMode(ParseMode.HTML);
					lSendMessage.enableHtml(true);
					break;
				}
				case MESSAGE_MARKDOWN: {
					lSendMessage.setParseMode(ParseMode.MARKDOWN);
					lSendMessage.enableMarkdown(true);
				}
			}
			lSendMessage.setText(aMessage);
			executeAsync(lSendMessage, new SendMessageCallback<>(this));
		} catch (TelegramApiException e) {
			loge(String.format("Cannot send message into '%d' chat: '%s'.", aChatId, e.toString()));
		}
	}

	public void sendSimpleMessage(Long aChatId, Integer aMessageId, String aMessage) {
		sendMessage(aChatId, aMessageId, aMessage, MessageMode.MESSAGE_SIMPLE);
	}

	public void sendHtmlMessage(Long aChatId, Integer aMessageId, String aMessage) {
		sendMessage(aChatId, aMessageId, aMessage, MessageMode.MESSAGE_HTML);
	}

	public void sendMarkdownMessage(Long aChatId, Integer aMessageId, String aMessage) {
		sendMessage(aChatId, aMessageId, aMessage, MessageMode.MESSAGE_MARKDOWN);
	}

	public void sendStickerToChat(Long aChatId, Integer aMessageId, String aStickerId) {
		try {
			final SendSticker lSendSticker = new SendSticker();
			lSendSticker.setReplyToMessageId(aMessageId);
			lSendSticker.setChatId(aChatId);
			lSendSticker.setSticker(aStickerId);
			execute(lSendSticker);
		} catch (TelegramApiException e) {
			loge(String.format("Cannot send sticker '%s' into '%d' chat: '%s'.", aStickerId, aChatId, e.toString()));
		}
	}

	private void onCommand(Update aUpdate) {
		final List<MessageEntity> lEntities = aUpdate.getMessage().getEntities();
		lEntities.stream().filter(messageEntity ->
		        messageEntity.getType().equals(K_BOT_COMMAND_ENTITY) &&
		        messageEntity.getOffset() == 0)
		                .forEach(command -> runCommand(command.getText(), aUpdate));
	}

	private void runCommand(String aCommandName, Update aUpdate) {
		mBotCommandFactory.getCommand(aCommandName).ifPresent(command -> command.prepare(this, aUpdate));
	}

	public void loge(String aTextToLog) {
		mBotLogger.error(aTextToLog);
	}

	public ReceivedMessage createReceivedMessage(Message aMessage) {
		return new ReceivedMessage(aMessage, mBotAdmins);
	}

	public LocalizationHelper getLocalizationHelper() {
		return mLocalizationHelper;
	}
}
