package ru.exlmoto.digest.flat;

import org.springframework.stereotype.Service;

import ru.exlmoto.digest.flat.generator.FlatTgHtmlGenerator;

@Service
public class FlatService {
	private final FlatTgHtmlGenerator generator;

	public FlatService(FlatTgHtmlGenerator generator) {
		this.generator = generator;
	}

	public String tgHtmlReportCian(String apiUrl, String viewUrl, int maxVariants) {
		return generator.getTgHtmlReportCian(apiUrl, viewUrl, maxVariants);
	}

	public String tgHtmlReportN1(String apiUrl, String viewUrl, int maxVariants) {
		return generator.getTgHtmlReportN1(apiUrl, viewUrl, maxVariants);
	}
}
