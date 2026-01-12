package com.tk.learn.cloudgateway.config;

import com.tk.learn.cloudgateway.filter.GatewayLoggingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration to register custom logging interceptor and static resources
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final GatewayLoggingInterceptor gatewayLoggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(gatewayLoggingInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/h2-console/**",
                    "/admin/**"
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Enable static resource serving for admin portal
        registry.addResourceHandler("/admin/**")
                .addResourceLocations("classpath:/static/admin/");

        // Ensure default static resources are still served
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}

