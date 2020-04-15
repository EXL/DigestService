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
