package com.tk.learn.cloudgateway.route;

import com.tk.learn.cloudgateway.audit.AuditService;
import com.tk.learn.cloudgateway.dynamic.CustomRouterFunctionMapping;
import com.tk.learn.cloudgateway.health.HealthCheckService;
import com.tk.learn.cloudgateway.health.HealthStatus;
import com.tk.learn.cloudgateway.util.JsonConverterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteService {

    private final JdbcClient jdbc;
    private final RouteRepository routeRepository;
    private final AuditService auditService;
    private final HealthCheckService healthCheckService;
    private final CustomRouterFunctionMapping routerMapping;
    private final JsonConverterUtil jsonConverter;

    @Transactional
    public RouteResponse createRoute(RouteRequest request, String createdBy) {
        try {
            // Check if route already exists
            if (routeRepository.existsById(request.getId())) {
                throw new IllegalArgumentException("Route with ID " + request.getId() + " already exists");
            }

            // Serialize predicates, filters, and metadata to JSON
            String predicatesJson = jsonConverter.serializePredicates(request.getPredicates());
            String filtersJson = jsonConverter.serializeFilters(request.getFilters());
            String metadataJson = jsonConverter.serializeMetadata(request.getMetadata());

            // Insert into single table with JSON columns
            jdbc.sql("INSERT INTO gw_routes (id, uri, order_no, enabled, version, predicates, filters, metadata, created_by, updated_by, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
                .params(request.getId(), request.getUri(), request.getOrder(), request.getEnabled(), 1,
                    predicatesJson, filtersJson, metadataJson, createdBy, createdBy,
                    LocalDateTime.now(), LocalDateTime.now())
                .update();

            // Log audit
            auditService.logAction(request.getId(), "CREATE", 1, createdBy, null, request, "Route created");

            // Refresh router
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
                    "SELECT id, uri, order_no, enabled, version, predicates, filters, metadata, created_by, updated_by, created_at, updated_at FROM gw_routes ORDER BY order_no")
                .query((rs, rowNum) -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", rs.getString("id"));
                    map.put("uri", rs.getString("uri"));
                    map.put("order", rs.getObject("order_no"));
                    map.put("enabled", rs.getBoolean("enabled"));
                    map.put("version", rs.getInt("version"));
                    map.put("predicates", rs.getString("predicates"));
                    map.put("filters", rs.getString("filters"));
                    map.put("metadata", rs.getString("metadata"));
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
                    "SELECT id, uri, order_no, enabled, version, predicates, filters, metadata, created_by, updated_by, created_at, updated_at FROM gw_routes WHERE id = ?")
                .param(routeId)
                .query((rs, rowNum) -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", rs.getString("id"));
                    map.put("uri", rs.getString("uri"));
                    map.put("order", rs.getObject("order_no"));
                    map.put("enabled", rs.getBoolean("enabled"));
                    map.put("version", rs.getInt("version"));
                    map.put("predicates", rs.getString("predicates"));
                    map.put("filters", rs.getString("filters"));
                    map.put("metadata", rs.getString("metadata"));
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

            // Serialize predicates, filters, and metadata to JSON
            String predicatesJson = jsonConverter.serializePredicates(request.getPredicates());
            String filtersJson = jsonConverter.serializeFilters(request.getFilters());
            String metadataJson = jsonConverter.serializeMetadata(request.getMetadata());

            // Update route with new JSON columns
            int newVersion = currentVersion + 1;
            jdbc.sql("UPDATE gw_routes SET uri = ?, order_no = ?, enabled = ?, version = ?, predicates = ?, filters = ?, metadata = ?, updated_by = ?, updated_at = ? WHERE id = ?")
                .params(request.getUri(), request.getOrder(), request.getEnabled(), newVersion,
                    predicatesJson, filtersJson, metadataJson, updatedBy, LocalDateTime.now(), routeId)
                .update();

            // Log audit
            auditService.logAction(routeId, "UPDATE", newVersion, updatedBy, oldValue, request, "Route updated");

            // Refresh router
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
            auditService.logAction(routeId, "DELETE", currentVersion + 1, deletedBy, oldValue, null, "Route deleted");

            // Delete audit records (no FK constraint, but cleanup for consistency)
            jdbc.sql("DELETE FROM gw_route_audit WHERE route_id = ?").param(routeId).update();

            // Delete route (single table now, no need to delete related records)
            jdbc.sql("DELETE FROM gw_routes WHERE id = ?").param(routeId).update();

            // Refresh router
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

    // Helper method - simplified to work with single row result
    private RouteResponse buildRouteResponse(String routeId, Map<String, Object> row) {
        // Deserialize JSON columns directly from single row
        List<RouteResponse.PredicateDto> predicates = jsonConverter.deserializePredicates((String) row.get("predicates"));
        List<RouteResponse.FilterDto> filters = jsonConverter.deserializeFilters((String) row.get("filters"));
        Map<String, String> metadata = jsonConverter.deserializeMetadata((String) row.get("metadata"));

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

