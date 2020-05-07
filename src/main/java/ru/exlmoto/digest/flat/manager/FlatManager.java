package ru.exlmoto.digest.flat.manager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.rest.RestHelper;

@Component
public class FlatManager {
	private final RestHelper rest;

	@Value("${flat.cian.slot1}")
	private String cianUrlOne;

	@Value("${flat.cian.slot2}")
	private String cianUrlTwo;

	@Value("${flat.n1.slot1}")
	private String n1UrlOne;

	@Value("${flat.n1.slot2}")
	private String n1UrlTwo;

	public FlatManager(RestHelper rest) {
		this.rest = rest;
	}

	public Answer<String> getHtmlContentCianFirst() {
		return rest.getRestResponse(cianUrlOne);
	}

	public Answer<String> getHtmlContentCianSecond() {
		return rest.getRestResponse(cianUrlTwo);
	}

	public Answer<String> getHtmlContentN1First() {
		return rest.getRestResponse(n1UrlOne);
	}

	public Answer<String> getHtmlContentN1Second() {
		return rest.getRestResponse(n1UrlTwo);
	}
}
