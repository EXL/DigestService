package ru.exlmoto.digest.entity;

import ru.exlmoto.digest.util.Role;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "member")
public class MemberEntity {
	@Id
	private long id;

	@Column(nullable = false)
	private String username;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private Role role;

	private boolean enable;

	public MemberEntity() {

	}

	public MemberEntity(long id, String username, String password, Role role, boolean enable) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.role = role;
		this.enable = enable;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
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

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	@Override
	public String toString() {
		return
			"MemberEntity{id=" + id +
			", username=" + username +
			", password=" + password +
			", role=" + role +
			", enable=" + enable +
			"}";
	}
}
