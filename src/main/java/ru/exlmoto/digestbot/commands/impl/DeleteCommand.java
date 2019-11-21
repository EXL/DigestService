package ru.exlmoto.digestbot.commands.impl;

import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.commands.BotAdminCommand;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.digestbot.yaml.impl.YamlLocalizationHelper;

@Component
public class DeleteCommand extends BotAdminCommand {
	@Override
	public void run(final DigestBot aDigestBot,
	                final YamlLocalizationHelper aLocalizationHelper,
	                final ReceivedMessage aReceivedMessage) {
		String lCommandText = aReceivedMessage.getMessageText();
		final String[] lCommandWithArgs = lCommandText.split(" ");

		String lAnswer = aLocalizationHelper.getLocalizedString("error.delete.format");

		if (lCommandWithArgs.length == 2) {
			int lDigestId = -1;
			try {
				lDigestId = NumberUtils.parseNumber(lCommandWithArgs[1], Integer.class);
				aDigestBot.getIDigestEntriesRepository().deleteById(lDigestId);
				lAnswer = String.format(aLocalizationHelper.getLocalizedString("command.delete.ok"), lDigestId);
			} catch (NumberFormatException e) {
				aDigestBot.getBotLogger().error(e.toString());
			} catch (Exception e) {
				aDigestBot.getBotLogger().error(e.toString());
				lAnswer = String.format(aLocalizationHelper.getLocalizedString("error.delete.entry"), lDigestId);
			}
		}

		aDigestBot.sendMarkdownMessage(aReceivedMessage.getChatId(), aReceivedMessage.getMessageId(), lAnswer);
	}
}
