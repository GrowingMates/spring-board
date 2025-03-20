package com.board.comment.controller;

import com.board.comment.controller.dto.reponse.CommentResponse;
import com.board.comment.controller.dto.reponse.CommentResponses;
import com.board.comment.controller.dto.request.CommentRequest;
import com.board.comment.service.CommentService;
import com.board.global.resolver.annotation.Auth;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/articles/{articleId}/comments")
    public ResponseEntity<CommentResponse> createComment(@RequestBody CommentRequest request, @Auth Long memberId, @PathVariable Long articleId) {
        CommentResponse response = commentService.createComment(request, memberId, articleId);
        URI location = URI.create("/api/articles/" + response.articleId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/articles/{articleId}/comments")
    public ResponseEntity<CommentResponses> showArticleComments(@PathVariable Long articleId) {
        return ResponseEntity.ok(commentService.showArticleComments(articleId));
    }

    @GetMapping("members/me/comments")
    public ResponseEntity<CommentResponses> showMemberComments(@Auth Long memberId) {
        return ResponseEntity.ok(commentService.showMemberArticles(memberId));
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@RequestBody CommentRequest request, @Auth Long memberId, @PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.updateComment(request, memberId, commentId));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> deleteComment(@Auth Long memberId, @PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.deleteComment(memberId, commentId));
    }
}
