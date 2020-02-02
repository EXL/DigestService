package ru.exlmoto.digest.motofan.generator;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.util.i18n.LocalizationHelper;
import ru.exlmoto.digest.motofan.json.MotofanPost;
import ru.exlmoto.digest.util.filter.FilterHelper;

@Component
public class MotofanTgHtmlGenerator {
	private final LocalizationHelper locale;
	private final FilterHelper filter;

	public MotofanTgHtmlGenerator(LocalizationHelper locale, FilterHelper filter) {
		this.locale = locale;
		this.filter = filter;
	}

	public String generateMotofanPostHtmlReport(MotofanPost post) {
		return
			locale.i18n("motofan.title") + "\n\n" +
			"<b>" + post.getAuthor() + "</b> " + locale.i18n("motofan.wrote") +
			" (<i>" + post.getTime() + "</i>):\n" + "<i>" + filterMotofanPost(post.getText()) + "</i>\n\n" +
			locale.i18n("motofan.read") + " <a href=\"" + post.getPost_link() + "\">" + post.getTitle() + "</a>";
	}

	public String filterMotofanPost(String text) {
		return filter.removeBbCodes(filter.removeHtmlTags(text));
	}
}
