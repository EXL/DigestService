package ru.exlmoto.digest.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bot_sub_greeting")
public class BotSubGreetingEntity {
	@Id
	private long ignored;

	public BotSubGreetingEntity() {

	}

	public BotSubGreetingEntity(long ignored) {
		this.ignored = ignored;
	}

	public long getIgnored() {
		return ignored;
	}

	public void setIgnored(long ignored) {
		this.ignored = ignored;
	}

	@Override
	public String toString() {
		return "BotSubGreetingEntity{ignored=" + ignored + "}";
	}
}
