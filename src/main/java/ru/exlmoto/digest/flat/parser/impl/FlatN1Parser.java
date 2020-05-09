/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL <exlmotodev@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.exlmoto.digest.flat.parser.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
	 * Phone:   result[i].original_phones[j].value
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
					applyPhonePatch(parsePhones(flat)),
					parseLink(flat.getAsJsonPrimitive("url").getAsString())
				));
			});
		} catch (JsonSyntaxException jse) {
			error = "Cannot parse JSON data from N1!";
			log.error(error, jse);
		}

		if (flats.isEmpty()) {
			if (error == null) {
				error = locale.i18n("flat.error.empty");
			}
			return Error(error);
		}

		return Ok(flats);
	}

	protected String parseLink(String url) {
		if (StringUtils.hasText(url)) {
			String trimmed = url.trim();
			if (trimmed.startsWith("//")) {
				return "https:" + trimmed;
			}
		}
		return url;
	}

	private String parseRooms(JsonObject params) {
		JsonElement count = params.get("rooms_count");
		return (count != null && !count.isJsonNull()) ? count.getAsString() : "1";
	}

	private String parseAddress(JsonObject params) {
		StringJoiner joiner = new StringJoiner("; ");
		JsonElement element = params.get("house_addresses");
		if (element != null && !element.isJsonNull()) {
			params.getAsJsonArray("house_addresses").forEach(item -> {
				JsonObject address = item.getAsJsonObject();
				JsonObject street = address.getAsJsonObject("street");
				joiner.add(
					street.getAsJsonPrimitive("name_ru").getAsString() +
					", " +
					address.getAsJsonPrimitive("house_number").getAsString()
				);
			});
		}
		return joiner.toString();
	}

	private String parseSquare(JsonObject params) {
		return String.format("%.1f", params.getAsJsonPrimitive("total_area").getAsInt() / 100.0d);
	}

	private String parseFloor(JsonObject params) {
		JsonElement floor = params.get("floor");
		JsonElement count = params.get("floors_count");
		return (floor != null && !floor.isJsonNull() && count != null && !count.isJsonNull()) ?
			floor.getAsString() + "/" + count.getAsString() : "1/1";
	}

	private String parsePrice(JsonObject params) {
		return adjustPrice(params.getAsJsonPrimitive("price").getAsString()) + " " + locale.i18n("flat.price.symbol");
	}

	private String parsePhones(JsonObject flat) {
		JsonArray phones = flat.getAsJsonArray("original_phones");
		if (onePhone) {
			String phone = "+" + phones.get(0).getAsJsonObject().getAsJsonPrimitive("value").getAsString();
			return StringUtils.hasText(phone) ? phone : "";
		} else {
			StringJoiner joiner = new StringJoiner(", ");
			phones.forEach(item -> {
				JsonObject phone = item.getAsJsonObject();
				joiner.add("+" + phone.getAsJsonPrimitive("value").getAsString());
			});
			return joiner.toString();
		}
	}
}
