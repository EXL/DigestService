package ru.exlmoto.digestbot.commands.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.commands.BotCommand;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.digestbot.yaml.impl.YamlChartIndexHelper;
import ru.exlmoto.digestbot.yaml.impl.YamlLocalizationHelper;

@Component
public class ChartsCommand extends BotCommand {
	private final YamlChartIndexHelper mYamlChartIndexHelper;

	@Autowired
	public ChartsCommand(final YamlChartIndexHelper aYamlChartIndexHelper) {
		mYamlChartIndexHelper = aYamlChartIndexHelper;
	}

	@Override
	public void run(final DigestBot aDigestBot,
	                final YamlLocalizationHelper aLocalizationHelper,
	                final ReceivedMessage aReceivedMessage) {
		aDigestBot.sendMarkdownMessageWithKeyboard(aReceivedMessage.getChatId(),
			aReceivedMessage.getMessageId(),
			aLocalizationHelper.getLocalizedString("command.charts") + "\n\n" +
				mYamlChartIndexHelper.getDescriptions(), null);
	}
}
