package com.tk.learn.cloudgateway.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tk.learn.cloudgateway.domain.AuditResponse;
import com.tk.learn.cloudgateway.domain.RouteAudit;
import com.tk.learn.cloudgateway.repository.RouteAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final RouteAuditRepository auditRepository;
    private final ObjectMapper objectMapper;

    public void logAction(String routeId, String action, Integer version, String createdBy,
                         Object oldValue, Object newValue, String description) {
        try {
            String oldValueJson = oldValue != null ? objectMapper.writeValueAsString(oldValue) : null;
            String newValueJson = newValue != null ? objectMapper.writeValueAsString(newValue) : null;

            RouteAudit audit = RouteAudit.builder()
                .routeId(routeId)
                .action(action)
                .version(version)
                .createdBy(createdBy)
                .oldValue(oldValueJson)
                .newValue(newValueJson)
                .description(description)
                .build();

            auditRepository.save(audit);
            log.info("Audit logged: {} action for route {}, version {}", action, routeId, version);
        } catch (Exception e) {
            log.error("Failed to log audit: {}", e.getMessage(), e);
        }
    }

    public List<AuditResponse> getAuditHistory(String routeId) {
        return auditRepository.findByRouteIdOrderByVersionDesc(routeId)
            .stream()
            .map(AuditResponse::fromEntity)
            .collect(Collectors.toList());
    }

    public List<AuditResponse> getAuditsByUser(String createdBy) {
        return auditRepository.findByCreatedBy(createdBy)
            .stream()
            .map(AuditResponse::fromEntity)
            .collect(Collectors.toList());
    }

    public List<AuditResponse> getAuditsByAction(String action) {
        return auditRepository.findByAction(action)
            .stream()
            .map(AuditResponse::fromEntity)
            .collect(Collectors.toList());
    }

    public List<AuditResponse> getAuditsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return auditRepository.findByCreatedAtBetween(startDate, endDate)
            .stream()
            .map(AuditResponse::fromEntity)
            .collect(Collectors.toList());
    }

    public List<AuditResponse> getVersionHistory(String routeId) {
        return getAuditHistory(routeId);
    }
}

