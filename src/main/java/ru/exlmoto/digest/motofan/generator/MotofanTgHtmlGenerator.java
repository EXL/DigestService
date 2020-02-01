package ru.exlmoto.digest.motofan.generator;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.util.i18n.LocalizationHelper;
import ru.exlmoto.digest.motofan.json.MotofanPost;
import ru.exlmoto.digest.util.text.FilterTextHelper;

@Component
public class MotofanTgHtmlGenerator {
	private final LocalizationHelper locale;
	private final FilterTextHelper filterTextHelper;

	public MotofanTgHtmlGenerator(LocalizationHelper locale, FilterTextHelper filterTextHelper) {
		this.locale = locale;
		this.filterTextHelper = filterTextHelper;
	}

	public String generateMotofanPostHtmlReport(MotofanPost post) {
		return
			locale.i18n("motofan.title") + "\n\n" +
			"<b>" + post.getAuthor() + "</b> " + locale.i18n("motofan.wrote") +
			" (<i>" + post.getTime() + "</i>):\n" + "<i>" + filterMotofanPost(post.getText()) + "</i>\n\n" +
			locale.i18n("motofan.read") + " <a href=\"" + post.getPost_link() + "\">" + post.getTitle() + "</a>";
	}

	public String filterMotofanPost(String text) {
		return filterTextHelper.removeBbCodes(filterTextHelper.removeHtmlTags(text));
	}
}
