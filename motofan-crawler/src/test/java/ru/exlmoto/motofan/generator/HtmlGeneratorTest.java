package ru.exlmoto.motofan.generator;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ru.exlmoto.motofan.MotofanConfigurationTest;
import ru.exlmoto.motofan.manager.helper.MotofanPostHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HtmlGeneratorTest extends MotofanConfigurationTest {
	@Autowired
	private HtmlGenerator generator;

	@Test
	public void testGenerateHtmlReport() {
		System.out.println(generator.generateHtmlReport(new MotofanPostHelper().getRandomMotofanPost(1L)));
		System.out.println(generator.generateHtmlReport(new MotofanPostHelper().getRandomMotofanPost(2L)));
		System.out.println(generator.generateHtmlReport(new MotofanPostHelper().getRandomMotofanPost(3L)));
	}

	@Test
	public void testRemoveHtmlAndBbTags() {
		assertEquals("", generator.removeHtmlAndBbTags(""));
		assertEquals("test text test", generator.removeHtmlAndBbTags("test [b]text[/b] test"));
		assertEquals("test text test bl", generator.removeHtmlAndBbTags("test [b]text[/b] test [bl"));
		assertEquals("test text test bl", generator.removeHtmlAndBbTags("test [b]text[/b] test [\\bl"));
		assertEquals("test text test", generator.removeHtmlAndBbTags("test <b>text</b> test"));
		assertEquals("test text test", generator.removeHtmlAndBbTags("test <b id=\"a\">text</b> test"));
	}

	@SpringBootApplication
	public static class MotofanConfigurationCommon {

	}
}
