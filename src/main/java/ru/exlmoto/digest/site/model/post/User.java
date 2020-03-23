package ru.exlmoto.digest.site.model.post;

public class User {
	private String avatar;
	private String username_html;
	private long user_id;
	private String group;
	private String messages_html;

	public User(String avatar, String username_html, long user_id, String group, String messages_html) {
		this.avatar = avatar;
		this.username_html = username_html;
		this.user_id = user_id;
		this.group = group;
		this.messages_html = messages_html;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getUsername_html() {
		return username_html;
	}

	public void setUsername_html(String username_html) {
		this.username_html = username_html;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getMessages_html() {
		return messages_html;
	}

	public void setMessages_html(String messages_html) {
		this.messages_html = messages_html;
	}
}
