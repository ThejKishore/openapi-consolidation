package com.tk.learn.cloudgateway.service;

import com.tk.learn.cloudgateway.domain.HealthStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthCheckService {

    private static final int TIMEOUT_MS = 5000;
    private static final int SLOW_THRESHOLD_MS = 3000;
    private final ConcurrentHashMap<String, HealthStatus> healthCache = new ConcurrentHashMap<>();

    @Cacheable(value = "routeHealth", key = "#routeId", unless = "#result == null")
    public HealthStatus checkRouteHealth(String routeId, String uri) {
        try {
            long startTime = System.currentTimeMillis();

            URI url = new URI(uri);
            HttpURLConnection conn = (HttpURLConnection) url.toURL().openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);

            int responseCode = conn.getResponseCode();
            long responseTime = System.currentTimeMillis() - startTime;

            conn.disconnect();

            if (responseCode >= 200 && responseCode < 300) {
                HealthStatus status = HealthStatus.healthy(routeId, (int) responseTime);
                healthCache.put(routeId, status);
                log.debug("Route {} health: UP ({} ms)", routeId, responseTime);
                return status;
            } else {
                HealthStatus status = HealthStatus.down(routeId, "HTTP " + responseCode);
                healthCache.put(routeId, status);
                log.warn("Route {} health: DOWN (HTTP {})", routeId, responseCode);
                return status;
            }
        } catch (Exception e) {
            HealthStatus status = HealthStatus.down(routeId, e.getMessage());
            healthCache.put(routeId, status);
            log.error("Route {} health check failed: {}", routeId, e.getMessage());
            return status;
        }
    }

    public HealthStatus getCachedHealth(String routeId) {
        return healthCache.getOrDefault(routeId, HealthStatus.unknown(routeId));
    }

    public void clearCache(String routeId) {
        healthCache.remove(routeId);
    }

    public void clearAllCache() {
        healthCache.clear();
    }
}

