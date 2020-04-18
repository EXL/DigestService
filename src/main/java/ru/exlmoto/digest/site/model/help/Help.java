package ru.exlmoto.digest.site.model.help;

public class Help {
	private long digestLength;
	private long digestPerPage;
	private String admins;

	public Help(long digestLength, long digestPerPage, String admins) {
		this.digestLength = digestLength;
		this.digestPerPage = digestPerPage;
		this.admins = admins;
	}

	public long getDigestLength() {
		return digestLength;
	}

	public void setDigestLength(long digestLength) {
		this.digestLength = digestLength;
	}

	public long getDigestPerPage() {
		return digestPerPage;
	}

	public void setDigestPerPage(long digestPerPage) {
		this.digestPerPage = digestPerPage;
	}

	public String getAdmins() {
		return admins;
	}

	public void setAdmins(String admins) {
		this.admins = admins;
	}
}
