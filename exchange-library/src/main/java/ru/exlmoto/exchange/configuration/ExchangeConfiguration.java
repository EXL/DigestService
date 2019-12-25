package ru.exlmoto.exchange.configuration;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import ru.exlmoto.exchange.entity.BankRuEntity;
import ru.exlmoto.exchange.repository.BankRuRepository;

@Setter
@Getter
@Configuration
@ConfigurationProperties("exchange")
@EnableJpaRepositories(basePackageClasses = BankRuRepository.class)
@EntityScan(basePackageClasses = BankRuEntity.class)
public class ExchangeConfiguration {
	private String bankRu;
	private String bankRuMirror;
	private String bankUa;
	private String bankUaMirror;
	private String bankBy;
	private String bankKz;
	private String metalRu;
	private String metalRuMirror;
}
