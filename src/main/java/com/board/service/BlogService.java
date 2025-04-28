package com.board.service;

import com.board.dto.request.ArticleCreateRequest;
import com.board.dto.request.ArticleUpdateRequest;
import com.board.entity.ArticleEntity;
import com.board.repository.BlogRepository;
import com.exception.custom.MyEntityNotFoundException;
import com.member.entity.MemberEntity;
import com.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BlogService {

    private final BlogRepository blogRepository;
    private final MemberService memberService;

    @Transactional
    public ArticleEntity save(ArticleCreateRequest request, Long memberId) {
        return blogRepository.save(ArticleEntity.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .member(memberService.findById(memberId))
                .build());
    }

    public Page<ArticleEntity> findAll(Pageable pageable) {
        return blogRepository.findAll(pageable);
    }

    public ArticleEntity findById(long id) {
        return findArticle(id);
    }

    @Transactional
    public ArticleEntity findByIdAndIncreaseViewCount(long id) {
        ArticleEntity article = findArticle(id);
        article.increaseViewCount();
        return article;
    }

    @Transactional
    public void delete(long id, Long memberId) {
        compareAuthors(id, memberService.findById(memberId));
        blogRepository.deleteById(id);
    }

    @Transactional
    public ArticleEntity update(long id, Long memberId, ArticleUpdateRequest request) {
        ArticleEntity article = compareAuthors(id, memberService.findById(memberId));
        article.update(request.getTitle(), request.getContent());
        return article;
    }

    private ArticleEntity compareAuthors(long articleId, MemberEntity member) {
        ArticleEntity article = findArticle(articleId);
        article.validateOwner(member);
        return article;
    }

    private ArticleEntity findArticle(long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> MyEntityNotFoundException.from(id));
    }
}
