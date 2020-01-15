package ru.exlmoto.digest.util.file;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/*
 * Source: https://www.baeldung.com/spring-load-resource-as-string
 * Why is used `StandardCharsets.UTF_8` instead of `Charset.defaultCharset().name()`?
 * Perhaps second one will not work as excepted on Windows OS.
 */

@Component
public class ResourceHelper {
	public String asString(Resource resource) {
		return asString(resource, StandardCharsets.UTF_8);
	}

	public String readFileToString(String path) {
		return readFileToString(path, StandardCharsets.UTF_8);
	}

	public String asString(Resource resource, Charset charset) {
		try (Reader reader = new InputStreamReader(resource.getInputStream(), charset)) {
			return FileCopyUtils.copyToString(reader);
		} catch (IOException ioe) {
			throw new UncheckedIOException(ioe);
		}
	}

	public String readFileToString(String path, Charset charset) {
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		Resource resource = resourceLoader.getResource(path);
		return asString(resource, charset);
	}
}
