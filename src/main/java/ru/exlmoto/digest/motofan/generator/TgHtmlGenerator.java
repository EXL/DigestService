package ru.exlmoto.digest.motofan.generator;

import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.i18n.LocalizationService;
import ru.exlmoto.digest.motofan.json.MotofanPost;
import ru.exlmoto.digest.util.Utils;

@RequiredArgsConstructor
@Component
public class TgHtmlGenerator {
	private final LocalizationService locale;
	private final Utils utils;

	public String generateHtmlReport(@NonNull MotofanPost post) {
		return
			locale.i18n("motofan.title") + "\n\n" +
			"<b>" + post.getAuthor() + "</b> " + locale.i18n("motofan.wrote") + " (" + post.getTime() + "):\n" +
			"<i>" + filterMotofanPost(post.getText()) + "</i>\n\n" +
			locale.i18n("motofan.read") + " <a href=\"" + post.getPost_link() + "\">" + post.getTitle() + "</a>";
	}

	private String removeBbCodes(@NonNull String text) {
		return text
			.replaceAll("\\[.*?]", " ")
			.replaceAll("\\[\\\\", " ")
			.replaceAll("\\[", " ").trim()
			.replaceAll(" +", " ");
	}

	public String filterMotofanPost(@NonNull String text) {
		return removeBbCodes(utils.removeHtmlTags(text));
	}
}
