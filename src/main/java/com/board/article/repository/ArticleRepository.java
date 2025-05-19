package com.board.article.repository;

import com.board.article.domain.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    Page<Article> findArticleByMemberId(Long memberId, Pageable pageable);

    Page<Article> findByIdLessThanOrderByIdDesc(Long lastId, Pageable pageable);

    @Modifying
    @Query("update Article a set a.memberId = null where a.memberId = :memberId")
    void updateMemberIdToNUll(@Param("memberId") Long memberId);
}
