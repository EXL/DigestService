package ru.exlmoto.digestbot.entities;

import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bot_digest_users")
public class DigestUserEntity {
	@Id
	private Integer id;

	@Column(name = "avatar_link", nullable = false, length = 2048)
	@Length(max = 2048)
	private String avatarLink;

	@Column(name = "username", nullable = false)
	private String username;

	@Column(name = "username_html", nullable = false, length = 1024)
	@Length(max = 1024)
	private String username_html;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatarLink() {
		return avatarLink;
	}

	public void setAvatarLink(String avatar) {
		this.avatarLink = avatar;
	}

	public String getUsername_html() {
		return username_html;
	}

	public void setUsername_html(String username_html) {
		this.username_html = username_html;
	}
}
