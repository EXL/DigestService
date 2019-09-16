package ru.exlmoto.digestbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.exlmoto.digestbot.handlers.MessageHandler;
import ru.exlmoto.digestbot.utils.ReceivedMessage;

import java.util.List;

//TODO: Component vs Service???
@Component
public class DigestBot extends TelegramLongPollingBot {
    @Value("${digestbot.name}")
    private String mBotUsername;

    @Value("${digestbot.token}")
    private String mBotToken;

    @Value("${digestbot.max_updates}")
    private int mMaxUpdates;

    private MessageHandler mMessageHandler;

    @Autowired
    public DigestBot(MessageHandler messageHandler) {
        this.mMessageHandler = messageHandler;
    }

    @Override
    public String getBotUsername() {
        return mBotUsername;
    }

    @Override
    public String getBotToken() {
        return mBotToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Create response and send it to the chat.
        BotApiMethod<?> response = null;
        if (update.hasMessage()) {
            response = mMessageHandler.handleMessage(update.getMessage());
        } else if (update.hasEditedMessage()) {
            response = mMessageHandler.handleMessage(update.getEditedMessage());
        }
        if (response != null) {
            try {
                execute(response);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        // Receive only a few recent updates for handling.
        int listSize = updates.size();
        if (listSize > mMaxUpdates) {
            updates.subList(0, listSize - mMaxUpdates).clear();
        }
        updates.forEach(this::onUpdateReceived);
    }
}
