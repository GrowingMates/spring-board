package com.board.comment.controller;

import com.board.comment.controller.dto.reponse.CommentResponse;
import com.board.comment.controller.dto.reponse.PageCommentResponse;
import com.board.comment.controller.dto.request.CommentRequest;
import com.board.comment.domain.Comment;
import com.board.comment.service.CommentService;
import com.board.global.resolver.annotation.Auth;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/articles/{articleId}/comments")
    public ResponseEntity<CommentResponse> createComment(@RequestBody CommentRequest request, @Auth Long memberId,
                                                         @PathVariable Long articleId) {
        Comment comment = commentService.createComment(request.content(), memberId, articleId);
        CommentResponse response = new CommentResponse(
                comment.getMemberId(),
                comment.getArticleId(),
                comment.getContent()
        );

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(response.articleId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/articles/{articleId}/comments")
    public ResponseEntity<PageCommentResponse> showArticleComments(
            @PathVariable Long articleId,
            @RequestParam Long lastId,
            @RequestParam int size
    ) {
        Page<Comment> comments = commentService.findArticleComments(articleId, lastId, size);
        return ResponseEntity.ok(new PageCommentResponse(comments));
    }

    @GetMapping("members/me/comments")
    public ResponseEntity<PageCommentResponse> showMemberComments(
            @Auth Long memberId,
            @RequestParam int page,
            @RequestParam int size
    ) {
        Page<Comment> comments = commentService.findMemberComments(memberId, page, size);
        return ResponseEntity.ok(new PageCommentResponse(comments));
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@RequestBody CommentRequest request, @Auth Long memberId,
                                                         @PathVariable Long commentId) {
        Comment comment = commentService.updateComment(request, memberId, commentId);
        CommentResponse response = new CommentResponse(
                comment.getMemberId(),
                comment.getArticleId(),
                comment.getContent()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> deleteComment(@Auth Long memberId, @PathVariable Long commentId) {
        Comment comment = commentService.deleteComment(memberId, commentId);
        CommentResponse response = new CommentResponse(
                comment.getMemberId(),
                comment.getArticleId(),
                comment.getContent()
        );

        return ResponseEntity.ok(response);
    }
}
