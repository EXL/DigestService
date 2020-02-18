package ru.exlmoto.digest.site.form;

public class GoToPageForm {
	private String page;
	private String path;
	private String text;

	public GoToPageForm(String page, String path) {
		this.page = page;
		this.path = path;
	}

	public GoToPageForm(String page, String path, String text) {
		this.page = page;
		this.path = path;
		this.text = text;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
