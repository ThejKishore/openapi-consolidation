package com.tk.learn.cloudgateway.route;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteRequest {

    @NotBlank(message = "Route ID is required")
    private String id;

    @NotBlank(message = "URI is required")
    private String uri;

    private Integer order;

    @NotNull(message = "Enabled status is required")
    private Boolean enabled;

    private List<PredicateRequest> predicates;

    private List<FilterRequest> filters;

    private Map<String, String> metadata;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PredicateRequest {
        @NotBlank(message = "Predicate name is required")
        private String name;

        private String args;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FilterRequest {
        @NotBlank(message = "Filter name is required")
        private String name;

        private String args;
    }
}

