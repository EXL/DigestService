package ru.exlmoto.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.exlmoto")
public class EngineApplication {
	public static void main(String[] args) {
		SpringApplication.run(EngineApplication.class, args);
	}
}
