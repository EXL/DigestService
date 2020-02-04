package ru.exlmoto.digest.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.exlmoto.digest.entity.BotDigestEntity;

import java.util.List;

public interface BotDigestRepository extends JpaRepository<BotDigestEntity, Long> {
	default void dropObsoleteDigests(long date, long chatId) {
		deleteBotDigestEntitiesByDateIsLessThanAndChatIsNot(date, chatId);
	}

	Page<BotDigestEntity> findBotDigestEntitiesByChat(Pageable pageable, long chatId);

	void deleteBotDigestEntitiesByDateIsLessThanAndChatIsNot(long date, long chatId);

	@Query("SELECT DISTINCT bot_digest_entity.user.id FROM BotDigestEntity bot_digest_entity")
	List<Long> allUsersId();
}
