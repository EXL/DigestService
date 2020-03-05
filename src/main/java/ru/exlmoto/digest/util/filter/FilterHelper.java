package ru.exlmoto.digest.util.filter;

import org.jsoup.Jsoup;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.util.i18n.LocaleHelper;

import javax.annotation.PostConstruct;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class FilterHelper {
	private final LocaleHelper locale;

	private String ellipsis;

	public FilterHelper(LocaleHelper locale) {
		this.locale = locale;
	}

	@PostConstruct
	private void setUp() {
		ellipsis = locale.i18n("bot.command.show.ellipsis");
	}

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

	public String arrangeString(String string, int length) {
		int stringLength = string.length();
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < length - stringLength; ++i) {
			stringBuilder.append(' ');
		}
		return string + stringBuilder.toString();
	}

	protected String ellipsisString(String text, int length, String ellipsis, int side, boolean arrange) {
		if (length > 0) {
			int textLength = text.length();
			if (textLength < length) {
				return (arrange) ? arrangeString(text, length) : text;
			}
			if (side < 0) {
				return ellipsis + text.substring(textLength - length + 1);
			} else if (side > 0){
				return text.substring(0, length - 1) + ellipsis;
			} else {
				return text.substring(0, length / 2) + ellipsis + text.substring(textLength - (length / 2) + 1);
			}
		}
		return text;
	}

	public String ellipsisRight(String text, int length) {
		return ellipsisString(text, length, ellipsis, 1, false);
	}

	public String ellipsisRightA(String text, int length) {
		return ellipsisString(text, length, ellipsis, 1, true);
	}

	public String ellipsisLeft(String text, int length) {
		return ellipsisString(text, length, ellipsis, -1, false);
	}

	public String ellipsisLeftA(String text, int length) {
		return ellipsisString(text, length, ellipsis, -1, true);
	}

	public String ellipsisMiddle(String text, int length) {
		return ellipsisString(text, length, ellipsis, 0, false);
	}
}
