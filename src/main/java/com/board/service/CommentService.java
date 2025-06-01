package com.board.service;

import com.board.dto.request.CommentCreateRequest;
import com.board.dto.request.CommentUpdateRequest;
import com.board.dto.response.CommentResponse;
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

    public Page<CommentResponse> findAllTopLevelComments(Long articleId, Pageable pageable) {
        ArticleEntity article = articleService.findById(articleId);
        Page<CommentEntity> comments = commentRepository.findByArticleAndParentIsNullAndIsDeletedFalse(article, pageable);
        return comments.map(comment -> CommentResponse.of(comment, getReplyCount(comment)));
    }

    public int getReplyCount(CommentEntity comment) {
        return commentRepository.countByParentAndIsDeletedFalse(comment);
    }

    @Transactional
    public CommentEntity createComment(Long articleId, CommentCreateRequest request, Long memberId) {
        ArticleEntity article = articleService.findById(articleId);
        MemberEntity member = findMemberById(memberId);

        if (request.getParentId() != null) {
            CommentEntity parent = findComment(request.getParentId());

            if (parent.isReply()) {
                throw new IllegalArgumentException("대댓글에는 대댓글을 달 수 없습니다");
            }

            if (!parent.getArticle().getId().equals(articleId)) {
                throw new IllegalArgumentException("부모 댓글이 해당 게시글에 속하지 않습니다");
            }

            CommentEntity comment = CommentEntity.builder()
                    .content(request.getContent())
                    .article(article)
                    .member(member)
                    .parent(parent)
                    .build();
            return commentRepository.save(comment);
        }

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

    public Page<CommentEntity> findReplies(Long parentId, Pageable pageable) {
        CommentEntity parent = findComment(parentId);
        if (parent.isReply()) {
            throw new IllegalArgumentException("대댓글에는 대댓글이 없습니다.");
        }
        return commentRepository.findByParentAndIsDeletedFalse(parent, pageable);
    }
}
