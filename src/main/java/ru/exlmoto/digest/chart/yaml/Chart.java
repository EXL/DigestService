package ru.exlmoto.digest.chart.yaml;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.springframework.util.StringUtils;

import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Chart {
	private String title;
	private String desc;
	private String button;
	private String path;

	public boolean isValid() {
		return
			!StringUtils.isEmpty(title) &&
			!StringUtils.isEmpty(desc) &&
			!StringUtils.isEmpty(button) &&
			!StringUtils.isEmpty(path);
	}

	public Chart(Map<String, String> map, String lang) {
		title = map.get("title_" + lang);
		desc = map.get("desc_" + lang);
		button = map.get("button_" + lang);
		path = map.get("api_url");
	}
}
