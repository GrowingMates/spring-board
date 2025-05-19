package com.board.member.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.board.member.domain.member.Member;
import com.board.member.loader.MemberTestDataLoader;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(MemberTestDataLoader.class)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void set() {
        member = new Member("신짱구", "짱구", "aaa", "password123");
    }

    @Test
    @DisplayName("특정 닉네임에 해당하는 유저가 있는지 판단한다.")
    void existsByMemberNickName() {
        // given
        String nickName = "짱구";

        // when
        boolean isExistMember = memberRepository.existsByMemberNickName(nickName);

        // then
        assertThat(isExistMember).isTrue();
    }

    @Test
    @DisplayName("특정 아이디에 해당하는 유저가 있는지 판단한다.")
    void existsByMemberLoginId() {
        // given
        String nickName = "aaa";

        // when
        boolean isExistMember = memberRepository.existsByMemberLoginId(nickName);

        // then
        assertThat(isExistMember).isTrue();
    }

    @Test
    @DisplayName("특정 로그인 아이디에 해당하는 유저를 조회한다.")
    void findMemberByMemberLoginId() {
        // given
        String id = "aaa";

        // when
        Optional<Member> foundMember = memberRepository.findMemberByMemberLoginId(id);

        // then
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get()).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(member);
    }

    @Test
    @DisplayName("특정 아이디에 해당하는 유저를 조회한다.")
    void findMemberById() {
        // given
        Long memberId = 1L;

        // when
        Optional<Member> foundMember = memberRepository.findMemberById(memberId);

        // then
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get()).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(member);
    }
}
