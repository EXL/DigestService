package ru.exlmoto.digest.util;

import org.jsoup.Jsoup;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class Utils {
	/*
	 * Remove html tags from text.
	 * See: https://stackoverflow.com/questions/14445386/how-to-remove-text-in-brackets-from-the-start-of-a-string
	 */
	public String removeHtmlTags(@NonNull String html) {
		return Jsoup.parse(html).text();
	}

	public String removeBbCodes(@NonNull String text) {
		return text
			.replaceAll("\\[.*?]", " ")
			.replaceAll("\\[\\\\", " ")
			.replaceAll("\\[", " ").trim()
			.replaceAll(" +", " ");
	}
}
