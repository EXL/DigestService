package ru.exlmoto.digest.flat.generator;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.flat.configuration.FlatConfiguration;
import ru.exlmoto.digest.flat.manager.FlatManager;
import ru.exlmoto.digest.flat.model.Flat;
import ru.exlmoto.digest.flat.parser.FlatParser;
import ru.exlmoto.digest.flat.parser.impl.FlatCianParser;
import ru.exlmoto.digest.flat.parser.impl.FlatN1Parser;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import java.util.List;

@Component
public class FlatTgHtmlGenerator {
	private final FlatConfiguration config;
	private final FlatManager manager;
	private final FlatCianParser cianParser;
	private final FlatN1Parser n1Parser;
	private final LocaleHelper locale;

	public FlatTgHtmlGenerator(FlatConfiguration config,
	                           FlatManager manager,
	                           FlatCianParser cianParser,
	                           FlatN1Parser n1Parser,
	                           LocaleHelper locale) {
		this.config = config;
		this.manager = manager;
		this.cianParser = cianParser;
		this.n1Parser = n1Parser;
		this.locale = locale;
	}

	public String getTgHtmlReportCian() {
		String body = getTgHtmlReportAux(manager.getXlsxCianFile(), cianParser);
		return body;
	}

	public String getTgHtmlReportN1() {
		String body = getTgHtmlReportAux(manager.getJsonN1Response(), n1Parser);
		return body;
	}

	private String getTgHtmlReportAux(Answer<String> content, FlatParser parser) {
		if (content.ok()) {
			Answer<List<Flat>> res = parser.getAvailableFlats(content.answer());
			if (res.ok()) {
				return getFlatList(res.answer());
			}
			return getTgHtmlError(res.error());
		}
		return getTgHtmlError(content.error());
	}

	private String getTgHtmlError(String error) {
		return error;
	}

	private String getFlatList(List<Flat> flats) {
		int max = config.getMaxVariants();
		int size = flats.size();

		StringBuilder builder = new StringBuilder();
		builder.append(String.format(locale.i18n("flat.header"), determineFlatCount(size, max), size));
		builder.append("\n");

		List<Flat> cropped = flats;
		if (size > max) {
			cropped = flats.subList(0, max);
		}

		cropped.forEach(flat -> builder
			.append("<pre>\n")
			.append(flat.getRooms())
			.append(locale.i18n("flat.room.postfix"))
			.append(", ")
			.append(flat.getSquares())
			.append(" ")
			.append(locale.i18n("flat.square.postfix"))
			.append(", ")
			.append(flat.getFloor())
			.append(" ")
			.append(locale.i18n("flat.floor"))
			.append("\n")
			.append(String.format(locale.i18n("flat.price"), flat.getPrice()))
			.append("\n")
			.append(String.format(locale.i18n("flat.address"), flat.getAddress()))
			.append("\n")
			.append(String.format(locale.i18n("flat.phone"), flat.getPhone()))
			.append("\n</pre>\n")
			.append(createTgHtmlLink(flat.getLink(), locale.i18n("flat.link")))
		);
		return builder.toString();
	}

	private String createTgHtmlLink(String link, String title) {
		return "<a href=\"" + link + "\">" + title + "</a>";
	}

	private int determineFlatCount(int size, int max) {
		return Math.min(size, max);
	}
}
