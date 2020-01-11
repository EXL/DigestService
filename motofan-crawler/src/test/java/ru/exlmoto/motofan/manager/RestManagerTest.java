package ru.exlmoto.motofan.manager;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ru.exlmoto.motofan.MotofanConfiguration;
import ru.exlmoto.motofan.MotofanConfigurationTest;
import ru.exlmoto.motofan.manager.json.MotofanPost;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RestManagerTest extends MotofanConfigurationTest {
	@Autowired
	private RestManager restManager;

	@Autowired
	private MotofanConfiguration config;

	@Test
	public void testGetLastMotofanPosts() {
		List<MotofanPost> posts = restManager.getLastMotofanPosts(config.getLastPostUrl());
		assertThat(posts).isNotNull();
		assertThat(posts).isNotEmpty();
	}

	@Test
	public void testGetLastMotofanPostsOnWrongNetwork() {
		assertThat(restManager.getLastMotofanPosts("fake-url")).isNull();
		assertThat(restManager.getLastMotofanPosts("https://exlmoto.ru")).isNull();
		assertThat(restManager.getLastMotofanPosts("https://exlmotor.ru")).isNull();
	}

	@SpringBootApplication(scanBasePackageClasses = { MotofanConfiguration.class })
	public static class ExchangeConfigurationCommon {

	}
}
