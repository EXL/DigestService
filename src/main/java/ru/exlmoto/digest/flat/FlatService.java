package ru.exlmoto.digest.flat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ru.exlmoto.digest.flat.generator.FlatTgHtmlGenerator;

@Service
public class FlatService {
	private final FlatTgHtmlGenerator generator;

	@Value("${flat.cian.slot1}")
	private String cianUrlOne;

	@Value("${flat.cian.slot2}")
	private String cianUrlTwo;

	@Value("${flat.n1.slot1}")
	private String n1UrlOne;

	@Value("${flat.n1.slot2}")
	private String n1UrlTwo;

	public FlatService(FlatTgHtmlGenerator generator) {
		this.generator = generator;
	}

	public String tgHtmlReportCianSlotFirst() {
		return generator.getTgHtmlReportCian(cianUrlOne);
	}

	public String tgHtmlReportN1SlotFirst() {
		return generator.getTgHtmlReportN1(n1UrlOne);
	}

	public String tgHtmlReportCianSlotSecond() {
		return generator.getTgHtmlReportCian(cianUrlTwo);
	}

	public String tgHtmlReportN1SlotSecond() {
		return generator.getTgHtmlReportN1(n1UrlTwo);
	}
}
