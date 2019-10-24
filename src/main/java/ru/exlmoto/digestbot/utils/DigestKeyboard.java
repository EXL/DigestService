package ru.exlmoto.digestbot.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.yaml.impl.YamlLocalizationHelper;

import java.util.ArrayList;
import java.util.List;

@Component
public class DigestKeyboard {
	private final YamlLocalizationHelper mYamlLocalizationHelper;
	private final Integer mPagesCount;

	public DigestKeyboard(final YamlLocalizationHelper aYamlLocalizationHelper,
	                      @Value("${digestbot.digest.pages}") final Integer aPagesCount) {
		mYamlLocalizationHelper = aYamlLocalizationHelper;
		mPagesCount = aPagesCount;
	}

	public InlineKeyboardMarkup getDigestKeyboard() {
		// TODO: init on startup???
		final InlineKeyboardMarkup lInlineKeyboardMarkup = new InlineKeyboardMarkup();
		final List<List<InlineKeyboardButton>> lKeyboardMarkup = new ArrayList<>();
		List<InlineKeyboardButton> lKeyboardRow = new ArrayList<>();

		for (int i = 0; i < mPagesCount; i++) {
			lKeyboardRow.add(new InlineKeyboardButton(
				mYamlLocalizationHelper.getLocalizedString("command.digest.page") + ' ' + (i + 1))
				                 .setCallbackData("page." + "asd"));
		}

		lKeyboardMarkup.add(lKeyboardRow);
		lInlineKeyboardMarkup.setKeyboard(lKeyboardMarkup);
		return lInlineKeyboardMarkup;
	}

	public void handleDigestKeyboard(final DigestBot aDigestBot,
	                                 final CallbackQuery aCallbackQuery) {
		int lPage = 0;
		try {
			lPage = Integer.parseInt(aCallbackQuery.getData().replace("page.", ""));
		} catch (NumberFormatException e) {
			aDigestBot.getBotLogger().error(String.format("Cannot parse number: '%s'.", e.toString()));
		}

		aDigestBot.createAndSendAnswerCallbackQuery(aCallbackQuery.getId(),
			mYamlLocalizationHelper.getLocalizedString("inline.digest.page") + ' ' + (lPage + 1));
	}
}
