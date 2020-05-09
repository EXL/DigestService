package ru.exlmoto.digest.flat.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import ru.exlmoto.digest.flat.manager.FlatManager;
import ru.exlmoto.digest.flat.model.Flat;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.filter.FilterHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import java.util.List;

import static ru.exlmoto.digest.util.Answer.Ok;
import static ru.exlmoto.digest.util.Answer.Error;

@Component
public class FlatTgHtmlGenerator {
	private final Logger log = LoggerFactory.getLogger(FlatTgHtmlGenerator.class);

	private final int LONG_URL_WIDTH = 50;

	private final FlatManager manager;
	private final LocaleHelper locale;
	private final FilterHelper filter;

	@Value("${general.date-format}")
	private String dateFormat;

	public FlatTgHtmlGenerator(FlatManager manager, LocaleHelper locale, FilterHelper filter) {
		this.manager = manager;
		this.locale = locale;
		this.filter = filter;
	}

	public String getTgHtmlReportCian(String apiUrl, String viewUrl, int maxVariants) {
		return getTgHtmlReportAux(apiUrl, viewUrl, maxVariants, manager.getCianFlatList(apiUrl), "CIAN",
			locale.i18n("flat.header.cian"), locale.i18n("flat.link.cian"));
	}

	public String getTgHtmlReportN1(String apiUrl, String viewUrl, int maxVariants) {
		return getTgHtmlReportAux(apiUrl, viewUrl, maxVariants, manager.getN1FlatList(apiUrl), "N1",
			locale.i18n("flat.header.n1"), locale.i18n("flat.link.n1"));
	}

	private String getTgHtmlReportAux(String apiUrl, String viewUrl, int maxVariants,
	                                  Answer<List<Flat>> flatAnswer,
	                                  String label, String header, String footer) {
		String link = filter.ellipsisMiddle(apiUrl, LONG_URL_WIDTH);
		log.info(String.format("=> Start receive %s Flat report on '%s' link.", label, link));
		Answer<String> res = getTgHtmlReport(flatAnswer, maxVariants);
		log.info(String.format("=> End receive %s Flat report on '%s' link.", label, link));
		if (res.ok()) {
			return "<strong>" + header + res.answer() + addSuggestionsLink(viewUrl, footer);
		} else {
			return sendError(label, res.error());
		}
	}

	private String sendError(String serviceId, String error) {
		return String.format(locale.i18n("flat.error"), serviceId, error);
	}

	private String addSuggestionsLink(String link, String title) {
		if (StringUtils.hasText(link)) {
			return "\n" + locale.i18n("flat.link.icon") + " " + createTgHtmlLink(link, title);
		}
		return "";
	}

	private Answer<String> getTgHtmlReport(Answer<List<Flat>> flatAnswer, int max) {
		if (flatAnswer.ok()) {
			return Ok(getFlatList(flatAnswer.answer(), max));
		}
		return Error(getTgHtmlError(flatAnswer.error()));
	}

	private String getTgHtmlError(String error) {
		return error;
	}

	private String getFlatList(List<Flat> flats, int max) {
		int size = flats.size();

		StringBuilder builder = new StringBuilder();
		builder
			.append(" ")
			.append(determineFlatCount(size, max))
			.append("/")
			.append(size)
			.append("\n")
			.append(String.format(locale.i18n("flat.header.date"),
				filter.getDateFromTimeStamp(dateFormat, filter.getCurrentUnixTime())))
			.append("</strong>\n");

		List<Flat> cropped = flats;
		if (size > max) {
			cropped = flats.subList(0, max);
		}

		size = cropped.size();
		for (int i = 0; i < size; ++i) {
			Flat flat = cropped.get(i);
			builder
				.append("\n")
				.append(i + 1)
				.append("| ")
				.append(flat.getRooms())
				.append(locale.i18n("flat.room.postfix"))
				.append(", ")
				.append(flat.getSquares())
				.append(" ")
				.append(locale.i18n("flat.square.postfix"))
				.append(", ")
				.append(flat.getFloor())
				.append(", <strong>")
				.append(createTgHtmlLink(flat.getLink(), flat.getPrice()))
				.append("</strong>\n")
				.append(flat.getAddress())
				.append("; ")
				.append(flat.getPhone())
				.append("\n");
		}
		return builder.toString();
	}

	private String createTgHtmlLink(String link, String title) {
		return "<a href=\"" + link + "\">" + title + "</a>";
	}

	private int determineFlatCount(int size, int max) {
		return Math.min(size, max);
	}
}
