package ru.exlmoto.digest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import ru.exlmoto.digest.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
	UserEntity findUserEntityById(long id);

	@Transactional
	void deleteUserEntityById(long id);
}
