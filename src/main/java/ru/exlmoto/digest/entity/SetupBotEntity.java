package ru.exlmoto.digest.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bot_setup")
public class SetupBotEntity {
	@Id
	private final int id = 1;

	private boolean logUpdates;
	private boolean showGreetings;
	private boolean silentMode;

	public SetupBotEntity() {

	}

	public SetupBotEntity(boolean logUpdates, boolean showGreetings, boolean silentMode) {
		this.logUpdates = logUpdates;
		this.showGreetings = showGreetings;
		this.silentMode = silentMode;
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
}
