package com.tk.learn.cloudgateway.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tk.learn.cloudgateway.domain.*;
import com.tk.learn.cloudgateway.dynamic.CustomRouterFunctionMapping;
import com.tk.learn.cloudgateway.dynamic.DbRouteModels;
import com.tk.learn.cloudgateway.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteService {

    private final JdbcClient jdbc;
    private final RouteRepository routeRepository;
    private final AuditService auditService;
    private final HealthCheckService healthCheckService;
    private final CustomRouterFunctionMapping routerMapping;
    private final ObjectMapper objectMapper;

    @Transactional
    public RouteResponse createRoute(RouteRequest request, String createdBy) {
        try {
            // Check if route already exists
            if (routeRepository.existsById(request.getId())) {
                throw new IllegalArgumentException("Route with ID " + request.getId() + " already exists");
            }

            // Create route record
            jdbc.sql("INSERT INTO gw_routes (id, uri, order_no, enabled, version, created_by, updated_by, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")
                .params(request.getId(), request.getUri(), request.getOrder(), request.getEnabled(), 1, createdBy, createdBy, LocalDateTime.now(), LocalDateTime.now())
                .update();

            // Add predicates
            if (request.getPredicates() != null && !request.getPredicates().isEmpty()) {
                for (int i = 0; i < request.getPredicates().size(); i++) {
                    RouteRequest.PredicateRequest pred = request.getPredicates().get(i);
                    jdbc.sql("INSERT INTO gw_route_predicates (route_id, ord, name, args) VALUES (?, ?, ?, ?)")
                        .params(request.getId(), i, pred.getName(), pred.getArgs())
                        .update();
                }
            }

            // Add filters
            if (request.getFilters() != null && !request.getFilters().isEmpty()) {
                for (int i = 0; i < request.getFilters().size(); i++) {
                    RouteRequest.FilterRequest filter = request.getFilters().get(i);
                    jdbc.sql("INSERT INTO gw_route_filters (route_id, ord, name, args) VALUES (?, ?, ?, ?)")
                        .params(request.getId(), i, filter.getName(), filter.getArgs())
                        .update();
                }
            }

            // Add metadata
            if (request.getMetadata() != null && !request.getMetadata().isEmpty()) {
                request.getMetadata().forEach((key, value) ->
                    jdbc.sql("INSERT INTO gw_route_metadata (route_id, k, v) VALUES (?, ?, ?)")
                        .params(request.getId(), key, value)
                        .update()
                );
            }

            // Log audit
            auditService.logAction(request.getId(), "CREATE", 1, createdBy, null, request, "Route created");

            // Refresh router - this makes the route available immediately
            log.info("🔄 Refreshing gateway router function to pick up new route: {}", request.getId());
            routerMapping.refresh();
            log.info("✅ Gateway router function refreshed successfully");

            log.info("Route created: {} -> {} by {}", request.getId(), request.getUri(), createdBy);
            return getRouteById(request.getId());
        } catch (Exception e) {
            log.error("Failed to create route: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create route: " + e.getMessage(), e);
        }
    }

    public List<RouteResponse> listAllRoutes() {
        List<RouteResponse> routes = new ArrayList<>();
        try {
            List<Map<String, Object>> results = jdbc.sql(
                    "SELECT id, uri, order_no, enabled, version, created_by, updated_by, created_at, updated_at FROM gw_routes ORDER BY order_no")
                .query((rs, rowNum) -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", rs.getString("id"));
                    map.put("uri", rs.getString("uri"));
                    map.put("order", rs.getObject("order_no"));
                    map.put("enabled", rs.getBoolean("enabled"));
                    map.put("version", rs.getInt("version"));
                    map.put("created_by", rs.getString("created_by"));
                    map.put("updated_by", rs.getString("updated_by"));
                    map.put("created_at", rs.getTimestamp("created_at").toLocalDateTime());
                    map.put("updated_at", rs.getTimestamp("updated_at").toLocalDateTime());
                    return map;
                })
                .list();

            for (Map<String, Object> row : results) {
                String routeId = (String) row.get("id");
                RouteResponse route = buildRouteResponse(routeId, row);
                routes.add(route);
            }
        } catch (Exception e) {
            log.error("Failed to list routes: {}", e.getMessage(), e);
        }
        return routes;
    }

    public RouteResponse getRouteById(String routeId) {
        try {
            Map<String, Object> result = jdbc.sql(
                    "SELECT id, uri, order_no, enabled, version, created_by, updated_by, created_at, updated_at FROM gw_routes WHERE id = ?")
                .param(routeId)
                .query((rs, rowNum) -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", rs.getString("id"));
                    map.put("uri", rs.getString("uri"));
                    map.put("order", rs.getObject("order_no"));
                    map.put("enabled", rs.getBoolean("enabled"));
                    map.put("version", rs.getInt("version"));
                    map.put("created_by", rs.getString("created_by"));
                    map.put("updated_by", rs.getString("updated_by"));
                    map.put("created_at", rs.getTimestamp("created_at").toLocalDateTime());
                    map.put("updated_at", rs.getTimestamp("updated_at").toLocalDateTime());
                    return map;
                })
                .optional()
                .orElse(null);

            if (result == null) {
                return null;
            }

            return buildRouteResponse(routeId, result);
        } catch (Exception e) {
            log.error("Failed to get route {}: {}", routeId, e.getMessage(), e);
            return null;
        }
    }

    @Transactional
    public RouteResponse updateRoute(String routeId, RouteRequest request, String updatedBy) {
        try {
            // Get current version
            Integer currentVersion = jdbc.sql("SELECT version FROM gw_routes WHERE id = ?")
                .param(routeId)
                .query(Integer.class)
                .optional()
                .orElseThrow(() -> new IllegalArgumentException("Route not found: " + routeId));

            // Get old value for audit
            RouteResponse oldValue = getRouteById(routeId);

            // Update route
            int newVersion = currentVersion + 1;
            jdbc.sql("UPDATE gw_routes SET uri = ?, order_no = ?, enabled = ?, version = ?, updated_by = ?, updated_at = ? WHERE id = ?")
                .params(request.getUri(), request.getOrder(), request.getEnabled(), newVersion, updatedBy, LocalDateTime.now(), routeId)
                .update();

            // Delete old predicates, filters, metadata
            jdbc.sql("DELETE FROM gw_route_predicates WHERE route_id = ?").param(routeId).update();
            jdbc.sql("DELETE FROM gw_route_filters WHERE route_id = ?").param(routeId).update();
            jdbc.sql("DELETE FROM gw_route_metadata WHERE route_id = ?").param(routeId).update();

            // Add new predicates
            if (request.getPredicates() != null && !request.getPredicates().isEmpty()) {
                for (int i = 0; i < request.getPredicates().size(); i++) {
                    RouteRequest.PredicateRequest pred = request.getPredicates().get(i);
                    jdbc.sql("INSERT INTO gw_route_predicates (route_id, ord, name, args) VALUES (?, ?, ?, ?)")
                        .params(routeId, i, pred.getName(), pred.getArgs())
                        .update();
                }
            }

            // Add new filters
            if (request.getFilters() != null && !request.getFilters().isEmpty()) {
                for (int i = 0; i < request.getFilters().size(); i++) {
                    RouteRequest.FilterRequest filter = request.getFilters().get(i);
                    jdbc.sql("INSERT INTO gw_route_filters (route_id, ord, name, args) VALUES (?, ?, ?, ?)")
                        .params(routeId, i, filter.getName(), filter.getArgs())
                        .update();
                }
            }

            // Add new metadata
            if (request.getMetadata() != null && !request.getMetadata().isEmpty()) {
                request.getMetadata().forEach((key, value) ->
                    jdbc.sql("INSERT INTO gw_route_metadata (route_id, k, v) VALUES (?, ?, ?)")
                        .params(routeId, key, value)
                        .update()
                );
            }

            // Log audit
            auditService.logAction(routeId, "UPDATE", newVersion, updatedBy, oldValue, request, "Route updated");

            // Refresh router - this makes the updated route configuration available immediately
            log.info("🔄 Refreshing gateway router function to pick up updated route: {}", routeId);
            routerMapping.refresh();
            log.info("✅ Gateway router function refreshed successfully");

            log.info("Route updated: {} -> {} by {} (version {})", routeId, request.getUri(), updatedBy, newVersion);
            return getRouteById(routeId);
        } catch (Exception e) {
            log.error("Failed to update route {}: {}", routeId, e.getMessage(), e);
            throw new RuntimeException("Failed to update route: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteRoute(String routeId, String deletedBy) {
        try {
            // Get current version
            Integer currentVersion = jdbc.sql("SELECT version FROM gw_routes WHERE id = ?")
                .param(routeId)
                .query(Integer.class)
                .optional()
                .orElseThrow(() -> new IllegalArgumentException("Route not found: " + routeId));

            // Get old value for audit
            RouteResponse oldValue = getRouteById(routeId);

            // Log deletion audit BEFORE deleting the route
            // This ensures the audit record is created with valid foreign key reference
            auditService.logAction(routeId, "DELETE", currentVersion + 1, deletedBy, oldValue, null, "Route deleted");

            // Delete audit records and related data (to avoid foreign key constraint violation)
            jdbc.sql("DELETE FROM gw_route_audit WHERE route_id = ?").param(routeId).update();
            jdbc.sql("DELETE FROM gw_route_predicates WHERE route_id = ?").param(routeId).update();
            jdbc.sql("DELETE FROM gw_route_filters WHERE route_id = ?").param(routeId).update();
            jdbc.sql("DELETE FROM gw_route_metadata WHERE route_id = ?").param(routeId).update();

            // Delete route (this will be the last step - no foreign keys reference it anymore)
            jdbc.sql("DELETE FROM gw_routes WHERE id = ?").param(routeId).update();

            // Refresh router - this makes the deletion effective immediately
            log.info("🔄 Refreshing gateway router function to remove deleted route: {}", routeId);
            routerMapping.refresh();
            log.info("✅ Gateway router function refreshed successfully");

            log.info("Route deleted: {} by {}", routeId, deletedBy);
        } catch (Exception e) {
            log.error("Failed to delete route {}: {}", routeId, e.getMessage(), e);
            throw new RuntimeException("Failed to delete route: " + e.getMessage(), e);
        }
    }

    @Transactional
    public RouteResponse enableRoute(String routeId, String updatedBy) {
        try {
            jdbc.sql("UPDATE gw_routes SET enabled = true, updated_by = ?, updated_at = ? WHERE id = ?")
                .params(updatedBy, LocalDateTime.now(), routeId)
                .update();

            auditService.logAction(routeId, "UPDATE", 1, updatedBy, null, null, "Route enabled");
            routerMapping.refresh();

            log.info("Route enabled: {} by {}", routeId, updatedBy);
            return getRouteById(routeId);
        } catch (Exception e) {
            log.error("Failed to enable route {}: {}", routeId, e.getMessage(), e);
            throw new RuntimeException("Failed to enable route: " + e.getMessage(), e);
        }
    }

    @Transactional
    public RouteResponse disableRoute(String routeId, String updatedBy) {
        try {
            jdbc.sql("UPDATE gw_routes SET enabled = false, updated_by = ?, updated_at = ? WHERE id = ?")
                .params(updatedBy, LocalDateTime.now(), routeId)
                .update();

            auditService.logAction(routeId, "UPDATE", 1, updatedBy, null, null, "Route disabled");
            routerMapping.refresh();

            log.info("Route disabled: {} by {}", routeId, updatedBy);
            return getRouteById(routeId);
        } catch (Exception e) {
            log.error("Failed to disable route {}: {}", routeId, e.getMessage(), e);
            throw new RuntimeException("Failed to disable route: " + e.getMessage(), e);
        }
    }

    // Helper method
    private RouteResponse buildRouteResponse(String routeId, Map<String, Object> row) {
        // Get predicates
        List<RouteResponse.PredicateDto> predicates = jdbc.sql(
                "SELECT name, args FROM gw_route_predicates WHERE route_id = ? ORDER BY ord")
            .param(routeId)
            .query((rs, rowNum) -> RouteResponse.PredicateDto.builder()
                .name(rs.getString("name"))
                .args(rs.getString("args"))
                .build())
            .list();

        // Get filters
        List<RouteResponse.FilterDto> filters = jdbc.sql(
                "SELECT name, args FROM gw_route_filters WHERE route_id = ? ORDER BY ord")
            .param(routeId)
            .query((rs, rowNum) -> RouteResponse.FilterDto.builder()
                .name(rs.getString("name"))
                .args(rs.getString("args"))
                .build())
            .list();

        // Get metadata
        Map<String, String> metadata = jdbc.sql(
                "SELECT k, v FROM gw_route_metadata WHERE route_id = ?")
            .param(routeId)
            .query((rs, rowNum) -> Map.entry(rs.getString("k"), rs.getString("v")))
            .list()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // Get health status
        HealthStatus health = healthCheckService.getCachedHealth(routeId);
        RouteResponse.HealthStatusDto healthDto = RouteResponse.HealthStatusDto.builder()
            .status(health.getStatus())
            .responseTime(health.getResponseTime())
            .lastChecked(health.getLastChecked())
            .build();

        return RouteResponse.builder()
            .id(routeId)
            .uri((String) row.get("uri"))
            .order((Integer) row.get("order"))
            .enabled((Boolean) row.get("enabled"))
            .version((Integer) row.get("version"))
            .createdBy((String) row.get("created_by"))
            .createdAt((LocalDateTime) row.get("created_at"))
            .updatedBy((String) row.get("updated_by"))
            .updatedAt((LocalDateTime) row.get("updated_at"))
            .predicates(predicates)
            .filters(filters)
            .metadata(metadata)
            .health(healthDto)
            .build();
    }
}

