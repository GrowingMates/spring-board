package com.board.article.service.event;

import com.board.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ArticleEventListener {

    private final CommentService commentService;

    @Transactional
    @EventListener
    public void handleMemberDeletedEvent(ArticleDeletedEvent event) {
        Long articleId = event.articleId();

        commentService.deleteCommentByArticle(articleId);
    }
}
