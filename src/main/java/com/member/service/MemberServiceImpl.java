package com.member.service;

import com.member.dto.MemberSignUpRequest;
import com.member.dto.MemberSignUpResponse;
import com.member.entity.MemberEntity;
import com.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public MemberSignUpResponse signUp(MemberSignUpRequest request) {
        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        if (memberRepository.findByNickName(request.getNickName()).isPresent()) {
            throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
        }

        MemberEntity member = MemberEntity.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickName(request.getNickName())
                .build();
        MemberEntity savedMember = memberRepository.save(member);

        return new MemberSignUpResponse(savedMember);
    }
}
