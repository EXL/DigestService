package ru.exlmoto.digest.site.model.participant;

import ru.exlmoto.digest.util.Role;

public class Participant {
	private String username;
	private Role role;
	private boolean enabled;

	public Participant(String username, Role role, boolean enabled) {
		this.username = username;
		this.role = role;
		this.enabled = enabled;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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
}
