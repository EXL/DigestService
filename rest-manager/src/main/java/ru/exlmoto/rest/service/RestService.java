package ru.exlmoto.rest.service;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;
import ru.exlmoto.rest.RestConfig;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
@EnableConfigurationProperties(RestConfig.class)
public class RestService {
	private final RestConfig config;

	public String getRawContent(String url) {
		String content = null;
		try {
			content = getRawContentSpring(createRestTemplate(), url);
		} catch (Exception e) {
			log.debug(String.format("Spring RestTemplate: Error while connect to '%s'.", url), e);
		}
		return content;
	}

	private RestTemplate createRestTemplate() {
		return new RestTemplateBuilder()
			       .setConnectTimeout(Duration.ofSeconds(config.getTimeOut()))
			       .setReadTimeout(Duration.ofSeconds(config.getTimeOut()))
			       .build();
	}

	private String getRawContentSpring(RestTemplate restTemplate, String url) throws Exception {
		if (checkForLength(restTemplate, url)) {
			return restTemplate.getForObject(url, String.class);
		}
		throw new Exception(String.format("Response data is too large (> %d).", config.getMaxBodySize()));
	}

	private boolean checkForLength(RestTemplate restTemplate, String url) {
		return restTemplate.headForHeaders(url).getContentLength() <= config.getMaxBodySize();
	}
}
