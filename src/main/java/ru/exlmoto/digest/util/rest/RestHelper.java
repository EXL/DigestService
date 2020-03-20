package ru.exlmoto.digest.util.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import ru.exlmoto.digest.util.Answer;

import javax.annotation.PostConstruct;

import java.io.File;
import java.io.FileOutputStream;

import java.net.URL;

import java.time.Duration;

import static ru.exlmoto.digest.util.Answer.Error;
import static ru.exlmoto.digest.util.Answer.Ok;

@Component
public class RestHelper {
	private final Logger log = LoggerFactory.getLogger(RestHelper.class);

	@Value("${rest.timeout-sec}")
	private long timeoutSec;

	@Value("${rest.max-body-size}")
	private long maxBodySize;

	private RestTemplate restTemplate;

	@PostConstruct
	private void setUp() {
		restTemplate = new RestTemplateBuilder()
			.setConnectTimeout(Duration.ofSeconds(timeoutSec))
			.setReadTimeout(Duration.ofSeconds(timeoutSec))
			.build();
	}

	public Answer<String> getRestResponse(String url) {
		return getRestResponseAux(url, String.class, false);
	}

	public <T> Answer<T> getRestResponse(String url, Class<T> type) {
		return getRestResponseAux(url, type, false);
	}

	public Answer<String> getRestFile(String url) {
		return getRestResponseAux(url, String.class, true);
	}

	private <T> Answer<T> getRestResponseAux(String url, Class<T> type, boolean getFile) {
		try {
			return getRestResponseAuxTry(url, type, getFile);
		} catch (Exception e) {
			log.error(String.format("RestTemplate #1: error while connect to '%s' or parsing response.", url), e);
			try {
				return getRestResponseAuxTry(url, type, getFile);
			} catch (Exception ex) {
				log.error(String.format("RestTemplate #2: error while connect to '%s' or parsing response.", url), ex);
				return Error(ex.getLocalizedMessage());
			}
		}
	}

	private <T> Answer<T> getRestResponseAuxTry(String url, Class<T> type, boolean getFile) {
		RestTemplate template = getRestTemplate();
		checkForLength(template, url);
		T content = (getFile) ? type.cast(getRestFileAux(template, url)) : template.getForObject(url, type);
		checkForNull(content);
		return Ok(content);
	}

	private String getRestFileAux(RestTemplate template, String url) {
		return template.execute(url, HttpMethod.GET, null, response -> {
			String suffix = "-" + new File(new URL(url).getPath()).getName();
			suffix = suffix.length() > 3 ? suffix : null;
			File tempFile = File.createTempFile("temp-", suffix);
			StreamUtils.copy(response.getBody(), new FileOutputStream(tempFile));
			return tempFile.getAbsolutePath();
		});
	}

	private <T> void checkForNull(T content) {
		Assert.notNull(content, "Received raw data is null.");
	}

	private void checkForLength(RestTemplate template, String url) {
		try {
			Assert.isTrue(
				template.headForHeaders(url).getContentLength() <= maxBodySize,
				String.format("Response data is too large (> %d bytes).", maxBodySize)
			);
		} catch (HttpServerErrorException hsee) {
			/*
			 * Some servers (i.e. Telegram servers) send error "501 Not Implemented" on head request.
			 * Ignore this exception here for such cases.
			 */
			if (hsee.getStatusCode() != HttpStatus.NOT_IMPLEMENTED) {
				throw hsee;
			}
		}
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}
}
