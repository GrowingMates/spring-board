package com.config.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class RedisConnectionTest {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void redis_set_and_get() {
        redisTemplate.opsForValue().set("testkey", "hello redis");
        String value = (String) redisTemplate.opsForValue().get("testkey");
        assertEquals("hello redis", value);
    }
}
