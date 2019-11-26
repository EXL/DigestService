package ru.exlmoto.digestbot.repos;

import org.springframework.data.repository.CrudRepository;
import ru.exlmoto.digestbot.entities.DigestUserEntity;

public interface IDigestUsersRepository extends CrudRepository<DigestUserEntity, Integer> {
	public DigestUserEntity findDigestUserEntityById(final Integer aId);
}
