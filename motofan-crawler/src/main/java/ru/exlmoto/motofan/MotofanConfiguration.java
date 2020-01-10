package ru.exlmoto.motofan;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties("motofan")
public class MotofanConfiguration {
	private String lastPostUrl;
}
