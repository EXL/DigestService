package ru.exlmoto.digest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import ru.exlmoto.digest.entity.BankRuEntity;
import ru.exlmoto.digest.repository.BankRuRepository;

@SpringBootApplication
@EntityScan(basePackageClasses = BankRuEntity.class)
@EnableJpaRepositories(basePackageClasses = BankRuRepository.class)
public class DigestApplication {
	public static void main(String[] args) {
		SpringApplication.run(DigestApplication.class, args);
	}
}
