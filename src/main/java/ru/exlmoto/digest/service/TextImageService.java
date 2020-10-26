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

package ru.exlmoto.digest.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JEditorPane;

@Service
public class TextImageService {
	private final Logger log = LoggerFactory.getLogger(TextImageService.class);

	public File renderSimpleHtmlToPngImage(String simpleHtml) {
		JEditorPane editorPane = new JEditorPane("text/html", simpleHtml);
		final Dimension size = editorPane.getPreferredSize();
		editorPane.setSize(size);
		final BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
		final Graphics graphics = image.createGraphics();
		editorPane.print(graphics);
		return saveImageToFile(image);
	}

	private File saveImageToFile(BufferedImage image) {
		try {
			File tempFile = File.createTempFile("temp-image-", ".png");
			ImageIO.write(image, "png", tempFile);
			return tempFile;
		} catch (IOException ioe) {
			log.error("Cannot create temporary image file!", ioe);
			return null;
		}
	}
}
