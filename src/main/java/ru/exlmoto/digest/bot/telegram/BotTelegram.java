package ru.exlmoto.digest.bot.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.GetMe;
import com.pengrad.telegrambot.response.GetMeResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.entity.SetupBotEntity;
import ru.exlmoto.digest.repository.SetupBotRepository;
import ru.exlmoto.digest.util.Answer;

import javax.annotation.PostConstruct;

import static ru.exlmoto.digest.util.Answer.Ok;
import static ru.exlmoto.digest.util.Answer.Error;

@Component
public class BotTelegram {
	private final Logger log = LoggerFactory.getLogger(BotTelegram.class);

	private final BotConfiguration config;
	private final SetupBotRepository repository;

	private TelegramBot bot;
	private String username;
	private String firstName;
	private int id;

	public BotTelegram(BotConfiguration config, SetupBotRepository repository) {
		this.config = config;
		this.repository = repository;
	}

	@PostConstruct
	private void setUp() {
		log.info("=> Start initialize Telegram Bot...");

		bot = new TelegramBot(config.getToken());
		GetMeResponse response = bot.execute(new GetMe());
		if (response.isOk()) {
			User botUser = response.user();
			Assert.notNull(botUser, "Cannot initialize Telegram Bot, bot user is null.");

			username = botUser.username();
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
	}

	private void overrideBotSettingsFromDataBase() {
		Answer<SetupBotEntity> res = getBotSetup();
		if (res.ok()) {
			SetupBotEntity setup = res.answer();
			log.info("===> Start apply settings from database.");

			boolean logUpdates = setup.isLogUpdates();
			log.info(String.format("====> Set 'bot.log-updates': '%b', default: '%b'.",
				logUpdates, config.isLogUpdates()));
			config.setLogUpdates(logUpdates);

			boolean showGreetings = setup.isShowGreetings();
			log.info(String.format("====> Set 'bot.show-greetings': '%b', default: '%b'.",
				showGreetings, config.isShowGreetings()));
			config.setShowGreetings(showGreetings);

			boolean silentMod = setup.isSilentMode();
			log.info(String.format("====> Set 'bot.silent': '%b', default: '%b'.",
				silentMod, config.isSilent()));
			config.setSilent(silentMod);

			log.info("===> End apply settings from database.");
		} else {
			log.warn(res.error());
		}
	}

	private Answer<SetupBotEntity> getBotSetup() {
		try {
			SetupBotEntity setupBotEntity = repository.getSetupBot();
			if (setupBotEntity != null) {
				return Ok(setupBotEntity);
			} else {
				saveTelegramBotSettings();
				return Error("===> Creating Telegram Bot settings table because it does not exist. First run?");
			}
		} catch (DataAccessException dae) {
			log.error("Cannot operate with Telegram Bot settings object from database.", dae);
		}
		throw new IllegalStateException("Telegram Bot settings is damaged.");
	}

	public void saveTelegramBotSettings() {
		repository.save(new SetupBotEntity(config.isLogUpdates(), config.isShowGreetings(), config.isSilent()));
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

	public int getId() {
		return id;
	}
}
