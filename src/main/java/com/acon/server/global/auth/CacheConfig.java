package com.acon.server.global.auth;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// TODO: 어노테이션을 활용하여 리팩
@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${jwt.refresh-token-expire-time}")
    private long REFRESH_TOKEN_EXPIRATION_TIME;

    @Bean
    public CacheManager cacheManager() {
        // TODO: initialCapacity, maximumSize 설정
        // 최대 용량 설정을 따로 진행하지 않음. 메모리 부족 문제 주의 필요
        Caffeine<Object, Object> caffeineBuilder = Caffeine.newBuilder()
                .expireAfterWrite(REFRESH_TOKEN_EXPIRATION_TIME, TimeUnit.MILLISECONDS);

        CaffeineCache refreshTokenCache = new CaffeineCache(
                "refreshTokenCache",
                caffeineBuilder.build()
        );

        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Collections.singletonList(refreshTokenCache));
        return cacheManager;
    }
}
