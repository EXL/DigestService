package ru.exlmoto.lab;

import org.springframework.data.repository.CrudRepository;

public interface IDigestUserRepository extends CrudRepository<DigestUserEntity, Integer> {
    public Iterable<DigestUserEntity> findByUsernameContainingIgnoreCase(String query);
}
