package com.board.member.service.member.event;

import com.board.article.service.ArticleService;
import com.board.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MemberEventListener {

    private final ArticleService articleService;
    private final CommentService commentService;

    @Transactional
    @EventListener
    public void handleMemberDeletedEvent(MemberDeletedEvent event) {
        Long memberId = event.memberId();

        articleService.updateMemberIdToNUll(memberId);
        commentService.updateMemberIdToNull(memberId);
    }
}
