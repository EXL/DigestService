package ru.exlmoto.digestbot.commands.impl;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.commands.BotCommand;
import ru.exlmoto.digestbot.entities.DigestEntity;
import ru.exlmoto.digestbot.entities.DigestUserEntity;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.digestbot.yaml.impl.YamlLocalizationHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DigestHashTag extends BotCommand {
	@Override
	public void run(final DigestBot aDigestBot,
	                final YamlLocalizationHelper aLocalizationHelper,
	                final ReceivedMessage aReceivedMessage) {
		// Remove hash tags.
		String lMessageText = aReceivedMessage.getMessageText().replaceAll("#digest|#news", "").trim();
		if (!lMessageText.isEmpty()) {
			aDigestBot.sendSimpleMessage(aReceivedMessage.getChatId(), aReceivedMessage.getMessageId(),
					aLocalizationHelper.getRandomLocalizedString("hashtag.digest.ok",
							aReceivedMessage.getAvailableUsername()));

			commitDigestToDataBase(aDigestBot, aReceivedMessage, lMessageText);
			commitUserToDataBase(aDigestBot, aReceivedMessage);
		}
	}

	private void commitDigestToDataBase(final DigestBot aDigestBot,
	                                    final ReceivedMessage aReceivedMessage,
	                                    final String aMessageText) {
		final DigestEntity lDigestEntity = new DigestEntity();
		lDigestEntity.setAuthor(aReceivedMessage.getMessageUsernameId());
		lDigestEntity.setDigest(aMessageText);
		/// TODO: HTMLize text
		/// UserCasts also
		lDigestEntity.setHtml(aMessageText);
		lDigestEntity.setDate(aReceivedMessage.getMessageDate().longValue());
		lDigestEntity.setChat(aReceivedMessage.getChatId());

		aDigestBot.getIDigestEntriesRepository().save(lDigestEntity);
	}

	private void commitUserToDataBase(final DigestBot aDigestBot,
	                                  final ReceivedMessage aReceivedMessage) {
		final DigestUserEntity lDigestUser = new DigestUserEntity();
		lDigestUser.setId(aReceivedMessage.getMessageUsernameId());
		final Pair<Boolean, String> lUserName = aReceivedMessage.getAvailableUsername();
		final String lCanonicalUserName = lUserName.getSecond();
		lDigestUser.setUsername(lCanonicalUserName);
		if (lUserName.getFirst()) {
			lDigestUser.setUsername_html(getHtmlUsername(lCanonicalUserName));
		} else {
			lDigestUser.setUsername_html(lCanonicalUserName);
		}

		// TODO: Get avatar from internet
		lDigestUser.setAvatar(aReceivedMessage.getMessageText());
		aDigestBot.getIDigestUsersRepository().save(lDigestUser);
	}

	private String getHtmlUsername(final String aUsername) {
		return "<a href=\"https://t.me/" + aUsername + "\" title=\"@" + aUsername + "\" target=\"_blank\">" +
				aUsername +
				"</a>";
	}
}
