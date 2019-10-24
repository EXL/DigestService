package ru.exlmoto.digestbot.yaml.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import ru.exlmoto.digestbot.yaml.YamlLoader;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class YamlLocalizationHelper extends YamlLoader {
	private final Map<String, ArrayList<String>> mYamlData;

	private final String mUsernameTag;
	private final String mUsernameCast;

	public YamlLocalizationHelper(@Value("${general.lang}") final String aLanguage,
	                              @Value("${digestbot.file.name.l10n.general}") final String aGeneralFilename,
	                              @Value("${general.username.tag}") final String aUsernameTag,
	                              @Value("${general.username.cast}") final String aUsernameCast) {
		mUsernameTag = aUsernameTag;
		mUsernameCast = aUsernameCast;

		mYamlData = loadYamlFromResources(aGeneralFilename + '_' + aLanguage);
	}

	public String getRandomLocalizedString(final String aPath, final Pair<Boolean, String> aUsername) {
		return replaceUsernameTagByRealName(getRandomLocalizedString(aPath), aUsername);
	}

	public String getRandomLocalizedString(final String aPath) {
		final ArrayList<String> lStringArrayList = mYamlData.get(aPath);
		final Integer lRandomIndex = getRandomIntInRangeFromZero(lStringArrayList.size());
		return lStringArrayList.get(lRandomIndex);
	}

	public String getLocalizedString(final String aPath, final Pair<Boolean, String> aUsername) {
		return replaceUsernameTagByRealName(getLocalizedString(aPath), aUsername);
	}

	private Integer getRandomIntInRangeFromZero(final int aMax) {
		return ThreadLocalRandom.current().nextInt(0, aMax);
	}

	public String getLocalizedString(final String aPath) {
		final ArrayList<String> lStringArrayList = mYamlData.get(aPath);
		if (lStringArrayList != null) {
			if (lStringArrayList.size() == 1) {
				return lStringArrayList.get(0).trim();
			} else if (lStringArrayList.size() < 1) {
				throw new NullPointerException("Localization String Array is Empty!");
			} else {
				final StringBuilder lCollectStrings = new StringBuilder();
				for (final String iString : lStringArrayList) {
					lCollectStrings.append(iString);
					lCollectStrings.append('\n');
				}
				return lCollectStrings.toString().trim();
			}
		} else {
			throw new NullPointerException("Localization String Array is Null!");
		}
	}

	private String replaceUsernameTagByRealName(final String aMessageText, final Pair<Boolean, String> aUsername) {
		final String lUsername = aUsername.getSecond();
		if (aUsername.getFirst()) {
			return aMessageText.replaceAll(mUsernameTag, mUsernameCast + lUsername);
		} else {
			return aMessageText.replaceAll(mUsernameTag, lUsername);
		}
	}
}
