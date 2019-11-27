package ru.exlmoto.digestbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.EntityType;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ru.exlmoto.digestbot.commands.BotCommand;
import ru.exlmoto.digestbot.commands.BotCommandFactory;
import ru.exlmoto.digestbot.repos.IDigestEntriesRepository;
import ru.exlmoto.digestbot.repos.IDigestUsersRepository;
import ru.exlmoto.digestbot.repos.IMotoFanSubscribersRepository;
import ru.exlmoto.digestbot.utils.*;
import ru.exlmoto.digestbot.workers.AvatarUpdater;
import ru.exlmoto.digestbot.workers.BankWorker;
import ru.exlmoto.digestbot.workers.DigestShredder;
import ru.exlmoto.digestbot.workers.MotoFanWorker;
import ru.exlmoto.digestbot.yaml.impl.YamlLocalizationHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DigestBot extends TelegramLongPollingBot {
	private final String mBotUsername;
	private final String mBotToken;
	private final String[] mBotAdmins;
	private final Integer mBotMaxUpdates;
	private final Integer mBotMaxResponseLength;
	private final Integer mBotInlineCoolDown;

	private final Logger mBotLogger;
	private final BotCommandFactory mBotCommandFactory;

	private final YamlLocalizationHelper mLocalizationHelper;
	private final ChartsKeyboard mChartsKeyboard;
	private final RatesKeyboard mRatesKeyboard;
	private final SubscribeKeyboard mSubscribeKeyboard;
	private final DigestKeyboard mDigestKeyboard;
	private final CallbackQueryHandler mCallbackQueryHandler;

	private final BankWorker mBankWorker;
	private final MotoFanWorker mMotoFanWorker;
	private final AvatarUpdater mAvatarUpdater;

	private final IMotoFanSubscribersRepository mIMotoFanSubscribersRepository;
	private final IDigestEntriesRepository mIDigestEntriesRepository;
	private final IDigestUsersRepository mIDigestUsersRepository;

	private enum MessageMode {
		MESSAGE_SIMPLE,
		MESSAGE_HTML,
		MESSAGE_MARKDOWN
	}

	@Autowired
	public DigestBot(@Value("${digestbot.name}") final String aBotUsername,
	                 @Value("${digestbot.token}") final String aBotToken,
	                 @Value("${digestbot.admins}") final String[] aBotAdmins,
	                 @Value("${digestbot.max_updates}") final Integer aBotMaxUpdates,
	                 @Value("${digestbot.response.maxsize}") final Integer aBotMaxResponseLength,
	                 @Value("${digestbot.inline.cooldown}") final Integer aBotInlineCoolDown,
	                 final BotCommandFactory aBotCommandFactory,
	                 final YamlLocalizationHelper aLocalizationHelper,
	                 final ChartsKeyboard aChartsKeyboard,
	                 final RatesKeyboard aRatesKeyboard,
	                 final SubscribeKeyboard aSubscribeKeyboard,
	                 final DigestKeyboard aDigestKeyboard,
	                 final BankWorker aBankWorker,
	                 final MotoFanWorker aMotoFanWorker,
	                 final DigestShredder aDigestShredder,
	                 final AvatarUpdater aAvatarUpdater,
	                 final IMotoFanSubscribersRepository aIMotoFanSubscribersRepository,
	                 final IDigestEntriesRepository aIDigestEntriesRepository,
	                 final IDigestUsersRepository aIDigestUsersRepository) {
		mBotUsername = aBotUsername;
		mBotToken = aBotToken;
		mBotAdmins = aBotAdmins;
		mBotMaxUpdates = aBotMaxUpdates;
		mBotMaxResponseLength = aBotMaxResponseLength;
		mBotInlineCoolDown = aBotInlineCoolDown;

		mBotLogger = LoggerFactory.getLogger(DigestBot.class);
		mBotCommandFactory = aBotCommandFactory;
		mLocalizationHelper = aLocalizationHelper;

		mChartsKeyboard = aChartsKeyboard;
		mRatesKeyboard = aRatesKeyboard;
		mSubscribeKeyboard = aSubscribeKeyboard;
		mDigestKeyboard = aDigestKeyboard;
		mCallbackQueryHandler = new CallbackQueryHandler();

		mIMotoFanSubscribersRepository = aIMotoFanSubscribersRepository;
		mIDigestEntriesRepository = aIDigestEntriesRepository;
		mIDigestUsersRepository = aIDigestUsersRepository;

		mBankWorker = aBankWorker;
		mBankWorker.setBotLogger(mBotLogger);

		mMotoFanWorker = aMotoFanWorker;
		mMotoFanWorker.setDigestBot(this);

		mAvatarUpdater = aAvatarUpdater;
		mAvatarUpdater.setBotLogger(mBotLogger);

		aDigestShredder.setBotLogger(mBotLogger);

		new Thread(() -> {
			mBankWorker.updateAllBanks();
			mMotoFanWorker.updateLatestMotoFanPosts();
		}).start();

		// TODO: Delete this shit
		testDataBase();
	}

	@Override
	public void onUpdateReceived(final Update aUpdate) {
		Message lMessage =
			(aUpdate.hasEditedMessage()) ? aUpdate.getEditedMessage() :
				(aUpdate.hasMessage()) ? aUpdate.getMessage() : null;
		if (lMessage != null) {
			handleMessage(lMessage);
		}
		if (aUpdate.hasCallbackQuery()) {
			mCallbackQueryHandler.handle(this, aUpdate.getCallbackQuery());
		}
	}

	/**
	 * Receive only a few recent updates for handling.
	 * See "digestbot.max_updates" setting in properties file.
	 * Default is 30.
	 */
	@Override
	public void onUpdatesReceived(final List<Update> aUpdates) {
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

	private void handleMessage(final Message aMessage) {
		if (aMessage.isCommand()) {
			onCommand(aMessage);
		} else {
			onHashTag(aMessage);
		}
	}

	private AnswerCallbackQuery createAnswerCallbackQuery(final String aCallbackId, final String aText) {
		final AnswerCallbackQuery lAnswerCallbackQuery = new AnswerCallbackQuery();
		lAnswerCallbackQuery.setCallbackQueryId(aCallbackId);
		lAnswerCallbackQuery.setText(aText);
		lAnswerCallbackQuery.setShowAlert(false);
		return lAnswerCallbackQuery;
	}

	public void createAndSendAnswerCallbackQuery(final String aCallbackId, final String aText) {
		try {
			execute(createAnswerCallbackQuery(aCallbackId, aText));
		} catch (TelegramApiException e) {
			mBotLogger.error(String.format("Cannot send callback query answer: '%s'.", e.toString()));
		}
	}

	private void editMessage(final Long aChatId,
	                         final Integer aMessageId,
	                         final String aNewMessage,
	                         final MessageMode aMessageMode,
	                         final InlineKeyboardMarkup aInlineKeyboardMarkup) {
		try {
			final EditMessageText lEditMessageText = new EditMessageText();
			lEditMessageText.setChatId(aChatId);
			lEditMessageText.setMessageId(aMessageId);
			switch (aMessageMode) {
				default: {
					break;
				}
				case MESSAGE_HTML: {
					lEditMessageText.setParseMode(ParseMode.HTML);
					lEditMessageText.enableHtml(true);
					break;
				}
				case MESSAGE_MARKDOWN: {
					lEditMessageText.setParseMode(ParseMode.MARKDOWN);
					lEditMessageText.enableMarkdown(true);
					break;
				}
			}
			if (aInlineKeyboardMarkup != null) {
				lEditMessageText.setReplyMarkup(aInlineKeyboardMarkup);
			}
			String lBotAnswerText = aNewMessage;
			if (aNewMessage.length() > mBotMaxResponseLength) {
				mBotLogger.warn(String.format(
					"Unexpectedly large response size! The message will be truncated to %d characters.",
					mBotMaxResponseLength));
				lBotAnswerText = lBotAnswerText.substring(0, mBotMaxResponseLength);
				lBotAnswerText += '\n' + mLocalizationHelper.getLocalizedString("warn.response.long");
			}
			lEditMessageText.setText(lBotAnswerText);
			// TODO: digestbot.file.downloader???
			lEditMessageText.disableWebPagePreview();
			execute(lEditMessageText);
		} catch (TelegramApiException e) {
			mBotLogger.error(String.format("Cannot edit message in '%d' chat: '%s'.", aChatId, e.toString()));
		}
	}

	private void sendMessage(final Long aChatId,
	                         final Integer aMessageId,
	                         final String aMessage,
	                         final MessageMode aMessageMode,
	                         final InlineKeyboardMarkup aInlineKeyboardMarkup) {
		try {
			final SendMessage lSendMessage = new SendMessage();
			lSendMessage.setChatId(aChatId);
			if (aMessageId != null) {
				lSendMessage.setReplyToMessageId(aMessageId);
			}
			if (aInlineKeyboardMarkup != null) {
				lSendMessage.setReplyMarkup(aInlineKeyboardMarkup);
			}
			switch (aMessageMode) {
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
					break;
				}
			}
			String lBotAnswerText = aMessage;
			if (aMessage.length() > mBotMaxResponseLength) {
				mBotLogger.warn(String.format(
					"Unexpectedly large response size! The message will be truncated to %d characters.",
					mBotMaxResponseLength));
				lBotAnswerText = lBotAnswerText.substring(0, mBotMaxResponseLength);
				lBotAnswerText += '\n' + mLocalizationHelper.getLocalizedString("warn.response.long");
			}
			lSendMessage.setText(lBotAnswerText);
			lSendMessage.disableNotification();
			// TODO: digestbot.file.downloader???
			lSendMessage.disableWebPagePreview();
			execute(lSendMessage);
		} catch (TelegramApiException e) {
			mBotLogger.error(String.format("Cannot send message into '%d' chat: '%s'.", aChatId, e.toString()));
		}
	}

	public void editSimpleMessage(final Long aChatId, final Integer aMessageId, final String aMessage) {
		editMessage(aChatId, aMessageId, aMessage, MessageMode.MESSAGE_SIMPLE, null);
	}

	public void sendSimpleMessage(final Long aChatId, final Integer aMessageId, final String aMessage) {
		sendMessage(aChatId, aMessageId, aMessage, MessageMode.MESSAGE_SIMPLE, null);
	}

	public void sendHtmlMessage(final Long aChatId, final Integer aMessageId, final String aMessage) {
		sendMessage(aChatId, aMessageId, aMessage, MessageMode.MESSAGE_HTML, null);
	}

	public void editHtmlMessage(final Long aChatId, final Integer aMessageId, final String aMessage) {
		editMessage(aChatId, aMessageId, aMessage, MessageMode.MESSAGE_HTML, null);
	}

	public void sendMarkdownMessage(final Long aChatId, final Integer aMessageId, final String aMessage) {
		sendMessage(aChatId, aMessageId, aMessage, MessageMode.MESSAGE_MARKDOWN, null);
	}

	public void editMarkdownMessage(final Long aChatId, final Integer aMessageId, final String aMessage) {
		editMessage(aChatId, aMessageId, aMessage, MessageMode.MESSAGE_MARKDOWN, null);
	}

	public void sendMarkdownMessageWithKeyboard(final Long aChatId,
	                                            final Integer aMessageId,
	                                            final String aMessage,
	                                            final InlineKeyboardMarkup aInlineKeyboardMarkup) {
		sendMessage(aChatId, aMessageId, aMessage, MessageMode.MESSAGE_MARKDOWN, aInlineKeyboardMarkup);
	}

	public void editMarkdownMessageWithKeyboard(final Long aChatId,
	                                            final Integer aMessageId,
	                                            final String aMessage,
	                                            final InlineKeyboardMarkup aInlineKeyboardMarkup) {
		editMessage(aChatId, aMessageId, aMessage, MessageMode.MESSAGE_MARKDOWN, aInlineKeyboardMarkup);
	}


	public void sendStickerToChat(final Long aChatId, final Integer aMessageId,
	                              final Long aOriginalChatId, final String aStickerId) {
		try {
			final SendSticker lSendSticker = new SendSticker();
			if (aMessageId != null) {
				lSendSticker.setReplyToMessageId(aMessageId);
			}
			lSendSticker.setChatId(aChatId);
			lSendSticker.setSticker(aStickerId);
			execute(lSendSticker);
		} catch (TelegramApiException e) {
			if (aOriginalChatId != null) {
				sendMarkdownMessage(aOriginalChatId, aMessageId,
					String.format(mLocalizationHelper.getLocalizedString("error.sticker"),
						aStickerId, aChatId, e.toString()));
			}
			mBotLogger.error(String.format("Cannot send sticker '%s' into '%d' chat: '%s'.",
				aStickerId, aChatId, e.toString()));
		}
	}

	public void sendPhotoToChatFromUrl(final Long aChatId, final Integer aMessageId,
	                                   final Long aOriginalSenderChatId, final String aCaption,
	                                   final String aImagePathOrUrl, boolean aIsFilePath) {
		File lTempFile = null;
		try {
			final SendPhoto lSendPhoto = new SendPhoto();
			if (aMessageId != null) {
				lSendPhoto.setReplyToMessageId(aMessageId);
			}
			lSendPhoto.setChatId(aChatId);
			if (aIsFilePath) {
				lTempFile = new File(aImagePathOrUrl);
				lSendPhoto.setPhoto(lTempFile);
			} else {
				lSendPhoto.setPhoto(aImagePathOrUrl);
			}
			if (aCaption != null) {
				lSendPhoto.setCaption(aCaption);
			}
			execute(lSendPhoto);
		} catch (TelegramApiException e) {
			if (aOriginalSenderChatId != null) {
				sendMarkdownMessage(aOriginalSenderChatId, aMessageId,
					String.format(mLocalizationHelper.getLocalizedString("error.image"),
						aImagePathOrUrl, aChatId, e.toString()));
			}
			mBotLogger.error(String.format("Cannot send photo into '%d' chat: '%s'.", aChatId, e.toString()));
		}
		if (lTempFile != null) {
			if (!lTempFile.delete()) {
				mBotLogger.error(String.format("Cannot delete temporary file '%s'.", aImagePathOrUrl));
			}
		}
	}

	// TODO: getOffset == from start of message
	private void onCommand(final Message aMessage) {
		final List<MessageEntity> lEntities = aMessage.getEntities();
		if (lEntities != null) {
			lEntities.stream().filter(aMessageEntity ->
					aMessageEntity.getType().equals(EntityType.BOTCOMMAND) &&
							aMessageEntity.getOffset() == 0)
					.forEach(command -> runCommand(command.getText(), aMessage));
		}
	}

	// TODO: Only first hashtag is relevant
	private void onHashTag(final Message aMessage) {
		final List<MessageEntity> lEntities = aMessage.getEntities();
		if (lEntities != null) {
			final List<String> lHashTags = new ArrayList<>();
			lEntities.stream().filter(aMessageEntity ->
					aMessageEntity.getType().equals(EntityType.HASHTAG))
					.forEach(hashTag -> lHashTags.add(hashTag.getText()));

			for (String iHashTag : lHashTags) {
				Optional<BotCommand> lOptionalBotCommand = mBotCommandFactory.getCommand(iHashTag);
				if (lOptionalBotCommand.isPresent()) {
					lOptionalBotCommand.ifPresent(aCommand -> aCommand.prepare(this, aMessage));
					break;
				}
			}
		}
	}

	private void runCommand(final String aCommandName, final Message aMessage) {
		mBotCommandFactory.getCommand(aCommandName).ifPresent(aCommand -> aCommand.prepare(this, aMessage));
	}

	public ReceivedMessage createReceivedMessage(final Message aMessage) {
		return new ReceivedMessage(aMessage, mBotAdmins);
	}

	public YamlLocalizationHelper getLocalizationHelper() {
		return mLocalizationHelper;
	}

	public ChartsKeyboard getChartsKeyboard() {
		return mChartsKeyboard;
	}

	public RatesKeyboard getRatesKeyboard() {
		return mRatesKeyboard;
	}

	public SubscribeKeyboard getSubscribeKeyboard() {
		return mSubscribeKeyboard;
	}

	public DigestKeyboard getDigestKeyboard() {
		return mDigestKeyboard;
	}

	public Logger getBotLogger() {
		return mBotLogger;
	}

	public Integer getBotInlineCoolDown() {
		return mBotInlineCoolDown;
	}

	public BankWorker getBankWorker() {
		return mBankWorker;
	}

	public IMotoFanSubscribersRepository getIMotoFanSubscribersRepository() {
		return mIMotoFanSubscribersRepository;
	}

	public IDigestEntriesRepository getIDigestEntriesRepository() {
		return mIDigestEntriesRepository;
	}

	public IDigestUsersRepository getIDigestUsersRepository() {
		return mIDigestUsersRepository;
	}

	private void testDataBase() {
		/*
		final MotoFanSubscriberEntity lMotoFanSubscriberEntity1 = new MotoFanSubscriberEntity();
		lMotoFanSubscriberEntity1.setSubscription_id(87336977L);
		final MotoFanSubscriberEntity lMotoFanSubscriberEntity2 = new MotoFanSubscriberEntity();
		lMotoFanSubscriberEntity2.setSubscription_id(-1001148683293L);
		mIMotoFanSubscribersRepository.save(lMotoFanSubscriberEntity1);
		mIMotoFanSubscribersRepository.save(lMotoFanSubscriberEntity2);
		 */
/*
		for (int i = 0; i < 9; i++) {
			final DigestEntity lDigestEntity = new DigestEntity();
			lDigestEntity.setAuthor(999);
			lDigestEntity.setDigest("1 Test " + (System.currentTimeMillis()) + " bbbbbbbbbbbbbbb");
			lDigestEntity.setHtml("Test " + (System.currentTimeMillis()));
			lDigestEntity.setDate(29999L);
			lDigestEntity.setChat(-1001148683296L);
			mIDigestEntriesRepository.save(lDigestEntity);
		}
*/
		// mIDigestEntriesRepository.deleteAll();
	}
}
