package ru.exlmoto.digestbot.entities;

import javax.persistence.*;

@Entity
@Table(name = "digest_entries")
public class DigestEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Integer author;

	private Long date;

	private Long chat;

	private String digest;

	private String html;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getAuthor() {
		return author;
	}

	public void setAuthor(Integer author) {
		this.author = author;
	}

	public Long getDate() {
		return date;
	}

	public void setDate(Long date) {
		this.date = date;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public Long getChat() {
		return chat;
	}

	public void setChat(Long chat) {
		this.chat = chat;
	}
}
