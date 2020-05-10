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

package ru.exlmoto.digest.exchange.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.exlmoto.digest.entity.ExchangeRateEntity;

import java.math.BigDecimal;

public abstract class MetalParser extends RateParser {
	private final Logger log = LoggerFactory.getLogger(MetalParser.class);

	protected BigDecimal gold = null;
	protected BigDecimal silver = null;
	protected BigDecimal platinum = null;
	protected BigDecimal palladium = null;

	@Override
	protected void setPrevGeneralValues(ExchangeRateEntity entity, BigDecimal prevValue) {
		if (prevValue != null) {
			entity.setPrevGold(updatePrevValue(entity.getGold(), gold, entity.getPrevGold()));
			entity.setPrevSilver(updatePrevValue(entity.getSilver(), silver, entity.getPrevSilver()));
			entity.setPrevPlatinum(updatePrevValue(entity.getPlatinum(), platinum, entity.getPrevPlatinum()));
			entity.setPrevPalladium(updatePrevValue(entity.getPalladium(), palladium, entity.getPrevPalladium()));
		} else {
			entity.setPrevGold(gold);
			entity.setPrevSilver(silver);
			entity.setPrevPlatinum(platinum);
			entity.setPrevPalladium(palladium);
		}
	}

	@Override
	protected void commitGeneralValues(ExchangeRateEntity entity) {
		entity.setGold(gold);
		entity.setSilver(silver);
		entity.setPlatinum(platinum);
		entity.setPalladium(palladium);
	}

	@Override
	protected boolean checkParsedValues() {
		return gold != null || silver != null || platinum != null || palladium != null;
	}

	@Override
	public void logParsedValues() {
		log.info(String.format(
			"===> Date: %s, Gold: %s, Silver: %s, Platinum: %s, Palladium: %s",
			date, gold, silver, platinum, palladium
			)
		);
	}
}
