package ru.exlmoto.digest.site.form;

import org.springframework.util.StringUtils;

public class UserForm {
	private boolean update;

	private Long id;
	private String avatar;
	private String username;

	public boolean checkForm() {
		return id != null && StringUtils.hasText(username);
	}

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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
			"UserForm{update=" + update +
			", id=" + id +
			", avatar=" + avatar +
			", username=" + username +
			"}";
	}
}
