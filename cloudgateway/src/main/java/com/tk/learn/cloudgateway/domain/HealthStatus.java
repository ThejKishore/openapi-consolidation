package com.tk.learn.cloudgateway.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthStatus {
    private String routeId;
    private String status; // UP, DOWN, SLOW, UNKNOWN
    private Integer responseTime; // in milliseconds
    private LocalDateTime lastChecked;
    private String errorMessage;

    public static HealthStatus unknown(String routeId) {
        return HealthStatus.builder()
            .routeId(routeId)
            .status("UNKNOWN")
            .responseTime(0)
            .lastChecked(LocalDateTime.now())
            .build();
    }

    public static HealthStatus healthy(String routeId, int responseTime) {
        String status = responseTime > 5000 ? "SLOW" : "UP";
        return HealthStatus.builder()
            .routeId(routeId)
            .status(status)
            .responseTime(responseTime)
            .lastChecked(LocalDateTime.now())
            .build();
    }

    public static HealthStatus down(String routeId, String errorMessage) {
        return HealthStatus.builder()
            .routeId(routeId)
            .status("DOWN")
            .responseTime(0)
            .lastChecked(LocalDateTime.now())
            .errorMessage(errorMessage)
            .build();
    }
}

