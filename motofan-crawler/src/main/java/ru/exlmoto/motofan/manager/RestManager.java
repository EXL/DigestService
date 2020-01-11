package ru.exlmoto.motofan.manager;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import ru.exlmoto.motofan.manager.json.MotofanPost;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class RestManager {
	@Value("${general.connection.timeout}")
	private int timeOutSec;

	public List<MotofanPost> getLastMotofanPosts(String url) {
		try {
			RestTemplate restTemplate =
				new RestTemplateBuilder()
					.setConnectTimeout(Duration.ofSeconds(timeOutSec))
					.setReadTimeout(Duration.ofSeconds(timeOutSec))
					.build();
			MotofanPost[] motofanPosts = restTemplate.getForObject(url, MotofanPost[].class);
			return (motofanPosts == null || motofanPosts.length == 0) ? null : Arrays.asList(motofanPosts);
		} catch (Exception e) {
			log.error(String.format("Spring RestTemplate: Error while connect to '%s'.", url), e);
		}
		return null;
	}
}
