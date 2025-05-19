package com.board.member.service.member;

import com.board.member.domain.member.Member;
import com.board.member.exception.MemberErrorCode;
import com.board.member.exception.MemberException;
import com.board.member.repository.MemberRepository;
import com.board.member.service.member.event.MemberDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Member updateMember(Long memberId, String requestName, String requestNickName, String requestId, String requestPassword) {
        Member member = findMember(memberId);
        member.update(requestName, requestNickName, requestId, requestPassword);

        return member;
    }

    public Member deleteMember(Long memberId) {
        Member member = findMember(memberId);
        memberRepository.delete(member);
        eventPublisher.publishEvent(new MemberDeletedEvent(memberId));

        return member;
    }

    @Transactional(readOnly = true)
    public Member findMember(Long memberId) {
        return memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));
    }
}
