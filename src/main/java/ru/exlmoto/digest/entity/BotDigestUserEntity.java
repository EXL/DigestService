package ru.exlmoto.digest.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bot_digest_user")
public class BotDigestUserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(nullable = false, length = 4095)
	private String avatar;

	@Column(nullable = false)
	private String username;

	@Column(nullable = false)
	private boolean usernameOk;

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

	public boolean isUsernameOk() {
		return usernameOk;
	}

	public void setUsernameOk(boolean usernameOk) {
		this.usernameOk = usernameOk;
	}

	@Override
	public String toString() {
		return
			"BotDigestUserEntity{id=" + id +
			", avatar=" + avatar +
			", username=" + username +
			", usernameOk=" + usernameOk +
			"}";
	}
}
