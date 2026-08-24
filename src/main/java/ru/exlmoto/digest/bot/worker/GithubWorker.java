/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2026 EXL <exlmotodev@gmail.com>
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.entity.BotSubGithubEntity;
import ru.exlmoto.digest.github.GithubService;
import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import java.util.List;

@Component
public class GithubWorker {
	private final Logger log = LoggerFactory.getLogger(GithubWorker.class);

	private final GithubService githubService;
	private final DatabaseService databaseService;
	private final BotSender sender;
	private final BotConfiguration config;

	@Value("${github.message-thread-id:1100646}")
	private int githubMessageThreadId;

	public GithubWorker(GithubService githubService,
	                     DatabaseService databaseService,
	                     BotSender sender,
	                     BotConfiguration config,
	                     LocaleHelper locale) {
		this.githubService = githubService;
		this.databaseService = databaseService;
		this.sender = sender;
		this.config = config;
	}

	@Scheduled(cron = "${cron.bot.github.report}")
	public void workOnGithubCommits() {
		try {
			List<BotSubGithubEntity> subscribers = databaseService.getAllGithubSubs();
			if (!subscribers.isEmpty()) {
				log.info("=> Start GitHub crawler work. Get new commits.");
				List<String> githubCommits = githubService.getNewGithubCommitsPosts();
				if (!githubCommits.isEmpty()) {
					log.info("=> GitHub crawler work. Send new commits.");
					sendNewGithubCommits(githubCommits, subscribers);
					log.info("=> End GitHub crawler work. Commits were sent.");
				} else {
					log.info("=> End GitHub crawler work. No new commits.");
				}
			} else {
				log.info("=> GitHub subscriber list is empty, GitHub commits crawler disabled.");
			}
		} catch (DataAccessException dae) {
			log.error("=> Cannot get GitHub subscribe object from database.", dae);
		} catch (RuntimeException re) {
			log.error("=> Runtime exception on GitHub Commits sender thread.", re);
		}
	}

	private void sendNewGithubCommits(List<String> githubCommits, List<BotSubGithubEntity> subscribers) {
		githubCommits.forEach(post -> subscribers.forEach(subscriber -> {
			long chatId = subscriber.getSubscription();
			log.info(String.format("=> Send GitHub Commit to chat '%d', commits: '%d', subscribers: '%d'.",
				chatId, githubCommits.size(), subscribers.size()));
			if (config.getMotofanChatId() != chatId) {
				sender.sendHtml(chatId, post);
			} else {
				sender.sendHtml(chatId, githubMessageThreadId, post);
			}
			try {
				Thread.sleep(config.getMessageDelay() * 1000L);
			} catch (InterruptedException ie) {
				throw new RuntimeException(ie);
			}
		}));
	}
}
