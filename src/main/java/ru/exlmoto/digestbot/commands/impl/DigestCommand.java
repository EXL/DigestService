package ru.exlmoto.digestbot.commands.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.commands.BotCommand;
import ru.exlmoto.digestbot.entities.DigestEntity;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.digestbot.yaml.impl.YamlLocalizationHelper;

@Component
public class DigestCommand extends BotCommand {
	private final Integer mDigestPerPage;
	private final int K_FIRST_PAGE = 0;

	public DigestCommand(@Value("${digestbot.digest.pages.posts}") final Integer aDigestPerPage) {
		mDigestPerPage = aDigestPerPage;
	}

	@Override
	public void run(final DigestBot aDigestBot,
	                final YamlLocalizationHelper aLocalizationHelper,
	                final ReceivedMessage aReceivedMessage) {
		final Long lChatId = aReceivedMessage.getChatId();
		final Integer lMessageId = aReceivedMessage.getMessageId();
		final YamlLocalizationHelper lYamlLocalizationHelper = aDigestBot.getLocalizationHelper();
		final Pair<Boolean, String> lUsername = aReceivedMessage.getAvailableUsername();

		final String lHello = lYamlLocalizationHelper.getRandomLocalizedString("command.digest.hello", lUsername);
		final String lEmpty = lYamlLocalizationHelper.getRandomLocalizedString("command.digest.empty", lUsername);
		final String lHeader = lYamlLocalizationHelper.getRandomLocalizedString("command.digest.header");

		final int lPage = K_FIRST_PAGE;

		final Page<DigestEntity> lDigestEntries =
			aDigestBot.getIDigestEntriesRepository().findDigestEntitiesByChat(PageRequest.of(lPage, mDigestPerPage,
				Sort.by(Order.desc("id"))), lChatId);
		final boolean lIsDigestListEmpty = lDigestEntries.isEmpty();

		// System.out.println(lDigestEntries.getNumberOfElements());
		// System.out.println(lDigestEntries.getTotalElements());

		StringBuilder lAnswer = new StringBuilder();

		if (!lIsDigestListEmpty) {
			lAnswer.append(lHello).append('\n').append(lHeader).append(" 1:\n");
		} else {
			lAnswer.append(lEmpty);
		}

		final String lMarker = lYamlLocalizationHelper.getLocalizedString("command.digest.marker");
		final String lMarkerNew = lYamlLocalizationHelper.getLocalizedString("command.digest.marker.new");

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

		aDigestBot.sendMarkdownMessageWithKeyboard(lChatId, lMessageId, lAnswer.toString(),
			(!lIsDigestListEmpty) ?
				aDigestBot.getDigestKeyboard().getDigestKeyboard(lDigestEntries.getTotalElements()) : null);
	}
}
