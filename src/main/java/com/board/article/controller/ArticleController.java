package com.board.article.controller;

import com.board.article.controller.dto.request.ArticleRequest;
import com.board.article.controller.dto.response.ArticleResponse;
import com.board.article.controller.dto.response.ArticleResponses;
import com.board.article.service.ArticleService;
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
@RequiredArgsConstructor
@RequestMapping("/api")
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("/articles")
    public ResponseEntity<ArticleResponse> createArticle(@RequestBody ArticleRequest request, @Auth Long memberId) {
        ArticleResponse response = articleService.createArticle(request, memberId);
        URI location = URI.create("/api/articles/" + response.articleId());
        return ResponseEntity.created(location)
                .body(response);
    }

    @GetMapping("/articles")
    public ResponseEntity<ArticleResponses> showAllArticles() {
        return ResponseEntity.ok(articleService.showAllArticles());
    }

    @GetMapping("/articles/{articleId}")
    public ResponseEntity<ArticleResponse> showArticle(@PathVariable Long articleId) {
        return ResponseEntity.ok(articleService.showArticle(articleId));
    }

    @GetMapping("/members/me/articles")
    public ResponseEntity<ArticleResponses> showMemberArticles(@Auth Long memberId) {
        return ResponseEntity.ok(articleService.showMemberArticles(memberId));
    }

    @PatchMapping("/articles/{articleId}")
    public ResponseEntity<ArticleResponse> updateArticle(@RequestBody ArticleRequest request, @PathVariable Long articleId, @Auth Long memberId) {
        return ResponseEntity.ok(articleService.updateArticle(request, articleId, memberId));
    }

    @DeleteMapping("/articles/{articleId}")
    public ResponseEntity<ArticleResponse> deleteArticle(@PathVariable Long articleId, @Auth Long memberId) {
        return ResponseEntity.ok(articleService.deleteArticle(articleId, memberId));
    }
}
