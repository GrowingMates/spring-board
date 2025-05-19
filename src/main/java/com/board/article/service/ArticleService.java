package com.board.article.service;

import com.board.article.domain.Article;
import com.board.article.exception.ArticleErrorCode;
import com.board.article.exception.ArticleException;
import com.board.article.repository.ArticleRepository;
import com.board.article.service.event.ArticleDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ArticleService {

    private static final String PAGE_SORT_DELIMITER = "id";
    private static final int NO_OFFSET_PAGING_PAGE = 0;

    private final ArticleRepository articleRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Article createArticle(Long memberId, String title, String content) {
        Article article = new Article(memberId, title, content);
        articleRepository.save(article);

        return article;
    }

    public Article updateArticle(Long articleId, Long memberId, String title, String content) {
        Article article = findArticle(articleId);
        article.validateAccessAboutArticle(memberId);
        article.update(title, content);

        return article;
    }

    public Article deleteArticle(Long articleId, Long memberId) {
        Article article = findArticle(articleId);
        article.validateAccessAboutArticle(memberId);
        articleRepository.delete(article);
        eventPublisher.publishEvent(new ArticleDeletedEvent(articleId));

        return article;
    }

    public void updateMemberIdToNUll(Long memberId) {
        articleRepository.updateMemberIdToNUll(memberId);
    }

    @Transactional(readOnly = true)
    public Page<Article> findAllArticles(Long lastId, int size) {
        Pageable articlePageable = PageRequest.of(NO_OFFSET_PAGING_PAGE, size, Sort.by(PAGE_SORT_DELIMITER).descending());
        return articleRepository.findByIdLessThanOrderByIdDesc(lastId, articlePageable);
    }

    @Transactional(readOnly = true)
    public Article findArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleException(ArticleErrorCode.NOT_FOUND_ARTICLE));
    }

    @Transactional(readOnly = true)
    public Page<Article> findMemberArticles(Long memberId, int page, int size) {
        Pageable articlePageable = PageRequest.of(page, size, Sort.by(PAGE_SORT_DELIMITER).descending());
        return articleRepository.findArticleByMemberId(memberId, articlePageable);
    }
}
