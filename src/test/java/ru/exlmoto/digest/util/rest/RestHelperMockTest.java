package ru.exlmoto.digest.util.rest;

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
import static org.junit.jupiter.api.Assertions.assertNull;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.client.ExpectedCount.manyTimes;

@SpringBootTest
class RestHelperMockTest {
	@SpyBean
	private RestHelper rest;

	private final RestTemplate restTemplate = new RestTemplateBuilder().build();

	@BeforeEach
	public void setUp() {
		when(rest.getRestTemplate()).thenReturn(restTemplate);
	}

	@Test
	public void testRestServiceEmptyAnswer() {
		Answer<String> res = getContentFromMockServer("");
		assertFalse(res.ok());
		assertNull(res.answer());
		assertThat(res.error()).isNotEmpty();
		System.out.println(res.error());
	}

	@Test
	public void testRestServiceNonEmptyAnswer() {
		Answer<String> res = getContentFromMockServer("Test answer");
		assertTrue(res.ok());
		assertThat(res.answer()).isNotEmpty();
		System.out.println(res.answer());
	}

	private Answer<String> getContentFromMockServer(String answer) {
		MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
		mockServer.expect(manyTimes(), MockRestRequestMatchers.anything())
			.andRespond(MockRestResponseCreators.withSuccess(answer, MediaType.APPLICATION_JSON));
		return rest.getRestResponse("fake-url");
	}
}
