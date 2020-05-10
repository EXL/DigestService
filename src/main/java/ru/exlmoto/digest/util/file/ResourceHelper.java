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

package ru.exlmoto.digest.util.file;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.File;
import java.io.UncheckedIOException;

import java.net.URL;

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

	public String getResourceFilePath(String path) {
		if (StringUtils.hasText(path)) {
			String resource = path.replaceAll("classpath:", "");
			if (StringUtils.hasText(resource)) {
				URL fileUri = getClass().getClassLoader().getResource(resource);
				if (fileUri != null) {
					return new File(fileUri.getFile()).getAbsolutePath();
				}
			}
		}
		return null;
	}
}
