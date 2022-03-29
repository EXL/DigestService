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

package ru.exlmoto.digest.repository;

import org.springframework.data.repository.CrudRepository;

import ru.exlmoto.digest.entity.ExchangeRateRbcEntity;

import java.util.Optional;

public interface ExchangeRateRbcRepository extends CrudRepository<ExchangeRateRbcEntity, Integer> {
	default Optional<ExchangeRateRbcEntity> getUsdCash() {
		return findById(ExchangeRateRbcEntity.RBC_ROW_USD_CASH);
	}

	default Optional<ExchangeRateRbcEntity> getEurCash() {
		return findById(ExchangeRateRbcEntity.RBC_ROW_EUR_CASH);
	}

	default Optional<ExchangeRateRbcEntity> getUsdExchange() {
		return findById(ExchangeRateRbcEntity.RBC_ROW_USD_EXCH);
	}

	default Optional<ExchangeRateRbcEntity> getEurExchange() {
		return findById(ExchangeRateRbcEntity.RBC_ROW_EUR_EXCH);
	}

	default Optional<ExchangeRateRbcEntity> getUsdCbrf() {
		return findById(ExchangeRateRbcEntity.RBC_ROW_USD_CBRF);
	}

	default Optional<ExchangeRateRbcEntity> getEurCbrf() {
		return findById(ExchangeRateRbcEntity.RBC_ROW_EUR_CBRF);
	}

	default Optional<ExchangeRateRbcEntity> getEurUsd() {
		return findById(ExchangeRateRbcEntity.RBC_ROW_EUR_USD);
	}

	default Optional<ExchangeRateRbcEntity> getBtcUsd() {
		return findById(ExchangeRateRbcEntity.RBC_ROW_BTC_USD);
	}
}
