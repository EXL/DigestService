package ru.exlmoto.digest.covid.json;

@Deprecated
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
