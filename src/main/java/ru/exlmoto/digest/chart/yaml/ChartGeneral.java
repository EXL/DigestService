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
	private String titleRu;
	private String titleEn;
	private String descRu;
	private String descEn;
	private String buttonRu;
	private String buttonEn;
	private String apiUrl;

	public boolean isValid() {
		return
			!StringUtils.isEmpty(titleRu) &&
			!StringUtils.isEmpty(titleEn) &&
			!StringUtils.isEmpty(descRu) &&
			!StringUtils.isEmpty(descEn) &&
			!StringUtils.isEmpty(buttonRu) &&
			!StringUtils.isEmpty(buttonEn) &&
			!StringUtils.isEmpty(apiUrl);
	}

	public ChartGeneral(Map<String, String> map) {
		titleRu = map.get("titleRu");
		titleEn = map.get("titleEn");
		descRu = map.get("descRu");
		descEn = map.get("descEn");
		buttonRu = map.get("buttonRu");
		buttonEn = map.get("buttonEn");
		apiUrl = map.get("apiUrl");
	}
}
