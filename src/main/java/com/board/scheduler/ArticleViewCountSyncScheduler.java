package com.board.scheduler;

import com.board.service.ArticleService;
import com.board.service.cache.ArticleViewCountCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class ArticleViewCountSyncScheduler {

    private final ArticleViewCountCacheService cacheService;
    private final ArticleService articleService;

    private static final String PREFIX = "article:viewCount:";

    @Scheduled(fixedRate = 300000)
    public void syncViewCountsToDB() {
        Map<Long, Long> cachedViewCounts = cacheService.getAllViewCounts();

        for (Map.Entry<Long, Long> entry : cachedViewCounts.entrySet()) {
            Long articleId = entry.getKey();
            Long viewCount = entry.getValue();

            articleService.incrementViewCount(articleId, viewCount);
            cacheService.reset(articleId); // 반영 후 캐시 초기화
        }

        log.info("동기화 완료: {}개의 게시글 조회수를 DB에 반영했습니다.", cachedViewCounts.size());
    }
}
