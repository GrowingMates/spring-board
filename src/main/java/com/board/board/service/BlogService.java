package com.board.board.service;

import com.board.board.domain.Article;
import com.board.board.dto.ArticleCreateRequest;
import com.board.board.dto.ArticleUpdateRequest;
import com.board.board.repository.BlogRepository;
import com.board.exception.MyEntityNotFoundException;
import com.board.member.entity.MemberEntity;
import com.board.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BlogService {

    private final BlogRepository blogRepository;
    private final MemberService memberService;

    public Article save(ArticleCreateRequest request) {
        MemberEntity member = memberService.findById(request.getMemberId());

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
        blogRepository.deleteById(id);
    }

    public Article update(long id, ArticleUpdateRequest request) {
        Article article = findArticle(id);

        article.update(request.getTitle(), request.getContent());
        return article;
    }

    private Article findArticle(long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> MyEntityNotFoundException.from(id));
    }
}
