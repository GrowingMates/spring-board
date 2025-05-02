package com.member.service;

import com.config.jwt.JwtUtil;
import com.config.jwt.TokenWithExpiration;
import com.exception.custom.EmailNotFoundException;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
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
    public LoginResponse login(LoginRequest request) {
        MemberEntity memberEntity = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> LoginException.from(ErrorMessage.NOT_CORRECT_LOGIN));

        Member member = new Member(memberEntity);
        member.checkPassword(request.getPassword());

        TokenWithExpiration tokenWithExpiration =
                jwtUtil.generateTokenWithExpiration(member.getEmail());
        return new LoginResponse(tokenWithExpiration.getToken(), tokenWithExpiration.getExpiration());
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


}
