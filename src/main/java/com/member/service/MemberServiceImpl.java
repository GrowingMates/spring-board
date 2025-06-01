package com.member.service;

import com.config.jwt.JwtUtil;
import com.config.jwt.TokenWithExpiration;
import com.config.jwt.token.RefreshToken;
import com.config.jwt.token.RefreshTokenService;
import com.exception.custom.EmailNotFoundException;
import com.exception.custom.InvalidToken;
import com.exception.custom.LoginException;
import com.exception.custom.MyEntityNotFoundException;
import com.exception.custom.SignUpException;
import com.member.domain.Member;
import com.member.dto.request.LoginRequest;
import com.member.dto.request.SignUpRequest;
import com.member.dto.response.LoginResponse;
import com.member.dto.response.SignUpResponse;
import com.member.entity.MemberEntity;
import com.member.message.ErrorMessage;
import com.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public SignUpResponse signUp(SignUpRequest request) {

        validateDuplicate(request);

        MemberEntity member = MemberEntity.builder()
                .email(request.getEmail())
                .password(request.getPassword()) // 스프링 시큐리티 추가 안해서 암호화 안함
                .nickName(request.getNickName())
                .build();
        MemberEntity savedMember = memberRepository.save(member);

        return new SignUpResponse(savedMember);
    }

    private void validateDuplicate(SignUpRequest request) {
        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw SignUpException.from(ErrorMessage.EMAIL_DUPLICATE);
        }
        if (memberRepository.findByNickName(request.getNickName()).isPresent()) {
            throw SignUpException.from(ErrorMessage.NICKNAME_DUPLICATE);
        }
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        MemberEntity memberEntity = memberRepository.findByEmailAndIsDeletedFalse(request.getEmail())
                .orElseThrow(() -> LoginException.from(ErrorMessage.NOT_CORRECT_LOGIN));

        Member member = new Member(memberEntity);
        member.checkPassword(request.getPassword());

        TokenWithExpiration accessToken =
                jwtUtil.generateAccessToken(member.getId());
        TokenWithExpiration refreshToken =
                jwtUtil.generateRefreshToken(member.getId());

        refreshTokenService.mergeToken(member.getId(), refreshToken.getToken());

        return new LoginResponse(accessToken, refreshToken);
    }

    @Override
    public MemberEntity findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> EmailNotFoundException.from(email));
    }

    @Override
    public MemberEntity findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> MyEntityNotFoundException.from(id));
    }

    @Override
    @Transactional
    public void logout(Long memberId) {
        refreshTokenService.deleteToken(memberId);
        log.info("회원 {} 로그아웃함", memberId);
    }

    @Transactional
    public void withdraw(Long memberId) {
        MemberEntity member = findById(memberId);
        member.softDelete();
    }

    @Override
    public LoginResponse reissueAccessToken(Long memberId) {
        RefreshToken savedToken = refreshTokenService.findByMemberId(memberId);

        if (!jwtUtil.isTokenValid(savedToken.getToken())) {
            throw InvalidToken.getInstance();
        }

        TokenWithExpiration newAccessToken = jwtUtil.generateAccessToken(memberId);
        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .build(); // 리프래시 토큰은 줄 필요 없음
    }
}
