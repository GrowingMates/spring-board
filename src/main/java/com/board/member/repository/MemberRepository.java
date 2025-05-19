package com.board.member.repository;

import com.board.member.domain.member.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByMemberNickName(String memberNickName);
    boolean existsByMemberLoginId(String loginId);
    Optional<Member> findMemberByMemberLoginId(String loginId);
    Optional<Member> findMemberById(Long id);
}
