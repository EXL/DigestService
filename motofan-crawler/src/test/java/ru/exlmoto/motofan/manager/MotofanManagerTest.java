package ru.exlmoto.motofan.manager;

import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;

import ru.exlmoto.motofan.MotofanConfiguration;
import ru.exlmoto.motofan.MotofanConfigurationTest;
import ru.exlmoto.motofan.manager.helper.MotofanPostHelper;
import ru.exlmoto.motofan.manager.json.MotofanPost;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.when;

public class MotofanManagerTest extends MotofanConfigurationTest {
	@SpyBean
	private MotofanManager motofanManager;

	@Test
	@DirtiesContext
	public void testGetLastMotofanPostsNull() {
		List<MotofanPost> firstStep = motofanManager.getLastMotofanPosts();
		List<MotofanPost> secondStep = motofanManager.getLastMotofanPosts();
		assertThat(firstStep).isNull();
		assertThat(secondStep).isNull();
	}

	@Test
	@DirtiesContext
	public void testGetLastMotofanPostsBugWithWrongArraySize() {
		List<MotofanPost> firstStep = motofanManager.getLastMotofanPosts();
		assertThat(firstStep).isNull();
		MotofanPost[] posts = new MotofanPost[2];
		when(motofanManager.getMotofanPostObjects()).thenReturn(posts);
		posts[0] = new MotofanPostHelper().getRandomMotofanPost(4000000000000000000L);
		posts[1] = new MotofanPostHelper().getRandomMotofanPost(3000000000000000000L);
		List<MotofanPost> secondStep = motofanManager.getLastMotofanPosts();
		assertEquals(2, secondStep.size());
	}

	@Test
	@DirtiesContext
	public void testGetLastMotofanPostsFake() {
		MotofanPost[] posts = new MotofanPost[3];
		posts[0] = new MotofanPostHelper().getRandomMotofanPost(2L);
		posts[1] = new MotofanPostHelper().getRandomMotofanPost(1L);
		posts[2] = new MotofanPostHelper().getRandomMotofanPost(0L);
		when(motofanManager.getMotofanPostObjects()).thenReturn(posts);
		List<MotofanPost> firstStep = motofanManager.getLastMotofanPosts();
		posts[0] = new MotofanPostHelper().getRandomMotofanPost(4L);
		posts[1] = new MotofanPostHelper().getRandomMotofanPost(3L);
		posts[2] = new MotofanPostHelper().getRandomMotofanPost(2L);
		List<MotofanPost> secondStep = motofanManager.getLastMotofanPosts();
		assertThat(firstStep).isNull();
		assertThat(secondStep).isNotEmpty();
		assertEquals(2, secondStep.size());
		System.out.println(secondStep);
	}

	@Test
	@DirtiesContext
	public void testGetLastMotofanPostsInHtml() {
		MotofanPost[] posts = new MotofanPost[3];
		posts[0] = new MotofanPostHelper().getRandomMotofanPost(2L);
		posts[1] = new MotofanPostHelper().getRandomMotofanPost(1L);
		posts[2] = new MotofanPostHelper().getRandomMotofanPost(0L);
		when(motofanManager.getMotofanPostObjects()).thenReturn(posts);
		List<String> firstStep = motofanManager.getLastMotofanPostsInHtml();
		posts[0] = new MotofanPostHelper().getRandomMotofanPost(4L);
		posts[1] = new MotofanPostHelper().getRandomMotofanPost(3L);
		posts[2] = new MotofanPostHelper().getRandomMotofanPost(2L);
		List<String> secondStep = motofanManager.getLastMotofanPostsInHtml();
		assertThat(firstStep).isEmpty();
		assertThat(secondStep).isNotEmpty();
		assertEquals(2, secondStep.size());
		System.out.println(secondStep);
	}

	@SpringBootApplication(scanBasePackageClasses = { MotofanConfiguration.class })
	public static class MotofanConfigurationCommon {

	}
}
