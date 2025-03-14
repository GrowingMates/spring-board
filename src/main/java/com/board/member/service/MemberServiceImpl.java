package com.board.member.service;

import com.board.exception.SingUpException;
import com.board.member.dto.MemberSignUpRequest;
import com.board.member.dto.MemberSignUpResponse;
import com.board.member.entity.MemberEntity;
import com.board.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public MemberSignUpResponse signUp(MemberSignUpRequest request) {
        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw SingUpException.from("이미 가입된 이메일입니다.");
        }
        if (memberRepository.findByNickName(request.getNickName()).isPresent()) {
            throw SingUpException.from("이미 사용중인 닉네임입니다.");
        }

        MemberEntity member = MemberEntity.builder()
                .email(request.getEmail())
                .password(request.getPassword()) // 스프링 시큐리티 추가 안해서 암호화 안함
                .nickName(request.getNickName())
                .build();
        MemberEntity savedMember = memberRepository.save(member);

        return new MemberSignUpResponse(savedMember);
    }
}
