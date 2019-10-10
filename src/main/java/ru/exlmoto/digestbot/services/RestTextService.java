package ru.exlmoto.digestbot.services;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

public class RestTextService {
	private final RestTemplate mRestTemplate;
	private final String mRequestUrl;

	public RestTextService(final RestTemplateBuilder aRestTemplateBuilder,
	                       final String aRequestUrl,
	                       final Integer aRequestTimeout) {
		mRequestUrl = aRequestUrl;

		final Duration lDuration = Duration.ofSeconds(aRequestTimeout);
		mRestTemplate = aRestTemplateBuilder.setConnectTimeout(lDuration).setReadTimeout(lDuration).build();
	}

	public Pair<Boolean, String> receiveObject() {
		String lErrorText = "Wrong Status Code!";
		try {
			final ResponseEntity<String> lResponse = mRestTemplate.getForEntity(mRequestUrl, String.class);
			final HttpStatus lStatusCode = lResponse.getStatusCode();
			if (lStatusCode == HttpStatus.OK) {
				final String lBody = lResponse.getBody();
				if (lBody != null) {
					return Pair.of(true, lBody);
				}
			} else {
				lErrorText += " " + lStatusCode.toString();
			}
		} catch (Exception e) {
			lErrorText = e.toString();
		}
		return Pair.of(false, lErrorText);
	}
}
