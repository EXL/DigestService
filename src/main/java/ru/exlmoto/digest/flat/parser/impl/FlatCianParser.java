package ru.exlmoto.digest.flat.parser.impl;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.flat.model.Flat;
import ru.exlmoto.digest.flat.parser.FlatParser;

import java.util.List;

@Component
public class FlatCianParser extends FlatParser {
	@Override
	public List<Flat> getAvailableFlats(String content) {
		System.out.println("========== CIAN ==========");
		System.out.println(content);
		System.out.println("========== CIAN ==========");

		return null;
	}
}
