package ru.exlmoto.digest.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bot_digest_user")
public class BotDigestUserEntity {
	@Id
	private long id;

	@Column(length = 4095)
	private String avatar;

	@Column(nullable = false)
	private String username;

	public BotDigestUserEntity() {

	}

	public BotDigestUserEntity(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return
			"BotDigestUserEntity{id=" + id +
			", avatar=" + avatar +
			", username=" + username +
			"}";
	}
}
