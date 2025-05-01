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
        return commentRepository.findByArticleIdAndDeletedFalseOrderByCreatedAtDesc(articleId, pageable);
    }

    @Transactional
    public CommentEntity createComment(CommentCreateRequest request, Long memberId) {
        ArticleEntity article = articleService.findById(request.getArticleId());
        MemberEntity member = findMemberById(memberId);
        CommentEntity comment = new CommentEntity(request.getContent(), article, member);
        return commentRepository.save(comment);
    }

    @Transactional
    public CommentEntity updateComment(CommentUpdateRequest request, Long memberId) {
        CommentEntity comment = findCommentAndValidateOwner(request.getCommentId(), findMemberById(memberId));
        comment.update(request.getContent());
        return comment;
    }

    @Transactional
    public void deleteComment(long commentId, Long memberId) {
        CommentEntity comment = findCommentAndValidateOwner(commentId, findMemberById(memberId));
        comment.delete();
    }

    private CommentEntity findCommentAndValidateOwner(long commentId, MemberEntity member) {
        CommentEntity comment = findComment(commentId);
        comment.validateOwner(member);
        return comment;
    }

    private CommentEntity findComment(long id) {
        return commentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> MyEntityNotFoundException.from(id));
    }

    private MemberEntity findMemberById(Long memberId) {
        return memberService.findById(memberId);
    }
}
