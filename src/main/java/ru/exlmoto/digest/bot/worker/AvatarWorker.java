package ru.exlmoto.digest.bot.worker;

import com.pengrad.telegrambot.model.User;

import org.jsoup.Jsoup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.rest.RestHelper;
import ru.exlmoto.digest.util.text.FilterTextHelper;

@Component
public class AvatarWorker {
	private final Logger log = LoggerFactory.getLogger(AvatarWorker.class);

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
		if (username != null) {
			Answer<String> res =
				rest.getRestResponse(filterText.checkLink(config.getTelegramShortUrl()) + username + "ASDas");
			if (res.ok()) {
				return isolateAvatar(res.answer());
			} else {
				log.error(String.format("Cannot get avatar for user '%s', reason: '%s'.", username, res.error()));
			}
		}
		// TODO: Default telegram avatar!
		return null;
	}

	protected String isolateAvatar(String content) {
		log.info(Jsoup.parse(content).getElementsByClass("tgme_page_photo_image").attr("src"));
		// TODO: Check extension????
		// TODO: Write tests with examples???
		return "this is avatar";
	}
}
