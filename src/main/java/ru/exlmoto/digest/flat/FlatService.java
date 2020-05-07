package ru.exlmoto.digest.flat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ru.exlmoto.digest.flat.generator.FlatTgHtmlGenerator;
import ru.exlmoto.digest.util.rest.RestHelper;

@Service
public class FlatService {
	private final FlatTgHtmlGenerator generator;

	public FlatService(FlatTgHtmlGenerator generator) {
		this.generator = generator;
	}

	public String tgHtmlReportCianSlotFirst() {
		return generator.getTgHtmlReportCianFirst();
	}

	public String tgHtmlReportCianSlotSecond() {
		return generator.getTgHtmlReportCianSecond();
	}


	public String tgHtmlReportN1SlotFirst() {
		return generator.getTgHtmlReportN1First();
	}

	public String tgHtmlReportN1SlotSecond() {
		return generator.getTgHtmlReportN1Second();
	}
}
