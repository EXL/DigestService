package ru.exlmoto.digest.util.text;

import org.jsoup.Jsoup;

import org.springframework.stereotype.Component;

@Component
public class FilterTextHelper {
	/*
	 * Remove html tags from text.
	 * See: https://stackoverflow.com/questions/14445386/how-to-remove-text-in-brackets-from-the-start-of-a-string
	 */
	public String removeHtmlTags(String html) {
		return Jsoup.parse(html).text();
	}

	public String removeBbCodes(String text) {
		return text
			.replaceAll("\\[.*?]", " ")
			.replaceAll("\\[\\\\", " ")
			.replaceAll("\\[", " ").trim()
			.replaceAll(" +", " ");
	}
}
