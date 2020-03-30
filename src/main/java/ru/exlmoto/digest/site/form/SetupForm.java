package ru.exlmoto.digest.site.form;

public class SetupForm {
	private boolean log;
	private boolean greeting;
	private boolean silent;

	public boolean isLog() {
		return log;
	}

	public void setLog(boolean log) {
		this.log = log;
	}

	public boolean isGreeting() {
		return greeting;
	}

	public void setGreeting(boolean greeting) {
		this.greeting = greeting;
	}

	public boolean isSilent() {
		return silent;
	}

	public void setSilent(boolean silent) {
		this.silent = silent;
	}
}
