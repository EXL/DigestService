package ru.exlmoto.digest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.exlmoto.digest.entity.BotDigestEntity;

public interface BotDigestRepository extends JpaRepository<BotDigestEntity, Long> {

}