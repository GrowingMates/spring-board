package com.member.repository;

import com.member.entity.MemberEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    Optional<MemberEntity> findByEmail(String email);

    Optional<MemberEntity> findByNickName(String nickName);

    Optional<MemberEntity> findByEmailAndIsDeletedFalse(String email);
}
