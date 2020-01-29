package ru.exlmoto.digest.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bot_setup")
public class SetupBotEntity {
	public static final int SETUP_ROW = 1;

	@Id
	private int id;

	@Column
	private boolean logUpdates;

	@Column
	private boolean showGreetings;

	@Column
	private boolean silentMode;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isLogUpdates() {
		return logUpdates;
	}

	public void setLogUpdates(boolean logUpdates) {
		this.logUpdates = logUpdates;
	}

	public boolean isShowGreetings() {
		return showGreetings;
	}

	public void setShowGreetings(boolean showGreetings) {
		this.showGreetings = showGreetings;
	}

	public boolean isSilentMode() {
		return silentMode;
	}

	public void setSilentMode(boolean silentMode) {
		this.silentMode = silentMode;
	}

	@Override
	public String toString() {
		return
			"SetupBotEntity{id=" + id +
			", logUpdates=" + logUpdates +
			", showGreetings=" + showGreetings +
			", silentMode=" + silentMode +
			"}";
	}
}
