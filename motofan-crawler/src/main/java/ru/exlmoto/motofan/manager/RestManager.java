package ru.exlmoto.motofan.manager;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import ru.exlmoto.motofan.manager.json.MotofanPost;

import java.time.Duration;

@Slf4j
@Component("restManagerMotofan")
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
			return checkMotofanPosts(getRestTemplate().getForObject(url, MotofanPost[].class));
		} catch (Exception e) {
			log.error(String.format("Spring RestTemplate: Error while connect to '%s' or parsing.", url), e);
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
}
