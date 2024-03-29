/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 EXL <exlmotodev@gmail.com>
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

package ru.exlmoto.digest.bot.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.GetMe;
import com.pengrad.telegrambot.response.GetMeResponse;
import com.pengrad.telegrambot.request.GetChatAdministrators;
import com.pengrad.telegrambot.response.GetChatAdministratorsResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.entity.BotSetupEntity;
import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.util.Answer;

import javax.annotation.PostConstruct;

import java.util.Optional;

import static ru.exlmoto.digest.util.Answer.Ok;
import static ru.exlmoto.digest.util.Answer.Error;

@Component
public class BotTelegram {
	private final Logger log = LoggerFactory.getLogger(BotTelegram.class);

	private final BotConfiguration config;
	private final DatabaseService service;
	private final BotHelper helper;

	private TelegramBot bot;
	private String username;
	private String firstName;
	private long id;

	public BotTelegram(BotConfiguration config, DatabaseService service, BotHelper helper) {
		this.config = config;
		this.service = service;
		this.helper = helper;
	}

	@PostConstruct
	private void setUp() {
		if (config.isInitialize()) {
			log.info("=> Start initialize Telegram Bot...");

			bot = new TelegramBot(config.getToken());
			GetMeResponse response = checkTelegramBotApiConnection();
			if (response.isOk()) {
				User botUser = response.user();
				Assert.notNull(botUser, "Cannot initialize Telegram Bot, bot user is null.");

				username = helper.getValidUsername(botUser);
				firstName = botUser.firstName();
				id = botUser.id();

				log.info(String.format("==> Hello! My name is '%s'.", firstName));
				log.info(String.format("==> My id is '%d'.", id));
				log.info(String.format("==> And my username is '%s'.", username));

				overrideBotSettingsFromDataBase();

				log.info("=> End initialize Telegram Bot.");
			} else {
				throw new IllegalStateException(String.format("Cannot initialize Telegram Bot, error: '%d, %s'.",
					response.errorCode(), response.description()));
			}
		} else {
			log.info("=> Telegram Bot initialize disable.");
		}
	}

	private GetMeResponse checkTelegramBotApiConnection() {
		try {
			return bot.execute(new GetMe());
		} catch (RuntimeException re) {
			log.error("Cannot connect to the Telegram Bot API services. Shutdown.", re);
			throw re;
		}
	}

	private void overrideBotSettingsFromDataBase() {
		Answer<BotSetupEntity> res = getBotSetup();
		if (res.ok()) {
			BotSetupEntity setup = res.answer();
			log.info("===> Start apply settings from database.");

			boolean logUpdates = setup.isLogUpdates();
			log.info(String.format("====> Set 'bot.log-updates': '%b', default: '%b'.",
				logUpdates, config.isLogUpdates()));
			config.setLogUpdates(logUpdates);

			boolean showGreetings = setup.isShowGreetings();
			log.info(String.format("====> Set 'bot.show-greetings': '%b', default: '%b'.",
				showGreetings, config.isShowGreetings()));
			config.setShowGreetings(showGreetings);

			boolean sendMotofanBirthdays = setup.isSendMotofanBirthdays();
			log.info(String.format("====> Set 'bot.send-motofan-birthdays': '%b', default: '%b'.",
				sendMotofanBirthdays, config.isSendMotofanBirthdays()));
			config.setSendMotofanBirthdays(sendMotofanBirthdays);

			boolean useButtonCaptcha = setup.isUseButtonCaptcha();
			log.info(String.format("====> Set 'bot.use-button-captcha': '%b', default: '%b'.",
				useButtonCaptcha, config.isUseButtonCaptcha()));
			config.setUseButtonCaptcha(useButtonCaptcha);

			boolean silentMod = setup.isSilentMode();
			log.info(String.format("====> Set 'bot.silent': '%b', default: '%b'.",
				silentMod, config.isSilent()));
			config.setSilent(silentMod);

			log.info("===> End apply settings from database.");
		}
	}

	private Answer<BotSetupEntity> getBotSetup() {
		try {
			Optional<BotSetupEntity> botSetupEntityOptional = service.getSettings();
			if (botSetupEntityOptional.isPresent()) {
				return Ok(botSetupEntityOptional.get());
			} else {
				log.warn("===> Telegram Bot settings table does not exist. First run?");
				createTelegramBotSettings();
				return Error("Error!");
			}
		} catch (DataAccessException dae) {
			log.error("Cannot operate with Telegram Bot settings object from database.", dae);
		}
		throw new IllegalStateException("Telegram Bot settings is damaged.");
	}

	protected void createTelegramBotSettings() {
		BotSetupEntity setup = new BotSetupEntity();
		setup.setId(BotSetupEntity.SETUP_ROW);
		updateTelegramBotSettingsAux(setup);
	}

	public void updateTelegramBotSettings() {
		service.getSettings().ifPresent(this::updateTelegramBotSettingsAux);
	}

	private void updateTelegramBotSettingsAux(BotSetupEntity setup) {
		setup.setLogUpdates(config.isLogUpdates());
		setup.setShowGreetings(config.isShowGreetings());
		setup.setSendMotofanBirthdays(config.isSendMotofanBirthdays());
		setup.setUseButtonCaptcha(config.isUseButtonCaptcha());
		setup.setSilentMode(config.isSilent());

		log.info(String.format("====> Start save settings: '%s'.", setup.toString()));
		service.saveSettings(setup);
		log.info("====> End save settings.");
	}

	public GetChatAdministratorsResponse chatAdministrators(long chatId) {
		try {
			return bot.execute(new GetChatAdministrators(chatId));
		} catch (RuntimeException re) {
			log.error(String.format("Cannot get administrator list for '%d' chat.", chatId));
			return null;
		}
	}

	public TelegramBot getBot() {
		return bot;
	}

	public String getUsername() {
		return username;
	}

	public String getFirstName() {
		return firstName;
	}

	public long getId() {
		return id;
	}
}
