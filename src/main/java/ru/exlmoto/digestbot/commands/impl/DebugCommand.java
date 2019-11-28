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
        lCommandText = aLocalizationHelper.getLocalizedString("command.debug.help");
        if (lCommandWithArgs.length == 2) {
            final String lCommand = lCommandWithArgs[1];
            switch (lCommand) {
                case "VRates": {
                    lCommandText = aLocalizationHelper.getLocalizedString("command.debug.data");
                    aDigestBot.getBankWorker().updateAllBanks();
                    break;
                }
                case "VPosts": {
                    lCommandText = aLocalizationHelper.getLocalizedString("command.debug.data");
                    aDigestBot.getMotoFanWorker().updateLatestMotoFanPosts();
                    break;
                }
                case "VShredder": {
                    lCommandText = aLocalizationHelper.getLocalizedString("command.debug.data");
                    aDigestBot.getDigestShredder().dropObsoleteDigests();
                    break;
                }
                case "VAvatars": {
                    lCommandText = aLocalizationHelper.getLocalizedString("command.debug.data");
                    aDigestBot.getAvatarUpdater().updateUserAvatars();
                    break;
                }
                case "VStatus" : {
                    lCommandText = aLocalizationHelper.getLocalizedString("command.debug.values") + "\n";
                    lCommandText += "BFileDownloader: `" + aDigestBot.getUseFileLoader() + "`\n";
                    lCommandText += "BLogUpdates: `" + aDigestBot.getShowUpdatesInLog() + "`\n";
                    lCommandText += "BStackForDelay: `" + aDigestBot.getUseStackForDelay() + "`\n";
                    lCommandText += "BGreetings: `" + aDigestBot.getShowGreetings() + "`\n";
                    lCommandText = lCommandText.trim();
                    break;
                }
                case "BFileDownloader": {
                    aDigestBot.toggleUseFileLoader();
                    lCommandText = String.format(aLocalizationHelper.getLocalizedString("command.debug.variable"),
                            lCommand, aDigestBot.getUseFileLoader());
                    break;
                }
                case "BLogUpdates": {
                    aDigestBot.toggleShowUpdatesInLog();
                    lCommandText = String.format(aLocalizationHelper.getLocalizedString("command.debug.variable"),
                            lCommand, aDigestBot.getShowUpdatesInLog());
                    break;
                }
                case "BStackForDelay": {
                    aDigestBot.toggleUseStackForDelay();
                    lCommandText = String.format(aLocalizationHelper.getLocalizedString("command.debug.variable"),
                            lCommand, aDigestBot.getUseStackForDelay());
                    break;
                }
                case "BGreetings": {
                    aDigestBot.toggleShowGreetings();
                    lCommandText = String.format(aLocalizationHelper.getLocalizedString("command.debug.variable"),
                            lCommand, aDigestBot.getShowGreetings());
                    break;
                }
            }
        }
        aDigestBot.sendMarkdownMessage(aReceivedMessage.getChatId(), aReceivedMessage.getMessageId(), lCommandText);
    }
}
