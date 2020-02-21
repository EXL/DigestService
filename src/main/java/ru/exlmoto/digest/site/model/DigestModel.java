package ru.exlmoto.digest.site.model;

import ru.exlmoto.digest.site.model.post.Post;

import java.util.List;

public class DigestModel {
	private String title;

	private long chatId;
	private String chatSlug;
	private String chatLink;

	private List<Post> digests;

	public DigestModel(List<Post> digests, String title, long chatId, String chatSlug, String chatLink) {
		this.digests = digests;

		this.title = title;

		this.chatId = chatId;
		this.chatSlug = chatSlug;
		this.chatLink = chatLink;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getChatId() {
		return chatId;
	}

	public void setChatId(long chatId) {
		this.chatId = chatId;
	}

	public String getChatSlug() {
		return chatSlug;
	}

	public void setChatSlug(String chatSlug) {
		this.chatSlug = chatSlug;
	}

	public String getChatLink() {
		return chatLink;
	}

	public void setChatLink(String chatLink) {
		this.chatLink = chatLink;
	}

	public List<Post> getDigests() {
		return digests;
	}

	public void setDigests(List<Post> digests) {
		this.digests = digests;
	}
}
