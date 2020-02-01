package ru.exlmoto.digest.motofan.generator;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.motofan.json.MotofanPostHelper;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class TgHtmlGeneratorTest {
	@Autowired
	private TgHtmlGenerator htmlGenerator;

	@SuppressWarnings("ConstantConditions")
	@Test
	public void testGeneratorTgHtmlOnNulls() {
		assertThrows(NullPointerException.class, () -> htmlGenerator.generateMotofanPostHtmlReport(null));
		assertThrows(NullPointerException.class, () -> htmlGenerator.filterMotofanPost(null));
	}

	@Test
	public void testGenerateHtmlReport() {
		generateHtmlReports(1L);
		generateHtmlReports(2L);
		generateHtmlReports(3L);
	}

	@Test
	public void testFilterMotofanPost() {
		assertEquals("", htmlGenerator.filterMotofanPost(""));
		assertEquals("test text test", htmlGenerator.filterMotofanPost("test [b]text[/b] test"));
		assertEquals("test text test bl", htmlGenerator.filterMotofanPost("test [b]text[/b] test [bl"));
		assertEquals("test text test bl", htmlGenerator.filterMotofanPost("test [b]text[/b] test [\\bl"));
		assertEquals("test text test", htmlGenerator.filterMotofanPost("test <b>text</b> test"));
		assertEquals("test text test", htmlGenerator.filterMotofanPost("test <b id=\"a\">text</b> test"));
	}

	private void generateHtmlReports(long timestamp) {
		String result =
			htmlGenerator.generateMotofanPostHtmlReport(new MotofanPostHelper().getRandomMotofanPost(timestamp));
		assertThat(result).isNotEmpty();
		System.out.println(result + "\n");
	}
}
