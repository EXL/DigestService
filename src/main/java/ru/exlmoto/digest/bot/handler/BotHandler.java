package ru.exlmoto.digest.bot.handler;

import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.EntityType;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import ru.exlmoto.digest.bot.ability.BotAbility;
import ru.exlmoto.digest.bot.ability.BotAbilityFactory;
import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

@Slf4j
@Component
public class BotHandler {
	private int delay = 0;
	private HashMap<Long, Long> callbackQueriesMap = new HashMap<>();

	private final BotConfiguration config;
	private final BotSender sender;
	private final LocalizationHelper locale;
	private final BotHelper helper;
	private final BotAbilityFactory abilityFactory;

	public BotHandler(BotConfiguration config, BotHelper helper, BotAbilityFactory abilityFactory) {
		this.config = config;
		this.helper = helper;
		this.locale = helper.getLocale();
		this.sender = helper.getSender();
		this.abilityFactory = abilityFactory;
	}

	@Scheduled(cron = "${cron.bot.callbacks.clear}")
	public void clearCallbackQueriesMap() {
		log.info("=> Start clear Callback Queries Map.");
		callbackQueriesMap.clear();
		log.info("=> End clear Callback Queries Map.");
	}

	public void onCommand(Message message) {
		message.getEntities().stream()
			.filter(entity -> entity.getType().equals(EntityType.BOTCOMMAND) && entity.getOffset() == 0)
			.forEach(entity -> abilityFactory.getAbility(entity.getText())
				.ifPresent(commandAbility -> commandAbility.process(helper, message)));
	}

	public void onHashTag(Message message) {
		List<String> hashTags = new ArrayList<>();
		message.getEntities().stream().filter(entity -> entity.getType().equals(EntityType.HASHTAG)).forEach(
			entity -> hashTags.add(entity.getText())
		);
		for (String hashTag : hashTags) {
			Optional<BotAbility> optionalBotAbility = abilityFactory.getAbility(hashTag);
			if (optionalBotAbility.isPresent()) {
				optionalBotAbility.ifPresent(hashTagAbility -> hashTagAbility.process(helper, message));
				break;
			}
		}
	}

	public void onCallbackQuery(CallbackQuery callbackQuery) {
		int cooldown = config.getCallbackCooldown();
		if (config.isDebugUseStack()) {
			long chatId = callbackQuery.getMessage().getChatId();
			long currentTime = helper.getCurrentUnixTime();
			if (callbackQueriesMap.containsKey(chatId) || callbackQueriesMap.get(chatId) <= currentTime - cooldown) {
				callbackQueriesMap.put(chatId, currentTime);
				// HandleCallbackQuery
			} else {
				sendCooldownAnswer(callbackQuery.getId(),
					cooldown - (currentTime - callbackQueriesMap.get(chatId)));
			}
		} else {
			if (delay == 0) {
				delayCooldown(cooldown);
				// HandleCallbackQuery
			} else {
				sendCooldownAnswer(callbackQuery.getId(), delay);
			}
		}
	}

	private void sendCooldownAnswer(String callbackQueryId, long cooldownSec) {
		sender.sendCallbackQueryAnswer(callbackQueryId,
			String.format(locale.i18n("bot.inline.error.cooldown"), cooldownSec));
	}

	private void delayCooldown(int cooldown) {
		delay = cooldown;
		new Thread(() -> {
			try {
				while (delay > 0) {
					// Seconds to milliseconds, 1 second.
					Thread.sleep(1000);
					delay -= 1;
				}
			} catch (InterruptedException ie) {
				log.error("Cannot delay cooldown thread.", ie);
			}
		}).start();
	}

	public void onNewUsers(Message message) {
		if (config.isDebugShowGreetings()) {
			List<User> users = message.getNewChatMembers();
			String usernames;
			if (users.size() == 1) {
				usernames = helper.getValidUsername(users.get(0));
			} else {
				StringJoiner joiner = new StringJoiner(", ");
				users.forEach(user -> joiner.add(helper.getValidUsername(user)));
				usernames = joiner.toString();
			}
			boolean isBotHere = usernames.contains(config.getUsername());
			String botAnswer = (isBotHere) ?
				locale.i18n("bot.event.added") :
				locale.i18nRU("bot.event.user.new", usernames);
			sender.replyMessage(message.getChatId(), message.getMessageId(), botAnswer);
		}
	}

	public void onLeftUser(Message message) {
		if (config.isDebugShowGreetings()) {
			String username = helper.getValidUsername(message.getLeftChatMember());
			if (!username.equals(config.getUsername())) {
				sender.replyMessage(message.getChatId(), message.getMessageId(),
					locale.i18nRU("bot.event.user.left", username));
			}
		}
	}

	public void onNewChatPhoto(Message message) {
		sender.replyMessage(message.getChatId(), message.getMessageId(), locale.i18n("bot.event.photo.change"));
	}
}
