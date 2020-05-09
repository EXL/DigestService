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
