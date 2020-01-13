package ru.exlmoto.chart.manager;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.time.Duration;

@Slf4j
@Component
public class RestFileManager {
	@Value("${general.connection.timeout}")
	private int timeOutSec;

	private final long maxFileSize = 1024 * 1024 * 5; // 5 MiB

	public RestTemplate getRestTemplate() {
		return new RestTemplateBuilder()
			.setConnectTimeout(Duration.ofSeconds(timeOutSec))
			.setReadTimeout(Duration.ofSeconds(timeOutSec))
			.setBufferRequestBody(false)
			.build();
	}

	public String receiveFile(String url) {
		try {
			return getRestTemplate().execute(url, HttpMethod.GET, null,
				response -> {
					if (response.getHeaders().getContentLength() > maxFileSize) {
						response.close();
						return null;
					}
					System.out.println("WTF???");
					String suffix = "-" + new File(new URL(url).getPath()).getName();
					suffix = suffix.length() > 3 ? suffix : null;
					File tempFile = File.createTempFile("temp-", suffix);
					StreamUtils.copy(response.getBody(), new FileOutputStream(tempFile));
					return tempFile.getAbsolutePath();
				});
		} catch (Exception e) {
			log.error("Cannot download file to system.", e);
		}
		return null;
	}
}
