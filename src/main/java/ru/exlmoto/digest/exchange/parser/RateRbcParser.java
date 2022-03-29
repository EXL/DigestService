/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 EXL <exlmotodev@gmail.com>
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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.util.StringUtils;

import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.util.rest.RestHelper;

public class RateRbcParser extends GeneralParser {
	private final Logger log = LoggerFactory.getLogger(RateRbcParser.class);

	private final int USD_CASH = 0;
	private final int EUR_CASH = 1;
	private final int USD_EXCH = 2;
	private final int EUR_EXCH = 3;
	private final int USD_CBRF = 4;
	private final int EUR_CBRF = 5;
	private final int EUR_USD  = 6;
	private final int BTC_USD  = 7;

	private String usdCashDate;
	private String usdCashSell;
	private String usdCashPurc;
	private String usdCashDiff;

	private String eurCashDate;
	private String eurCashSell;
	private String eurCashPurc;
	private String eurCashDiff;

	private String usdExchDate;
	private String usdExchSell;
	private String usdExchPurc;
	private String usdExchDiff;

	private String eurExchDate;
	private String eurExchSell;
	private String eurExchPurc;
	private String eurExchDiff;

	private String usdCbrfDate;
	private String usdCbrfSell;
	private String usdCbrfPurc;
	private String usdCbrfDiff;

	private String eurCbrfDate;
	private String eurCbrfSell;
	private String eurCbrfPurc;
	private String eurCbrfDiff;

	private String eurUsdDate;
	private String eurUsdSell;
	private String eurUsdPurc;
	private String eurUsdDiff;

	private String btcUsdDate;
	private String btcUsdSell;
	private String btcUsdPurc;
	private String btcUsdDiff;

	private void setUsdCash(JsonObject object) {
		usdCashDate = filterCommas(filterSpaces(object.getAsJsonPrimitive("maxDealDate").getAsString()));
		usdCashSell = filterCommas(filterSpaces(object.getAsJsonPrimitive("value1").getAsString()));
		usdCashPurc = filterCommas(filterSpaces(object.getAsJsonPrimitive("value2").getAsString()));
		usdCashDiff = filterCommas(filterSpaces(object.getAsJsonPrimitive("change").getAsString()));
	}

	private void setEurCash(JsonObject object) {
		eurCashDate = filterCommas(filterSpaces(object.getAsJsonPrimitive("maxDealDate").getAsString()));
		eurCashSell = filterCommas(filterSpaces(object.getAsJsonPrimitive("value1").getAsString()));
		eurCashPurc = filterCommas(filterSpaces(object.getAsJsonPrimitive("value2").getAsString()));
		eurCashDiff = filterCommas(filterSpaces(object.getAsJsonPrimitive("change").getAsString()));
	}

	private void setUsdExch(JsonObject object) {
		usdExchDate = filterCommas(filterSpaces(object.getAsJsonPrimitive("maxDealDate").getAsString()));
		usdExchSell = filterCommas(filterSpaces(object.getAsJsonPrimitive("closevalue").getAsString()));
		usdExchPurc = filterCommas(filterSpaces(object.getAsJsonPrimitive("value2").getAsString()));
		usdExchDiff = filterCommas(filterSpaces(object.getAsJsonPrimitive("change").getAsString()));
	}

	private void setEurExch(JsonObject object) {
		eurExchDate = filterCommas(filterSpaces(object.getAsJsonPrimitive("maxDealDate").getAsString()));
		eurExchSell = filterCommas(filterSpaces(object.getAsJsonPrimitive("closevalue").getAsString()));
		eurExchPurc = filterCommas(filterSpaces(object.getAsJsonPrimitive("value2").getAsString()));
		eurExchDiff = filterCommas(filterSpaces(object.getAsJsonPrimitive("change").getAsString()));
	}

	private void setUsdCbrf(JsonObject object) {
		usdCbrfDate = filterCommas(filterSpaces(object.getAsJsonPrimitive("maxDealDate").getAsString()));
		usdCbrfSell = filterCommas(filterSpaces(object.getAsJsonPrimitive("closevalue").getAsString()));
		usdCbrfPurc = filterCommas(filterSpaces(object.getAsJsonPrimitive("value2").getAsString()));
		usdCbrfDiff = filterCommas(filterSpaces(object.getAsJsonPrimitive("change").getAsString()));
	}

