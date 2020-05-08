package ru.exlmoto.digest.flat.parser.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.flat.model.Flat;
import ru.exlmoto.digest.flat.parser.FlatParser;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import static ru.exlmoto.digest.util.Answer.Ok;
import static ru.exlmoto.digest.util.Answer.Error;

@Component
public class FlatN1Parser extends FlatParser {
	private final Logger log = LoggerFactory.getLogger(FlatN1Parser.class);

	private final LocaleHelper locale;

	/*
	 * JSON Parameters.
	 *
	 * Room:    result[i].params.rooms_count
	 * Address: result[i].params.house_addresses[j].house_number + result[i].params.house_addresses[j].street.name_ru
	 * Square:  result[i].params.total_area / 100
	 * Floor:   result[i].params.floor / result[i].params.floors_count
	 * Price:   result[i].params.price
	 * Phone:   result[i].original_phones[j].formatted
	 * Link:    result[i].url
	 */

	public FlatN1Parser(LocaleHelper locale) {
		this.locale = locale;
	}

	@Override
	public Answer<List<Flat>> getAvailableFlats(String content) {
		String error = null;
		List<Flat> flats = new ArrayList<>();
		try {
			JsonParser.parseString(content).getAsJsonObject().getAsJsonArray("result").forEach(item -> {
				JsonObject flat = item.getAsJsonObject();
				JsonObject params = flat.getAsJsonObject("params");
				flats.add(new Flat(
					parseRooms(params),
					parseSquare(params),
					parseFloor(params),
					parseAddress(params),
					parsePrice(params),
					parsePhones(flat),
					parseLink(flat.getAsJsonPrimitive("url").getAsString())
				));
			});
		} catch (JsonSyntaxException jse) {
			error = "Cannot parse JSON data from N1!";
			log.error(error, jse);
		}

		flats.forEach(System.out::println);

		if (flats.isEmpty()) {
			if (error == null) {
				error = "Flat list is empty, please check N1 JSON parser.";
			}
			return Error(error);
		}

		return Ok(flats);
	}

	protected String parseRooms(JsonObject params) {
		return params.getAsJsonPrimitive("rooms_count").getAsString();
	}

	protected String parseAddress(JsonObject params) {
		StringJoiner joiner = new StringJoiner("; ");

		params.getAsJsonArray("house_addresses").forEach(item -> {
			JsonObject address = item.getAsJsonObject();
			JsonObject street = address.getAsJsonObject("street");
			joiner.add(
				street.getAsJsonPrimitive("name_ru").getAsString() +
				", " +
				address.getAsJsonPrimitive("house_number").getAsString()
			);
		});

		return joiner.toString();
	}

	protected String parseSquare(JsonObject params) {
		return String.format("%.2f", params.getAsJsonPrimitive("total_area").getAsInt() / 100.0D);
	}

	protected String parseFloor(JsonObject params) {
		return params.getAsJsonPrimitive("floor").getAsString() + "/" +
			params.getAsJsonPrimitive("floors_count").getAsString();
	}

	protected String parsePrice(JsonObject params) {
		return adjustPrice(params.getAsJsonPrimitive("price").getAsString()) + " " + locale.i18n("flat.price.symbol");
	}

	protected String parsePhones(JsonObject flat) {
		StringJoiner joiner = new StringJoiner(", ");
		flat.getAsJsonArray("original_phones").forEach(item -> {
			JsonObject phone = item.getAsJsonObject();
			joiner.add(phone.getAsJsonPrimitive("formatted").getAsString());
		});
		return joiner.toString();
	}

	protected String parseLink(String url) {
		if (!url.startsWith("http")) {
			return "https:" + url;
		}
		return url;
	}
}
