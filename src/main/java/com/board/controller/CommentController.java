package com.board.controller;

import com.board.dto.request.CommentCreateRequest;
import com.board.dto.request.CommentUpdateRequest;
import com.board.dto.response.CommentResponse;
import com.board.entity.CommentEntity;
import com.board.service.CommentService;
import com.config.auth.annotation.AuthenticatedMember;
import com.util.page.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<PageResponse<CommentResponse>> findAllComments(@RequestParam Long articleId,
                                                                         @RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size,
                                                                         @RequestParam(defaultValue = "createdAt") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sort));
        Page<CommentResponse> commentPage = commentService.findAllComments(articleId, pageable)
                .map(CommentResponse::new);

        return ResponseEntity.ok(PageResponse.from(commentPage));
    }

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(@Valid @RequestBody CommentCreateRequest request,
                                                      @AuthenticatedMember Long memberId) {
        CommentEntity savedComment = commentService.createComment(request, memberId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommentResponse(savedComment));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable long commentId,
                                                         @Valid @RequestBody CommentUpdateRequest request,
                                                         @AuthenticatedMember Long memberId) {
        request.setCommentId(commentId);
        CommentEntity updatedComment = commentService.updateComment(request, memberId);
        return ResponseEntity.ok()
                .body(new CommentResponse(updatedComment));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable long commentId,
                                              @AuthenticatedMember Long memberId) {
        commentService.deleteComment(commentId, memberId);
        return ResponseEntity.noContent()
                .build();
    }
}
