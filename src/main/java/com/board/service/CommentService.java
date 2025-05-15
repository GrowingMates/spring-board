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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleService articleService;
    private final MemberService memberService;

    public Page<CommentEntity> findAllComments(Long articleId, Pageable pageable) {
        ArticleEntity article = articleService.findById(articleId);
        return commentRepository.findByArticleAndIsDeletedFalse(article, pageable);
    }

    @Transactional
    public CommentEntity createComment(Long articleId, CommentCreateRequest request, Long memberId) {
        ArticleEntity article = articleService.findById(articleId);
        MemberEntity member = findMemberById(memberId);
        CommentEntity comment = new CommentEntity(request.getContent(), article, member);
        return commentRepository.save(comment);
    }

    @Transactional
    public CommentEntity updateComment(Long articleId, Long commentId, CommentUpdateRequest request, Long memberId) {
        CommentEntity comment = findComment(commentId);
        comment.validateArticle(articleId);
        MemberEntity member = findMemberById(memberId);
        comment.update(request.getContent(), member);
        return comment;
    }

    @Transactional
    public void deleteComment(Long articleId, Long commentId, Long memberId) {
        CommentEntity comment = findComment(commentId);
        comment.validateArticle(articleId);
        MemberEntity member = findMemberById(memberId);
        comment.softDelete(member);
    }

    private CommentEntity findComment(Long id) {
        return commentRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> MyEntityNotFoundException.from(id));
    }

    private MemberEntity findMemberById(Long memberId) {
        return memberService.findById(memberId);
    }
}
