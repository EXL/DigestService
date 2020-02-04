package ru.exlmoto.digest.bot.worker;

import com.pengrad.telegrambot.model.User;

import org.jsoup.Jsoup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.repository.BotDigestUserRepository;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.rest.RestHelper;
import ru.exlmoto.digest.util.filter.FilterHelper;

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
			if (link != null) {
				for (Extension value : values()) {
					if (link.endsWith("." + value.name()) || link.endsWith("." + value.name().toUpperCase())) {
						return true;
					}
				}
			}
			return false;
		}
	}

	private final BotConfiguration config;
	private final RestHelper rest;
	private final FilterHelper filter;
	private final BotDigestUserRepository digestUserRepository;

	public AvatarWorker(BotConfiguration config,
	                    RestHelper rest,
	                    FilterHelper filter,
	                    BotDigestUserRepository digestUserRepository) {
		this.config = config;
		this.rest = rest;
		this.filter = filter;
		this.digestUserRepository = digestUserRepository;
	}

	@Scheduled(cron = "${cron.bot.avatars.update}")
	public void updateUserAvatars() {
		log.info("=> Start update user avatars.");

		try {
			digestUserRepository.findUsersWithUsername().forEach(digestUserEntity -> {
				String username = digestUserEntity.getUsername();
				log.info(String.format("==> Update avatar for: '%s'.", username));
				digestUserEntity.setAvatar(getAvatarLink(username.substring(1)));
				digestUserRepository.save(digestUserEntity);
			});
		} catch (DataAccessException dae) {
			log.error("Database error while updating user avatars.", dae);
		}

		log.info("=> End update user avatars.");
	}

	public String getAvatarLink(User user) {
		return getAvatarLink(user.username());
	}

	public String getAvatarLink(String username) {
		if (username != null) {
			String url = filter.checkLink(config.getTelegramShortUrl()) + username;

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
		return null;
	}

	protected Answer<String> isolateAvatar(String content) {
		String link = Jsoup.parse(content).getElementsByClass("tgme_page_photo_image").attr("src");
		if (!link.isEmpty()) {
			return Ok(link);
		}
		return Error("There is no avatar link on the page: '%s'.");
	}
}
