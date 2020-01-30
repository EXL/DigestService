package ru.exlmoto.digest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.exlmoto.digest.entity.BotDigestUserEntity;

public interface BotDigestUserRepository extends JpaRepository<BotDigestUserEntity, Long> {

}
