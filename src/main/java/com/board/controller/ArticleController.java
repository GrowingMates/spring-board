package com.board.controller;

import com.board.dto.request.ArticleCreateRequest;
import com.board.dto.request.ArticleUpdateRequest;
import com.board.dto.response.ArticleResponse;
import com.board.entity.ArticleEntity;
import com.board.service.ArticleService;
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
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("")
    public ResponseEntity<ArticleResponse> addArticle(@Valid @RequestBody ArticleCreateRequest request,
                                                      @AuthenticatedMember Long memberId) {
        ArticleEntity savedArticle = articleService.save(request, memberId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ArticleResponse(savedArticle));
    }

    @GetMapping("")
    public ResponseEntity<PageResponse<ArticleResponse>> findAllArticles(@RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size,
                                                                         @RequestParam(defaultValue = "createdAt") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sort));

        Page<ArticleResponse> articlePage = articleService.findAll(pageable)
                .map(ArticleResponse::withoutContent);

        return ResponseEntity.ok()
                .body(PageResponse.from(articlePage));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id) {
        ArticleEntity article = articleService.findByIdAndIncreaseViewCount(id);

        return ResponseEntity.ok()
                .body(new ArticleResponse(article));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable long id,
                                              @AuthenticatedMember Long memberId) {
        articleService.delete(id, memberId);

        return ResponseEntity.noContent()
                .build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponse> updateArticle(@PathVariable long id,
                                                         @AuthenticatedMember Long memberId,
                                                         @Valid @RequestBody ArticleUpdateRequest request) {
        ArticleEntity updateArticle = articleService.update(id, memberId, request);

        return ResponseEntity.ok()
                .body(new ArticleResponse(updateArticle));
    }
}
