package com.board.member.service;

import com.board.config.jwt.JwtUtil;
import com.board.exception.custom.EmailNotFoundException;
import com.board.exception.custom.MyEntityNotFoundException;
import com.board.exception.custom.SignUpException;
import com.board.member.domain.Member;
import com.board.member.dto.LoginRequest;
import com.board.member.dto.LoginResponse;
import com.board.member.dto.MemberSignUpRequest;
import com.board.member.dto.MemberSignUpResponse;
import com.board.member.entity.MemberEntity;
import com.board.member.message.ErrorMessage;
import com.board.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60; // 1시간

    @Override
    @Transactional
    public MemberSignUpResponse signUp(MemberSignUpRequest request) {

        validateDuplicate(request);

        MemberEntity member = MemberEntity.builder()
                .email(request.getEmail())
                .password(request.getPassword()) // 스프링 시큐리티 추가 안해서 암호화 안함
                .nickName(request.getNickName())
                .build();
        MemberEntity savedMember = memberRepository.save(member);

        return new MemberSignUpResponse(savedMember);
    }

    private void validateDuplicate(MemberSignUpRequest request) {
        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw SingUpException.from(ErrorMessage.EMAIL_DUPLICATE);
        }
        if (memberRepository.findByNickName(request.getNickName()).isPresent()) {
            throw SingUpException.from(ErrorMessage.NICKNAME_DUPLICATE);
        }
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        MemberEntity memberEntity = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NOT_CORRECT_LOGIN));

        Member member = new Member(memberEntity);

        if (!member.checkPassword(request.getPassword())) {
            throw new IllegalArgumentException(ErrorMessage.NOT_CORRECT_LOGIN);
        }

        String token = jwtUtil.generateToken(member.getEmail(), ACCESS_TOKEN_EXPIRATION);

        return new LoginResponse(token, ACCESS_TOKEN_EXPIRATION);
    }

    @Override
    public MemberEntity findByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("이메일을 찾을 수 없습니다."));
    }

    @Override
    public MemberEntity findById(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다."));
    }


}
