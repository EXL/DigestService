package ru.exlmoto.digest.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
@Table(name = "bot_sub_digest", uniqueConstraints = { @UniqueConstraint(columnNames = { "subscription" }) })
public class SubDigestEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private long subscription;

	public SubDigestEntity() {

	}

	public SubDigestEntity(long subscription) {
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
			"DigestSubEntity{id=" + id +
			", subscription=" + subscription +
			"}";
	}
}
