package ru.exlmoto.digestbot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public class DigestBot extends TelegramLongPollingBot {

    private static int z = 0;

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(z + " " + update.toString());
        z++;
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        if (updates.size() < 20) {
            updates.forEach(this::onUpdateReceived);
        }
    }

    @Override
    public String getBotUsername() {
        return null;
    }

    @Override
    public String getBotToken() {
        return null;
    }
}
