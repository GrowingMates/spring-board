package com.board.article.controller;

import com.board.article.controller.dto.request.ArticleRequest;
import com.board.article.controller.dto.response.ArticleResponse;
import com.board.article.controller.dto.response.PageArticleResponse;
import com.board.article.domain.Article;
import com.board.article.service.ArticleService;
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
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("/articles")
    public ResponseEntity<ArticleResponse> createArticle(@RequestBody ArticleRequest request, @Auth Long memberId) {
        Article article = articleService.createArticle(memberId, request.title(), request.content());
        ArticleResponse response = new ArticleResponse(
                article.getId(),
                article.getMemberId(),
                article.getTitle(),
                article.getContent()
        );

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(response.articleId())
                .toUri();

        return ResponseEntity.created(location)
                .body(response);
    }

    @GetMapping("/articles")
    public ResponseEntity<PageArticleResponse> showAllArticles(
            @RequestParam Long lastId,
            @RequestParam int size
    ) {
        Page<Article> articles = articleService.findAllArticles(lastId, size);
        return ResponseEntity.ok(new PageArticleResponse(articles));
    }

    @GetMapping("/articles/{articleId}")
    public ResponseEntity<ArticleResponse> showArticle(@PathVariable Long articleId) {
        Article article = articleService.findArticle(articleId);
        ArticleResponse response = new ArticleResponse(
                article.getId(),
                article.getMemberId(),
                article.getTitle(),
                article.getContent()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/members/me/articles")
    public ResponseEntity<PageArticleResponse> showMemberArticles(
            @Auth Long memberId,
            @RequestParam int page,
            @RequestParam int size
    ) {
        Page<Article> articles = articleService.findMemberArticles(memberId, page, size);
        return ResponseEntity.ok(new PageArticleResponse(articles));
    }

    @PatchMapping("/articles/{articleId}")
    public ResponseEntity<ArticleResponse> updateArticle(
            @RequestBody ArticleRequest request,
            @PathVariable Long articleId,
            @Auth Long memberId
    ) {
        Article article = articleService.updateArticle(articleId, memberId, request.title(), request.content());
        ArticleResponse response = new ArticleResponse(
                article.getId(),
                article.getMemberId(),
                article.getTitle(),
                article.getContent()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/articles/{articleId}")
    public ResponseEntity<ArticleResponse> deleteArticle(@PathVariable Long articleId, @Auth Long memberId) {
        Article article = articleService.deleteArticle(articleId, memberId);
        ArticleResponse response = new ArticleResponse(
                article.getId(),
                article.getMemberId(),
                article.getTitle(),
                article.getContent()
        );

        return ResponseEntity.ok(response);
    }
}
