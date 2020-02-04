package ru.exlmoto.digest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.exlmoto.digest.entity.BotDigestUserEntity;

import java.util.List;

public interface BotDigestUserRepository extends JpaRepository<BotDigestUserEntity, Long> {
	List<BotDigestUserEntity> findBotDigestUserEntitiesByUsernameStartsWith(String startWith);

	default List<BotDigestUserEntity> findUsersWithUsername() {
		return findBotDigestUserEntitiesByUsernameStartsWith("@");
	}
}
