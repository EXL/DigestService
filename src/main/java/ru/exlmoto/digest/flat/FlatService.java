package ru.exlmoto.digest.flat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ru.exlmoto.digest.flat.generator.FlatTgHtmlGenerator;

@Service
public class FlatService {
	private final FlatTgHtmlGenerator generator;

	@Value("${flat.cian.url}")
	private String cianUrl;

	@Value("${flat.n1.url}")
	private String n1Url;

	public FlatService(FlatTgHtmlGenerator generator) {
		this.generator = generator;
	}

	public String tgHtmlReportCian() {
		return generator.getTgHtmlReportCian(cianUrl);
	}

	public String tgHtmlReportN1() {
		return generator.getTgHtmlReportN1(n1Url);
	}
}
