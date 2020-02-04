package ru.exlmoto.digest.bot.worker;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.bot.util.UpdateHelper;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.file.ResourceHelper;
import ru.exlmoto.digest.bot.worker.AvatarWorker.Extension;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AvatarWorkerTest {
	@Autowired
	private AvatarWorker worker;

	@Autowired
	private ResourceHelper resourceHelper;
	
	private final UpdateHelper update = new UpdateHelper();

	@Test
	public void testGetAvatarLink() {
		String avatar = worker.getAvatarLink(update.getUser("exlmoto"));
		assertTrue(Extension.checkImageExtension(avatar));

		avatar = worker.getAvatarLink(update.getUser("unknown_username_321003131"));
		assertFalse(Extension.checkImageExtension(avatar));
	}

	@Test
	public void testCheckImageExtension() {
		assertTrue(Extension.checkImageExtension("https://test.org/image.jpg"));
		assertTrue(Extension.checkImageExtension("https://test.org/image.JPG"));
		assertTrue(Extension.checkImageExtension("https://test.org/image.png"));
		assertTrue(Extension.checkImageExtension("https://test.org/image.PNG"));
		assertTrue(Extension.checkImageExtension("https://test.org/image.gif"));
		assertTrue(Extension.checkImageExtension("https://test.org/image.GIF"));

		assertFalse(Extension.checkImageExtension("https://test.org/image.webp"));
		assertFalse(Extension.checkImageExtension("https://test.org/image.WEBP"));
		assertFalse(Extension.checkImageExtension("https://test.org/image.php"));
		assertFalse(Extension.checkImageExtension("https://test.org/image_no_ext"));
	}

	@Test
	public void testIsolateAvatar() {
		Answer<String> res =
			worker.isolateAvatar(resourceHelper.readFileToString("classpath:bot/avatarNull.html"));
		assertFalse(res.ok());

		res = worker.isolateAvatar(resourceHelper.readFileToString("classpath:bot/avatarEmpty.html"));
		assertFalse(res.ok());

		res = worker.isolateAvatar(resourceHelper.readFileToString("classpath:bot/avatarExlmoto.html"));
		assertTrue(res.ok());

		res = worker.isolateAvatar(resourceHelper.readFileToString("classpath:bot/avatarOther.html",
			StandardCharsets.ISO_8859_1));
		assertFalse(res.ok());

		res = worker.isolateAvatar(resourceHelper.readFileToString("classpath:bot/avatarUnknown.html"));
		assertFalse(res.ok());
	}
}
