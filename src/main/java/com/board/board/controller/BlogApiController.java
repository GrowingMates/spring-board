package com.board.board.controller;

import com.board.board.dto.request.ArticleCreateRequest;
import com.board.board.dto.request.ArticleUpdateRequest;
import com.board.board.dto.response.ArticleResponse;
import com.board.board.entity.ArticleEntity;
import com.board.board.service.BlogService;
import com.board.config.auth.annotation.AuthenticatedMember;
import com.board.member.entity.MemberEntity;
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
                                                      @AuthenticatedMember MemberEntity member) {
        ArticleEntity savedArticle = blogService.save(request, member);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ArticleResponse(savedArticle));
    }

    @GetMapping("")
    public ResponseEntity<List<ArticleResponse>> findAllArticles(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<ArticleResponse> articles = blogService.findAll(pageable)
                .stream()
                .map(ArticleResponse::new)
                .toList();

        return ResponseEntity.ok()
                .body(articles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id) {
        ArticleEntity article = blogService.findById(id);

        return ResponseEntity.ok()
                .body(new ArticleResponse(article));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable long id,
                                              @AuthenticatedMember MemberEntity member) {
        blogService.delete(id, member);

        return ResponseEntity.noContent()
                .build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponse> updateArticle(@PathVariable long id,
                                                         @AuthenticatedMember MemberEntity member,
                                                         @Valid @RequestBody ArticleUpdateRequest request) {
        ArticleEntity updateArticle = blogService.update(id, member, request);

        return ResponseEntity.ok()
                .body(new ArticleResponse(updateArticle));
    }
}
