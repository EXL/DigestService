package ru.exlmoto.rest;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties("rate")
public class RestConfiguration {
	private long timeOut;
	private long maxBodySize;
}
