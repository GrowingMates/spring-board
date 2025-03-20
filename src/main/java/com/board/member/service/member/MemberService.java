package com.board.member.service.member;

import com.board.member.controller.member.dto.reponse.MemberResponse;
import com.board.member.controller.member.dto.request.MemberRequest;
import com.board.member.domain.member.Member;
import com.board.member.repository.MemberRepository;
import com.board.member.service.member.exception.NotFoundMemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public MemberResponse showMember(Long memberId) {
        Member member = getMember(memberId);
        return new MemberResponse(
                member.getMemberName(),
                member.getMemberNickName(),
                member.getMemberLoginId(),
                member.getMemberPassword()
        );
    }

    public MemberResponse updateMember(Long memberId, MemberRequest request) {
        Member member = getMember(memberId);
        member.update(request.name(), request.nickName(), request.id(), request.password());
        return new MemberResponse(
                member.getMemberName(),
                member.getMemberNickName(),
                member.getMemberLoginId(),
                member.getMemberPassword()
        );
    }

    public MemberResponse deleteMember(Long memberId) {
        Member member = getMember(memberId);
        memberRepository.delete(member);
        return new MemberResponse(
                member.getMemberName(),
                member.getMemberNickName(),
                member.getMemberLoginId(),
                member.getMemberPassword()
        );
    }

    private Member getMember(Long memberId) {
        return memberRepository.findMemberById(memberId)
                .orElseThrow(NotFoundMemberException::new);
    }
}
