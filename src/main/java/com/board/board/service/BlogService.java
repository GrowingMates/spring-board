package com.board.board.service;

import com.board.board.domain.Article;
import com.board.board.dto.ArticleCreateRequest;
import com.board.board.dto.ArticleUpdateRequest;
import com.board.board.repository.BlogRepository;
import com.board.config.auth.AuthUtil;
import com.board.exception.MyEntityNotFoundException;
import com.board.member.entity.MemberEntity;
import com.board.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@RequiredArgsConstructor
@Service
@Transactional
public class BlogService {

    private final BlogRepository blogRepository;
    private final MemberService memberService;
    private final AuthUtil authUtil;

    public Article save(ArticleCreateRequest request) {
        String email = authUtil.getUser();
        MemberEntity member = memberService.findByEmail(email);

        return blogRepository.save(Article.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .member(member)
                .build());
    }

    @Transactional(readOnly = true)
    public Page<Article> findAll(Pageable pageable) {
        return blogRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Article findById(long id) {
        return findArticle(id);
    }

    public void delete(long id) {
        compareAuthors(id);
        blogRepository.deleteById(id);
    }

    public Article update(long id, ArticleUpdateRequest request) {
        Article article = compareAuthors(id);
        article.update(request.getTitle(), request.getContent());
        return article;
    }

    private Article compareAuthors(long articleId) {
        String email = authUtil.getUser();
        MemberEntity member = memberService.findByEmail(email);
        Article article = findArticle(articleId);
        if (!Objects.equals(member.getId(), article.getMember().getId())) {
            throw new IllegalArgumentException("게시글의 작성자가 다릅니다.");
        }
        return article;
    }

    private Article findArticle(long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> MyEntityNotFoundException.from(id));
    }
}
