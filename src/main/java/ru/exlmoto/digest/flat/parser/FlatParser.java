package ru.exlmoto.digest.flat.parser;

import ru.exlmoto.digest.flat.model.Flat;

import java.util.List;

public abstract class FlatParser {
	public abstract List<Flat> getAvailableFlats(String url);
}
