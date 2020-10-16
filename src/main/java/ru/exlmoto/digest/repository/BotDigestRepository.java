/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL <exlmotodev@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.exlmoto.digest.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import ru.exlmoto.digest.entity.BotDigestEntity;
import ru.exlmoto.digest.entity.BotDigestUserEntity;

import java.util.List;
import java.util.Optional;

public interface BotDigestRepository extends JpaRepository<BotDigestEntity, Long> {
	Optional<BotDigestEntity> findBotDigestEntityByChatAndMessageId(long chatId, long messageId);

	default void dropObsoleteDigests(long date, long chatId) {
		deleteBotDigestEntitiesByDateIsLessThanAndChatIsNot(date, chatId);
	}

	Page<BotDigestEntity> findBotDigestEntitiesByChat(Pageable pageable, long chatId);

	long countBotDigestEntitiesByChat(long chatId);

	Page<BotDigestEntity> findByDigestContainingIgnoreCaseAndChatEquals(Pageable pageable, String query, long chatId);

	default Page<BotDigestEntity> findBotDigestEntitiesByChat(Pageable pageable, String query, long chatId) {
		return findByDigestContainingIgnoreCaseAndChatEquals(pageable, query, chatId);
	}

	long countBotDigestEntitiesByDigestContainingIgnoreCaseAndChatEquals(String query, long chatId);

	default long countBotDigestEntitiesByChat(String query, long chatId) {
		return countBotDigestEntitiesByDigestContainingIgnoreCaseAndChatEquals(query, chatId);
	}

	Page<BotDigestEntity> findByDigestContainingIgnoreCaseAndUserEqualsAndChatEquals(Pageable pageable,
	                                                                                 String query,
	                                                                                 BotDigestUserEntity user,
	                                                                                 long chatId);

	default Page<BotDigestEntity> findBotDigestEntitiesByChat(Pageable pageable,
	                                                          String query,
	                                                          BotDigestUserEntity user,
	                                                          long chatId) {
		return findByDigestContainingIgnoreCaseAndUserEqualsAndChatEquals(pageable, query, user, chatId);
	}

	long countBotDigestEntitiesByDigestContainingIgnoreCaseAndUserEqualsAndChatEquals(String query,
	                                                                                  BotDigestUserEntity user,
	                                                                                  long chatId);

	default long countBotDigestEntitiesByChat(String query, BotDigestUserEntity user, long chatId) {
		return countBotDigestEntitiesByDigestContainingIgnoreCaseAndUserEqualsAndChatEquals(query, user, chatId);
	}

	List<BotDigestEntity> findBotDigestEntitiesByChat(Sort sort, long chatId);

	long countBotDigestEntitiesByUserEqualsAndChatEquals(BotDigestUserEntity user, long chatId);

	default long countBotDigestEntitiesByChat(BotDigestUserEntity user, long chatId) {
		return countBotDigestEntitiesByUserEqualsAndChatEquals(user, chatId);
	}

	@Transactional
	void deleteBotDigestEntitiesByDateIsLessThanAndChatIsNot(long date, long chatId);

	@Query("SELECT DISTINCT bot_digest_entity.user.id FROM BotDigestEntity bot_digest_entity")
	List<Long> allUserIds();

	@Query("SELECT DISTINCT bot_digest_entity.user FROM BotDigestEntity bot_digest_entity " +
		"WHERE bot_digest_entity.chat = :chatId")
	List<BotDigestUserEntity> allUsersByChat(@Param("chatId") Long chatId);
}
