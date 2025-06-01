package com.board.service.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class ArticleViewCountCacheService {

    private static final String PREFIX = "article:viewCount:";

    private final RedisTemplate<String, String> redisTemplate;

    public void increase(Long articleId) {
        String key = getKey(articleId);
        redisTemplate.opsForValue().increment(key);
    }

    public long getViewCount(Long articleId) {
        String key = getKey(articleId);
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return 0;
        }
        return Long.parseLong(value);
    }

    public void reset(Long articleId) {
        String key = getKey(articleId);
        redisTemplate.delete(key);
    }

    public Map<Long, Long> getAllViewCounts() {
        Set<String> keys = redisTemplate.keys(PREFIX + "*");
        Map<Long, Long> result = new HashMap<>();
        if (keys.isEmpty()) {
            return result;
        }
        for (String key : keys) {
            String value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                Long articleId = Long.parseLong(key.replace(PREFIX, ""));
                result.put(articleId, Long.parseLong(value));
            }
        }
        return result;
    }

    private String getKey(Long articleId) {
        return PREFIX + articleId;
    }
}
