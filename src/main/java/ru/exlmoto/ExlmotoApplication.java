package ru.exlmoto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
@PropertySource(value = { "classpath:ru_exlmoto.properties" })
public class ExlmotoApplication {
    public static void main(String[] args) {
        /* Initialize Telegram Bot API */
        ApiContextInitializer.init();

        SpringApplication.run(ExlmotoApplication.class, args);
    }
}
