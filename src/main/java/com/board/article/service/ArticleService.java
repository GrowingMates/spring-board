package com.board.article.service;

import com.board.article.controller.dto.request.ArticleRequest;
import com.board.article.controller.dto.response.ArticleResponse;
import com.board.article.controller.dto.response.ArticleResponses;
import com.board.article.domain.Article;
import com.board.article.repository.ArticleRepository;
import com.board.article.service.exception.ForbiddenAccessArticleException;
import com.board.article.service.exception.NotFoundArticleException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleResponse createArticle(ArticleRequest request, Long memberId) {
        Article article = new Article(memberId, request.title(), request.content());
        articleRepository.save(article);

        return new ArticleResponse(
                article.getId(),
                article.getMemberId(),
                article.getTitle(),
                article.getContent()
        );
    }

    public ArticleResponses showAllArticles() {
        List<ArticleResponse> articleResponses = getAllArticles().stream()
                .map(article -> new ArticleResponse(
                        article.getId(),
                        article.getMemberId(),
                        article.getTitle(),
                        article.getContent()
                ))
                .toList();

        return new ArticleResponses(articleResponses);
    }

    public ArticleResponse showArticle(Long articleId) {
        Article article = getArticle(articleId);

        return new ArticleResponse(
                article.getId(),
                article.getMemberId(),
                article.getTitle(),
                article.getContent()
        );
    }

    public ArticleResponses showMemberArticles(Long memberId) {
        List<ArticleResponse> articleResponses = getMemberArticles(memberId).stream()
                .map(article -> new ArticleResponse(
                        article.getId(),
                        article.getMemberId(),
                        article.getTitle(),
                        article.getContent()
                ))
                .toList();

        return new ArticleResponses(articleResponses);
    }

    public ArticleResponse updateArticle(ArticleRequest request, Long articleId, Long memberId) {
        Article article = getArticle(articleId);
        validateAccessAboutArticle(memberId, article);
        article.update(request.title(), request.content());

        return new ArticleResponse(
                article.getId(),
                article.getMemberId(),
                article.getTitle(),
                article.getContent()
        );
    }

    public ArticleResponse deleteArticle(Long articleId, Long memberId) {
        Article article = getArticle(articleId);
        validateAccessAboutArticle(memberId, article);
        articleRepository.delete(article);

        return new ArticleResponse(
                article.getId(),
                article.getMemberId(),
                article.getTitle(),
                article.getContent()
        );
    }

    private void validateAccessAboutArticle(Long memberId, Article article) {
        if(!Objects.equals(article.getMemberId(), memberId)) {
            throw new ForbiddenAccessArticleException();
        }
    }

    @Transactional(readOnly = true)
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Article getArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(NotFoundArticleException::new);
    }

    @Transactional(readOnly = true)
    public List<Article> getMemberArticles(Long memberId) {
        return articleRepository.findArticleByMemberId(memberId);
    }
}
