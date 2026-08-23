/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2026 EXL <exlmotodev@gmail.com>
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

package ru.exlmoto.digest.exchange.key;

import ru.exlmoto.digest.entity.ExchangeRateMoexEntity;

public class ExchangeMoexKey {
	public static final String USD_EXCH_ID = "USD000UTSTOM";
	public static final String EUR_EXCH_ID = "EUR_RUB__TOM";
	public static final String EUR_USD_ID  = "EURUSD000TOM";

	public static int convertMoexIdToDatabaseId(String key) {
		switch (key) {
			default:
			case USD_EXCH_ID: return ExchangeRateMoexEntity.MOEX_ROW_USD_EXCH;
			case EUR_EXCH_ID: return ExchangeRateMoexEntity.MOEX_ROW_EUR_EXCH;
			case EUR_USD_ID:  return ExchangeRateMoexEntity.MOEX_ROW_EUR_USD;
		}
	}
}
