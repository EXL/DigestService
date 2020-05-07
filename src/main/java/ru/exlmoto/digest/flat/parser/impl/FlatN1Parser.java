package ru.exlmoto.digest.flat.parser.impl;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.flat.model.Flat;
import ru.exlmoto.digest.flat.parser.FlatParser;

import java.util.List;

@Component
public class FlatN1Parser extends FlatParser {
	@Override
	public List<Flat> getAvailableFlats(String url) {
		return null;
	}
}
