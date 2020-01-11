package ru.exlmoto.motofan.manager;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import ru.exlmoto.motofan.MotofanConfiguration;
import ru.exlmoto.motofan.MotofanConfigurationTest;
import ru.exlmoto.motofan.manager.helper.InnerFileHelper;
import ru.exlmoto.motofan.manager.json.MotofanPost;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.when;

public class RestManagerTest extends MotofanConfigurationTest {
	@SpyBean
	private RestManager restManager;

	@Autowired
	private MotofanConfiguration config;

	private RestTemplate restTemplate = new RestTemplateBuilder().build();

	@Test
	public void testGetLastMotofanPosts() {
		MotofanPost[] posts = restManager.getLastMotofanPosts(config.getLastPostUrl());
		assertThat(posts).isNotEmpty();
		System.out.println(posts[0].getTitle());
	}

	@Test
	public void testGetLastMotofanPostsWrongUrl() {
		assertThat(restManager.getLastMotofanPosts("fake-url")).isNull();
		assertThat(restManager.getLastMotofanPosts("https://exlmoto.ru")).isNull();
		assertThat(restManager.getLastMotofanPosts("https://exlmotor.ru")).isNull();
	}

	@Test
	public void testIncorrectJson() {
		when(restManager.getRestTemplate()).thenReturn(restTemplate);
		MotofanPost[] posts = fakeRestTemplateResult("motofan.json");
		assertThat(posts).isNotEmpty();
		System.out.println(posts[0].getTitle());
		assertThat(fakeRestTemplateResult("motofan-null.json")).isNull();
		assertThat(fakeRestTemplateResult("motofan-incorrect.json")).isNull();
		assertThat(fakeRestTemplateResult("motofan-empty.json")).isNull();
		assertThat(fakeRestTemplateResult("motofan-another.json")).isNull();
		assertThat(fakeRestTemplateResult("motofan-wrong.json")).isNull();
		assertThat(fakeRestTemplateResult("motofan-chunk.json")).isNull();
	}

	private MotofanPost[] fakeRestTemplateResult(String filename) {
		MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
		mockServer.expect(MockRestRequestMatchers.anything()).andRespond(MockRestResponseCreators.withSuccess(
			new InnerFileHelper().getFileContent(filename), MediaType.APPLICATION_JSON)
		);
		return restManager.getLastMotofanPosts("fake-url");
	}

	@SpringBootApplication(scanBasePackageClasses = { MotofanConfiguration.class })
	public static class MotofanConfigurationCommon {

	}
}
