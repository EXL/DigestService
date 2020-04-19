package ru.exlmoto.digest.site.model;

import ru.exlmoto.digest.site.model.post.Post;

import java.util.List;

public class DigestModel {
	private String title;
	private String description;

	private List<Post> digests;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Post> getDigests() {
		return digests;
	}

	public void setDigests(List<Post> digests) {
		this.digests = digests;
	}
}
