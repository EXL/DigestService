package ru.exlmoto.digest.chart.yaml;

import org.springframework.util.StringUtils;

import java.util.Map;

public class Chart {
	private String title;
	private String desc;
	private String button;
	private String url;
	private String path = "";

	public boolean isValid() {
		return
			!StringUtils.isEmpty(title) &&
			!StringUtils.isEmpty(desc) &&
			!StringUtils.isEmpty(button) &&
			!StringUtils.isEmpty(url);
	}

	public Chart() {

	}

	public Chart(Map<String, String> map, String lang) {
		title = map.get("title_" + lang);
		desc = map.get("desc_" + lang);
		button = map.get("button_" + lang);
		url = map.get("api_url");
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getButton() {
		return button;
	}

	public void setButton(String button) {
		this.button = button;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return
			"Chart{title=" + title +
			", desc=" + desc +
			", button=" + button +
			", url=" + url +
			", path=" + path +
			"}";
	}
}
