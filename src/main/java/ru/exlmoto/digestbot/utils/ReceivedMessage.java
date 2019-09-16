package ru.exlmoto.digestbot.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.thymeleaf.util.ArrayUtils;

public class ReceivedMessage {
    private final Long mChatId;
    private final Integer mMessageDate;
    private final Integer mMessageId;
    private final String mMessageText;
    private final String mMessageUsername;
    private final boolean mIsMessageForwarded;
    private final boolean mIsMessageCommand;
    private final boolean mIsUserAdmin;

    private String[] mAdministrators;

    public ReceivedMessage(Message message, String[] administrators) {
        this.mChatId = (message.getChatId() != null) ? message.getChatId() : 0;
        this.mMessageDate = (message.getDate() != null) ? message.getDate() : 0;
        this.mMessageId = (message.getMessageId() != null) ? message.getMessageId() : 0;
        this.mMessageText = (message.getText() != null) ? message.getText() : "empty";
        this.mMessageUsername = (message.getFrom().getUserName() != null) ?
                message.getFrom().getUserName() : message.getFrom().getFirstName();
        this.mIsMessageForwarded = (message.getForwardDate() != null);
        this.mIsMessageCommand = message.isCommand();
        this.mIsUserAdmin = this.userHasAdminRights();
        this.mAdministrators = administrators;
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

    public String getMessageText() {
        return mMessageText;
    }

    public String getMessageUsername() {
        return mMessageUsername;
    }

    public boolean isMessageForwarded() {
        return mIsMessageForwarded;
    }

    public boolean isMessageCommand() {
        return mIsMessageCommand;
    }

    public boolean isUserAdmin() {
        return mIsUserAdmin;
    }

    private boolean userHasAdminRights() {
        return true;
        // return this.mMessageUsername != null && ArrayUtils.contains(mAdministrators, this.mMessageUsername);
    }
}
