package ru.exlmoto.digestbot.utils;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import ru.exlmoto.digestbot.DigestBot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Component
public class RandomCatchPhrases {
	private final DigestBot mDigestBot;

	private final String mLanguage;
	private final String mFilename;
	private JSONObject mJsonObject;

	@Autowired
	public RandomCatchPhrases(@Value("${general.lang}") final String aLanguage,
	                          @Value("${digestbot.filename.random.phrases}") final String aFilename,
	                          final DigestBot aDigestBot) {
		mLanguage = aLanguage;
		mFilename = aFilename;
		mDigestBot = aDigestBot;

		initializeJsonObject();
	}

	private void initializeJsonObject() {
		try {
			final File lJsonFile = ResourceUtils.getFile(
				"classpath:digestbot/" + mFilename + mLanguage + ".json");
			final String lJsonContent = new String(Files.readAllBytes(lJsonFile.toPath()));
			mJsonObject = new JSONObject(lJsonContent);
		} catch (IOException e) {
			mDigestBot.getBotLogger().error(String.format("Cannot get or open file '%s', error: '%s'.",
				mFilename, e.toString()));
			mJsonObject = null;
		}
	}

	public String getRandomLocalizedStringWithUsername(final String aPath, final String aUsername) {
		return mDigestBot.getLocalizationHelper().replaceUsernameTagByRealName(
			getRandomLocalizedString(aPath), aUsername);
	}

	public String getRandomLocalizedString(final String aPath) {
		if (mJsonObject == null) {
			return null;
		}
		final String[] lStrings = (String[]) mJsonObject.get(aPath);
		return lStrings[mDigestBot.getLocalizationHelper().getRandomIntInRangeFromZero(lStrings.length)];
	}
}
