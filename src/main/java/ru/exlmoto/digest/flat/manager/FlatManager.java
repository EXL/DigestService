package ru.exlmoto.digest.flat.manager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.rest.RestHelper;

@Component
public class FlatManager {
	private final RestHelper rest;

	@Value("${flat.cian.url}")
	private String cianFileUrl;

	@Value("${flat.n1.url}")
	private String n1JsonUrl;

	public FlatManager(RestHelper rest) {
		this.rest = rest;
	}

	public Answer<String> getXlsxCianFile() {
		System.out.println(cianFileUrl);
		return rest.getRestFile(cianFileUrl);
	}

	public Answer<String> getJsonN1Response() {
		return rest.getRestResponse(n1JsonUrl);
	}
}
