package ru.exlmoto.digest.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bot_sub_covid")
public class BotSubCovidEntity {
	@Id
	private long subscription;

	@Column(nullable = false)
	private String name;

	public BotSubCovidEntity() {

	}

	public BotSubCovidEntity(long subscription, String name) {
		this.subscription = subscription;
		this.name = name;
	}

	public long getSubscription() {
		return subscription;
	}

	public void setSubscription(long subscription) {
		this.subscription = subscription;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "BotSubCovidEntity{subscription=" + subscription +
			", name=" + name +
			"}";
	}
}