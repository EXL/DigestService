package ru.exlmoto.digest.flat.parser;

import ru.exlmoto.digest.flat.model.Flat;
import ru.exlmoto.digest.util.Answer;

import java.util.List;

public abstract class FlatParser {
	public abstract Answer<List<Flat>> getAvailableFlats(String content);
}
