package ru.exlmoto.digestbot.repos;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import ru.exlmoto.digestbot.entities.DigestSubscriberEntity;

import java.util.ArrayList;

public interface IDigestSubscribersRepository extends CrudRepository<DigestSubscriberEntity, Long> {
    public ArrayList<DigestSubscriberEntity> findAll(final Pageable aPageable);
    public DigestSubscriberEntity findOneDigestSubscriberEntityBySubscription(final Long aSubsciption);
    public void deleteDigestSubscriberEntityBySubscription(final Long aSubscription);
}
