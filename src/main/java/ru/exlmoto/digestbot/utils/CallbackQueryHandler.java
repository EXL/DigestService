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
			if (aCallbackQuery.getData().startsWith("chart")) {
				aDigestBot.getChartsKeyboard().handleRatesKeyboard(aDigestBot, aCallbackQuery);
			}
		}).start();
	}

	private void coolDown(final DigestBot aDigestBot, final Integer aCoolDown) {
		mSeconds = aCoolDown;
		new Thread(() -> {
			try {
				while (mSeconds > 0) {
					mSeconds -= 1;
					// Seconds to milliseconds.
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
				aDigestBot.getBotLogger().error(String.format("Inline delay error: '%s'.", e.toString()));
			}
		}).start();
	}
}
