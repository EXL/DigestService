package ru.exlmoto.digestbot.yaml.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ru.exlmoto.digestbot.yaml.YamlLoader;

import java.util.ArrayList;
import java.util.Map;

@Component
public class YamlRatesIndexHelper extends YamlLoader {
	private final int K_BUTTON_LABEL = 2;
	private final int K_BANK_LINK    = 3;

	private final Map<String, ArrayList<String>> mYamlData;

	private int mTitleIndex;

	public YamlRatesIndexHelper(@Value("${digestbot.file.name.rates}") final String aGeneralFilename,
	                            @Value("${general.lang}") final String aLanguage) {
		mYamlData = loadYamlFromResources(aGeneralFilename);
		if (aLanguage.startsWith(K_ENGLISH_LANG)) {
			mTitleIndex = K_TITLE_EN;
		} else if (aLanguage.startsWith(K_RUSSIAN_LANG)) {
			mTitleIndex = K_TITLE_RU;
		}
	}

	public ArrayList<String> getKeys() {
		final ArrayList<String> lKeys = new ArrayList<>();
		mYamlData.forEach((iKey, iValue) -> {
			if (iKey.startsWith("i.rate")) {
				lKeys.add(iKey);
			}
		});
		return lKeys;
	}

	public ArrayList<String> getButtonLabels() {
		final ArrayList<String> lLabels = new ArrayList<>();
		mYamlData.forEach((iKey, iValue) -> {
			if (iKey.startsWith("i.rate")) {
				lLabels.add(iValue.get(K_BUTTON_LABEL));
			}
		});
		return lLabels;
	}

	public String getTitleByKey(final String aKey) {
		return mYamlData.get(aKey).get(mTitleIndex);
	}

	public String getButtonLabel(final String aKey) {
		return mYamlData.get(aKey).get(K_BUTTON_LABEL);
	}

	public String getBankLink(final String aKey) {
		return mYamlData.get(aKey).get(K_BANK_LINK);
	}

	public String getSingleParameter(final String aKey) {
		return mYamlData.get(aKey).get(0);
	}
}
