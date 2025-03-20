package com.board.article.loader;

import com.board.article.domain.Article;
import com.board.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleDataLoader implements CommandLineRunner {

    private final ArticleRepository repository;

    @Override
    public void run(String... args) throws Exception {
        repository.save(new Article(1L, "신형만에 관하여", "아내는 신봉선"));
        repository.save(new Article(1L, "신짱구", "바보"));
    }
}
