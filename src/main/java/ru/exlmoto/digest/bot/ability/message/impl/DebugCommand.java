package ru.exlmoto.digest.bot.ability.message.impl;

import com.pengrad.telegrambot.model.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.message.MessageAdminAbility;
import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.telegram.BotTelegram;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.bot.worker.AvatarWorker;
import ru.exlmoto.digest.bot.worker.CallbackQueriesWorker;
import ru.exlmoto.digest.bot.worker.DigestWorker;
import ru.exlmoto.digest.bot.worker.MotofanWorker;
import ru.exlmoto.digest.exchange.ExchangeService;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

@Component
public class DebugCommand extends MessageAdminAbility {
	private final Logger log = LoggerFactory.getLogger(DebugCommand.class);

	private final BotTelegram telegram;
	private final BotConfiguration config;
	private final ExchangeService exchangeService;
	private final MotofanWorker motofanWorker;
	private final AvatarWorker avatarWorker;
	private final DigestWorker digestWorker;
	private final CallbackQueriesWorker callbackQueriesWorker;

	public DebugCommand(BotTelegram telegram,
	                    BotConfiguration config,
	                    ExchangeService exchangeService,
	                    MotofanWorker motofanWorker,
	                    AvatarWorker avatarWorker,
	                    DigestWorker digestWorker,
	                    CallbackQueriesWorker callbackQueriesWorker) {
		this.telegram = telegram;
		this.config = config;
		this.exchangeService = exchangeService;
		this.motofanWorker = motofanWorker;
		this.avatarWorker = avatarWorker;
		this.digestWorker = digestWorker;
		this.callbackQueriesWorker = callbackQueriesWorker;
	}

	private enum Option {
		VRates,
		VPosts,
		VShredder,
		VAvatars,
		VQueries,
		BLogUpdates,
		BGreetings,
		BSilent,
		UNHANDLED_DEFAULT
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocaleHelper locale, Message message) {
		String text = locale.i18n("bot.command.debug.help") + addStatus(locale);
		String[] arguments = message.text().split(" ");
		if (arguments.length == 2) {
			switch (checkOption(arguments[1])) {
				case VRates: { text = processRates(locale); break; }
				case VPosts: { text = processPosts(locale); break; }
				case VShredder: { text = processShredder(locale); break; }
				case VAvatars: { text = processAvatars(locale); break; }
				case VQueries: { text = processQueries(locale); break; }
				case BLogUpdates: { text = toggleUpdates(locale); break; }
				case BGreetings: { text = toggleGreetings(locale); break; }
				case BSilent: { text = toggleSilent(locale); break; }
			}
		}
		sender.replyMarkdown(message.chat().id(), message.messageId(), text);
	}

	private Option checkOption(String argument) {
		try {
			return Option.valueOf(argument);
		} catch (IllegalArgumentException iae) {
			return Option.UNHANDLED_DEFAULT;
		}
	}

	private String processRates(LocaleHelper locale) {
		exchangeService.updateAllRates();
		return locale.i18n("bot.command.debug.data");
	}

	private String processPosts(LocaleHelper locale) {
		motofanWorker.workOnMotofanPosts();
		return locale.i18n("bot.command.debug.data");
	}

	private String processShredder(LocaleHelper locale) {
		digestWorker.obsoleteDataShredder();
		return locale.i18n("bot.command.debug.data");
	}

	private String processAvatars(LocaleHelper locale) {
		avatarWorker.updateUserAvatars();
		return locale.i18n("bot.command.debug.data");
	}

	private String processQueries(LocaleHelper locale) {
		callbackQueriesWorker.clearCallbackQueriesMap();
		return locale.i18n("bot.command.debug.data");
	}

	private String addStatus(LocaleHelper locale) {
		return
			"\n\n" + locale.i18n("bot.command.debug.values") + "\n```\n" +
			Option.BLogUpdates + ": " + config.isLogUpdates() + "\n" +
			Option.BGreetings + ": " + config.isShowGreetings() + "\n" +
			Option.BSilent + ": " + config.isSilent() + "\n```";
	}

	private String toggleUpdates(LocaleHelper locale) {
		boolean value = !config.isLogUpdates();
		config.setLogUpdates(value);
		return saveSettingsToDataBase(locale, Option.BLogUpdates, value);
	}

	private String toggleGreetings(LocaleHelper locale) {
		boolean value = !config.isShowGreetings();
		config.setShowGreetings(value);
		return saveSettingsToDataBase(locale, Option.BGreetings, value);
	}

	private String toggleSilent(LocaleHelper locale) {
		boolean value = !config.isSilent();
		config.setSilent(value);
		return saveSettingsToDataBase(locale, Option.BSilent, value);
	}

	private String saveSettingsToDataBase(LocaleHelper locale, Option variableName, boolean value) {
		try {
			telegram.updateTelegramBotSettings();
			return String.format(locale.i18n("bot.command.debug.variable"), variableName, value);
		} catch (DataAccessException dae) {
			log.error("Cannot save Telegram Bot settings object to database.", dae);
			return String.format(locale.i18n("bot.error.database"), dae.getLocalizedMessage());
		}
	}
}
