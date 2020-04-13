package ru.exlmoto.digest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import ru.exlmoto.digest.entity.MemberEntity;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
	MemberEntity findMemberEntityById(long id);

	@Transactional
	void deleteMemberEntityById(long id);
}
