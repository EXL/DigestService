package ru.exlmoto.digestbot.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.entities.DigestEntity;
import ru.exlmoto.digestbot.yaml.impl.YamlLocalizationHelper;

import java.util.ArrayList;
import java.util.List;

@Component
public class DigestKeyboard {
	private final YamlLocalizationHelper mYamlLocalizationHelper;
	private final Long mPagesCount;
	private final Integer mDigestPerPage;
	private final int K_FIRST_PAGE = 0; // TODO: move this to Constants class like news markers count!

	public DigestKeyboard(final YamlLocalizationHelper aYamlLocalizationHelper,
	                      @Value("${digestbot.digest.pages.max}") final Long aPagesCount,
	                      @Value("${digestbot.digest.pages.posts}") final Integer aDigestPerPage) {
		mYamlLocalizationHelper = aYamlLocalizationHelper;
		mPagesCount = aPagesCount;
		mDigestPerPage = aDigestPerPage;
	}

	public InlineKeyboardMarkup getDigestKeyboard(final Long aTotalElements) {
		// TODO: init on startup??? For another ones.

		Long lButtonsCount = ((aTotalElements - 1) / 10) + 1;
		if (lButtonsCount <= 1) {
			return null;
		}
		if (lButtonsCount > mPagesCount) {
			lButtonsCount = mPagesCount;
		}

		final InlineKeyboardMarkup lInlineKeyboardMarkup = new InlineKeyboardMarkup();
		final List<List<InlineKeyboardButton>> lKeyboardMarkup = new ArrayList<>();
		List<InlineKeyboardButton> lKeyboardRow = new ArrayList<>();

		for (int i = 0; i < lButtonsCount; i++) {
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

		final Long lChatId = aCallbackQuery.getMessage().getChatId();

		aDigestBot.createAndSendAnswerCallbackQuery(aCallbackQuery.getId(),
			mYamlLocalizationHelper.getLocalizedString("inline.digest.page") + ' ' + (lPage + 1));

		final Pair<Boolean, String> lCorrectName =
			ReceivedMessage.determineCorrectName(
					aCallbackQuery.getFrom().getUserName(),
					aCallbackQuery.getFrom().getFirstName(),
					aCallbackQuery.getFrom().getLastName());

		final String lHello = mYamlLocalizationHelper.getRandomLocalizedString("command.digest.hello",
			lCorrectName);
		final String lEmpty = mYamlLocalizationHelper.getRandomLocalizedString("command.digest.empty",
			lCorrectName);
		final String lHeader = mYamlLocalizationHelper.getRandomLocalizedString("command.digest.header");

		final Page<DigestEntity> lDigestEntries =
			aDigestBot.getIDigestEntriesRepository().findDigestEntitiesByChat(PageRequest.of(lPage, mDigestPerPage,
				Sort.by(Sort.Order.desc("id"))), lChatId);
		final boolean lIsDigestListEmpty = lDigestEntries.isEmpty();

		StringBuilder lAnswer = new StringBuilder();

		if (!lIsDigestListEmpty) {
			lAnswer.append(lHello).append('\n').append(lHeader).append(' ').append(lPage + 1).append(":\n");
		} else {
			lAnswer.append(lEmpty);
		}

		final String lMarker = mYamlLocalizationHelper.getLocalizedString("command.digest.marker");
		final String lMarkerNew = mYamlLocalizationHelper.getLocalizedString("command.digest.marker.new");

		int iNewCount = 3;
		for (DigestEntity iDigestEntity:
			lDigestEntries) {
			lAnswer.append(lMarker).append(' ');
			if (lPage == K_FIRST_PAGE && iNewCount > 0) {
				lAnswer.append(lMarkerNew).append(' ');
			}
			lAnswer.append(iDigestEntity.getDigest()).append("\n");
			--iNewCount;
		}

		aDigestBot.editMarkdownMessageWithKeyboard(lChatId,
			aCallbackQuery.getMessage().getMessageId(), lAnswer.toString(),
			getDigestKeyboard(lDigestEntries.getTotalElements()));
	}
}
