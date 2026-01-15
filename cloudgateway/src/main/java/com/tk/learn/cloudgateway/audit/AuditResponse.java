package com.tk.learn.cloudgateway.audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditResponse {
    private Long auditId;
    private String routeId;
    private String action;
    private Integer version;
    private String createdBy;
    private LocalDateTime createdAt;
    private String oldValue;
    private String newValue;
    private String description;

    public static AuditResponse fromEntity(RouteAudit audit) {
        return AuditResponse.builder()
            .auditId(audit.getAuditId())
            .routeId(audit.getRouteId())
            .action(audit.getAction())
            .version(audit.getVersion())
            .createdBy(audit.getCreatedBy())
            .createdAt(audit.getCreatedAt())
            .oldValue(audit.getOldValue())
            .newValue(audit.getNewValue())
            .description(audit.getDescription())
            .build();
    }
}

