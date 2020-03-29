package ru.exlmoto.digest.site.model.digest;

public class Digest {
	private long id;
	private String username;
	private long chat;
	private String date;
	private String text;

	public Digest(long id, String username, long chat, String date, String text) {
		this.id = id;
		this.username = username;
		this.chat = chat;
		this.date = date;
		this.text = text;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public long getChat() {
		return chat;
	}

	public void setChat(long chat) {
		this.chat = chat;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
