package ru.exlmoto.digest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.exlmoto.digest.entity.BotDigestUserEntity;

import java.util.List;

public interface BotDigestUserRepository extends JpaRepository<BotDigestUserEntity, Long> {
	default List<BotDigestUserEntity> findUsersWithUsername() {
		return findBotDigestUserEntitiesByUsernameStartsWith("@");
	}

	List<BotDigestUserEntity> findBotDigestUserEntitiesByUsernameStartsWith(String startWith);

	BotDigestUserEntity getBotDigestUserEntityById(long id);
}
