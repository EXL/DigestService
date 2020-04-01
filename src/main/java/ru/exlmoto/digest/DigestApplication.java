package ru.exlmoto.digest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackageClasses = DigestApplication.class)
@EntityScan(basePackageClasses = DigestApplication.class)
@EnableJpaRepositories(basePackageClasses = DigestApplication.class)
@EnableScheduling
@EnableWebSecurity
@EnableAsync
public class DigestApplication {
	public static void main(String[] args) {
		SpringApplication.run(DigestApplication.class, args);
	}
}
