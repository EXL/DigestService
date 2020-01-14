package ru.exlmoto.digest.motofan.generator;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.i18n.LocalizationService;
import ru.exlmoto.digest.motofan.json.MotofanPost;
import ru.exlmoto.digest.util.Utils;

@RequiredArgsConstructor
@Component
public class TgHtmlGenerator {
	private final LocalizationService locale;
	private final Utils utils;

	public String generateHtmlReport(MotofanPost post) {
		return
			locale.i18n("motofan.title") + "\n\n" +
			"<b>" + post.getAuthor() + "</b> " + locale.i18n("motofan.wrote") + " (" + post.getTime() + "):\n" +
			"<i>" + filterMotofanPost(post.getText()) + "</i>\n\n" +
			locale.i18n("motofan.read") + " <a href=\"" + post.getPost_link() + "\">" + post.getTitle() + "</a>";
	}

	public String filterMotofanPost(String text) {
		return utils.removeBbCodes(utils.removeHtmlTags(text));
	}
}
