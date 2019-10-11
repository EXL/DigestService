package ru.exlmoto.digestbot.services;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class RestTextService extends RestService {
	public RestTextService(final RestTemplateBuilder aRestTemplateBuilder,
	                       final Integer aRequestTimeout) {
		super(aRestTemplateBuilder, aRequestTimeout);
	}

	public Pair<Boolean, String> receiveObject(final String aRequestUrl) {
		String lErrorText = "Wrong Status Code!";
		try {
			final ResponseEntity<String> lResponse = getRestTemplate().getForEntity(aRequestUrl, String.class);
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
