/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL
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

package ru.exlmoto.digest.site.form;

import ru.exlmoto.digest.entity.ExchangeRateEntity;

public class ExchangeForm {
	private ExchangeRateEntity rub;
	private ExchangeRateEntity uah;
	private ExchangeRateEntity byn;
	private ExchangeRateEntity kzt;
	private ExchangeRateEntity metal;

	public ExchangeRateEntity getRub() {
		return rub;
	}

	public void setRub(ExchangeRateEntity rub) {
		this.rub = rub;
	}

	public ExchangeRateEntity getUah() {
		return uah;
	}

	public void setUah(ExchangeRateEntity uah) {
		this.uah = uah;
	}

	public ExchangeRateEntity getByn() {
		return byn;
	}

	public void setByn(ExchangeRateEntity byn) {
		this.byn = byn;
	}

	public ExchangeRateEntity getKzt() {
		return kzt;
	}

	public void setKzt(ExchangeRateEntity kzt) {
		this.kzt = kzt;
	}

	public ExchangeRateEntity getMetal() {
		return metal;
	}

	public void setMetal(ExchangeRateEntity metal) {
		this.metal = metal;
	}
}
