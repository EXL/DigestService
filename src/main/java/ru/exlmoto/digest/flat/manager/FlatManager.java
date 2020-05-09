package ru.exlmoto.digest.flat.manager;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.flat.model.Flat;
import ru.exlmoto.digest.flat.parser.impl.FlatCianParser;
import ru.exlmoto.digest.flat.parser.impl.FlatN1Parser;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.util.List;

import static ru.exlmoto.digest.util.Answer.Error;

@Component
public class FlatManager {
	private final RestHelper rest;
	private final FlatCianParser cianParser;
	private final FlatN1Parser n1Parser;

	public FlatManager(RestHelper rest, FlatCianParser cianParser, FlatN1Parser n1Parser) {
		this.rest = rest;
		this.cianParser = cianParser;
		this.n1Parser = n1Parser;
	}

	public Answer<List<Flat>> getCianFlatList(String cianXlsxUrl) {
		Answer<String> res = rest.getRestFile(cianXlsxUrl);
		if (res.ok()) {
			return cianParser.getAvailableFlats(res.answer());
		}
		return Error(res.error());
	}

	public Answer<List<Flat>> getN1FlatList(String n1JsonUrl) {
		Answer<String> res = rest.getRestResponse(n1JsonUrl);
		if (res.ok()) {
			return n1Parser.getAvailableFlats(res.answer());
		}
		return Error(res.error());
	}
}
