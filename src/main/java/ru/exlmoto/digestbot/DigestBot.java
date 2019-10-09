package ru.exlmoto.digestbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.exlmoto.digestbot.commands.CommandsContext;
import ru.exlmoto.digestbot.commands.CommandsManager;

import java.util.List;

@Component
public class DigestBot extends TelegramLongPollingBot {
	private final String mBotUserName;
	private final String mBotToken;
	private final String[] mBotAdmins;
	private final int mBotMaxUpdates;
	private final Logger mBotLogger;
	private final CommandsManager mCommandsManager;

	private enum MessageMode {
		MESSAGE_SIMPLE,
		MESSAGE_HTML,
		MESSAGE_MARKDOWN
	}

	public DigestBot(@Value("${digestbot.name}") String aBotUserName,
	                 @Value("${digestbot.token}") String aBotToken,
	                 @Value("${digestbot.admins}") String[] aBotAdmins,
	                 @Value("${digestbot.max_updates}") int aBotMaxUpdates,
	                 @Lazy CommandsManager aCommandsManager) {
		this.mBotUserName = aBotUserName;
		this.mBotToken = aBotToken;
		this.mBotAdmins = aBotAdmins;
		this.mBotMaxUpdates = aBotMaxUpdates;
		this.mCommandsManager = aCommandsManager;
		this.mBotLogger = LoggerFactory.getLogger(DigestBot.class);
	}

	@Override
	public void onUpdateReceived(Update aUpdate) {
		// mBotLogger.info("Im Here!");
		if (aUpdate.hasMessage() || aUpdate.hasEditedMessage()) {
			final Message lMessage = aUpdate.getMessage();
			final String lText = lMessage.getText();
			final Integer lUserId = lMessage.getFrom().getId();
			final Long lChatId = lMessage.getChatId();

			final CommandsContext lCommandsContext = new CommandsContext();
			lCommandsContext.setChatId(lUserId.longValue());
			lCommandsContext.setText("Text=" + lText);

			this.mCommandsManager.executeCommand(lMessage.getText(), lCommandsContext);
		}
	}

	/* Receive only a few recent updates for handling.         */
	/* See "digestbot.max_updates" setting in properties file. */
	/* Default is 30.                                          */
	@Override
	public void onUpdatesReceived(List<Update> aUpdates) {
		final int lListSize = aUpdates.size();
		if (lListSize > this.mBotMaxUpdates) {
			aUpdates.subList(0, lListSize - this.mBotMaxUpdates).clear();
		}
		aUpdates.forEach(this::onUpdateReceived);
	}

	@Override
	public String getBotUsername() {
		return this.mBotUserName;
	}

	@Override
	public String getBotToken() {
		return this.mBotToken;
	}

	private boolean sendMessage(Long aChatId, String aMessage, MessageMode aMessageMode) {
		try {
			SendMessage lSendMessage = new SendMessage();
			lSendMessage.setChatId(aChatId);
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
			execute(lSendMessage);
			return true;
		} catch (TelegramApiException e) {
			mBotLogger.error(String.format("Cannot send simple message into %d chat: %s.", aChatId, e.toString()));
			return false;
		}
	}

	public boolean sendSimpleMessage(Long aChatId, String aMessage) {
		return sendMessage(aChatId, aMessage, MessageMode.MESSAGE_SIMPLE);
	}

	public boolean sendHtmlMessage(Long aChatId, String aMessage) {
		return sendMessage(aChatId, aMessage, MessageMode.MESSAGE_HTML);
	}

	public boolean sendMarkdownMessage(Long aChatId, String aMessage) {
		return sendMessage(aChatId, aMessage, MessageMode.MESSAGE_MARKDOWN);
	}
}
