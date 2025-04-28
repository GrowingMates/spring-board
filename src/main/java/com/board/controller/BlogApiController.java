package com.board.controller;

import com.board.dto.request.ArticleCreateRequest;
import com.board.dto.request.ArticleUpdateRequest;
import com.board.dto.response.ArticleResponse;
import com.board.entity.ArticleEntity;
import com.board.service.BlogService;
import com.config.auth.annotation.AuthenticatedMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/articles")
public class BlogApiController {

    private final BlogService blogService;

    @PostMapping("")
    public ResponseEntity<ArticleResponse> addArticle(@Valid @RequestBody ArticleCreateRequest request,
                                                      @AuthenticatedMember Long memberId) {
        ArticleEntity savedArticle = blogService.save(request, memberId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ArticleResponse(savedArticle));
    }

    @GetMapping("")
    public ResponseEntity<List<ArticleResponse>> findAllArticles(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<ArticleResponse> articles = blogService.findAll(pageable)
                .stream()
                .map(ArticleResponse::withoutContent)
                .toList();

        return ResponseEntity.ok()
                .body(articles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id) {
        ArticleEntity article = blogService.findByIdAndIncreaseViewCount(id);

        return ResponseEntity.ok()
                .body(new ArticleResponse(article));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable long id,
                                              @AuthenticatedMember Long memberId) {
        blogService.delete(id, memberId);

        return ResponseEntity.noContent()
                .build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponse> updateArticle(@PathVariable long id,
                                                         @AuthenticatedMember Long memberId,
                                                         @Valid @RequestBody ArticleUpdateRequest request) {
        ArticleEntity updateArticle = blogService.update(id, memberId, request);

        return ResponseEntity.ok()
                .body(new ArticleResponse(updateArticle));
    }
}
