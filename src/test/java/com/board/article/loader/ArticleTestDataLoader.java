package com.board.article.loader;

import com.board.article.domain.Article;
import com.board.article.repository.ArticleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ArticleTestDataLoader {

    private final ArticleRepository repository;

    public ArticleTestDataLoader(ArticleRepository repository) {
        this.repository = repository;
    }

    @Bean
    public CommandLineRunner testData() {
        return args -> {
            repository.save(new Article(1L, "신형만에 관하여", "아내는 신봉선"));
            repository.save(new Article(1L, "신짱구", "바보"));
        };
    }
}
