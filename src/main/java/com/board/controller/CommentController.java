package com.board.controller;

import com.board.dto.request.CommentCreateRequest;
import com.board.dto.request.CommentUpdateRequest;
import com.board.dto.response.CommentResponse;
import com.board.entity.CommentEntity;
import com.board.service.CommentService;
import com.config.auth.annotation.AuthenticatedMember;
import com.util.page.PageResponse;
import com.util.sort.SortUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/articles/{articleId}/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<PageResponse<CommentResponse>> findAllComments(@PathVariable Long articleId,
                                                                         @RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size,
                                                                         @RequestParam(defaultValue = "latest") String sort) {
        Pageable pageable = PageRequest.of(page, size, SortUtils.getCommentSort(sort));
        return ResponseEntity.ok(PageResponse.from(commentService.findAllTopLevelComments(articleId, pageable)));
    }

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long articleId,
                                                      @Valid @RequestBody CommentCreateRequest request,
                                                      @AuthenticatedMember Long memberId) {
        CommentEntity savedComment = commentService.createComment(articleId, request, memberId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommentResponse.of(savedComment, 0));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long articleId,
                                                         @PathVariable Long commentId,
                                                         @Valid @RequestBody CommentUpdateRequest request,
                                                         @AuthenticatedMember Long memberId) {
        CommentEntity updatedComment = commentService.updateComment(articleId, commentId, request, memberId);
        int replyCount = commentService.getReplyCount(updatedComment);

        return ResponseEntity.ok()
                .body(CommentResponse.of(updatedComment, replyCount));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long articleId,
                                              @PathVariable Long commentId,
                                              @AuthenticatedMember Long memberId) {
        commentService.deleteComment(articleId, commentId, memberId);
        return ResponseEntity.noContent()
                .build();
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<PageResponse<CommentResponse>> findReplies(@PathVariable Long articleId,
                                                                     @PathVariable Long commentId,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size,
                                                                     @RequestParam(defaultValue = "oldest") String sort) {
        Pageable pageable = PageRequest.of(page, size, SortUtils.getCommentSort(sort));
        Page<CommentEntity> replies = commentService.findReplies(commentId, pageable);
        return ResponseEntity.ok(PageResponse.from(replies.map(CommentResponse::fromReply)));
    }
}
