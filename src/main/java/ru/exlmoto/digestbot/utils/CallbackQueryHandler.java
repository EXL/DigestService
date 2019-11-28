package ru.exlmoto.digestbot.utils;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import ru.exlmoto.digestbot.DigestBot;

import java.util.HashMap;

public class CallbackQueryHandler {
	private int mSeconds = 0;

	private final HashMap<Long, Long> mCallBackQueriesStack = new HashMap<>();

	public void handle(final DigestBot aDigestBot, final CallbackQuery aCallbackQuery) {
		final Integer lCoolDown = aDigestBot.getBotInlineCoolDown();
		if (aDigestBot.getUseStackForDelay()) {
			final Long lChatId = aCallbackQuery.getMessage().getChatId();
			// TODO: Use this function in another
			final Long lCurrentTime = (System.currentTimeMillis() / 1000L);
			if (!mCallBackQueriesStack.containsKey(lChatId) ||
					mCallBackQueriesStack.get(lChatId) <= lCurrentTime - lCoolDown) {
				mCallBackQueriesStack.put(lChatId, lCurrentTime);
				handleCallbackQuery(aDigestBot, aCallbackQuery);
			} else {
				sendCoolDownAnswer(aDigestBot, aCallbackQuery,
						(int) (lCoolDown - (lCurrentTime - mCallBackQueriesStack.get(lChatId))));
			}
		} else {
			if (mSeconds == 0) {
				coolDown(aDigestBot, lCoolDown);
				handleCallbackQuery(aDigestBot, aCallbackQuery);
			} else {
				sendCoolDownAnswer(aDigestBot, aCallbackQuery, mSeconds);
			}
		}
	}

	private void sendCoolDownAnswer(final DigestBot aDigestBot,
									final CallbackQuery aCallbackQuery,
									final Integer aCoolDown) {
		aDigestBot.createAndSendAnswerCallbackQuery(aCallbackQuery.getId(),
				String.format(aDigestBot.getLocalizationHelper().getLocalizedString("inline.error.cooldown"),
						aCoolDown));
	}

	private void handleCallbackQuery(final DigestBot aDigestBot, final CallbackQuery aCallbackQuery) {
		new Thread(() -> {
			final String lData = aCallbackQuery.getData();
			if (lData.startsWith("chart.")) {
				aDigestBot.getChartsKeyboard().handleChartsKeyboard(aDigestBot, aCallbackQuery);
			} else if (lData.startsWith("i.rate.")) {
				aDigestBot.getRatesKeyboard().handleRatesKeyboard(aDigestBot, aCallbackQuery);
			} else if (lData.startsWith("subscribe.")) {
				aDigestBot.getSubscribeKeyboard().handleSubscribeKeyboard(aDigestBot, aCallbackQuery);
			} else if (lData.startsWith("page.")) {
				aDigestBot.getDigestKeyboard().handleDigestKeyboard(aDigestBot, aCallbackQuery);
			}
		}).start();
	}

	private void coolDown(final DigestBot aDigestBot, final Integer aCoolDown) {
		mSeconds = aCoolDown;
		new Thread(() -> {
			try {
				while (mSeconds > 0) {
					// Seconds to milliseconds, 1 second.
					Thread.sleep(1000);
					mSeconds -= 1;
				}
			} catch (InterruptedException e) {
				aDigestBot.getBotLogger().error(String.format("Inline delay error: '%s'.", e.toString()));
			}
		}).start();
	}
}
