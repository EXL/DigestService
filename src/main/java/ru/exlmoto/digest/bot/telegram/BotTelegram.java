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
import ru.exlmoto.digest.entity.BotSetupEntity;
import ru.exlmoto.digest.repository.BotSetupRepository;
import ru.exlmoto.digest.util.Answer;

import javax.annotation.PostConstruct;

import java.util.Optional;

import static ru.exlmoto.digest.util.Answer.Ok;
import static ru.exlmoto.digest.util.Answer.Error;

@Component
public class BotTelegram {
	private final Logger log = LoggerFactory.getLogger(BotTelegram.class);

	private final BotConfiguration config;
	private final BotSetupRepository repository;

	private TelegramBot bot;
	private String username;
	private String firstName;
	private int id;

	public BotTelegram(BotConfiguration config, BotSetupRepository repository) {
		this.config = config;
		this.repository = repository;
	}

	@PostConstruct
	private void setUp() {
		log.info("=> Start initialize Telegram Bot...");

		bot = new TelegramBot(config.getToken());
		GetMeResponse response = checkTelegramBotApiConnection();
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

			boolean silentMod = setup.isSilentMode();
			log.info(String.format("====> Set 'bot.silent': '%b', default: '%b'.",
				silentMod, config.isSilent()));
			config.setSilent(silentMod);

			log.info("===> End apply settings from database.");
		}
	}

	private Answer<BotSetupEntity> getBotSetup() {
		try {
			Optional<BotSetupEntity> botSetupEntityOptional = repository.getSetupBot();
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
		repository.getSetupBot().ifPresent(this::updateTelegramBotSettingsAux);
	}

	private void updateTelegramBotSettingsAux(BotSetupEntity setup) {
		setup.setLogUpdates(config.isLogUpdates());
		setup.setShowGreetings(config.isShowGreetings());
		setup.setSilentMode(config.isSilent());

		log.info(String.format("====> Start save settings: '%s'.", setup.toString()));
		repository.save(setup);
		log.info("====> End save settings.");
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
