package ru.exlmoto.exchange.manager;

import lombok.extern.slf4j.Slf4j;

import org.jsoup.Jsoup;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Slf4j
@Component
public class RestManager {
	private final int TIMEOUT_SEC = 15;

	public String getContent(String url) {
		try {
			return getRawContentJsoup(url);
		} catch (Exception e) {
			log.error(String.format("Attempt #1, Jsoup: Error while connect to '%s'.", url), e);
			try {
				return getRawContentSpring(url);
			} catch (Exception ex) {
				log.error(String.format("Attempt #2, Spring RestTemplate: Error while connect to '%s'.", url), ex);
			}
		}
		return null;
	}

	private String getRawContentSpring(String url) {
		RestTemplate restTemplate = new RestTemplateBuilder().setConnectTimeout(
			Duration.ofSeconds(TIMEOUT_SEC)
		).build();
		String rawData = restTemplate.getForObject(url, String.class);
		Assert.notNull(rawData, "Spring RestTemplate: Received raw data is null.");
		return rawData;
	}

	private String getRawContentJsoup(String url) throws Exception {
		String rawData = Jsoup.connect(url).timeout(TIMEOUT_SEC * 1000).get().outerHtml();
		Assert.notNull(rawData, "Jsoup: Received raw data is null.");
		return rawData;
	}
}
