package ru.exlmoto.digest.rest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import ru.exlmoto.digest.util.Answer;

import java.time.Duration;

@Slf4j
@Service
public class RestService {
	@Value("${rest.timeout-sec}")
	private long timeoutSec;

	@Value("${rest.max-body-size}")
	private long maxBodySize;

	public Answer getRawContent(String url) {
		String contentOrError;
		boolean isContent = false;
		try {
			contentOrError = getRawContentSpring(createRestTemplate(), url);
			isContent = true;
		} catch (Exception e) {
			log.debug(String.format("Spring RestTemplate: Error while connect to '%s'.", url), e);
			contentOrError = e.getLocalizedMessage();
		}
		return new Answer(isContent, contentOrError);
	}

	public RestTemplate createRestTemplate() {
		return new RestTemplateBuilder()
			.setConnectTimeout(Duration.ofSeconds(timeoutSec))
			.setReadTimeout(Duration.ofSeconds(timeoutSec))
			.build();
	}

	private String getRawContentSpring(RestTemplate restTemplate, String url) throws Exception {
		if (checkForLength(restTemplate, url)) {
			String content = restTemplate.getForObject(url, String.class);
			Assert.hasText(content, "Received raw data is null or empty.");
			return content;
		}
		throw new Exception(String.format("Response data is too large (> %d bytes).", maxBodySize));
	}

	private boolean checkForLength(RestTemplate restTemplate, String url) {
		return restTemplate.headForHeaders(url).getContentLength() <= maxBodySize;
	}
}
