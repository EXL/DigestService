package ru.exlmoto.digest.flat.manager;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.rest.RestHelper;

@Component
public class FlatManager {
	private final RestHelper rest;

	public FlatManager(RestHelper rest) {
		this.rest = rest;
	}

	public Answer<String> getXlsxCianFile(String cianFileUrl) {
		return rest.getRestFile(cianFileUrl);
	}

	public Answer<String> getJsonN1Response(String n1JsonUrl) {
		return rest.getRestResponse(n1JsonUrl);
	}
}
