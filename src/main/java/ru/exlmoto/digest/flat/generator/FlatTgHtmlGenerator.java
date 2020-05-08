package ru.exlmoto.digest.flat.generator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import ru.exlmoto.digest.flat.manager.FlatManager;
import ru.exlmoto.digest.flat.model.Flat;
import ru.exlmoto.digest.flat.parser.FlatParser;
import ru.exlmoto.digest.flat.parser.impl.FlatCianParser;
import ru.exlmoto.digest.flat.parser.impl.FlatN1Parser;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.filter.FilterHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import java.util.List;

import static ru.exlmoto.digest.util.Answer.Ok;
import static ru.exlmoto.digest.util.Answer.Error;

@Component
public class FlatTgHtmlGenerator {
	private final FlatManager manager;
	private final FlatCianParser cianParser;
	private final FlatN1Parser n1Parser;
	private final LocaleHelper locale;
	private final FilterHelper filter;

	@Value("${general.date-format}")
	private String dateFormat;

	public FlatTgHtmlGenerator(FlatManager manager,
	                           FlatCianParser cianParser,
	                           FlatN1Parser n1Parser,
	                           LocaleHelper locale,
	                           FilterHelper filter) {
		this.manager = manager;
		this.cianParser = cianParser;
		this.n1Parser = n1Parser;
		this.locale = locale;
		this.filter = filter;
	}

	public String getTgHtmlReportCian(String apiUrl, String viewUrl, int maxVariants) {
		Answer<String> res = getTgHtmlReportAux(manager.getXlsxCianFile(apiUrl), cianParser, maxVariants);
		if (res.ok()) {
			return
				"<strong>" + locale.i18n("flat.header.cian") +              // Header.
				res.answer() +                                              // Body.
				addSuggestionsLink(viewUrl, locale.i18n("flat.link.cian")); // Footer.
		} else {
			return sendError("CIAN", res.error());
		}
	}

	public String getTgHtmlReportN1(String apiUrl, String viewUrl, int maxVariants) {
		Answer<String> res = getTgHtmlReportAux(manager.getJsonN1Response(apiUrl), n1Parser, maxVariants);
		if (res.ok()) {
			return
				"<strong>" + locale.i18n("flat.header.n1") +                // Header.
				res.answer() +                                              // Body.
				addSuggestionsLink(viewUrl, locale.i18n("flat.link.n1"));   // Footer.
		} else {
			return sendError("N1", res.error());
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

	private Answer<String> getTgHtmlReportAux(Answer<String> content, FlatParser parser, int max) {
		if (content.ok()) {
			Answer<List<Flat>> res = parser.getAvailableFlats(content.answer());
			if (res.ok()) {
				List<Flat> flats = res.answer();
				if (!flats.isEmpty()) {
					return Ok(getFlatList(flats, max));
				}
				else {
					return Error(getTgHtmlError(locale.i18n("flat.error.empty")));
				}
			}
			return Error(getTgHtmlError(res.error()));
		}
		return Error(getTgHtmlError(content.error()));
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
