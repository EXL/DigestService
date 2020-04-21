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

package ru.exlmoto.digest.covid.json;

public class Region {
	private String name;

	private long cases;
	private long recover;
	private long deaths;

	private long diff;
	private long sick; // Always zero?

	public Region() {

	}

	public Region(String name, long cases, long recover, long deaths, long diff, long sick) {
		this.name = name;
		this.cases = cases;
		this.recover = recover;
		this.deaths = deaths;
		this.diff = diff;
		this.sick = sick;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getCases() {
		return cases;
	}

	public void setCases(long cases) {
		this.cases = cases;
	}

	public long getRecover() {
		return recover;
	}

	public void setRecover(long recover) {
		this.recover = recover;
	}

	public long getDeaths() {
		return deaths;
	}

	public void setDeaths(long deaths) {
		this.deaths = deaths;
	}

	public long getDiff() {
		return diff;
	}

	public void setDiff(long diff) {
		this.diff = diff;
	}

	public long getSick() {
		return sick;
	}

	public void setSick(long sick) {
		this.sick = sick;
	}

	@Override
	public String toString() {
		return
			"Region{name=" + name +
			", cases=" + cases +
			", recover=" + recover +
			", deaths=" + deaths +
			", diff=" + diff +
			", sick=" + sick +
			"}";
	}
}
