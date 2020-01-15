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
public class ChartGeneral {
	private String title;
	private String desc;
	private String button;
	private String apiUrl;

	public boolean isValid() {
		return
			!StringUtils.isEmpty(title) &&
			!StringUtils.isEmpty(desc) &&
			!StringUtils.isEmpty(button) &&
			!StringUtils.isEmpty(apiUrl);
	}

	public ChartGeneral(Map<String, String> map, String lang) {
		title = map.get("title_" + lang);
		desc = map.get("desc_" + lang);
		button = map.get("button_" + lang);
		apiUrl = map.get("api_url");
	}
}
