package ru.exlmoto.lab;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

public interface IDigestRepository extends CrudRepository<DigestEntity, Integer> {
    Iterable<DigestEntity> findAll(Sort sort);

    Page<DigestEntity> findAll(Pageable pageable);
}
