package com.tk.learn.cloudgateway.dynamic;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;

@Configuration
@RequiredArgsConstructor
public class DynamicRoutingConfig {

    private final CustomRouterFunctionMapping mapping;

    @PostConstruct
    void init() {
        // Ensure highest precedence so DB routes evaluated before default RouterFunctionMapping
        mapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @Bean
    ApplicationRunner preloadRoutes() {
        return args -> mapping.refresh();
    }
}
