package com.board.article.repository;

import com.board.article.domain.Article;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findArticleByMemberId(Long memberId);
}
