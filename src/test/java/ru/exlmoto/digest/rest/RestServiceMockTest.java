package ru.exlmoto.digest.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import ru.exlmoto.digest.util.Answer;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.client.ExpectedCount.twice;

@SpringBootTest
class RestServiceMockTest {
	@SpyBean
	private RestService rest;

	private RestTemplate restTemplate = new RestTemplateBuilder().build();

	@BeforeEach
	public void setUp() {
		when(rest.createRestTemplate()).thenReturn(restTemplate);
	}

	@Test
	public void testRestServiceEmptyAnswer() {
		Answer answer = getContentFromMockServer("");
		assertFalse(answer.status());
		assertThat(answer.answer()).isNotEmpty();
		System.out.println(answer.answer());
	}

	@Test
	public void testRestServiceNonEmptyAnswer() {
		Answer answer = getContentFromMockServer("Test answer");
		assertTrue(answer.status());
		assertThat(answer.answer()).isNotEmpty();
		System.out.println(answer.answer());
	}

	private Answer getContentFromMockServer(String answer) {
		MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
		mockServer.expect(twice(), MockRestRequestMatchers.anything())
			.andRespond(MockRestResponseCreators.withSuccess(answer, MediaType.APPLICATION_JSON));
		return rest.getRawContent("fake-url");
	}
}
