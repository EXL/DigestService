package ru.exlmoto.digestbot.utils;

import org.springframework.data.util.Pair;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.thymeleaf.util.ArrayUtils;

public class ReceivedMessage {
	private final Long mChatId;
	private final Integer mMessageDate;
	private final Integer mMessageId;
	private final Integer mMessageUsernameId;
	private final String mMessageText;
	private final String mMessageUsername;
	private final String mMessageUserFirstName;
	private final String mMessageUserLastName;
	private final boolean mIsUserAdmin;

	public ReceivedMessage(final Message aMessage,
						   final String[] aAdministrators) {
		mChatId = aMessage.getChatId();
		mMessageDate = aMessage.getDate();
		mMessageId = aMessage.getMessageId();
		mMessageText = aMessage.getText();
		mMessageUsername = aMessage.getFrom().getUserName();
		mMessageUserFirstName = aMessage.getFrom().getFirstName();
		mMessageUserLastName = aMessage.getFrom().getLastName();
		mMessageUsernameId = aMessage.getFrom().getId();
		mIsUserAdmin = isUserHasAdminRights(mMessageUsername, aAdministrators);
	}

	private boolean isUserHasAdminRights(final Object aMessageUsername,
	                                     final Object[] aAdministrators) {
		return ArrayUtils.contains(aAdministrators, aMessageUsername);
	}

	public Long getChatId() {
		return mChatId;
	}

	public Integer getMessageDate() {
		return mMessageDate;
	}

	public Integer getMessageId() {
		return mMessageId;
	}

	public Integer getMessageUsernameId() {
		return mMessageUsernameId;
	}

	public String getMessageText() {
		return mMessageText;
	}

	public String getMessageUsername() {
		return mMessageUsername;
	}

	public Pair<Boolean, String> getAvailableUsername() {
		return determineCorrectName(mMessageUsername, mMessageUserFirstName, mMessageUserLastName);
	}

	// TODO: Something bad with this.
	public static Pair<Boolean, String> determineCorrectName(final String aUsername,
															 final String aFirstName,
															 final String aLastName) {
		if (aUsername == null || aUsername.equals("null")) {
			if (aLastName == null || aLastName.equals("null")) {
				return Pair.of(false, aFirstName);
			}
			return Pair.of(false, aFirstName + ' ' + aLastName);
		} else {
			return Pair.of(true, aUsername);
		}
	}

	public boolean isIsUserAdmin() {
		return mIsUserAdmin;
	}

	@Override
	public String toString() {
		return "ReceivedMessage{" +
				"mChatId=" + mChatId +
				", mMessageDate=" + mMessageDate +
				", mMessageId=" + mMessageId +
				", mMessageUsernameId=" + mMessageUsernameId +
				", mMessageText='" + mMessageText + '\'' +
				", mMessageUsername='" + mMessageUsername + '\'' +
				", mMessageUserFirstName='" + mMessageUserFirstName + '\'' +
				", mMessageUserLastName='" + mMessageUserLastName + '\'' +
				", mIsUserAdmin=" + mIsUserAdmin +
				'}';
	}
}
