package com.tk.learn.cloudgateway.dynamic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.function.RouterFunction;

import java.net.URI;
import java.util.*;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

/**
 * Retrieves custom RouterFunction built from database-backed route configuration.
 * Uses Spring JdbcClient to read routes, predicates, and filters.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomRouterFunctionRetriever {

    private final JdbcClient jdbc;

    public RouterFunction<?> retrieve() {
        try {
            List<DbRouteModels.DbRoute> routes = loadRoutesFromDb();
            if (routes.isEmpty()) {
                log.warn("⚠️ No enabled DB routes found, returning empty but valid router function");
                return route().build();  // Return empty valid RouterFunction instead of null
            }

            log.info("📋 Building DB RouterFunction with {} enabled route(s):", routes.size());

            var builder = route();
            routes.stream()
                    .filter(r -> r.enabled)
                    .sorted(Comparator.comparing(r -> Optional.ofNullable(r.order).orElse(0)))
                    .forEach(r -> {
                        log.info("  ✓ Route: {} [enabled] -> {}", r.id, r.uri);
                        // Currently support Path predicate(s) and forward to URI.
                        List<String> paths = r.predicates.stream()
                                .filter(p -> "Path".equalsIgnoreCase(p.name()))
                                .map(DbRouteModels.PredicateRow::args)
                                .filter(StringUtils::hasText)
                                .toList();

                        if (!paths.isEmpty()) {
                            for (String p : paths) {
                                log.info("    - Predicate: Path {}", p);
                            }
                        }

                        // Log filters (currently not applied - use YAML config for filters)
                        if (r.filters != null && !r.filters.isEmpty()) {
                            for (DbRouteModels.FilterRow filter : r.filters) {
                                log.warn("    ⚠️ Filter {} not yet supported in DB routes - use YAML config instead", filter.name());
                            }
                        }

                        URI uri = URI.create(r.uri);
                        if (paths.isEmpty()) {
                            // If no Path predicate provided, map everything
                            log.warn("    ⚠️ No Path predicate provided, mapping to /**");
                            builder.route(path("/**"), http(uri));
                        } else {
                            for (String p : paths) {
                                builder.route(path(p), http(uri));
                            }
                        }
                    });
            RouterFunction<?> rf = builder.build();
            log.info("✅ DB RouterFunction built successfully with {} routes", routes.size());
            return rf;
        } catch (Exception e) {
            log.error("❌ Failed to build DB routes: {}", e.getMessage(), e);
            log.info("Returning empty but valid router function due to error");
            return route().build();  // Return empty valid RouterFunction instead of null on error
        }
    }

    private List<DbRouteModels.DbRoute> loadRoutesFromDb() {
        // routes
        Map<String, DbRouteModels.DbRoute> routes = new LinkedHashMap<>();
        jdbc.sql("select id, uri, order_no, enabled from gw_routes where enabled = true")
                .query((rs, row) -> {
                    var r = new DbRouteModels.DbRoute();
                    r.id = rs.getString("id");
                    r.uri = rs.getString("uri");
                    r.order = rs.getObject("order_no") != null ? rs.getInt("order_no") : null;
                    r.enabled = rs.getBoolean("enabled");
                    return r;
                })
                .list()
                .forEach(r -> routes.put(r.id, r));

        if (routes.isEmpty()) return List.of();

        // predicates
        jdbc.sql("select route_id, name, args from gw_route_predicates where route_id in (:ids) order by ord asc")
                .param("ids", routes.keySet())
                .query((rs, row) -> new Object[]{rs.getString("route_id"), rs.getString("name"), rs.getString("args")} )
                .list()
                .forEach(arr -> routes.get((String) arr[0]).predicates.add(new DbRouteModels.PredicateRow((String) arr[1], (String) arr[2])));

        // filters (stored, but not yet applied in builder - kept for future extension)
        jdbc.sql("select route_id, name, args from gw_route_filters where route_id in (:ids) order by ord asc")
                .param("ids", routes.keySet())
                .query((rs, row) -> new Object[]{rs.getString("route_id"), rs.getString("name"), rs.getString("args")} )
                .list()
                .forEach(arr -> routes.get((String) arr[0]).filters.add(new DbRouteModels.FilterRow((String) arr[1], (String) arr[2])));

        // metadata
        jdbc.sql("select route_id, k, v from gw_route_metadata where route_id in (:ids)")
                .param("ids", routes.keySet())
                .query((rs, row) -> new Object[]{rs.getString("route_id"), rs.getString("k"), rs.getString("v")} )
                .list()
                .forEach(arr -> routes.get((String) arr[0]).metadata.put((String) arr[1], (String) arr[2]));

        return new ArrayList<>(routes.values());
    }
}
