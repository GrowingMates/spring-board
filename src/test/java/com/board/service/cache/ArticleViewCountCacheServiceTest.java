package com.board.service.cache;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ArticleViewCountCacheServiceTest {

    @Autowired
    private ArticleViewCountCacheService articleViewCountCacheService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String KEY_PREFIX = "article:viewCount:";

    @BeforeEach
    void setup() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @AfterEach
    void tearDown() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @DisplayName("increase() 호출 시 게시글 조회수가 1 증가한다.")
    @Test
    void 게시글_조회수가_1_증가() {
        // given (준비)
        Long articleId = 100L;
        String redisKey = KEY_PREFIX + articleId;

        // when (실행)
        articleViewCountCacheService.increase(articleId); // 1번째 증가
        articleViewCountCacheService.increase(articleId); // 2번째 증가
        articleViewCountCacheService.increase(articleId); // 3번째 증가

        // then (검증)
        // 1. 서비스 메서드를 통해 반환되는 조회수 검증
        long viewCount = articleViewCountCacheService.getViewCount(articleId);
        assertThat(viewCount).isEqualTo(3);

        // 2. Redis에 실제로 저장된 값도 검증
        String storedValueInRedis = redisTemplate.opsForValue().get(redisKey);
        assertThat(storedValueInRedis).isEqualTo("3");
    }

    @DisplayName("getViewCount() 호출 시 캐시에 값이 없으면 0을 반환한다.")
    @Test
    void 캐시에_값이_없으면_0을_반환() {
        // given (준비)
        Long articleId = 200L; // 아직 Redis에 없는 ID

        // when (실행)
        long viewCount = articleViewCountCacheService.getViewCount(articleId);

        // then (검증)
        assertThat(viewCount).isEqualTo(0);
    }

    @DisplayName("reset() 호출 시 해당 게시글의 조회수 캐시가 삭제된다.")
    @Test
    void 게시글의_조회수_캐시가_삭제() {
        // given (준비)
        Long articleId = 300L;
        String redisKey = KEY_PREFIX + articleId;
        articleViewCountCacheService.increase(articleId); // 조회수 1로 만듦
        assertThat(articleViewCountCacheService.getViewCount(articleId)).isEqualTo(1);

        // when (실행)
        articleViewCountCacheService.reset(articleId); // 조회수 초기화 (삭제)

        // then (검증)
        // 1. 서비스 메서드를 통해 조회수가 0으로 반환되는지 확인
        long viewCountAfterReset = articleViewCountCacheService.getViewCount(articleId);
        assertThat(viewCountAfterReset).isEqualTo(0);

        // 2. Redis에 실제로 해당 키가 없는지 확인
        Boolean keyExists = redisTemplate.hasKey(redisKey);
        assertThat(keyExists).isFalse();
    }
}
