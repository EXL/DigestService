package ru.exlmoto.digest.site.model.post;

public class Post {
	private boolean highlight;

	private String description_html;

	private String username;
	private String username_html;
	private String avatar;
	private String group;

	private String date;

	private String post_html;

	private long user_id;
	private String digests_html;

	public Post(boolean highlight,
	            String description_html,
	            String username,
	            String username_html,
	            String avatar,
	            String group,
	            String date,
	            String post_html,
	            long user_id,
	            String digests_html) {
		this.highlight = highlight;

		this.description_html = description_html;

		this.username = username;
		this.username_html = username_html;
		this.avatar = avatar;
		this.group = group;

		this.date = date;
		this.post_html = post_html;

		this.user_id = user_id;
		this.digests_html = digests_html;
	}

	public boolean isHighlight() {
		return highlight;
	}

	public void setHighlight(boolean highlight) {
		this.highlight = highlight;
	}

	public String getDescription_html() {
		return description_html;
	}

	public void setDescription_html(String description_html) {
		this.description_html = description_html;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername_html() {
		return username_html;
	}

	public void setUsername_html(String username_html) {
		this.username_html = username_html;
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

	public String getPost_html() {
		return post_html;
	}

	public void setPost_html(String post_html) {
		this.post_html = post_html;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public String getDigests_html() {
		return digests_html;
	}

	public void setDigests_html(String digests_html) {
		this.digests_html = digests_html;
	}
}
