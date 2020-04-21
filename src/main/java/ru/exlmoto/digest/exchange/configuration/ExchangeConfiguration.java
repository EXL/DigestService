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

package ru.exlmoto.digest.exchange.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "exchange")
public class ExchangeConfiguration {
	private String bankRu;
	private String bankRuMirror;
	private String bankUa;
	private String bankUaMirror;
	private String bankBy;
	private String bankKz;
	private String metalRu;
	private String metalRuMirror;

	public String getBankRu() {
		return bankRu;
	}

	public void setBankRu(String bankRu) {
		this.bankRu = bankRu;
	}

	public String getBankRuMirror() {
		return bankRuMirror;
	}

	public void setBankRuMirror(String bankRuMirror) {
		this.bankRuMirror = bankRuMirror;
	}

	public String getBankUa() {
		return bankUa;
	}

	public void setBankUa(String bankUa) {
		this.bankUa = bankUa;
	}

	public String getBankUaMirror() {
		return bankUaMirror;
	}

	public void setBankUaMirror(String bankUaMirror) {
		this.bankUaMirror = bankUaMirror;
	}

	public String getBankBy() {
		return bankBy;
	}

	public void setBankBy(String bankBy) {
		this.bankBy = bankBy;
	}

	public String getBankKz() {
		return bankKz;
	}

	public void setBankKz(String bankKz) {
		this.bankKz = bankKz;
	}

	public String getMetalRu() {
		return metalRu;
	}

	public void setMetalRu(String metalRu) {
		this.metalRu = metalRu;
	}

	public String getMetalRuMirror() {
		return metalRuMirror;
	}

	public void setMetalRuMirror(String metalRuMirror) {
		this.metalRuMirror = metalRuMirror;
	}
}
