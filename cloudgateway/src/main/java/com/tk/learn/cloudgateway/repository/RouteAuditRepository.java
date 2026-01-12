package com.tk.learn.cloudgateway.repository;

import com.tk.learn.cloudgateway.domain.RouteAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RouteAuditRepository extends JpaRepository<RouteAudit, Long> {

    List<RouteAudit> findByRouteId(String routeId);

    List<RouteAudit> findByAction(String action);

    List<RouteAudit> findByCreatedBy(String createdBy);

    List<RouteAudit> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<RouteAudit> findByRouteIdAndActionOrderByVersionDesc(String routeId, String action);

    List<RouteAudit> findByRouteIdOrderByVersionDesc(String routeId);
}

