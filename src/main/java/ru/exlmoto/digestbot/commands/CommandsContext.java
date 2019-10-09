package ru.exlmoto.digestbot.commands;

import java.util.HashMap;
import java.util.Map;

public class CommandsContext {
	private final Map<String, Object> mContext;

	public CommandsContext() {
		this.mContext = new HashMap<>();
	}

	public CommandsContext setText(String aText) {
		this.mContext.put("text", aText);
		return this;
	}

	public String getText() {
		return (String) this.mContext.get("text");
	}

	public CommandsContext setChatId(Long aChatId) {
		this.mContext.put("chatId", aChatId);
		return this;
	}

	public Long getChatId() {
		return (Long) this.mContext.get("chatId");
	}
}
