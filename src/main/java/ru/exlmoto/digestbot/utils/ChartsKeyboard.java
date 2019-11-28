package ru.exlmoto.digestbot.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.services.impl.FileService;
import ru.exlmoto.digestbot.yaml.impl.YamlChartsIndexHelper;
import ru.exlmoto.digestbot.yaml.impl.YamlLocalizationHelper;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChartsKeyboard {
	private final YamlChartsIndexHelper mYamlChartsIndexHelper;
	private final FileService mFileService;

	@Autowired
	public ChartsKeyboard(final YamlChartsIndexHelper aYamlChartsIndexHelper,
	                      final FileService aFileService) {
		mYamlChartsIndexHelper = aYamlChartsIndexHelper;
		mFileService = aFileService;
	}

	public InlineKeyboardMarkup getChartsKeyboard() {
		final ArrayList<String> lButtonStrings = mYamlChartsIndexHelper.getButtons();
		final ArrayList<String> lKeys = mYamlChartsIndexHelper.getKeys();

		final InlineKeyboardMarkup lInlineKeyboardMarkup = new InlineKeyboardMarkup();
		final List<List<InlineKeyboardButton>> lKeyboardMarkup = new ArrayList<>();
		List<InlineKeyboardButton> lKeyboardRow = new ArrayList<>();

		// 4x5 from 20 elements.
		for (int i = 0; i < lButtonStrings.size(); ++i) {
			if (i % 4 == 0) {
				lKeyboardMarkup.add(lKeyboardRow);
				lKeyboardRow = new ArrayList<>();
			}
			lKeyboardRow.add(new InlineKeyboardButton(lButtonStrings.get(i)).setCallbackData(lKeys.get(i)));
		}
		lKeyboardMarkup.add(lKeyboardRow);
		lInlineKeyboardMarkup.setKeyboard(lKeyboardMarkup);
		return lInlineKeyboardMarkup;
	}

	public YamlChartsIndexHelper getYamlChartsIndexHelper() {
		return mYamlChartsIndexHelper;
	}

	public void handleChartsKeyboard(final DigestBot aDigestBot, final CallbackQuery aCallbackQuery) {
		final YamlLocalizationHelper lLocalizationHelper = aDigestBot.getLocalizationHelper();
		final Message lMessage = aCallbackQuery.getMessage();
		final Long lChatId = lMessage.getChatId();
		final Integer lMessageId = lMessage.getMessageId();
		final String lKey = aCallbackQuery.getData();
		final String lTitle = mYamlChartsIndexHelper.getTitleByKey(lKey);
		final String lApiLink = mYamlChartsIndexHelper.getApiLinkByKey(lKey);

		aDigestBot.createAndSendAnswerCallbackQuery(aCallbackQuery.getId(),
			lLocalizationHelper.getLocalizedString("inline.chart.selected") + ' ' + lTitle);

		if (aDigestBot.getUseFileLoader()) {
			final Pair<Boolean, String> lAnswer = mFileService.receiveObject(lApiLink);
			final String lResult = lAnswer.getSecond();
			if (lAnswer.getFirst()) {
				aDigestBot.sendPhotoToChatFromUrl(lChatId, lMessageId, lChatId, lTitle, lResult, true);
			} else {
				aDigestBot.getBotLogger().error(String.format("Cannot get chart image: '%s'.", lResult));
				aDigestBot.sendMarkdownMessage(lChatId, lMessageId,
					String.format(lLocalizationHelper.getLocalizedString("error.chart"), lResult));
			}
		} else {
			aDigestBot.sendPhotoToChatFromUrl(lChatId, lMessageId, lChatId, lTitle, lApiLink, false);
		}
	}
}
