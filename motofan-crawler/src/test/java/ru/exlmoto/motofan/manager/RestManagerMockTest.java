package ru.exlmoto.motofan.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import ru.exlmoto.motofan.MotofanConfigurationTest;
import ru.exlmoto.motofan.manager.helper.InnerFileHelper;
import ru.exlmoto.motofan.manager.json.MotofanPost;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.when;

class RestManagerMockTest extends MotofanConfigurationTest {
	@SpyBean
	private RestManager restManager;

	private RestTemplate restTemplate = new RestTemplateBuilder().build();

	@BeforeEach
	public void setUpMock() {
		when(restManager.getRestTemplate()).thenReturn(restTemplate);
	}

	@Test
	public void testIncorrectJson() {
		assertThat(fakeRestTemplateResult("motofan.json")).isNotNull();
		assertThat(fakeRestTemplateResult("motofan-null.json")).isNull();
		assertThat(fakeRestTemplateResult("motofan-incorrect.json")).isNull();
		assertThat(fakeRestTemplateResult("motofan-empty.json")).isNull();
		assertThat(fakeRestTemplateResult("motofan-another.json")).isNull();
		assertThat(fakeRestTemplateResult("motofan-wrong.json")).isNull();
		assertThat(fakeRestTemplateResult("motofan-chunk.json")).isNull();
	}

	private List<MotofanPost> fakeRestTemplateResult(String filename) {
		MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
		mockServer.expect(MockRestRequestMatchers.anything()).andRespond(MockRestResponseCreators.withSuccess(
			new InnerFileHelper().getFileContent(filename), MediaType.APPLICATION_JSON)
		);
		return restManager.getLastMotofanPosts("fake-url");
	}

	@SpringBootApplication
	public static class ExchangeConfigurationCommon {

	}
}