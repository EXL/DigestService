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

package ru.exlmoto.digest.site.form;

public class FlatForm {
	private int maxVariants;
	private String apiCianUrl;
	private String apiN1Url;
	private String viewCianUrl;
	private String viewN1Url;

	public int getMaxVariants() {
		return maxVariants;
	}

	public void setMaxVariants(int maxVariants) {
		this.maxVariants = maxVariants;
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
}
