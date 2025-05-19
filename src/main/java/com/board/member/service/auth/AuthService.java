package com.board.member.service.auth;

import com.board.member.domain.auth.TokenProvider;
import com.board.member.domain.member.Member;
import com.board.member.exception.MemberErrorCode;
import com.board.member.exception.MemberException;
import com.board.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    public Long signUp(String loginId, String name, String nickName, String password) {
        checkDuplicateLoginId(loginId);
        checkDuplicateNickName(nickName);
        Member member = new Member(name, nickName, loginId, passwordEncoder.encode(password));
        memberRepository.save(member);

        return member.getId();
    }

    @Transactional(readOnly = true)
    public String login(String loginId, String password) {
        Member member = findMemberByLoginId(loginId);
        checkPassword(password, member.getMemberPassword());

        return tokenProvider.create(member.getId());
    }

    private Member findMemberByLoginId(String loginId) {
        return memberRepository.findMemberByMemberLoginId(loginId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_MATCH_LOGIN_ID));
    }

    private void checkPassword(String requestPassword, String password) {
        if (!passwordEncoder.matches(requestPassword, password)) {
            throw new MemberException(MemberErrorCode.NOT_MATCH_PASSWORD);
        }
    }

    private void checkDuplicateLoginId(String loginId) {
        if (memberRepository.existsByMemberLoginId(loginId)) {
            throw new MemberException(MemberErrorCode.DUPLICATE_LOGIN_ID);
        }
    }

    private void checkDuplicateNickName(String memberNickName) {
        if (memberRepository.existsByMemberNickName(memberNickName)) {
            throw new MemberException(MemberErrorCode.DUPLICATE_NICKNAME);
        }
    }
}
