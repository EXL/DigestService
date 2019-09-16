package ru.exlmoto.digestbot.handlers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.exlmoto.digestbot.utils.ReceivedMessage;

@Component
public class MessageHandler {
    // List of user commands.
    private final String mCommandDigest = "/digest";
    private final String mCommandRates = "/rates";
    private final String mCommandCharts = "/charts";
    private final String mCommandCoffee = "/coffee";
    private final String mCommandGame = "/game";
    private final String mCommandHelp = "/help";
    private final String mCommandStart = "/start";
    // List of administrator commands.
    private final String mAdminCommandHello = "/hello";
    private final String mAdminCommandHi = "/hi";
    private final String mAdminCommandSend = "/send";
    private final String mAdminCommandSticker = "/sticker";
    private final String mAdminCommandDelete = "/delete";
    private final String mAdminCommandView = "/view";
    private final String mAdminCommandHost = "/host";

    private final String mUsernameTag = "%username%";

    @Value("${digestbot.admins}")
    private String[] administrators;

    public BotApiMethod<?> handleMessage(Message message) {
        // Useful for debug.
        // System.out.println(message);

        ReceivedMessage receivedMessage = new ReceivedMessage(message, administrators);

        // Skip all forwarded messages.
        if (receivedMessage.isMessageForwarded()) {
            return null;
        }

        if (receivedMessage.isMessageCommand()) {
            final String command = receivedMessage.getMessageText();
            if (command.startsWith(mCommandDigest)) {

            } else if (command.startsWith(mCommandRates)) {

            } else if (command.startsWith(mCommandCharts)) {

            } else if (command.startsWith(mCommandCoffee)) {

            } else if (command.startsWith(mCommandGame)) {

            } else if (command.startsWith(mCommandHelp)) {

            } else if (command.startsWith(mCommandStart)) {

            } else if (command.startsWith(mAdminCommandHi) || command.startsWith(mAdminCommandHello)) {
                if (receivedMessage.isUserAdmin()) {
                    return prepareSimpleTextResponse(receivedMessage, "Hello");
                }
            } else if (command.startsWith(mAdminCommandSend)) {

            } else if (command.startsWith(mAdminCommandSticker)) {

            } else if (command.startsWith(mAdminCommandDelete)) {

            } else if (command.startsWith(mAdminCommandView)) {

            } else if (command.startsWith(mAdminCommandHost)) {

            }
        }

        System.out.println(receivedMessage);

        return null;
    }

    private BotApiMethod<?> prepareSimpleTextResponse(ReceivedMessage receivedMessage, String message) {
        if (message != null && !message.isEmpty()) {
            if (message.contains(mUsernameTag)) {
                message = message.replaceAll(mUsernameTag, '@' + receivedMessage.getMessageUsername());
            }
            return new SendMessage()
                    .setChatId(receivedMessage.getChatId())
                    .setText(message)
                    .setReplyToMessageId(receivedMessage.getMessageId());
        } else {
            return null;
        }
    }
}
