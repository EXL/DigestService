package ru.exlmoto.digestbot.yaml.impl;

import org.python.antlr.ast.Str;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ru.exlmoto.digestbot.yaml.YamlLoader;

import java.util.ArrayList;
import java.util.Map;

@Component
public class YamlChartIndexHelper extends YamlLoader {
	private final int K_TITLE_RU = 0;
	private final int K_TITLE_EN = 1;
	private final int K_DESCR_RU = 2;
	private final int K_DESCR_EN = 3;
	private final int K_BUTTN_RU = 4;
	private final int K_BUTTN_EN = 5;
	private final int K_API_LINK = 6;

	private final String K_RUSSIAN_LANG = "ru";
	private final String K_ENGLISH_LANG = "en";

	private int mTitleIndex;
	private int mDescriptionIndex;
	private int mButtonsIndex;

	private final Map<String, ArrayList<String>> mYamlData;

	public YamlChartIndexHelper(@Value("${digestbot.file.name.charts}") final String aGeneralFilename,
	                            @Value("${general.lang}") final String aLanguage) {
		mYamlData = loadYamlFromResources(aGeneralFilename);

		if (aLanguage.startsWith(K_ENGLISH_LANG)) {
			mTitleIndex = K_TITLE_EN;
			mDescriptionIndex = K_DESCR_EN;
			mButtonsIndex = K_BUTTN_EN;
		} else if (aLanguage.startsWith(K_RUSSIAN_LANG)) {
			mTitleIndex = K_TITLE_RU;
			mDescriptionIndex = K_DESCR_RU;
			mButtonsIndex = K_BUTTN_RU;
		}
	}

	public String getDescriptions() {
		final StringBuilder lStringBuilder = new StringBuilder();
		mYamlData.forEach((k, v) -> {
			lStringBuilder.append(v.get(mDescriptionIndex));
			lStringBuilder.append('\n');
		});
		return "```\n" + lStringBuilder.toString().trim() + "\n```";
	}
}
