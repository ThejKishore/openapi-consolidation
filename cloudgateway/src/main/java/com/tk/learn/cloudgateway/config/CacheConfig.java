package com.tk.learn.cloudgateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("routeHealth");
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}

