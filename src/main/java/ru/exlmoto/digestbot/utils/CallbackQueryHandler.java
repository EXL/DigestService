package ru.exlmoto.digestbot.utils;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import ru.exlmoto.digestbot.DigestBot;

public class CallbackQueryHandler {
	private int mSeconds = 0;

	public void handle(final DigestBot aDigestBot, final CallbackQuery aCallbackQuery) {
		final Integer lCoolDown = aDigestBot.getBotInlineCoolDown();
		if (mSeconds == 0) {
			coolDown(aDigestBot, lCoolDown);
			handleCallbackQuery(aDigestBot, aCallbackQuery);
		} else {
			aDigestBot.createAndSendAnswerCallbackQuery(aCallbackQuery.getId(),
				String.format(aDigestBot.getLocalizationHelper().getLocalizedString("inline.error.cooldown"),
					mSeconds));
		}
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
