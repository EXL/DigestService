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
	public void run(final DigestBot aDigestBot,
	                final LocalizationHelper aLocalizationHelper,
	                final ReceivedMessage aReceivedMessage) {
		final Pair<Boolean, String> lAnswer = mHostIpService.receiveObject();
		String lAnswerText = aLocalizationHelper.getLocalizedString("digestbot.command.hostip");
		final String lAnswerString = lAnswer.getSecond();
		if (!lAnswer.getFirst()) {
			lAnswerText = aLocalizationHelper.getLocalizedString("digestbot.error.hostip");
			aDigestBot.getBotLogger().error(String.format("Cannot get host ip address %s", lAnswerString));
		}
		lAnswerText += "\n`" + lAnswerString + '`';
		aDigestBot.sendMarkdownMessage(aReceivedMessage.getChatId(),
			aReceivedMessage.getMessageId(),
			lAnswerText);
	}
}
