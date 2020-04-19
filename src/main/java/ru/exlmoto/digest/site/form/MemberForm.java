package ru.exlmoto.digest.site.form;

import org.springframework.util.StringUtils;

import ru.exlmoto.digest.util.Role;

public class MemberForm {
	private boolean update;

	private Long id;
	private String username;
	private String password;
	private Role role;
	private boolean enabled;

	public boolean checkForm() {
		return role != null && StringUtils.hasText(username) && StringUtils.hasText(password);
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String toString() {
		return
			"MemberForm{update=" + update +
			", id=" + id +
			", username=" + username +
			", password=<cropped>" +
			", role=" + role +
			", enabled=" + enabled +
			"}";
	}
}
