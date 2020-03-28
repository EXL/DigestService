package ru.exlmoto.digest.covid;

import org.springframework.stereotype.Service;

import ru.exlmoto.digest.covid.generator.CovidJsonGenerator;

@Service
public class CovidService {
	private final CovidJsonGenerator jsonGenerator;

	public CovidService(CovidJsonGenerator jsonGenerator) {
		this.jsonGenerator = jsonGenerator;
	}

	public String jsonReport() {
		return jsonGenerator.getJsonReport();
	}
}
