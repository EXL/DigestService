/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL <exlmotodev@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.exlmoto.digest.util.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.HttpClientErrorException;
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
	private int timeoutSec;

	@Value("${rest.max-body-size}")
	private long maxBodySize;

	@Value("${rest.simple-http-client}")
	private boolean simpleHttpClient;

	private RestTemplate restTemplate;

	@PostConstruct
	private void setUp() {
		if (simpleHttpClient) {
			SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
			simpleClientHttpRequestFactory.setConnectTimeout(timeoutSec * 1000);
			simpleClientHttpRequestFactory.setReadTimeout(timeoutSec * 1000);
			restTemplate = new RestTemplate(simpleClientHttpRequestFactory);
		} else {
			restTemplate = new RestTemplateBuilder()
				.setConnectTimeout(Duration.ofSeconds(timeoutSec))
				.setReadTimeout(Duration.ofSeconds(timeoutSec))
				.build();
		}
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
		} catch (HttpClientErrorException hcee) {
			/*
			 * Ignore "405 Method Not Allowed" error on head request.
			 */

			if (hcee.getStatusCode() != HttpStatus.METHOD_NOT_ALLOWED) {
				throw hcee;
			}
		}
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}
}
