package com.board.member.service.auth;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.board.global.resolver.exception.TokenExpirationException;
import com.board.global.resolver.exception.TokenVerificationException;
import com.board.member.controller.auth.dto.request.LoginRequest;
import com.board.member.controller.auth.dto.request.SignUpRequest;
import com.board.member.controller.auth.dto.response.SignUpResponse;
import com.board.member.domain.auth.TokenProvider;
import com.board.member.domain.member.Member;
import com.board.member.repository.MemberRepository;
import com.board.member.service.auth.exception.ExistLoginIdException;
import com.board.member.service.auth.exception.NotMatchLoginIdException;
import com.board.member.service.auth.exception.ExistNickNameException;
import com.board.global.resolver.exception.TokenInvalidException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    public SignUpResponse signUp(SignUpRequest request) {
        checkDuplicateLoginId(request.loginId());
        checkDuplicateNickName(request.memberNickName());
        Member member = new Member(request.memberName(), request.memberNickName(), request.loginId(), request.password());
        memberRepository.save(member);

        return new SignUpResponse(member.getId(), tokenProvider.create(member.getId()));
    }

    @Transactional(readOnly = true)
    public String login(LoginRequest request) {
        Member member = findMemberByLoginId(request.loginId());
        member.checkPassword(request.password());

        return tokenProvider.create(member.getId());
    }

    public Long verifyAndExtractToken(String token) {
        try {
            return extractToken(token);
        } catch (JWTDecodeException e) {
            throw new TokenVerificationException();
        } catch (TokenExpiredException e) {
            throw new TokenExpirationException();
        }
    }

    private Long extractToken(String token) {
        return verifyToken(token).getClaim("memberId")
                .asLong();
    }

    private DecodedJWT verifyToken(String token) {
        return Optional.of(tokenProvider.verifyToken(token))
                .orElseThrow(TokenInvalidException::new);
    }

    private Member findMemberByLoginId(String loginId) {
        return memberRepository.findMemberByMemberLoginId(loginId)
                .orElseThrow(NotMatchLoginIdException::new);
    }

    private void checkDuplicateLoginId(String loginId) {
        if(memberRepository.existsByMemberLoginId(loginId)) {
            throw new ExistLoginIdException();
        }
    }

    private void checkDuplicateNickName(String memberNickName) {
        if(memberRepository.existsByMemberNickName(memberNickName)) {
            throw new ExistNickNameException();
        }
    }
}
