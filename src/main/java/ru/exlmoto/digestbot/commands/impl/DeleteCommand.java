package ru.exlmoto.digestbot.commands.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import org.springframework.util.NumberUtils;
import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.commands.BotAdminCommand;
import ru.exlmoto.digestbot.entities.DigestEntity;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.digestbot.yaml.impl.YamlLocalizationHelper;

@Component
public class DeleteCommand extends BotAdminCommand {
	private final int K_FIRST_PAGE = 0;

	private final Integer mDigestPerPage;
	private final Integer mChopStringShort;
	private final Integer mChopStringLong;

	public DeleteCommand(@Value("${digestbot.delete.posts}") final Integer aDigestPerPage,
						 @Value("${digestbot.delete.chop.short}") final Integer aChopStringShort,
						 @Value("${digestbot.delete.chop.long}") final Integer aChopStringLong) {
		mDigestPerPage = aDigestPerPage;
		mChopStringShort = aChopStringShort;
		mChopStringLong = aChopStringLong;
	}

	@Override
	public void run(final DigestBot aDigestBot,
	                final YamlLocalizationHelper aLocalizationHelper,
	                final ReceivedMessage aReceivedMessage) {
		String lCommandText = aReceivedMessage.getMessageText();
		final String[] lCommandWithArgs = lCommandText.split(" ");

		String lAnswer = aLocalizationHelper.getLocalizedString("error.delete.empty");
		final String lEllipsis = aLocalizationHelper.getLocalizedString("command.delete.ellipsis");

		if (lCommandWithArgs.length != 2) {
			final Page<DigestEntity> lDigestEntries = aDigestBot.getIDigestEntriesRepository().findAll(
					PageRequest.of(K_FIRST_PAGE, mDigestPerPage, Sort.by(Sort.Order.desc("id"))));
			if (!lDigestEntries.isEmpty()) {
				final StringBuilder lStringBuilder = new StringBuilder();
				lStringBuilder.append(aLocalizationHelper.getLocalizedString("command.delete.show.header"));
				lStringBuilder.append("```\n");
				lStringBuilder.append("id    user  chat  date  post\n");
				for (DigestEntity iDigest : lDigestEntries) {
					lStringBuilder.append(ellipsisString(String.valueOf(iDigest.getId()),
							mChopStringShort, lEllipsis, false)).append(' ');
					lStringBuilder.append(ellipsisString(String.valueOf(iDigest.getAuthor()),
							mChopStringShort, lEllipsis, false)).append(' ');
					lStringBuilder.append(ellipsisString(String.valueOf(iDigest.getChat()),
							mChopStringShort, lEllipsis, false)).append(' ');
					lStringBuilder.append(ellipsisString(String.valueOf(iDigest.getDate()),
							mChopStringShort, lEllipsis, false)).append(' ');
					lStringBuilder.append(ellipsisString(iDigest.getDigest(),
							mChopStringLong, lEllipsis, true)).append('\n');
				}
				lStringBuilder.append("\n```");
				lAnswer = lStringBuilder.toString();
			}
		} else {
			final String lStringDigestId = lCommandWithArgs[1];
			int lDigestId = -1;
			try {
				lDigestId = NumberUtils.parseNumber(lStringDigestId, Integer.class);
				aDigestBot.getIDigestEntriesRepository().deleteById(lDigestId);
				lAnswer = String.format(aLocalizationHelper.getLocalizedString("command.delete.ok"), lDigestId);
			} catch (NumberFormatException e) {
				aDigestBot.getBotLogger().error(e.toString());
				lAnswer = aLocalizationHelper.getLocalizedString("error.delete.format");
			} catch (Exception e) {
				aDigestBot.getBotLogger().error(e.toString());
				lAnswer = String.format(aLocalizationHelper.getLocalizedString("error.delete.error"), lDigestId);
			}
		}

		aDigestBot.sendMarkdownMessage(aReceivedMessage.getChatId(), aReceivedMessage.getMessageId(), lAnswer);
	}

	private String arrangeString(final String aString, final int aLength, final int aStringLength) {
		final StringBuilder lStringBuilder = new StringBuilder();
		for (int i = 0; i < aLength - aStringLength; ++i) {
			lStringBuilder.append(' ');
		}
		return aString + lStringBuilder.toString();
	}

	private String ellipsisString(final String aString, final int aLength,
								  final String aEllipsis, final boolean aRight) {
		final int lStringLength = aString.length();
		if (lStringLength < aLength) {
			return (aRight) ? aString : arrangeString(aString, aLength, lStringLength);
		}
		return (aRight) ?
				aString.substring(0, aLength - 1) + aEllipsis :
				aEllipsis + aString.substring(lStringLength - aLength + 1);
	}
}
