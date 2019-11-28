package ru.exlmoto.digestbot.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ru.exlmoto.digestbot.entities.DigestEntity;

import java.util.ArrayList;

public interface IDigestEntriesRepository extends CrudRepository<DigestEntity, Integer> {
	public Page<DigestEntity> findAll(Pageable pageable);
	public Page<DigestEntity> findDigestEntitiesByChat(Pageable pageable, Long chat);

	@Query("select distinct digest_entity.author from DigestEntity digest_entity")
	public ArrayList<Integer> findAllAuthorsId();

	public void deleteAllByDateIsLessThanAndChatIsNot(Long date, Long chat);
}
