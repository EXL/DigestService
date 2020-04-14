package ru.exlmoto.digest.covid.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.covid.json.RegionFull;
import ru.exlmoto.digest.covid.parser.Covid2GisApiParser;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.filter.FilterHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import java.time.Instant;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class CovidTgHtmlGenerator {
	private final Logger log = LoggerFactory.getLogger(CovidTgHtmlGenerator.class);

	private final Covid2GisApiParser parser;
	private final LocaleHelper locale;
	private final FilterHelper filter;

	@Value("${covid.date-format}")
	private String dateFormat;

	public CovidTgHtmlGenerator(Covid2GisApiParser parser, LocaleHelper locale, FilterHelper filter) {
		this.parser = parser;
		this.locale = locale;
		this.filter = filter;
	}

	public String getTgHtmlReport(String covidUrl, String casesPath, String historyPath) {
		log.info(String.format("=> Start receive last COVID-2019 report on '%s' path.", casesPath));
		Answer<Pair<List<RegionFull>, Map<String, String>>> res =
			parser.parse2GisApiData(covidUrl, casesPath, historyPath);
		log.info(String.format("=> End receive last COVID-2019 report on '%s' path.", casesPath));
		if (res.ok()) {
			Pair<List<RegionFull>, Map<String, String>> answer = res.answer();
			return generateTgHtmlReport(answer.getFirst(), answer.getSecond(), getLocale(casesPath));
		} else {
			log.error(String.format(locale.i18n("covid.error"), res.error()));
		}
		return null;
	}

	private Locale getLocale(String casesPath) {
		String langTag = casesPath.substring(casesPath.lastIndexOf("covid19-"), 2);
		System.out.println(langTag);
		return Locale.forLanguageTag(langTag);
	}

	private String generateTgHtmlReport(List<RegionFull> cases, Map<String, String> history, Locale lang) {
		String report = generateHeader(history, lang);
		report += generateTable(cases, lang);
		return report;
	}

	private String generateHeader(Map<String, String> history, Locale lang) {
		String head = locale.i18nW("covid.head", lang);
		head += "\n\n<em>";
		head += String.format(locale.i18nW("covid.head.cases", lang), history.get("cases")) +
			getDiff(history.get("cases_diff"), false, lang) + "\n";
		head += String.format(locale.i18nW("covid.head.recover", lang), history.get("recover")) +
			getDiff(history.get("recover_diff"), false, lang) + "\n";
		head += String.format(locale.i18nW("covid.head.deaths", lang), history.get("deaths")) +
			getDiff(history.get("deaths_diff"), false, lang) + "\n";
		head += String.format(locale.i18nW("covid.head.date", lang), getDateFormat(history.get("date"))) + "\n";
		head += "</em>\n";
		head += locale.i18nW("covid.head.home", lang);
		return head;
	}

	private String generateTable(List<RegionFull> cases, Locale lang) {
		final int CHOP_NUMBER = 8;
		final int CHOP_CASES = 12;
		final int CHOP_REGION = 12;

		StringBuilder builder = new StringBuilder("\n\n<pre>");
		builder.append(filter.arrangeString(locale.i18nW("covid.table.region", lang), CHOP_REGION)).append(" ");
		builder.append(filter.arrangeString(locale.i18nW("covid.table.cases", lang), CHOP_CASES)).append(" ");
		builder.append(filter.arrangeString(locale.i18nW("covid.table.recover", lang), CHOP_NUMBER)).append(" ");
		builder.append(filter.arrangeString(locale.i18nW("covid.table.deaths", lang), CHOP_NUMBER)).append("\n");

		for (int i = 0; i < CHOP_NUMBER * 2 + CHOP_REGION + CHOP_CASES + 3; ++i) {
			builder.append("-");
		}
		builder.append("\n");

		for (RegionFull report : cases) {
			builder.append(filter.ellipsisRightA(report.getTerritoryName(), CHOP_REGION)).append(" ");
			builder.append(filter.ellipsisRightA(report.getConfirmed() +
				getDiff(String.valueOf(report.getConfirmedInc()), true, lang), CHOP_CASES)).append(" ");
			builder.append(filter.ellipsisRightA(report.getRecovered() +
				getDiff(String.valueOf(report.getRecoveredInc()), true, lang), CHOP_CASES)).append(" ");
			builder.append(filter.ellipsisRightA(report.getDeaths() +
				getDiff(String.valueOf(report.getDeathsInc()), true, lang), CHOP_CASES)).append("\n");
		}
		builder.append("</pre>").append("\n");
		builder.append(locale.i18nW("covid.source", lang));

		return builder.toString();
	}

	private String getDateFormat(String date) {
		return filter.getDateFromTimeStamp(dateFormat, Instant.parse(date).getEpochSecond());
	}

	private String getDiff(String diff, boolean clear, Locale lang) {
		if (!diff.equals("0") && !diff.startsWith("-")) {
			return (clear) ?
				" " + String.format(locale.i18n("covid.increase"), diff) :
				String.format(locale.i18nW("covid.increase.new", lang), diff);
		}
		return "";
	}
}
