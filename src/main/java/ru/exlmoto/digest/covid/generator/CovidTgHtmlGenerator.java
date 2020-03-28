package ru.exlmoto.digest.covid.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.covid.json.Region;
import ru.exlmoto.digest.covid.parser.Covid2GisParser;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.filter.FilterHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import java.time.Instant;

import java.util.List;
import java.util.Map;

@Component
public class CovidTgHtmlGenerator {
	private final Logger log = LoggerFactory.getLogger(CovidTgHtmlGenerator.class);

	private final Covid2GisParser parser;
	private final LocaleHelper locale;
	private final FilterHelper filter;

	@Value("${covid.date-format}")
	private String dateFormat;

	public CovidTgHtmlGenerator(Covid2GisParser parser, LocaleHelper locale, FilterHelper filter) {
		this.parser = parser;
		this.locale = locale;
		this.filter = filter;
	}

	public String getTgHtmlReport(String covidUrl) {
		log.info("=> Start receive last COVID-2019 report.");
		Answer<Pair<List<Region>, Map<String, String>>> res = parser.parse2GisData(covidUrl);
		log.info("=> End receive last COVID-2019 report.");
		if (res.ok()) {
			Pair<List<Region>, Map<String, String>> answer = res.answer();
			return generateTgHtmlReport(answer.getFirst(), answer.getSecond());
		} else {
			return String.format(locale.i18n("covid.error"), res.error());
		}
	}

	private String generateTgHtmlReport(List<Region> cases, Map<String, String> history) {
		String report = generateHeader(history);
		report += generateTable(cases);
		return report;
	}

	private String generateHeader(Map<String, String> history) {
		String head = locale.i18n("covid.head");
		head += "\n\n<em>";
		head += String.format(locale.i18n("covid.head.cases"), history.get("cases")) +
			getDiff(history.get("cases_diff"), false) + "\n";
		head += String.format(locale.i18n("covid.head.recover"), history.get("recover")) +
			getDiff(history.get("recover_diff"), false) + "\n";
		head += String.format(locale.i18n("covid.head.deaths"), history.get("deaths")) +
			getDiff(history.get("deaths_diff"), false) + "\n";
		head += String.format(locale.i18n("covid.head.date"), getDateFormat(history.get("date"))) + "\n";
		head += "</em>\n";
		head += locale.i18n("covid.head.home");
		return head;
	}

	private String generateTable(List<Region> cases) {
		final int CHOP_NUMBER = 5;
		final int CHOP_CASES = 10;
		final int CHOP_REGION = 20;

		StringBuilder builder = new StringBuilder("\n\n<pre>");
		builder.append(filter.arrangeString(locale.i18n("covid.table.region"), CHOP_REGION)).append(" ");
		builder.append(filter.arrangeString(locale.i18n("covid.table.cases"), CHOP_CASES)).append(" ");
		builder.append(filter.arrangeString(locale.i18n("covid.table.recover"), CHOP_NUMBER)).append(" ");
		builder.append(filter.arrangeString(locale.i18n("covid.table.deaths"), CHOP_NUMBER)).append("\n");

		for (int i = 0; i < CHOP_NUMBER * 2 + CHOP_REGION + CHOP_CASES + 3; ++i) {
			builder.append("-");
		}
		builder.append("\n");

		for (Region report : cases) {
			builder.append(filter.ellipsisRightA(report.getName(), CHOP_REGION)).append(" ");
			builder.append(filter.ellipsisRightA(report.getCases() +
				getDiff(String.valueOf(report.getDiff()), true), CHOP_CASES)).append(" ");
			builder.append(filter.ellipsisRightA(String.valueOf(report.getRecover()), CHOP_NUMBER)).append(" ");
			builder.append(filter.ellipsisRightA(String.valueOf(report.getDeaths()), CHOP_NUMBER)).append("\n");
		}
		builder.append("</pre>").append("\n");
		builder.append(locale.i18n("covid.source"));

		return builder.toString();
	}

	private String getDateFormat(String date) {
		return filter.getDateFromTimeStamp(dateFormat, Instant.parse(date).getEpochSecond());
	}

	private String getDiff(String diff, boolean clear) {
		if (!diff.equals("0") && !diff.startsWith("-")) {
			return (clear) ?
				" " + String.format(locale.i18n("covid.increase"), diff) :
				String.format(locale.i18n("covid.increase.new"), diff);
		}
		return "";
	}
}
