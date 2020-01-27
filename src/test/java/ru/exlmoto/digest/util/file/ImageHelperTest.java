package ru.exlmoto.digest.util.file;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.util.Answer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = { "image.download-file=true", "image.use-image-io-read=true" })
class ImageHelperTest {
	@Autowired
	private ImageHelper imageHelper;

	@Test
	public void testGetImageByLink() {
		// Wrong link.
		Answer<String> res = imageHelper.getImageByLink("https://exlmotor.ru");
		assertFalse(res.ok());
		System.out.println("Get: " + res.error());

		// Wrong content.
		res = imageHelper.getImageByLink("https://exlmoto.ru");
		assertFalse(res.ok());
		System.out.println("Get: " + res.error());

		// Good image.
		res = imageHelper.getImageByLink("https://api.z-lab.me/charts/wti.png");
		assertTrue(res.ok());
		System.out.println("Get: " + res.answer());

		// Another good image without url-extension.
		res = imageHelper.getImageByLink("https://forum.motofan.ru/index.php?act=Attach&type=post&id=274426");
		assertTrue(res.ok());
		System.out.println("Get: " + res.answer());
	}
}
