package ru.exlmoto.digestbot.entities;

import javax.persistence.*;

@Entity
@Table(name = "digestbot_subs_digest", uniqueConstraints={@UniqueConstraint(columnNames = {"subscription_id"})})
public class DigestSubscriberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="subscription_id")
    private Long subscription;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getSubscription() {
        return subscription;
    }

    public void setSubscription(Long subscription) {
        this.subscription = subscription;
    }
}
