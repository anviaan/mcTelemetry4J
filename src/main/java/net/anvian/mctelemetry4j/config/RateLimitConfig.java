package net.anvian.mctelemetry4j.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bucket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitConfig {

    @Bean
    public Cache<String, Bucket> rateLimitBuckets() {
        return Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofMinutes(10))
                .maximumSize(50_000)
                .build();
    }
}
