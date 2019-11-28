package ru.exlmoto.digestbot.repos;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.exlmoto.digestbot.entities.DigestUserEntity;

import java.util.ArrayList;
import java.util.HashSet;

public interface IDigestUsersRepository extends CrudRepository<DigestUserEntity, Integer> {
	public DigestUserEntity findDigestUserEntityById(final Integer aId);

	public void deleteAllById(final Integer aId);

	@Query("select distinct digest_user_entity.id from DigestUserEntity digest_user_entity")
	public ArrayList<Integer> findAllAuthorsId();

	public ArrayList<DigestUserEntity> findAllByUsernameOkIsTrue();
}
