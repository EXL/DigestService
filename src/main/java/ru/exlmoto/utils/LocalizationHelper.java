package ru.exlmoto.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class LocalizationHelper {
	private final String mDelimiter;
	private final String mUsernameTag;
	private final String mUsernameCast;

	private final MessageSource mMessageSource;

	@Autowired
	public LocalizationHelper(@Value("${general.lang}") final String aLanguage,
	                          @Value("${general.delimiter}") final String aDelimiter,
	                          @Value("${general.username.tag}") final String aUsernameTag,
	                          @Value("${general.username.cast}") final String aUsernameCast,
	                          @Qualifier("messageSource") final MessageSource aMessageSource) {
		mMessageSource = aMessageSource;
		mDelimiter = aDelimiter;
		mUsernameTag = aUsernameTag;
		mUsernameCast = aUsernameCast;

		LocaleContextHolder.setDefaultLocale(Locale.forLanguageTag(aLanguage));
	}

	public String getLocalizedString(final String path) {
		return mMessageSource.getMessage(path, null, LocaleContextHolder.getLocale());
	}

	public String getRandomLocalizedString(final String path) {
		final String[] lAllMessagesArray = getLocalizedString(path).split(mDelimiter);
		return lAllMessagesArray[getRandomIntInRangeFromZero(lAllMessagesArray.length)];
	}

	private int getRandomIntInRangeFromZero(final int aMax) {
		return ThreadLocalRandom.current().nextInt(0, aMax);
	}

	public String replaceUsernameTagByRealName(final String aMessageText, final String aUsername) {
		return aMessageText.replaceAll(mUsernameTag, mUsernameCast + aUsername);
	}
}