	private void setEurCbrf(JsonObject object) {
		eurCbrfDate = filterCommas(filterSpaces(object.getAsJsonPrimitive("maxDealDate").getAsString()));
		eurCbrfSell = filterCommas(filterSpaces(object.getAsJsonPrimitive("closevalue").getAsString()));
		eurCbrfPurc = filterCommas(filterSpaces(object.getAsJsonPrimitive("value2").getAsString()));
		eurCbrfDiff = filterCommas(filterSpaces(object.getAsJsonPrimitive("change").getAsString()));
	}

	private void setEurUsd(JsonObject object) {
		eurUsdDate = filterCommas(filterSpaces(object.getAsJsonPrimitive("maxDealDate").getAsString()));
		eurUsdSell = filterCommas(filterSpaces(object.getAsJsonPrimitive("closevalue").getAsString()));
		eurUsdPurc = filterCommas(filterSpaces(object.getAsJsonPrimitive("value2").getAsString()));
		eurUsdDiff = filterCommas(filterSpaces(object.getAsJsonPrimitive("change").getAsString()));
	}

	private void setBtcUsd(JsonObject object) {
		btcUsdDate = filterCommas(filterSpaces(object.getAsJsonPrimitive("maxDealDate").getAsString()));
		btcUsdSell = filterCommas(filterSpaces(object.getAsJsonPrimitive("closevalue").getAsString()));
		btcUsdPurc = filterCommas(filterSpaces(object.getAsJsonPrimitive("value2").getAsString()));
		btcUsdDiff = filterCommas(filterSpaces(object.getAsJsonPrimitive("change").getAsString()));
	}

	public boolean parse(String content) {
		if (StringUtils.hasLength(content)) {
			try {
				JsonObject document = JsonParser.parseString​(content).getAsJsonObject();
				JsonArray arr = document.getAsJsonArray​("shared_key_indicators");
				if (!arr.isEmpty()) {
					setUsdCash(arr.get(USD_CASH).getAsJsonObject().getAsJsonObject("item").getAsJsonObject("prepared"));
					setEurCash(arr.get(EUR_CASH).getAsJsonObject().getAsJsonObject("item").getAsJsonObject("prepared"));
					setUsdExch(arr.get(USD_EXCH).getAsJsonObject().getAsJsonObject("item").getAsJsonObject("prepared"));
					setEurExch(arr.get(EUR_EXCH).getAsJsonObject().getAsJsonObject("item").getAsJsonObject("prepared"));
					setUsdCbrf(arr.get(USD_CBRF).getAsJsonObject().getAsJsonObject("item").getAsJsonObject("prepared"));
					setEurCbrf(arr.get(EUR_CBRF).getAsJsonObject().getAsJsonObject("item").getAsJsonObject("prepared"));
					setEurUsd(arr.get(EUR_USD).getAsJsonObject().getAsJsonObject("item").getAsJsonObject("prepared"));
					setBtcUsd(arr.get(BTC_USD).getAsJsonObject().getAsJsonObject("item").getAsJsonObject("prepared"));
					return true;
				}
			} catch (Exception e) {
				log.error("Cannot parse json string.", e);
			}
		}
		return false;
	}

	private void commit(DatabaseService service) {
		logParsedValues();
	}

	public boolean commitRates(String url, DatabaseService service, RestHelper rest) {
		try {
			if (parse(rest.getRestResponse(url).answer())) {
				commit(service);
				return true;
			}
		} catch (DataAccessException dae) {
			log.error("Cannot save object to database.", dae);
		}
		return false;
	}

	public void logParsedValues() {
		log.info("==> Using RBC");
		log.info(logHelper(usdCashDate, usdCashSell, usdCashPurc, usdCashDiff));
		log.info(logHelper(eurCashDate, eurCashSell, eurCashPurc, eurCashDiff));
		log.info(logHelper(usdExchDate, usdExchSell, usdExchPurc, usdExchDiff));
		log.info(logHelper(eurExchDate, eurExchSell, eurExchPurc, eurExchDiff));
		log.info(logHelper(usdCbrfDate, usdCbrfSell, usdCbrfPurc, usdCbrfDiff));
		log.info(logHelper(eurCbrfDate, eurCbrfSell, eurCbrfPurc, eurCbrfDiff));
		log.info(logHelper(eurUsdDate, eurUsdSell, eurUsdPurc, eurUsdDiff));
		log.info(logHelper(btcUsdDate, btcUsdSell, btcUsdPurc, btcUsdDiff));
	}

	private String logHelper(String s1, String s2, String s3, String s4) {
		return "===> Date: " + s1 + ", Sell: " + s2 + ", Purchase: " + s3 + ", Difference: " + s4;
	}
}
