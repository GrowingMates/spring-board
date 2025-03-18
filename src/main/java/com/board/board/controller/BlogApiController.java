package com.board.board.controller;

import com.board.board.domain.Article;
import com.board.board.dto.ArticleCreateRequest;
import com.board.board.dto.ArticleResponse;
import com.board.board.dto.ArticleUpdateRequest;
import com.board.board.service.BlogService;
import com.board.config.auth.AuthUtil;
import com.board.member.entity.MemberEntity;
import com.board.member.service.MemberService;
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
    private final MemberService memberService;
    private final AuthUtil authUtil;

    @PostMapping("")
    public ResponseEntity<ArticleResponse> addArticle(@RequestBody ArticleCreateRequest request) {

        if (!authUtil.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = authUtil.getUser();
        MemberEntity member = memberService.findByEmail(email);
        request.setMemberId(member.getId());
        Article savedArticle = blogService.save(request);

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
        Article article = blogService.findById(id);

        return ResponseEntity.ok()
                .body(new ArticleResponse(article));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable long id) {
        blogService.delete(id);

        return ResponseEntity.noContent()
                .build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponse> updateArticle(@PathVariable long id,
                                                         @RequestBody ArticleUpdateRequest request) {
        Article updateArticle = blogService.update(id, request);

        return ResponseEntity.ok()
                .body(new ArticleResponse(updateArticle));
    }
}
