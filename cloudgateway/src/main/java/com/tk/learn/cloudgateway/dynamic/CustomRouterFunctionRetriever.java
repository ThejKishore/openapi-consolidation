package com.tk.learn.cloudgateway.dynamic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tk.learn.cloudgateway.common.dynamic.DbRoute;
import com.tk.learn.cloudgateway.common.dynamic.FilterRow;
import com.tk.learn.cloudgateway.common.dynamic.PredicateRow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;

import java.net.URI;
import java.util.*;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.rewritePath;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.stripPrefix;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

/**
 * Retrieves custom RouterFunction built from database-backed route configuration.
 * Uses Spring JdbcClient to read routes with JSON-based predicates, filters, and metadata.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomRouterFunctionRetriever {

    private final JdbcClient jdbc;
    private final ObjectMapper objectMapper;

    public RouterFunction retrieve() {
        try {
            List<DbRoute> routes = loadRoutesFromDb();
            if (routes.isEmpty()) {
                log.warn("⚠️ No enabled DB routes found, returning empty but valid router function");
                return route().build();
            }

            log.info("📋 Building DB RouterFunction with {} enabled route(s):", routes.size());

            var builder = route();
            routes.stream()
                    .filter(r -> r.enabled)
                    .sorted(Comparator.comparing(r -> Optional.ofNullable(r.order).orElse(0)))
                    .forEach(r -> {
                        log.info("  ✓ Route: {} [enabled] -> {}", r.id, r.uri);
                        Optional<String> pathPredicate = r.predicates.stream()
                                .filter(p -> "Path".equalsIgnoreCase(p.name()))
                                .map(PredicateRow::args)
                                .filter(StringUtils::hasText).findFirst();
                        URI uri = URI.create(r.uri);
                        if (!pathPredicate.isPresent()) {
                            // If no Path predicate provided, map everything
                            log.warn("    ⚠️ No Path predicate provided, mapping to /**");
                            builder.route(path("/**"), http(uri));
                        } else {
                            builder.route(path(pathPredicate.get()), http(uri));
                        }

                        //Add StripPrefix filter
                        r.filters.stream()
                                .filter(f -> "StripPrefix".equalsIgnoreCase(f.name()))
                                .findFirst().ifPresent(f-> setStripPrefixFilter(f, builder));

                        r.filters.stream()
                                .filter(f -> "RewritePath".equalsIgnoreCase(f.name()))
                                .findFirst().ifPresent(f-> setRewriteFilter(f, builder));

                    });
            RouterFunction<?> rf = builder.build();
            log.info("✅ DB RouterFunction built successfully with {} routes", routes.size());
            return rf;
        } catch (Exception e) {
            log.error("❌ Failed to build DB routes: {}", e.getMessage(), e);
            log.info("Returning empty but valid router function due to error");
            return route().build();
        }
    }

    private static void setStripPrefixFilter(FilterRow f, RouterFunctions.Builder builder) {
        int prefixValue = Integer.parseInt(f.args());
        builder.before(stripPrefix(prefixValue));
    }

    private static void setRewriteFilter(FilterRow f, RouterFunctions.Builder builder) {
        String[] prefixValue = f.args().split(",");
        if( prefixValue.length > 1 ) {
            builder.before(rewritePath(prefixValue[0], prefixValue[1].trim()));
        }
    }

    /**
     * Load routes from database using the simplified schema with JSON columns
     */
    private List<DbRoute> loadRoutesFromDb() {
        List<DbRoute> routes = jdbc.sql("""
            SELECT id, uri, order_no, enabled, version, predicates, filters, metadata
            FROM gw_routes
            WHERE enabled = true
            ORDER BY order_no
            """)
                .query((rs, row) -> {
                    DbRoute route = new DbRoute();
                    route.id = rs.getString("id");
                    route.uri = rs.getString("uri");
                    route.order = rs.getObject("order_no") != null ? rs.getInt("order_no") : null;
                    route.enabled = rs.getBoolean("enabled");
                    route.version = rs.getInt("version");

                    // Deserialize JSON columns
                    String predicatesJson = rs.getString("predicates");
                    String filtersJson = rs.getString("filters");
                    String metadataJson = rs.getString("metadata");

                    // Parse predicates JSON
                    route.predicates = deserializePredicates(predicatesJson);

                    // Parse filters JSON
                    route.filters = deserializeFilters(filtersJson);

                    // Parse metadata JSON
                    route.metadata = deserializeMetadata(metadataJson);

                    return route;
                })
                .list();

        return routes;
    }

    /**
     * Deserialize predicates from JSON string
     */
    private List<PredicateRow> deserializePredicates(String json) {
        if (json == null || json.isEmpty() || "[]".equals(json)) {
            return new ArrayList<>();
        }
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, String>> predicates = objectMapper.readValue(json, List.class);
            return predicates.stream()
                    .map(p -> new PredicateRow(p.get("name"), p.get("args")))
                    .toList();
        } catch (Exception e) {
            log.error("Failed to deserialize predicates: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Deserialize filters from JSON string
     */
    private List<FilterRow> deserializeFilters(String json) {
        if (json == null || json.isEmpty() || "[]".equals(json)) {
            return new ArrayList<>();
        }
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, String>> filters = objectMapper.readValue(json, List.class);
            return filters.stream()
                    .map(f -> new FilterRow(f.get("name"), f.get("args")))
                    .toList();
        } catch (Exception e) {
            log.error("Failed to deserialize filters: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Deserialize metadata from JSON string
     */
    @SuppressWarnings("unchecked")
    private Map<String, String> deserializeMetadata(String json) {
        if (json == null || json.isEmpty() || "{}".equals(json)) {
            return new LinkedHashMap<>();
        }
        try {
            Map<String, String> metadata = objectMapper.readValue(json, Map.class);
            return new LinkedHashMap<>(metadata);
        } catch (Exception e) {
            log.error("Failed to deserialize metadata: {}", e.getMessage());
            return new LinkedHashMap<>();
        }
    }
}
