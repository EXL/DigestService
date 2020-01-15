package ru.exlmoto.digest.util.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.rest.RestHelper;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static ru.exlmoto.digest.util.Answer.Error;
import static ru.exlmoto.digest.util.Answer.Ok;

/*
 * Source: https://stackoverflow.com/a/35484655, https://stackoverflow.com/a/18208521
 *
 * Profiling on cold runs:
 * 1. isFileImageProbing() took 132 ms | isFileImageRead() took 286 ms
 * 2. isFileImageProbing() took  80 ms | isFileImageRead() took 116 ms
 * 3. isFileImageProbing() took  38 ms | isFileImageRead() took 110 ms
 * 4. isFileImageProbing() took  28 ms | isFileImageRead() took  87 ms
 * 5. isFileImageProbing() took  27 ms | isFileImageRead() took  99 ms
 * 6. isFileImageProbing() took  21 ms | isFileImageRead() took 127 ms
 */

@Slf4j
@RequiredArgsConstructor
@Component
public class ImageHelper {
	@Value("${image.use-probing}")
	private boolean useProbing;

	@Value("${image.download-file}")
	private boolean downloadFile;

	private final RestHelper restHelper;

	public Answer<String> getImageByLink(String url) {
		if (!downloadFile) {
			return Ok(url);
		}
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
		File imageFile = new File(path);
		return (useProbing) ? isFileImageProbing(imageFile) : isFileImageRead(imageFile);
	}

	public boolean isFileImageProbing(File file) {
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

	public boolean isFileImageRead(File file) {
		try {
			return ImageIO.read(file) != null;
		} catch (IOException ioe) {
			log.error(String.format("Cannot open file on '%s' path.", file.toPath()), ioe);
		}
		return false;
	}
}
