package ru.exlmoto.digestbot.commands.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.commands.BotAdminCommand;
import ru.exlmoto.digestbot.services.impl.HostIpService;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.utils.LocalizationHelper;

@Component
public class HostIpCommand extends BotAdminCommand {
	private final HostIpService mHostIpService;

	@Autowired
	public HostIpCommand(final HostIpService aHostIpService) {
		mHostIpService = aHostIpService;
	}

	@Override
	public void run(final DigestBot aDigestBot, final ReceivedMessage aReceivedMessage) {
		final LocalizationHelper lLocalizationHelper = aDigestBot.getLocalizationHelper();
		new Thread(() -> {
			final Pair<Boolean, String> lAnswer = mHostIpService.receiveObject();
			String lAnswerText = lLocalizationHelper.getLocalizedString("digestbot.command.hostip");
			if (!lAnswer.getFirst()) {
				lAnswerText = lLocalizationHelper.getLocalizedString("digestbot.error.hostip");
			}
			lAnswerText += "\n`" + lAnswer.getSecond() + '`';
			aDigestBot.sendMarkdownMessage(aReceivedMessage.getChatId(), aReceivedMessage.getMessageId(), lAnswerText);
		}).start();
	}
}
