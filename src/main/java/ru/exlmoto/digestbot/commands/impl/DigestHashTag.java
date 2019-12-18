package ru.exlmoto.digestbot.commands.impl;

import org.jsoup.Jsoup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.commands.BotCommand;
import ru.exlmoto.digestbot.entities.DigestEntity;
import ru.exlmoto.digestbot.entities.DigestUserEntity;
import ru.exlmoto.digestbot.services.impl.AvatarService;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.digestbot.yaml.impl.YamlLocalizationHelper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DigestHashTag extends BotCommand {
	private final AvatarService mAvatarService;

	private final Long mMotoFanChatId;
	private final String mMotoFanSlug;

	@Autowired
	public DigestHashTag(final AvatarService aAvatarService,
						 @Value("${digestbot.chat.motofan}") final Long aMotoFanChatId,
						 @Value("${digestbot.chat.motofan.slug}") final String aMotoFanSlug) {
		mAvatarService = aAvatarService;
		mMotoFanChatId = aMotoFanChatId;
		mMotoFanSlug = aMotoFanSlug;
	}

	@Override
	public void run(final DigestBot aDigestBot,
	                final YamlLocalizationHelper aLocalizationHelper,
	                final ReceivedMessage aReceivedMessage) {
		// Remove hash tags.
		final String lMessageText = handleMessageText(aReceivedMessage.getMessageText());
		if (!lMessageText.isEmpty()) {
			aDigestBot.sendSimpleMessage(aReceivedMessage.getChatId(), aReceivedMessage.getMessageId(),
					aLocalizationHelper.getRandomLocalizedString("hashtag.digest.ok",
							aReceivedMessage.getAvailableUsername()));

			commitDigestToDataBase(aDigestBot, aReceivedMessage, lMessageText);
			commitUserToDataBase(aDigestBot, aReceivedMessage);

			if (aReceivedMessage.getChatId().equals(mMotoFanChatId)) {
				sendDigestToSubscribers(aDigestBot, aLocalizationHelper, aReceivedMessage, lMessageText);
			}
		}
	}

	private void commitDigestToDataBase(final DigestBot aDigestBot,
	                                    final ReceivedMessage aReceivedMessage,
	                                    final String aMessageText) {
		final DigestEntity lDigestEntity = new DigestEntity();
		lDigestEntity.setAuthor(aReceivedMessage.getMessageUsernameId());
		lDigestEntity.setDigest(aMessageText);
		lDigestEntity.setHtml(activateUsersInDigest(activateLinksInDigest(aMessageText)));
		lDigestEntity.setDate(aReceivedMessage.getMessageDate().longValue());
		lDigestEntity.setChat(aReceivedMessage.getChatId());
		lDigestEntity.setMessage_id(aReceivedMessage.getMessageId().longValue());

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
			lDigestUser.setUsername_html(activateLinksInUsername(lCanonicalUserName));
			lDigestUser.setUsernameOk(true);
		} else {
			lDigestUser.setUsername_html(lCanonicalUserName);
			lDigestUser.setUsernameOk(false);
		}
		lDigestUser.setAvatarLink(
			mAvatarService.getAvatarUrlByUserName(aReceivedMessage.getMessageUsername()).getSecond());
		aDigestBot.getIDigestUsersRepository().save(lDigestUser);
	}

	private String activateLinksInUsername(final String aUsername) {
		return "<a href=\"https://t.me/" + aUsername + "\" title=\"@" + aUsername + "\" target=\"_blank\">" +
			aUsername + "</a>";
	}

	private String activateUsersInDigest(final String aDigest) {
		return activateLinkAux(
			aDigest,
			"\\B@[a-z0-9_-]+",
			"<a href=\"https://t.me/%1$s\" title=\"%1$s\" target=\"_blank\">%1$s</a>"
		).replaceAll("https://t.me/@", "https://t.me/");
	}

	// https://stackoverflow.com/a/28269120
	private String activateLinksInDigest(final String aDigest) {
		return activateLinkAux(
			aDigest,
			"((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)",
			"<a href=\"%1$s\" title=\"%1$s\" target=\"_blank\">%1$s</a>"
		);
	}

	private String activateLinkAux(final String aContent,
	                               final String aRegEx,
	                               final String aReplacement) {
		final Matcher lMatcher = Pattern.compile(aRegEx,
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL).matcher(aContent);
		final StringBuffer lStringBuffer = new StringBuffer();
		while (lMatcher.find()) {
			lMatcher.appendReplacement(lStringBuffer, String.format(aReplacement, lMatcher.group(0).trim()));
		}
		lMatcher.appendTail(lStringBuffer);
		return lStringBuffer.toString();
	}

	private void sendDigestToSubscribers(final DigestBot aDigestBot,
										 final YamlLocalizationHelper aYamlLocalizationHelper,
										 final ReceivedMessage aReceivedMessage,
										 final String aDigest) {
		new Thread(() -> aDigestBot.getIDigestSubscribersRepository().findAll().forEach((aSubscriberEntity) -> {
			aDigestBot.sendHtmlMessage(aSubscriberEntity.getSubscription(), null,
					formatDigestSubscriberPost(aYamlLocalizationHelper, aReceivedMessage, aDigest));
			try {
				Thread.sleep(aDigestBot.getBotInlineCoolDown() * 1000);
			} catch (InterruptedException e) {
				aDigestBot.getBotLogger()
						.error(String.format("Cannot delay sendDigestToSubscribers() thread: '%s'.", e.toString()));
			}
		})).start();
	}

	private String formatDigestSubscriberPost(final YamlLocalizationHelper aYamlLocalizationHelper,
											  final ReceivedMessage aReceivedMessage,
											  final String aDigest) {
		String aUserName = aReceivedMessage.getAvailableUsername().getSecond();
		if (aReceivedMessage.getAvailableUsername().getFirst()) {
			aUserName = '@' + aUserName;
		}
		return aYamlLocalizationHelper.getLocalizedString("hashtag.digest.subscribe.title") +
				"\n\n<b>" + aUserName + "</b> " +
				aYamlLocalizationHelper.getLocalizedString("hashtag.digest.subscribe.writing") +
				" (" + getDateFromTimeStamp(aReceivedMessage.getMessageDate().longValue()) + "):\n<i>" + aDigest +
				"</i>\n\n" + aYamlLocalizationHelper.getLocalizedString("hashtag.digest.subscribe.read") +
				" <a href=\"" + "https://t.me/" + mMotoFanSlug + "/" + aReceivedMessage.getMessageId() + "\">" +
				aYamlLocalizationHelper.getLocalizedString("hashtag.digest.subscribe.link") + "</a>";
	}

	private String getDateFromTimeStamp(final Long aTimeStamp) {
		return DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
				.withZone(ZoneId.systemDefault()).format(Instant.ofEpochSecond(aTimeStamp));
	}

	private String handleMessageText(final String aMessageText) {
		// Remove all HTML-tags.
		return Jsoup.parse(aMessageText.replaceAll("#digest|#news", "").trim()).text();
	}
}
