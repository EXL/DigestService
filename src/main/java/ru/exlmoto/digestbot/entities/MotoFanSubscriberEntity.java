package ru.exlmoto.digestbot.entities;

import javax.persistence.*;

@Entity
@Table(name = "motofan_subscription")
public class MotoFanSubscriberEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Long subscription_id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Long getSubscription_id() {
		return subscription_id;
	}

	public void setSubscription_id(Long subscription_id) {
		this.subscription_id = subscription_id;
	}
}
