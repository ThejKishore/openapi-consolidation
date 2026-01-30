package com.tk.learn.cloudgateway.audit;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Audit Management", description = "Admin APIs for viewing route audit history")
@SecurityRequirement(name = "bearer-jwt")
public class AdminAuditController {

    private final AuditService auditService;

    @GetMapping("/api/audit/logs")
    @Operation(summary = "Get all audit logs", description = "View all audit logs across all routes")
    public ResponseEntity<List<AuditResponse>> getAllAuditLogs() {
        try {
            List<AuditResponse> audits = auditService.getAllAuditLogs();
            return ResponseEntity.ok(audits);
        } catch (Exception e) {
            log.error("Failed to get all audit logs: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/api/admin/audit/routes/{routeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audit history for route", description = "View all changes made to a specific route")
    public ResponseEntity<List<AuditResponse>> getRouteAuditHistory(@PathVariable String routeId) {
        try {
            List<AuditResponse> audits = auditService.getAuditHistory(routeId);
            return ResponseEntity.ok(audits);
        } catch (Exception e) {
            log.error("Failed to get audit history for route {}: {}", routeId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/api/admin/audit/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audits by user", description = "View all changes made by a specific user")
    public ResponseEntity<List<AuditResponse>> getAuditsByUser(@PathVariable String userId) {
        try {
            List<AuditResponse> audits = auditService.getAuditsByUser(userId);
            return ResponseEntity.ok(audits);
        } catch (Exception e) {
            log.error("Failed to get audits by user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/api/admin/audit/actions/{action}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audits by action", description = "View all changes of a specific action type (CREATE, UPDATE, DELETE)")
    public ResponseEntity<List<AuditResponse>> getAuditsByAction(@PathVariable String action) {
        try {
            List<AuditResponse> audits = auditService.getAuditsByAction(action);
            return ResponseEntity.ok(audits);
        } catch (Exception e) {
            log.error("Failed to get audits by action {}: {}", action, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/api/admin/audit/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audits by date range", description = "View changes within a specific date range")
    public ResponseEntity<List<AuditResponse>> getAuditsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<AuditResponse> audits = auditService.getAuditsByDateRange(startDate, endDate);
            return ResponseEntity.ok(audits);
        } catch (Exception e) {
            log.error("Failed to get audits by date range: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/api/admin/audit/versions/{routeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get version history", description = "View all versions of a route with their changes")
    public ResponseEntity<List<AuditResponse>> getVersionHistory(@PathVariable String routeId) {
        try {
            List<AuditResponse> versions = auditService.getVersionHistory(routeId);
            return ResponseEntity.ok(versions);
        } catch (Exception e) {
            log.error("Failed to get version history for route {}: {}", routeId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
