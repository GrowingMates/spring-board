package com.board.service;

import com.board.dto.request.ArticleCreateRequest;
import com.board.dto.request.ArticleUpdateRequest;
import com.board.dto.response.ArticleResponse;
import com.board.entity.ArticleEntity;
import com.board.repository.ArticleRepository;
import com.board.service.cache.ArticleViewCountCacheService;
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
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final MemberService memberService;
    private final ArticleViewCountCacheService viewCountCacheService;

    @Transactional
    public ArticleEntity save(ArticleCreateRequest request, Long memberId) {
        return articleRepository.save(ArticleEntity.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .member(findMemberById(memberId))
                .build());
    }

    public Page<ArticleEntity> findAll(Pageable pageable) {
        return articleRepository.findAllByIsDeletedFalse(pageable);
    }

    @Transactional
    public void delete(Long id, Long memberId) {
        getOwnedArticle(id, memberId).softDelete();
        viewCountCacheService.reset(id);
    }

    @Transactional
    public ArticleEntity update(Long id, Long memberId, ArticleUpdateRequest request) {
        ArticleEntity article = getOwnedArticle(id, memberId);
        article.update(request.getTitle(), request.getContent());
        return article;
    }

    private ArticleEntity getOwnedArticle(Long articleId, Long memberId) {
        ArticleEntity article = findById(articleId);
        MemberEntity member = findMemberById(memberId);
        article.validateOwner(member);
        return article;
    }

    public ArticleResponse getArticleWithViewCount(Long articleId) {
        ArticleEntity article = findById(articleId);
        viewCountCacheService.increase(articleId);
        long totalViewCount = article.getViewCount() + viewCountCacheService.getViewCount(articleId);
        return ArticleResponse.from(article, totalViewCount);
    }

    @Transactional
    public void incrementViewCount(Long articleId, long viewCount) {
        ArticleEntity article = findById(articleId);
        article.increaseViewCount(viewCount);
    }

    public ArticleEntity findById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> MyEntityNotFoundException.from(id));
    }

    private MemberEntity findMemberById(Long memberId) {
        return memberService.findById(memberId);
    }
}
