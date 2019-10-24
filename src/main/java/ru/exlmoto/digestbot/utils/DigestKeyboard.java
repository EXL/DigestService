package ru.exlmoto.digestbot.utils;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
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
		// TODO: init on startup??? For another ones.
		// Dynamically.
		final InlineKeyboardMarkup lInlineKeyboardMarkup = new InlineKeyboardMarkup();
		final List<List<InlineKeyboardButton>> lKeyboardMarkup = new ArrayList<>();
		List<InlineKeyboardButton> lKeyboardRow = new ArrayList<>();

		for (int i = 0; i < mPagesCount; i++) {
			lKeyboardRow.add(new InlineKeyboardButton(
				mYamlLocalizationHelper.getLocalizedString("command.digest.page") + ' ' + (i + 1))
				                 .setCallbackData("page." + i));
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

		final Pair<Boolean, String> lCorrectName =
			ReceivedMessage.determineCorrectName(aCallbackQuery.getFrom().getUserName(),
				aCallbackQuery.getFrom().getFirstName());

		final String lHello = mYamlLocalizationHelper.getRandomLocalizedString("command.digest.hello",
			lCorrectName);
		final String lEmpty = mYamlLocalizationHelper.getRandomLocalizedString("command.digest.empty",
			lCorrectName);
		final String lHeader = mYamlLocalizationHelper.getRandomLocalizedString("command.digest.header");

		String lAnswer = "";

		if (true) {
			lAnswer += lHello + '\n' + lHeader + ' ' + (lPage + 1) + ":\n";
		} else {
			lAnswer += lEmpty;
		}

		aDigestBot.editMarkdownMessageWithKeyboard(aCallbackQuery.getMessage().getChatId(),
			aCallbackQuery.getMessage().getMessageId(), lAnswer, getDigestKeyboard());
	}
}
