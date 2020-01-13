package ru.exlmoto.digest.rest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import ru.exlmoto.digest.motofan.json.MotofanPost;
import ru.exlmoto.digest.util.Answer;

import javax.annotation.PostConstruct;
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
	public void setUp() {
		restTemplate = new RestTemplateBuilder()
			.setConnectTimeout(Duration.ofSeconds(timeoutSec))
			.setReadTimeout(Duration.ofSeconds(timeoutSec))
			.build();
	}

	public Answer getRawContent(@NonNull String url) {
		String contentOrError;
		boolean isContent = false;
		try {
			contentOrError = getRawContentSpring(getRestTemplate(), url);
			isContent = true;
		} catch (Exception e) {
			log.debug(String.format("Spring RestTemplate: Error while connect to '%s'.", url), e);
			contentOrError = e.getLocalizedMessage();
		}
		return new Answer(isContent, contentOrError);
	}

	public MotofanPost[] getLastMotofanPosts(@NonNull String url) {
		try {
			return getLastMotofanPostsAux(getRestTemplate(), url);
		} catch (Exception e) {
			log.debug(String.format("Spring RestTemplate: Error while connect to '%s' or parsing JSON.", url), e);
		}
		return null;
	}

	private MotofanPost[] checkMotofanPosts(MotofanPost[] posts) throws Exception {
		if (posts == null || posts.length == 0) {
			throw new Exception("MotofanPost JSON object array is null or empty.");
		}
		for (MotofanPost post : posts) {
			if (!post.isValid()) {
				throw new Exception("MotofanPost JSON object fails validation.");
			}
		}
		return posts;
	}

	private MotofanPost[] getLastMotofanPostsAux(@NonNull RestTemplate restTemplate, @NonNull String url)
		throws Exception {
		if (checkForLength(restTemplate, url)) {
			return checkMotofanPosts(getRestTemplate().getForObject(url, MotofanPost[].class));
		}
		throw new Exception(String.format("Response data is too large (> %d bytes).", maxBodySize));
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	private String getRawContentSpring(@NonNull RestTemplate restTemplate, @NonNull String url) throws Exception {
		if (checkForLength(restTemplate, url)) {
			String content = restTemplate.getForObject(url, String.class);
			Assert.hasText(content, "Received raw data is null or empty.");
			return content;
		}
		throw new Exception(String.format("Response data is too large (> %d bytes).", maxBodySize));
	}

	private boolean checkForLength(@NonNull RestTemplate restTemplate, @NonNull String url) {
		return restTemplate.headForHeaders(url).getContentLength() <= maxBodySize;
	}
}
