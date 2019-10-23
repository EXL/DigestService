package ru.exlmoto.digestbot.repos;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import ru.exlmoto.digestbot.entities.MotoFanSubscriberEntity;

import java.util.ArrayList;

public interface IMotoFanSubscribersRepository extends CrudRepository<MotoFanSubscriberEntity, Long> {
	public ArrayList<MotoFanSubscriberEntity> findAll(final Pageable aPageable);
	public MotoFanSubscriberEntity findOneMotoFanSubscriberEntityBySubscription(final Long aSubscription);
	public void deleteMotoFanSubscriberEntityBySubscription(final Long aSubscription);
}
