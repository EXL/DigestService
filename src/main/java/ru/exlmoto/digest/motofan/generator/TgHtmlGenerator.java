package ru.exlmoto.digest.motofan.generator;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.util.i18n.LocalizationHelper;
import ru.exlmoto.digest.motofan.json.MotofanPost;
import ru.exlmoto.digest.util.Util;

@RequiredArgsConstructor
@Component
public class TgHtmlGenerator {
	private final LocalizationHelper locale;
	private final Util util;

	public String generateHtmlReport(MotofanPost post) {
		return
			locale.i18n("motofan.title") + "\n\n" +
			"<b>" + post.getAuthor() + "</b> " + locale.i18n("motofan.wrote") + " (" + post.getTime() + "):\n" +
			"<i>" + filterMotofanPost(post.getText()) + "</i>\n\n" +
			locale.i18n("motofan.read") + " <a href=\"" + post.getPost_link() + "\">" + post.getTitle() + "</a>";
	}

	public String filterMotofanPost(String text) {
		return util.removeBbCodes(util.removeHtmlTags(text));
	}
}
