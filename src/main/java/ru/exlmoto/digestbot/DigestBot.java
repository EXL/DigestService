package ru.exlmoto.digestbot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class DigestBot extends TelegramLongPollingBot {
    @Value("${digestbot.name}")
    private String username;

    @Value("${digestbot.token}")
    private String token;

    @Value("${digestbot.max_updates}")
    private int max_updates;

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update.toString());
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        if (updates.size() < max_updates) {
            updates.forEach(this::onUpdateReceived);
        }
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
