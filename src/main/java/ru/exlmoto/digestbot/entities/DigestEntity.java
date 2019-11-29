package ru.exlmoto.digestbot.entities;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;

@Entity
@Table(name = "digestbot_entries")
public class DigestEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Integer author;

	private Long date;

	private Long chat;

	@Column(name = "digest", nullable = false, length = 2048)
	@Length(max = 2048)
	private String digest;

	@Column(name = "digest_html", nullable = false, length = 4096)
	@Length(max = 4096)
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
