package ru.exlmoto.digestbot.commands.impl;

import org.springframework.stereotype.Component;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.commands.BotAdminCommand;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.digestbot.yaml.impl.YamlLocalizationHelper;

@Component
public class DebugCommand extends BotAdminCommand {
    @Override
    public void run(final DigestBot aDigestBot,
                    final YamlLocalizationHelper aLocalizationHelper,
                    final ReceivedMessage aReceivedMessage) {
        String lCommandText = aReceivedMessage.getMessageText();
        final String[] lCommandWithArgs = lCommandText.split(" ");
        final String lCommand = lCommandWithArgs[1];

        switch (lCommand) {
            default: {
                lCommandText = aLocalizationHelper.getLocalizedString("command.debug.help");
                break;
            }
            case "RefreshRates": {
                lCommandText = aLocalizationHelper.getLocalizedString("command.debug.data");
                aDigestBot.getBankWorker().updateAllBanks();
                break;
            }
            case "UseUriInsteadFiles": {
                break;
            }
            case "EnableLogUpdates": {
                break;
            }
            case "UseStackForDelay": {
                break;
            }
            case "DisableGreetings": {
                break;
            }
        }

        aDigestBot.sendMarkdownMessage(aReceivedMessage.getChatId(), aReceivedMessage.getMessageId(), lCommandText);
    }
}
