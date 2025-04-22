package com.board.service;

import com.board.dto.request.CommentCreateRequest;
import com.board.dto.request.CommentUpdateRequest;
import com.board.entity.ArticleEntity;
import com.board.entity.CommentEntity;
import com.board.repository.CommentRepository;
import com.exception.custom.MyEntityNotFoundException;
import com.member.entity.MemberEntity;
import com.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final BlogService blogService;
    private final MemberService memberService;

    @Transactional
    public CommentEntity createComment(CommentCreateRequest request, Long memberId) {
        ArticleEntity article = blogService.findById(request.getArticleId());
        MemberEntity member = memberService.findById(memberId);
        CommentEntity comment = new CommentEntity(request.getContent(), article, member);
        return commentRepository.save(comment);
    }

    @Transactional
    public CommentEntity updateComment(CommentUpdateRequest request, Long memberId) {
        CommentEntity comment = compareAuthors(request.getCommentId(), memberService.findById(memberId));
        comment.update(request.getContent());
        return comment;
    }

    @Transactional
    public void deleteComment(long commentId, Long memberId) {
        compareAuthors(commentId, memberService.findById(memberId));
        commentRepository.deleteById(commentId);
    }

    private CommentEntity compareAuthors(long commentId, MemberEntity member) {
        CommentEntity comment = findComment(commentId);
        comment.validateOwner(member);
        return comment;
    }

    private CommentEntity findComment(long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> MyEntityNotFoundException.from(id));
    }
}
