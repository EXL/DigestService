package ru.exlmoto.digestbot.repos;

import org.springframework.data.repository.CrudRepository;
import ru.exlmoto.digestbot.entities.DigestUserEntity;

import java.util.ArrayList;

public interface IDigestUsersRepository extends CrudRepository<DigestUserEntity, Integer> {
	public DigestUserEntity findDigestUserEntityById(final Integer aId);
	public ArrayList<DigestUserEntity> findAllByUsernameOkIsTrue();
}
