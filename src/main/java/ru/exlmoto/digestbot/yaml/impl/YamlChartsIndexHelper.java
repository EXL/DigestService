package ru.exlmoto.digestbot.yaml.impl;

import org.python.antlr.ast.Str;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ru.exlmoto.digestbot.yaml.YamlLoader;

import java.util.ArrayList;
import java.util.Map;

@Component
public class YamlChartsIndexHelper extends YamlLoader {
	private final int K_DESCR_RU = 2;
	private final int K_DESCR_EN = 3;
	private final int K_BUTTN_RU = 4;
	private final int K_BUTTN_EN = 5;
	private final int K_API_LINK = 6;

	private int mTitleIndex;
	private int mDescriptionIndex;
	private int mButtonsIndex;

	private final Map<String, ArrayList<String>> mYamlData;

	public YamlChartsIndexHelper(@Value("${digestbot.file.name.charts}") final String aGeneralFilename,
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

	public String getTitleByKey(final String aKey) {
		return mYamlData.get(aKey).get(mTitleIndex);
	}

	public String getApiLinkByKey(final String aKey) {
		return mYamlData.get(aKey).get(K_API_LINK);
	}

	public ArrayList<String> getButtons() {
		final ArrayList<String> lButtonLabels = new ArrayList<>();
		mYamlData.forEach((iKey, iValue) -> lButtonLabels.add(iValue.get(mButtonsIndex)));
		return lButtonLabels;
	}

	public ArrayList<String> getKeys() {
		final ArrayList<String> lKeys = new ArrayList<>();
		mYamlData.forEach((iKey, iValue) -> lKeys.add(iKey));
		return lKeys;
	}

	public String getDescriptions() {
		final StringBuilder lStringBuilder = new StringBuilder();
		mYamlData.forEach((iKey, iValue) -> {
			lStringBuilder.append(iValue.get(mDescriptionIndex));
			lStringBuilder.append('\n');
		});
		return "```\n" + lStringBuilder.toString().trim() + "\n```";
	}
}
