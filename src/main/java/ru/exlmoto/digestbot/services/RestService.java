package ru.exlmoto.digestbot.services;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.util.Pair;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

public abstract class RestService {
	private final RestTemplate mRestTemplate;

	public RestService(final RestTemplateBuilder aRestTemplateBuilder,
	                   final Integer aRequestTimeout) {
		final Duration lDuration = Duration.ofSeconds(aRequestTimeout);
		mRestTemplate = aRestTemplateBuilder.setConnectTimeout(lDuration).setReadTimeout(lDuration).build();
	}

	public RestTemplate getRestTemplate() {
		return mRestTemplate;
	}

	public abstract Pair<Boolean, ?> receiveObject(final String aRequestUrl);
}
