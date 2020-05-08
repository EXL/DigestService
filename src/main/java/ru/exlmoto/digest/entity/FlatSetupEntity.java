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

package ru.exlmoto.digest.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "flat_setup")
public class FlatSetupEntity {
	public static final int FLAT_ROW = 1;

	@Id
	private int id;

	@Column
	private int maxVariants;

	@Column
	private String apiCianUrl;

	@Column
	private String apiN1Url;

	@Column
	private String viewCianUrl;

	@Column
	private String viewN1Url;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getApiCianUrl() {
		return apiCianUrl;
	}

	public void setApiCianUrl(String apiCianUrl) {
		this.apiCianUrl = apiCianUrl;
	}

	public String getApiN1Url() {
		return apiN1Url;
	}

	public void setApiN1Url(String apiN1Url) {
		this.apiN1Url = apiN1Url;
	}

	public String getViewCianUrl() {
		return viewCianUrl;
	}

	public void setViewCianUrl(String viewCianUrl) {
		this.viewCianUrl = viewCianUrl;
	}

	public String getViewN1Url() {
		return viewN1Url;
	}

	public void setViewN1Url(String viewN1Url) {
		this.viewN1Url = viewN1Url;
	}

	@Override
	public String toString() {
		return
			"FlatSetupEntity{id=" + id +
			", apiCianUrl=" + apiCianUrl +
			", apiN1Url=" + apiN1Url +
			", viewCianUrl=" + viewCianUrl +
			", viewN1Url=" + viewN1Url +
			"}";
	}
}
