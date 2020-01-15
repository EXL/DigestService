package ru.exlmoto.digest.motofan;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import ru.exlmoto.digest.motofan.json.MotofanPost;
import ru.exlmoto.digest.rest.RestHelper;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.resource.ResourceHelper;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.client.ExpectedCount.manyTimes;

@SpringBootTest
class MotofanServiceTest {
	@Value("${motofan.last-post-url}")
	private String lastPostUrl;

	@Autowired
	private MotofanService service;

	@Autowired
	private ResourceHelper resourceHelper;

	@SpyBean
	private RestHelper rest;

	private final RestTemplate restTemplate = new RestTemplateBuilder().build();

	@Test
	public void testGetLastMotofanPosts() {
		Answer<MotofanPost[]> res = rest.getRestResponse(lastPostUrl, MotofanPost[].class);
		assertThat(res.answer()).isNotEmpty();
		System.out.println(res.answer()[0].getTitle());
	}

	@Test
	public void testGetLastMotofanPostsWrongUrl() {
		assertThat(rest.getRestResponse("fake-url", MotofanPost[].class).answer()).isNull();
		assertThat(rest.getRestResponse("https://exlmoto.ru", MotofanPost[].class).answer()).isNull();
		assertThat(rest.getRestResponse("https://exlmotor.ru", MotofanPost[].class).answer()).isNull();
	}

	@Test
	public void testIncorrectJson() {
		when(rest.getRestTemplate()).thenReturn(restTemplate);
		MotofanPost[] posts = fakeRestTemplateResult("classpath:motofan/posts.json");
		assertThat(posts).isNotEmpty();
		System.out.println(posts[0].getTitle());
		assertThat(fakeRestTemplateResult("classpath:motofan/posts-null.json")).isNull();
		assertThat(fakeRestTemplateResult("classpath:motofan/posts-incorrect.json")).isNull();
		assertThat(fakeRestTemplateResult("classpath:motofan/posts-empty.json")).isNull();
		assertThat(fakeRestTemplateResult("classpath:motofan/posts-another.json")).isNull();
		assertThat(fakeRestTemplateResult("classpath:motofan/posts-wrong.json")).isNull();
		assertThat(fakeRestTemplateResult("classpath:motofan/posts-chunk.json")).isNull();
	}

	private MotofanPost[] fakeRestTemplateResult(String filename) {
		MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
		mockServer.expect(manyTimes(), MockRestRequestMatchers.anything()).andRespond(
			MockRestResponseCreators.withSuccess(resourceHelper.readFileToString(filename), MediaType.APPLICATION_JSON)
		);
		return service.getMotofanPostObjects();
	}
}
