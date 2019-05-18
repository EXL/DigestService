package ru.exlmoto.lab;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface IDigestRepository extends CrudRepository<DigestEntity, Integer> {
    public Page<DigestEntity> findAll(Pageable pageable);

    public Page<DigestEntity> findByDigestContainingIgnoreCase(String query, Pageable pageable);
}
