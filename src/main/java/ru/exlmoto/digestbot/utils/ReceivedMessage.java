package ru.exlmoto.digestbot.utils;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.thymeleaf.util.ArrayUtils;

public class ReceivedMessage {
	private final Long mChatId;
	private final Integer mMessageDate;
	private final Integer mMessageId;
	private final Integer mMessageUsernameId;
	private final String mMessageText;
	private final String mMessageUsername;
	private final boolean mIsMessageCommand;
	private final boolean mIsUserAdmin;

	public ReceivedMessage(Message aMessage, String[] aAdministrators) {
		mChatId = aMessage.getChatId();
		mMessageDate = aMessage.getDate();
		mMessageId = aMessage.getMessageId();
		mMessageText = aMessage.getText();
		mMessageUsername = aMessage.getFrom().getUserName();
		mMessageUsernameId = aMessage.getFrom().getId();
		mIsMessageCommand = aMessage.isCommand();
		mIsUserAdmin = isUserHasAdminRights(mMessageUsername, aAdministrators);
	}

	private boolean isUserHasAdminRights(Object aMessageUsername, Object[] aAdministrators) {
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

	public boolean isIsMessageCommand() {
		return mIsMessageCommand;
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
				", mIsMessageCommand=" + mIsMessageCommand +
				", mIsUserAdmin=" + mIsUserAdmin +
				'}';
	}
}
