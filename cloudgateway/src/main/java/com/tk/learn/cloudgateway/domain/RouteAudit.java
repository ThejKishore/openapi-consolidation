package com.tk.learn.cloudgateway.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "gw_route_audit", indexes = {
    @Index(name = "idx_gw_audit_route", columnList = "route_id"),
    @Index(name = "idx_gw_audit_created_at", columnList = "created_at"),
    @Index(name = "idx_gw_audit_created_by", columnList = "created_by")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditId;

    @Column(nullable = false)
    private String routeId;

    @Column(nullable = false, length = 20)
    private String action; // CREATE, UPDATE, DELETE

    @Column(nullable = false)
    private Integer version;

    @Column(name = "created_by", length = 255)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Lob
    @Column(name = "old_value")
    private String oldValue; // JSON format

    @Lob
    @Column(name = "new_value")
    private String newValue; // JSON format

    @Column(columnDefinition = "TEXT")
    private String description;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}

