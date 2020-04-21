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

public class RegionFull {
	private String territoryName;

	private long confirmed;
	private long recovered;
	private long deaths;

	private long confirmedInc;
	private long recoveredInc;
	private long deathsInc;

	public RegionFull() {

	}

	public RegionFull(String territoryName,
	                  long confirmed, long recovered, long deaths,
	                  long confirmedInc, long recoveredInc, long deathsInc) {
		this.territoryName = territoryName;
		this.confirmed = confirmed;
		this.recovered = recovered;
		this.deaths = deaths;
		this.confirmedInc = confirmedInc;
		this.recoveredInc = recoveredInc;
		this.deathsInc = deathsInc;
	}

	public String getTerritoryName() {
		return territoryName;
	}

	public void setTerritoryName(String territoryName) {
		this.territoryName = territoryName;
	}

	public long getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(long confirmed) {
		this.confirmed = confirmed;
	}

	public long getRecovered() {
		return recovered;
	}

	public void setRecovered(long recovered) {
		this.recovered = recovered;
	}

	public long getDeaths() {
		return deaths;
	}

	public void setDeaths(long deaths) {
		this.deaths = deaths;
	}

	public long getConfirmedInc() {
		return confirmedInc;
	}

	public void setConfirmedInc(long confirmedInc) {
		this.confirmedInc = confirmedInc;
	}

	public long getRecoveredInc() {
		return recoveredInc;
	}

	public void setRecoveredInc(long recoveredInc) {
		this.recoveredInc = recoveredInc;
	}

	public long getDeathsInc() {
		return deathsInc;
	}

	public void setDeathsInc(long deathsInc) {
		this.deathsInc = deathsInc;
	}

	@Override
	public String toString() {
		return
			"RegionFull{territoryName=" + territoryName +
			", confirmed=" + confirmed +
			", recovered=" + recovered +
			", deaths=" + deaths +
			", confirmedInc=" + confirmedInc +
			", recoveredInc=" + recoveredInc +
			", deathsInc=" + deathsInc +
			"}";
	}
}
