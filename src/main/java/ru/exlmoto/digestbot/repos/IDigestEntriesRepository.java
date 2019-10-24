package ru.exlmoto.digestbot.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import ru.exlmoto.digestbot.entities.DigestEntity;

public interface IDigestEntriesRepository extends CrudRepository<DigestEntity, Integer> {
	public Page<DigestEntity> findAll(Pageable pageable);
	public Page<DigestEntity> findDigestEntitiesByChat(Pageable pageable, Long chat);
}
