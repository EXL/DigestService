package ru.exlmoto.digest.util.file;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.util.Answer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
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
	}
}
