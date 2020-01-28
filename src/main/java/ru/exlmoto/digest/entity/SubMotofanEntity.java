package ru.exlmoto.digest.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
@Table(name = "bot_sub_motofan", uniqueConstraints = { @UniqueConstraint(columnNames = { "subscription" }) })
public class SubMotofanEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private long subscription;

	public SubMotofanEntity() {

	}

	public SubMotofanEntity(long subscription) {
		this.subscription = subscription;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
			"MotofanSubEntity{id=" + id +
			", subscription=" + subscription +
			"}";
	}
}
