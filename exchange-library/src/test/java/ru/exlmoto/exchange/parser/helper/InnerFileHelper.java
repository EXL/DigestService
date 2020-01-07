package ru.exlmoto.exchange.parser.helper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class InnerFileHelper {
	public String content(String filename, String charsetName) {
		try {
			return new String(
				Files.readAllBytes(
					Paths.get(
						Objects.requireNonNull(
							getClass().getClassLoader().getResource("parser/" + filename)
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

	public String content(String filename) {
		return content(filename, Charset.defaultCharset().name());
	}
}
