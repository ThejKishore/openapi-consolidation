package com.tk.learn.cloudgateway.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteResponse {
    private String id;
    private String uri;
    private Integer order;
    private Boolean enabled;
    private Integer version;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
    private List<PredicateDto> predicates;
    private List<FilterDto> filters;
    private Map<String, String> metadata;
    private HealthStatusDto health;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PredicateDto {
        private String name;
        private String args;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FilterDto {
        private String name;
        private String args;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HealthStatusDto {
        private String status; // UP, DOWN, SLOW
        private Integer responseTime; // in milliseconds
        private LocalDateTime lastChecked;
    }
}

