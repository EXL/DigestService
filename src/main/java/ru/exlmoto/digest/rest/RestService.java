package ru.exlmoto.digest.rest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import ru.exlmoto.digest.util.Answer;

import javax.annotation.PostConstruct;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.time.Duration;

@Slf4j
@Service
public class RestService {
	@Value("${rest.timeout-sec}")
	private long timeoutSec;

	@Value("${rest.max-body-size}")
	private long maxBodySize;

	private RestTemplate restTemplate;

	@PostConstruct
	private void setUp() {
		restTemplate = new RestTemplateBuilder()
			.setConnectTimeout(Duration.ofSeconds(timeoutSec))
			.setReadTimeout(Duration.ofSeconds(timeoutSec))
			.build();
	}

	public Answer<String> getRestResponse(String url) {
		return getRestResponseAux(url, String.class, false);
	}

	public <T> Answer<T> getRestResponse(String url, Class<T> type) {
		return getRestResponseAux(url, type, false);
	}

	public Answer<String> getRestFile(String url) {
		return getRestResponseAux(url, String.class, true);
	}

	private <T> Answer<T> getRestResponseAux(String url, Class<T> type, boolean getFile) {
		try {
			RestTemplate template = getRestTemplate();
			checkForLength(template, url);
			T content = (getFile) ? type.cast(getRestFileAux(template, url)) : template.getForObject(url, type);
			checkForNull(content);
			return new Answer<>("", content);
		} catch (Exception e) {
			log.error(String.format("Spring RestTemplate: Error while connect to '%s' or parsing response.", url), e);
			return new Answer<>(e.getLocalizedMessage(), null);
		}
	}

	private String getRestFileAux(RestTemplate template, String url) {
		return template.execute(url, HttpMethod.GET, null, response -> {
			String suffix = "-" + new File(new URL(url).getPath()).getName();
			suffix = suffix.length() > 3 ? suffix : null;
			File tempFile = File.createTempFile("temp-", suffix);
			StreamUtils.copy(response.getBody(), new FileOutputStream(tempFile));
			return tempFile.getAbsolutePath();
		});
	}

	private <T> void checkForNull(T content) {
		Assert.notNull(content, "Received raw data is null.");
	}

	private void checkForLength(RestTemplate template, String url) {
		Assert.isTrue(
			template.headForHeaders(url).getContentLength() <= maxBodySize,
			String.format("Response data is too large (> %d bytes).", maxBodySize)
		);
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}
}
