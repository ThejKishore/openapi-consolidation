package com.tk.learn.cloudgateway.route;

import com.tk.learn.cloudgateway.common.dynamic.DbRoute;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RouteRepository {

    private final JdbcClient jdbcClient;

    /**
     * Find all routes by enabled status
     */
    public List<DbRoute> findByEnabled(Boolean enabled) {
        return jdbcClient.sql("""
            SELECT id, uri, order_no, enabled, version, predicates, filters, metadata
            FROM gw_routes
            WHERE enabled = ?
            """)
            .param(enabled)
            .query(this::mapToRoute)
            .list();
    }

    /**
     * Find a route by ID
     */
    public DbRoute findById(String id) {
        return jdbcClient.sql("""
            SELECT id, uri, order_no, enabled, version, predicates, filters, metadata
            FROM gw_routes
            WHERE id = ?
            """)
            .param(id)
            .query(this::mapToRoute)
            .optional()
            .orElse(null);
    }

    /**
     * Check if a route exists by ID
     */
    public boolean existsById(String id) {
        Integer count = jdbcClient.sql("""
            SELECT COUNT(*)
            FROM gw_routes
            WHERE id = ?
            """)
            .param(id)
            .query(Integer.class)
            .single();
        return count != null && count > 0;
    }

    /**
     * Find all routes
     */
    public List<DbRoute> findAll() {
        return jdbcClient.sql("""
            SELECT id, uri, order_no, enabled, version, predicates, filters, metadata
            FROM gw_routes
            ORDER BY order_no
            """)
            .query(this::mapToRoute)
            .list();
    }

    /**
     * Save a route (insert or update)
     */
    public void save(DbRoute route) {
        if (existsById(route.id)) {
            jdbcClient.sql("""
                UPDATE gw_routes
                SET uri = ?, order_no = ?, enabled = ?, version = ?, predicates = ?, filters = ?, metadata = ?
                WHERE id = ?
                """)
                .params(route.uri, route.order, route.enabled, route.version,
                    route.predicatesJson, route.filtersJson, route.metadataJson, route.id)
                .update();
        } else {
            jdbcClient.sql("""
                INSERT INTO gw_routes
                (id, uri, order_no, enabled, version, predicates, filters, metadata)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """)
                .params(route.id, route.uri, route.order, route.enabled, route.version,
                    route.predicatesJson, route.filtersJson, route.metadataJson)
                .update();
        }
    }

    /**
     * Delete a route by ID
     */
    public void deleteById(String id) {
        jdbcClient.sql("""
            DELETE FROM gw_routes
            WHERE id = ?
            """)
            .param(id)
            .update();
    }

    /**
     * Map ResultSet row to DbRoute entity
     */
    private DbRoute mapToRoute(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        DbRoute route = new DbRoute();
        route.id = rs.getString("id");
        route.uri = rs.getString("uri");
        route.order = rs.getObject("order_no") != null ? rs.getInt("order_no") : null;
        route.enabled = rs.getBoolean("enabled");
        route.version = rs.getInt("version");
        route.predicatesJson = rs.getString("predicates");
        route.filtersJson = rs.getString("filters");
        route.metadataJson = rs.getString("metadata");
        return route;
    }
}

