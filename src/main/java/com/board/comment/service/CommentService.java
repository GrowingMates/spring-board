package com.board.comment.service;

import com.board.comment.controller.dto.reponse.CommentResponse;
import com.board.comment.controller.dto.reponse.CommentResponses;
import com.board.comment.controller.dto.request.CommentRequest;
import com.board.comment.domain.Comment;
import com.board.comment.repository.CommentRepository;
import com.board.comment.service.exception.ForbiddenAccessCommentException;
import com.board.comment.service.exception.NotFoundCommentException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentResponse createComment(CommentRequest request, Long memberId, Long articleId) {
        Comment comment = new Comment(memberId, articleId, request.content());
        commentRepository.save(comment);

        return new CommentResponse(
                comment.getMemberId(),
                comment.getArticleId(),
                comment.getContent()
        );
    }

    public CommentResponses showArticleComments(Long articleId) {
        List<CommentResponse> commentResponses = getArticleComments(articleId).stream()
                .map(comment -> new CommentResponse(
                        comment.getMemberId(),
                        comment.getArticleId(),
                        comment.getContent()
                ))
                .toList();

        return new CommentResponses(commentResponses);
    }

    public CommentResponses showMemberArticles(Long memberId) {
        List<CommentResponse> commentResponses = getMemberComments(memberId).stream()
                .map(comment -> new CommentResponse(
                        comment.getMemberId(),
                        comment.getArticleId(),
                        comment.getContent()
                ))
                .toList();

        return new CommentResponses(commentResponses);
    }

    public CommentResponse updateComment(CommentRequest request, Long memberId, Long commentId) {
        Comment comment = getComment(commentId);
        validateAccessAboutComment(memberId, comment);
        comment.update(request.content());

        return new CommentResponse(
                comment.getMemberId(),
                comment.getArticleId(),
                comment.getContent()
        );
    }

    public CommentResponse deleteComment(Long memberId, Long commentId) {
        Comment comment = getComment(commentId);
        validateAccessAboutComment(memberId, comment);
        commentRepository.delete(comment);

        return new CommentResponse(
                comment.getMemberId(),
                comment.getArticleId(),
                comment.getContent()
        );
    }

    private void validateAccessAboutComment(Long memberId, Comment comment) {
        if (!Objects.equals(comment.getMemberId(), memberId)) {
            throw new ForbiddenAccessCommentException();
        }
    }

    @Transactional(readOnly = true)
    public List<Comment> getArticleComments(Long articleId) {
        return commentRepository.findAllByArticleId(articleId);
    }

    @Transactional(readOnly = true)
    public List<Comment> getMemberComments(Long memberId) {
        return commentRepository.findAllByMemberId(memberId);
    }

    @Transactional(readOnly = true)
    public Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(NotFoundCommentException::new);
    }
}
