package ru.exlmoto.digestbot.repos;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import ru.exlmoto.digestbot.entities.MotoFanSubscriberEntity;

import java.util.ArrayList;

public interface IMotoFanSubscribersRepository extends CrudRepository<MotoFanSubscriberEntity, Long> {
	public ArrayList<MotoFanSubscriberEntity> findAll(Pageable aPageable);
}
