package com.tk.learn.cloudgateway.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tk.learn.cloudgateway.common.dynamic.FilterRow;
import com.tk.learn.cloudgateway.common.dynamic.PredicateRow;
import com.tk.learn.cloudgateway.route.RouteRequest;
import com.tk.learn.cloudgateway.route.RouteResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class for converting between JSON strings and domain objects.
 * Handles serialization/deserialization of predicates, filters, and metadata.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JsonConverterUtil {

    private final ObjectMapper objectMapper;

    /**
     * Convert predicates list to JSON string
     */
    public String serializePredicates(List<RouteRequest.PredicateRequest> predicates) {
        if (predicates == null || predicates.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(predicates);
        } catch (Exception e) {
            log.error("Failed to serialize predicates: {}", e.getMessage(), e);
            return "[]";
        }
    }

    /**
     * Convert filters list to JSON string
     */
    public String serializeFilters(List<RouteRequest.FilterRequest> filters) {
        if (filters == null || filters.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(filters);
        } catch (Exception e) {
            log.error("Failed to serialize filters: {}", e.getMessage(), e);
            return "[]";
        }
    }

    /**
     * Convert metadata map to JSON string
     */
    public String serializeMetadata(Map<String, String> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (Exception e) {
            log.error("Failed to serialize metadata: {}", e.getMessage(), e);
            return "{}";
        }
    }

    /**
     * Convert JSON string to predicates list
     */
    public List<RouteResponse.PredicateDto> deserializePredicates(String json) {
        if (json == null || json.isEmpty() || "[]".equals(json)) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory()
                .constructCollectionType(List.class, RouteResponse.PredicateDto.class));
        } catch (Exception e) {
            log.error("Failed to deserialize predicates: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Convert JSON string to filters list
     */
    public List<RouteResponse.FilterDto> deserializeFilters(String json) {
        if (json == null || json.isEmpty() || "[]".equals(json)) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory()
                .constructCollectionType(List.class, RouteResponse.FilterDto.class));
        } catch (Exception e) {
            log.error("Failed to deserialize filters: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Convert JSON string to metadata map
     */
    public Map<String, String> deserializeMetadata(String json) {
        if (json == null || json.isEmpty() || "{}".equals(json)) {
            return new LinkedHashMap<>();
        }
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory()
                .constructMapType(LinkedHashMap.class, String.class, String.class));
        } catch (Exception e) {
            log.error("Failed to deserialize metadata: {}", e.getMessage(), e);
            return new LinkedHashMap<>();
        }
    }

    /**
     * Convert predicates JSON to DbRoute PredicateRow list
     */
    public List<PredicateRow> deserializePredicateRows(String json) {
        List<RouteResponse.PredicateDto> dtos = deserializePredicates(json);
        return dtos.stream()
            .map(dto -> new PredicateRow(dto.getName(), dto.getArgs()))
            .collect(Collectors.toList());
    }

    /**
     * Convert filters JSON to DbRoute FilterRow list
     */
    public List<FilterRow> deserializeFilterRows(String json) {
        List<RouteResponse.FilterDto> dtos = deserializeFilters(json);
        return dtos.stream()
            .map(dto -> new FilterRow(dto.getName(), dto.getArgs()))
            .collect(Collectors.toList());
    }
}

