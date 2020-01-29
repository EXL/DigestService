package ru.exlmoto.digest.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bot_sub_motofan")
public class SubMotofanEntity {
	@Id
	private long subscription;

	private String name;

	public SubMotofanEntity() {

	}

	public SubMotofanEntity(long subscription, String name) {
		this.subscription = subscription;
		this.name = name;
	}

	public long getSubscription() {
		return subscription;
	}

	public void setSubscription(long subscription) {
		this.subscription = subscription;
	}

	@Override
	public String toString() {
		return
			"SubMotofanEntity{subscription=" + subscription +
			", name='" + name + '\'' +
			"}";
	}
}
