/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

@Component
public class ImageHelper {
	private final Logger log = LoggerFactory.getLogger(ImageHelper.class);

	@Value("${image.use-image-io-read}")
	private boolean useImageIoRead;

	@Value("${image.download-file}")
	private boolean downloadFile;

	private final RestHelper rest;

	public ImageHelper(RestHelper rest) {
		this.rest = rest;
	}

	public Answer<String> getImageByLink(String url) {
		if (!downloadFile) {
			return Ok(url);
		}
		Answer<String> res = rest.getRestFile(url);
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
		return (useImageIoRead) ? isFileImageRead(imageFile) : isFileImageProbing(imageFile);
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
