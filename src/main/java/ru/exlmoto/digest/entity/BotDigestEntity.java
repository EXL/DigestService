package ru.exlmoto.digest.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "bot_digest")
public class BotDigestEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false)
	private long chat;

	@Column(nullable = false)
	private long date;

	@Column
	private Long messageId;

	@Column(nullable = false, length = 4095)
	private String digest;

	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
	private BotDigestUserEntity user;

	public BotDigestEntity() {

	}

	public BotDigestEntity(long chat, long date, long messageId, String digest, BotDigestUserEntity user) {
		this.chat = chat;
		this.date = date;
		this.messageId = messageId;
		this.digest = digest;
		this.user = user;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getChat() {
		return chat;
	}

	public void setChat(long chat) {
		this.chat = chat;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public Long getMessageId() {
		return messageId;
	}

	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public BotDigestUserEntity getUser() {
		return user;
	}

	public void setUser(BotDigestUserEntity user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return
			"BotDigestEntity{id=" + id +
			", chat=" + chat +
			", date=" + date +
			", messageId=" + messageId +
			", digest=" + digest +
			", user=" + user +
			"}";
	}
}
