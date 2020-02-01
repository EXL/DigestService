package ru.exlmoto.digest.bot.worker;

import com.pengrad.telegrambot.model.User;

import org.jsoup.Jsoup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.telegram.BotTelegram;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.rest.RestHelper;
import ru.exlmoto.digest.util.text.FilterTextHelper;

import static ru.exlmoto.digest.util.Answer.Ok;
import static ru.exlmoto.digest.util.Answer.Error;

@Component
public class AvatarWorker {
	private final Logger log = LoggerFactory.getLogger(AvatarWorker.class);

	protected enum Extension {
		jpg,
		png,
		gif;

		public static boolean checkImageExtension(String link) {
			for (Extension value: values()) {
				if (link.endsWith("." + value.name()) || link.endsWith("." + value.name().toUpperCase())) {
					return true;
				}
			}
			return false;
		}
	}

	private final BotConfiguration config;
	private final RestHelper rest;
	private final FilterTextHelper filterText;

	public AvatarWorker(BotConfiguration config, RestHelper rest, FilterTextHelper filterText) {
		this.config = config;
		this.rest = rest;
		this.filterText = filterText;
	}

	public String getAvatarLink(User user) {
		String username = user.username();
		String url = filterText.checkLink(config.getTelegramShortUrl()) + username;

		if (username != null) {
			Answer<String> resContent = rest.getRestResponse(url);
			if (resContent.ok()) {
				Answer<String> resAvatarLink = isolateAvatar(resContent.answer());
				if (resAvatarLink.ok()) {
					String avatarLink = resAvatarLink.answer();
					if (!Extension.checkImageExtension(avatarLink)) {
						log.warn(String.format("Avatar link ends with no image extension: %s.", avatarLink));
					}
					return avatarLink;
				} else {
					log.error(String.format(resAvatarLink.error(), url));
				}
			} else {
				log.error(String.format("Cannot get avatar for user '%s', reason: '%s'.",
					username, resContent.error()));
			}
		}
		// TODO: Default telegram avatar!
		return "default-avatar link";
	}

	protected Answer<String> isolateAvatar(String content) {
		String link = Jsoup.parse(content).getElementsByClass("tgme_page_photo_image").attr("src");
		if (!link.isEmpty()) {
			return Ok(link);
		}
		return Error("There is no avatar link on the page: '%s'.");
	}
}