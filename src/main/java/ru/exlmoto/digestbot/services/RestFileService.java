package ru.exlmoto.digestbot.services;

import org.apache.commons.io.FilenameUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpMethod;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Objects;

public class RestFileService extends RestService {
	public RestFileService(final RestTemplateBuilder aRestTemplateBuilder,
	                       final Integer aRequestTimeout) {
		super(aRestTemplateBuilder, aRequestTimeout);
	}

	@Override
	public Pair<Boolean, String> receiveObject(final String aRequestUrl) {
		try {
			final String lFileName = new File(new URL(aRequestUrl).getPath()).getName();
			final String lExtension = FilenameUtils.getExtension(lFileName);
			final String lClearFileName = FilenameUtils.removeExtension(lFileName);
			return Pair.of(true, Objects.requireNonNull(getRestTemplate().execute(aRequestUrl, HttpMethod.GET,
				null, aClientHttpResponse -> {
					File lTempFile = File.createTempFile(lExtension, lClearFileName);
					StreamUtils.copy(aClientHttpResponse.getBody(), new FileOutputStream(lTempFile));
					return lTempFile.getAbsolutePath();
				})));
		} catch (Exception e) {
			return Pair.of(false, e.toString());
		}
	}
}
