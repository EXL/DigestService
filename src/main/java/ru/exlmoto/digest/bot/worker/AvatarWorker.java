/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL <exlmotodev@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.exlmoto.digest.bot.worker;

import com.pengrad.telegrambot.model.User;

import org.jsoup.Jsoup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.service.DatabaseService;
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
				Extension[] extensions = values();
				for (Extension value : extensions) {
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
	private final DatabaseService service;

	public AvatarWorker(BotConfiguration config,
	                    RestHelper rest,
	                    FilterHelper filter,
	                    DatabaseService service) {
		this.config = config;
		this.rest = rest;
		this.filter = filter;
		this.service = service;
	}

	@Scheduled(cron = "${cron.bot.avatars.update}")
	public void updateUserAvatars() {
		log.info("=> Start update user avatars.");

		try {
			service.getDigestUsersWithUsername().forEach(digestUserEntity -> {
				String username = digestUserEntity.getUsername();
				log.info(String.format("==> Update avatar for: '%s'.", username));
				digestUserEntity.setAvatar(getAvatarLink(username.substring(1)));
				service.saveDigestUser(digestUserEntity);
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
