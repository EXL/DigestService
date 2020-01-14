package ru.exlmoto.digest.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class ResourceFileHelper {
	public String getFileContent(String path, String charsetName) {
		try {
			return new String(
				Files.readAllBytes(
					Paths.get(
						Objects.requireNonNull(
							getClass().getClassLoader().getResource(path)
						).toURI()
					)
				),
				Charset.forName(charsetName)
			);
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getFileContent(String path) {
		// Charset.defaultCharset().name() will not work as excepted on Windows OS.
		return getFileContent( path, "UTF-8");
	}
}
