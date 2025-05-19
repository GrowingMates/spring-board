package com.board.article.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.board.article.controller.dto.request.ArticleRequest;
import com.board.article.domain.Article;
import com.board.article.exception.ArticleErrorCode;
import com.board.article.exception.ArticleException;
import com.board.article.repository.ArticleRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleService articleService;

    private Article article;
    private Page<Article> articlePage;

    @BeforeEach
    void set() {
        article = new Article(1L, "제목1", "내용1");
        List<Article> articleList = List.of(article);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        articlePage = new PageImpl<>(articleList, pageable, articleList.size());
    }


    @Nested
    class 정상_동작_테스트를_진행한다 {

        @Test
        @DisplayName("게시글을 생성한다.")
        void createArticle() {
            // given
            ArticleRequest request = new ArticleRequest("제목1", "내용1");
            Long memberId = 1L;
            given(articleRepository.save(any(Article.class))).willReturn(article);

            // when
            Article response = articleService.createArticle(memberId, request.title(), request.content());

            // then
            assertThat(response)
                    .extracting(Article::getTitle, Article::getContent, Article::getMemberId)
                    .containsExactly("제목1", "내용1", memberId);
        }

        @Test
        @DisplayName("모든 게시글을 조회한다.")
        void showAllArticles() {
            // given
            Long lastId = 0L;
            int size = 10;
            Pageable pageable = PageRequest.of(0, size, Sort.by("id").descending());
            given(articleRepository.findByIdLessThanOrderByIdDesc(lastId, pageable))
                    .willReturn(articlePage);

            // when
            Page<Article> responses = articleService.findAllArticles(lastId, size);

            // then
            assertThat(responses).hasSize(1)
                    .extracting(Article::getTitle)
                    .containsExactly("제목1");
        }

        @Test
        @DisplayName("게시글을 단건 조회한다.")
        void showArticle() {
            // given
            Long articleId = 1L;
            given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

            // when
            Article response = articleService.findArticle(articleId);

            // then
            assertThat(response.getTitle()).isEqualTo("제목1");
        }

        @Test
        @DisplayName("유저가 작성한 게시글을 모두 조회한다.")
        void showMemberArticles() {
            // given
            Long memberId = 1L;
            int page = 0;
            int size = 10;
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            given(articleRepository.findArticleByMemberId(memberId, pageable)).willReturn(articlePage);

            // when
            Page<Article> responses = articleService.findMemberArticles(memberId, page, size);

            // then
            assertThat(responses).hasSize(1)
                    .extracting(Article::getTitle)
                    .containsExactly("제목1");
        }

        @Test
        @DisplayName("게시글을 수정한다.")
        void updateArticle() {
            // given
            Long articleId = 1L;
            Long memberId = 1L;
            ArticleRequest request = new ArticleRequest("수정된 제목", "수정된 내용");
            given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

            // when
            Article response = articleService.updateArticle(articleId, memberId, request.title(), request.content());

            // then
            assertThat(response.getTitle()).isEqualTo("수정된 제목");
        }

        @Test
        @DisplayName("게시글을 삭제한다.")
        void deleteArticle() {
            // given
            Long articleId = 1L;
            Long memberId = 1L;
            given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

            // when
            Article response = articleService.deleteArticle(articleId, memberId);

            // then
            assertThat(response.getTitle()).isEqualTo("제목1");
        }
    }

    @Nested
    class 예외_처리_테스트를_진행한다 {

        @Test
        @DisplayName("존재하지 않는 게시글을 조회할 경우 예외가 발생한다.")
        void notFoundArticle() {
            // given
            Long articleId = 3L;
            given(articleRepository.findById(articleId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> articleService.findArticle(articleId))
                    .isInstanceOf(ArticleException.class)
                    .hasMessageContaining(ArticleErrorCode.NOT_FOUND_ARTICLE.message());
        }

        @Test
        @DisplayName("게시글 작성자가 아닌 사용자가 게시글을 수정하려 할 경우 예외가 발생한다.")
        void forbiddenUpdateArticle() {
            // given
            Long articleId = 1L;
            Long memberId = 2L;
            ArticleRequest request = new ArticleRequest("수정 제목", "수정 내용");
            given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

            // when & then
            assertThatThrownBy(() -> articleService.updateArticle(articleId, memberId, request.title(), request.content()))
                    .isInstanceOf(ArticleException.class)
                    .hasMessageContaining(ArticleErrorCode.FORBIDDEN_ACCESS_ARTICLE.message());
        }

        @Test
        @DisplayName("게시글 작성자가 아닌 사용자가 게시글을 삭제하려 할 경우 예외가 발생한다.")
        void forbiddenDeleteArticle() {
            // given
            Long articleId = 1L;
            Long memberId = 2L;
            given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

            // when & then
            assertThatThrownBy(() -> articleService.deleteArticle(articleId, memberId))
                    .isInstanceOf(ArticleException.class)
                    .hasMessageContaining(ArticleErrorCode.FORBIDDEN_ACCESS_ARTICLE.message());
        }
    }
}
