package com.tk.learn.cloudgateway.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RouteAuditRepository {

    private final JdbcClient jdbcClient;

    /**
     * Find all audit records for a specific route, ordered by version (desc)
     */
    public List<RouteAudit> findByRouteIdOrderByVersionDesc(String routeId) {
        return jdbcClient.sql("""
            SELECT audit_id, route_id, action, version, created_by, created_at, old_value, new_value, description
            FROM gw_route_audit
            WHERE route_id = ?
            ORDER BY version DESC
            """)
            .param(routeId)
            .query(this::mapToAudit)
            .list();
    }

    /**
     * Find all audit records for a specific route
     */
    public List<RouteAudit> findByRouteId(String routeId) {
        return jdbcClient.sql("""
            SELECT audit_id, route_id, action, version, created_by, created_at, old_value, new_value, description
            FROM gw_route_audit
            WHERE route_id = ?
            ORDER BY created_at DESC
            """)
            .param(routeId)
            .query(this::mapToAudit)
            .list();
    }

    /**
     * Find all audit records by action type
     */
    public List<RouteAudit> findByAction(String action) {
        return jdbcClient.sql("""
            SELECT audit_id, route_id, action, version, created_by, created_at, old_value, new_value, description
            FROM gw_route_audit
            WHERE action = ?
            ORDER BY created_at DESC
            """)
            .param(action)
            .query(this::mapToAudit)
            .list();
    }

    /**
     * Find all audit records created by a specific user
     */
    public List<RouteAudit> findByCreatedBy(String createdBy) {
        return jdbcClient.sql("""
            SELECT audit_id, route_id, action, version, created_by, created_at, old_value, new_value, description
            FROM gw_route_audit
            WHERE created_by = ?
            ORDER BY created_at DESC
            """)
            .param(createdBy)
            .query(this::mapToAudit)
            .list();
    }

    /**
     * Find all audit records within a date range
     */
    public List<RouteAudit> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return jdbcClient.sql("""
            SELECT audit_id, route_id, action, version, created_by, created_at, old_value, new_value, description
            FROM gw_route_audit
            WHERE created_at BETWEEN ? AND ?
            ORDER BY created_at DESC
            """)
            .params(startDate, endDate)
            .query(this::mapToAudit)
            .list();
    }

    /**
     * Find audit records for a specific route and action, ordered by version (desc)
     */
    public List<RouteAudit> findByRouteIdAndActionOrderByVersionDesc(String routeId, String action) {
        return jdbcClient.sql("""
            SELECT audit_id, route_id, action, version, created_by, created_at, old_value, new_value, description
            FROM gw_route_audit
            WHERE route_id = ? AND action = ?
            ORDER BY version DESC
            """)
            .params(routeId, action)
            .query(this::mapToAudit)
            .list();
    }

    /**
     * Save an audit record
     */
    public RouteAudit save(RouteAudit audit) {
        int auditId = jdbcClient.sql("""
            INSERT INTO gw_route_audit
            (route_id, action, version, created_by, created_at, old_value, new_value, description)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """)
            .params(audit.getRouteId(), audit.getAction(), audit.getVersion(),
                audit.getCreatedBy(), audit.getCreatedAt(), audit.getOldValue(),
                audit.getNewValue(), audit.getDescription())
            .update();

        audit.setAuditId(Integer.toUnsignedLong(auditId));
        return audit;
    }

    /**
     * Find audit record by ID
     */
    public RouteAudit findById(Long auditId) {
        return jdbcClient.sql("""
            SELECT audit_id, route_id, action, version, created_by, created_at, old_value, new_value, description
            FROM gw_route_audit
            WHERE audit_id = ?
            """)
            .param(auditId)
            .query(this::mapToAudit)
            .optional()
            .orElse(null);
    }

    /**
     * Check if audit record exists
     */
    public boolean existsById(Long auditId) {
        Integer count = jdbcClient.sql("""
            SELECT COUNT(*)
            FROM gw_route_audit
            WHERE audit_id = ?
            """)
            .param(auditId)
            .query(Integer.class)
            .single();
        return count != null && count > 0;
    }

    /**
     * Map ResultSet row to RouteAudit entity
     */
    private RouteAudit mapToAudit(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        return RouteAudit.builder()
            .auditId(rs.getLong("audit_id"))
            .routeId(rs.getString("route_id"))
            .action(rs.getString("action"))
            .version(rs.getInt("version"))
            .createdBy(rs.getString("created_by"))
            .createdAt(rs.getTimestamp("created_at") != null ?
                rs.getTimestamp("created_at").toLocalDateTime() : null)
            .oldValue(rs.getString("old_value"))
            .newValue(rs.getString("new_value"))
            .description(rs.getString("description"))
            .build();
    }
}

