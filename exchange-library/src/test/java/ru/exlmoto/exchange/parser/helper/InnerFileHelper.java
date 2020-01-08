package ru.exlmoto.exchange.parser.helper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class InnerFileHelper {
	public String getFileContent(String filename, String prefix, String charsetName) {
		try {
			return new String(
				Files.readAllBytes(
					Paths.get(
						Objects.requireNonNull(
							getClass().getClassLoader().getResource(prefix + filename)
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

	public String getFileContent(String filename, String charsetName) {
		return getFileContent(filename, "parser/", charsetName);
	}

	public String getFileContent(String filename) {
		return getFileContent(filename, "parser/", Charset.defaultCharset().name());
	}
}
