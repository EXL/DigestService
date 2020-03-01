package ru.exlmoto.digest.site.model.post;

public class Post {
	private long id;

	private String username;
	private String avatar;
	private String group;

	private String date;

	private String post;

	public Post(long id, String username, String avatar, String group, String date, String post) {
		this.id = id;

		this.username = username;
		this.avatar = avatar;
		this.group = group;

		this.date = date;
		this.post = post;
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

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getPost() {
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}
}
