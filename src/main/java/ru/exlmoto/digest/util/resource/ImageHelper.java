package ru.exlmoto.digest.util.resource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static ru.exlmoto.digest.util.Answer.Error;

/*
 * Source: https://stackoverflow.com/a/35484655
 */

@Slf4j
@RequiredArgsConstructor
@Component
public class ImageHelper {
	private final RestHelper restHelper;

	public Answer<String> getImageByLink(String url) {
		Answer<String> res = restHelper.getRestFile(url);
		if (res.ok()) {
			String path = res.answer();
			if (isFileImage(path)) {
				return res;
			}
			return Error(String.format("File on '%s' path is not image!", path));
		}
		return res;
	}

	public boolean isFileImage(String path) {
		return isFileImage(new File(path));
	}

	public boolean isFileImage(File file) {
		try {
			String mimeType = Files.probeContentType(file.toPath());
			if (mimeType != null && mimeType.split("/")[0].equals("image")) {
				return true;
			}
		} catch (IOException ioe) {
			log.error(String.format("Cannot probe content type on '%s' path.", file.toPath()), ioe);
		}
		return false;
	}
}
