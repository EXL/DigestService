package ru.exlmoto.exchange;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
@ActiveProfiles("test")
public abstract class ExchangeConfigurationTest {

	@SpringBootApplication
	@EnableConfigurationProperties(ExchangeConfiguration.class)
	public static class ExchangeConfigurationCommon {

	}
}
