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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        Page<CommentResponse> commentPage = commentService.findAllComments(articleId, pageable)
                .map(CommentResponse::new);

        return ResponseEntity.ok(PageResponse.from(commentPage));
    }

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long articleId,
                                                      @Valid @RequestBody CommentCreateRequest request,
                                                      @AuthenticatedMember Long memberId) {
        CommentEntity savedComment = commentService.createComment(articleId, request, memberId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommentResponse(savedComment));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long articleId,
                                                         @PathVariable Long commentId,
                                                         @Valid @RequestBody CommentUpdateRequest request,
                                                         @AuthenticatedMember Long memberId) {
        CommentEntity updatedComment = commentService.updateComment(articleId, commentId, request, memberId);
        return ResponseEntity.ok()
                .body(new CommentResponse(updatedComment));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long articleId,
                                              @PathVariable Long commentId,
                                              @AuthenticatedMember Long memberId) {
        commentService.deleteComment(articleId, commentId, memberId);
        return ResponseEntity.noContent()
                .build();
    }
}
