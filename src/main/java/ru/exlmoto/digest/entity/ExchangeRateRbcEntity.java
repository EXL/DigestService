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

package ru.exlmoto.digest.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "exchange_rate_rbc")
public class ExchangeRateRbcEntity {
	public static final int RBC_ROW_USD_CASH = 1;
	public static final int RBC_ROW_EUR_CASH = 2;
	public static final int RBC_ROW_USD_EXCH = 3;
	public static final int RBC_ROW_EUR_EXCH = 4;
	public static final int RBC_ROW_USD_CBRF = 5;
	public static final int RBC_ROW_EUR_CBRF = 6;
	public static final int RBC_ROW_EUR_USD  = 7;
	public static final int RBC_ROW_BTC_USD  = 8;

	@Id
	private int id;

	@Column(nullable = false)
	private String date;

	@Column(nullable = false)
	private String purchase;

	@Column(nullable = false)
	private String sale;

	@Column(nullable = false)
	private String difference;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getPurchase() {
		return purchase;
	}

	public void setPurchase(String purchase) {
		this.purchase = purchase;
	}

	public String getSale() {
		return sale;
	}

	public void setSale(String sale) {
		this.sale = sale;
	}

	public String getDifference() {
		return difference;
	}

	public void setDifference(String difference) {
		this.difference = difference;
	}

	@Override
	public String toString() {
		return
			"ExchangeRateRbcEntity{id=" + id +
			", date=" + date +
			", purchase=" + purchase +
			", sale=" + sale +
			", difference=" + difference +
			"}";
	}
}
