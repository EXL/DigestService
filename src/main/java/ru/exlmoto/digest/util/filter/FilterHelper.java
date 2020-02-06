package ru.exlmoto.digest.util.filter;

import org.jsoup.Jsoup;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class FilterHelper {
	/*
	 * Remove html tags from text.
	 * See: https://stackoverflow.com/questions/14445386/how-to-remove-text-in-brackets-from-the-start-of-a-string
	 */
	public String removeHtmlTags(String html) {
		return Jsoup.parse(html).text();
	}

	public String removeBbCodes(String str) {
		return str
			.replaceAll("\\[.*?]", " ")
			.replaceAll("\\[\\\\", " ")
			.replaceAll("\\[", " ").trim()
			.replaceAll(" +", " ");
	}

	public String checkLink(String url) {
		return url.endsWith("/") ? url : url + "/";
	}

	public String getDateFromTimeStamp(String dateFormat, long timestamp) {
		return DateTimeFormatter.ofPattern(dateFormat)
			.withZone(ZoneId.systemDefault()).format(Instant.ofEpochSecond(timestamp));
	}

	public String strip(String str) {
		return str.replaceAll("\\s+", " ").trim();
	}

	public String escapeTags(String str) {
		return str.replaceAll("<", "&#60;").replaceAll(">", "&#62;");
	}

	public String removeUserCasts(String str) {
		return str.replaceAll("\\B@", "");
	}
}
