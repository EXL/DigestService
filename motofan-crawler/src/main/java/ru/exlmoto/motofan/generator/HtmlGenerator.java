package ru.exlmoto.motofan.generator;

import lombok.RequiredArgsConstructor;

import org.jsoup.Jsoup;

import org.springframework.stereotype.Component;

import ru.exlmoto.motofan.generator.helper.GeneratorHelper;
import ru.exlmoto.motofan.manager.json.MotofanPost;

@RequiredArgsConstructor
@Component
public class HtmlGenerator {
	private final GeneratorHelper helper;

	public String generateHtmlReport(MotofanPost post) {
		return helper.i18n("title") + "\n\n" +
			"<b>" + post.getAuthor() + "</b> " + helper.i18n("wrote") + " (" + post.getTime() + "):\n" +
			"<i>" + removeHtmlAndBbTags(post.getText()) + "</i>\n\n" +
			helper.i18n("read") + " <a href=\"" + post.getPost_link() + "\">" + post.getTitle() + "</a>";
	}

	// https://stackoverflow.com/questions/14445386/how-to-remove-text-in-brackets-from-the-start-of-a-string
	public String removeHtmlAndBbTags(final String aText) {
		return Jsoup.parse(aText).text()
			.replaceAll("\\[.*?\\]", " ")
			.replaceAll("\\[\\\\", " ")
			.replaceAll("\\[", " ").trim()
			.replaceAll(" +", " ");
	}
}
