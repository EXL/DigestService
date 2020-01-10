package ru.exlmoto.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.exlmoto")
public class DigestApplication {
	public static void main(String[] args) {
		SpringApplication.run(DigestApplication.class, args);
	}
}
