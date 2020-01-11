package ru.exlmoto.motofan.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.mock.mockito.SpyBean;

import ru.exlmoto.motofan.MotofanConfiguration;
import ru.exlmoto.motofan.MotofanConfigurationTest;
import ru.exlmoto.motofan.manager.json.MotofanPost;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.when;

public class MotofanManagerTest extends MotofanConfigurationTest {
	@SpyBean
	private MotofanManager motofanManager;

	@Test
	public void testGetLastMotofanPostsNull() {
		List<MotofanPost> firstStep = motofanManager.getLastMotofanPosts();
		List<MotofanPost> secondStep = motofanManager.getLastMotofanPosts();
		assertThat(firstStep).isNull();
		assertThat(secondStep).isNull();
	}

	@Test
	public void testGetLastMotofanPostsFake() {
		MotofanPost[] posts = new MotofanPost[3];
		posts[0] = getRandomMotofanPost(2L);
		posts[1] = getRandomMotofanPost(1L);
		posts[2] = getRandomMotofanPost(0L);
		when(motofanManager.getMotofanPostObjects()).thenReturn(posts);
		List<MotofanPost> firstStep = motofanManager.getLastMotofanPosts();
		posts[0] = getRandomMotofanPost(4L);
		posts[1] = getRandomMotofanPost(3L);
		posts[2] = getRandomMotofanPost(2L);
		List<MotofanPost> secondStep = motofanManager.getLastMotofanPosts();
		assertThat(firstStep).isNull();
		assertThat(secondStep).isNotEmpty();
		System.out.println(secondStep);
	}

	private MotofanPost getRandomMotofanPost(Long timestamp) {
		MotofanPost motofanPost = new MotofanPost();
		motofanPost.setTimestamp(timestamp);
		motofanPost.setTime(String.valueOf(new Random().nextLong()));
		motofanPost.setTopic(new Random().nextLong());
		motofanPost.setPost(new Random().nextLong());
		motofanPost.setTopic_link(String.valueOf(new Random().nextLong()));
		motofanPost.setPost_link(String.valueOf(new Random().nextLong()));
		motofanPost.setAuthor(String.valueOf(new Random().nextLong()));
		motofanPost.setTitle(String.valueOf(new Random().nextLong()));
		motofanPost.setText(String.valueOf(new Random().nextLong()));
		return motofanPost;
	}

	@SpringBootApplication(scanBasePackageClasses = { MotofanConfiguration.class })
	public static class ExchangeConfigurationCommon {

	}
}
