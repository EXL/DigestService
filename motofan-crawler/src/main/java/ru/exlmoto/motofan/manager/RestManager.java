package ru.exlmoto.motofan.manager;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import ru.exlmoto.motofan.manager.json.MotofanPost;

import java.time.Duration;

@Slf4j
@Component
public class RestManager {
	@Value("${general.connection.timeout}")
	private int timeOutSec;

	public RestTemplate getRestTemplate() {
		return new RestTemplateBuilder()
			.setConnectTimeout(Duration.ofSeconds(timeOutSec))
			.setReadTimeout(Duration.ofSeconds(timeOutSec))
			.build();
	}

	public MotofanPost[] getLastMotofanPosts(String url) {
		try {
			MotofanPost[] motofanPosts = getRestTemplate().getForObject(url, MotofanPost[].class);
			if (motofanPosts != null && motofanPosts.length > 0 && checkMotofanPosts(motofanPosts)) {
				return motofanPosts;
			}
		} catch (Exception e) {
			log.error(String.format("Spring RestTemplate: Error while connect to '%s'.", url), e);
		}
		return null;
	}

	private boolean checkMotofanPosts(MotofanPost[] posts) {
		for (MotofanPost post : posts) {
			if (!post.isValid()) {
				log.error("MotofanPost JSON object fails validation.");
				return false;
			}
		}
		return true;
	}
}
