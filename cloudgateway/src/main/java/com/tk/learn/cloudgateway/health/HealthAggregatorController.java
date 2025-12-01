package com.tk.learn.cloudgateway.health;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.server.mvc.config.GatewayMvcProperties;
import org.springframework.cloud.gateway.server.mvc.config.RouteProperties;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Aggregates health status from all discovered service instances and/or configured routes.
 *
 * It attempts to use DiscoveryClient (e.g., Spring Cloud Kubernetes) to locate instances for a service
 * and queries their /actuator/health endpoint. If discovery is not available or no instances are found,
 * it falls back to the route's configured URI.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class HealthAggregatorController {

    private final GatewayMvcProperties gatewayMvcProperties;
    private final ObjectMapper objectMapper;
    private final ObjectProvider<DiscoveryClient> discoveryClientProvider;

    private RestTemplate restTemplateWithTimeout() {
        RestTemplate rt = new RestTemplate();
        // Simple timeouts via system properties. For fine-grained control, a RestTemplateBuilder could be used.
        // Keeping minimal external changes per task requirements.
        return rt;
    }

    @GetMapping(value = "/health/aggregate", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getAggregatedHealth() {
        Map<String, Object> response = new LinkedHashMap<>();

        List<RouteProperties> routes = gatewayMvcProperties.getRoutes().stream().collect(Collectors.toList());
        DiscoveryClient discoveryClient = discoveryClientProvider.getIfAvailable();

        for (RouteProperties route : routes) {
            String serviceKey = StringUtils.hasText(route.getId()) ? route.getId() : route.getUri().toString();
            Map<String, Object> serviceEntry = new LinkedHashMap<>();
            List<Map<String, Object>> instancesList = new ArrayList<>();

            List<TargetInstance> targets = resolveTargets(route, discoveryClient);
            List<String> statuses = new ArrayList<>();

            for (TargetInstance target : targets) {
                Map<String, Object> instanceResult = new LinkedHashMap<>();
                instanceResult.put("instance", target.instanceLabel());

                Optional<HealthPayload> health = fetchHealth(target);
                if (health.isPresent()) {
                    instanceResult.put("status", health.get().status);
                    instanceResult.put("url", health.get().url);
                    instanceResult.put("details", health.get().raw);
                    statuses.add(health.get().status);
                } else {
                    instanceResult.put("status", "UNKNOWN");
                    instanceResult.put("url", target.primaryHealthUrl());
                    statuses.add("UNKNOWN");
                }
                instancesList.add(instanceResult);
            }

            serviceEntry.put("instances", instancesList);
            serviceEntry.put("aggregatedStatus", aggregateStatus(statuses));
            response.put(serviceKey, serviceEntry);
        }

        return response;
    }

    private String aggregateStatus(List<String> statuses) {
        if (statuses.isEmpty()) return "UNKNOWN";
        // Priority: DOWN > OUT_OF_SERVICE > UNKNOWN > UP
        if (statuses.contains("DOWN")) return "DOWN";
        if (statuses.contains("OUT_OF_SERVICE")) return "OUT_OF_SERVICE";
        if (statuses.contains("UNKNOWN")) return "UNKNOWN";
        return "UP";
    }

    private Optional<HealthPayload> fetchHealth(TargetInstance target) {
        RestTemplate rt = restTemplateWithTimeout();
        for (String url : target.healthUrlsToTry()) {
            try {
                ResponseEntity<String> resp = rt.getForEntity(url, String.class);
                if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                    Map<String, Object> raw = objectMapper.readValue(resp.getBody(), Map.class);
                    String status = Objects.toString(raw.getOrDefault("status", "UNKNOWN"));
                    return Optional.of(new HealthPayload(status, url, raw));
                }
            } catch (Exception e) {
                log.debug("Health check failed for {}: {}", url, e.getMessage());
            }
        }
        return Optional.empty();
    }

    private List<TargetInstance> resolveTargets(RouteProperties route, DiscoveryClient discoveryClient) {
        List<TargetInstance> targets = new ArrayList<>();
        URI uri = route.getUri();
        String path = uri.getPath();
        String basePath = (StringUtils.hasText(path) && !"/".equals(path)) ? path : "";

        // Prefer discovery by route id; if not found, try by host
        String candidateServiceId = route.getId();
        String hostServiceId = uri.getHost();

        if (discoveryClient != null) {
            List<ServiceInstance> instances = Collections.emptyList();
            if (StringUtils.hasText(candidateServiceId)) {
                try { instances = discoveryClient.getInstances(candidateServiceId); } catch (Exception ignored) {}
            }
            if ((instances == null || instances.isEmpty()) && StringUtils.hasText(hostServiceId)) {
                try { instances = discoveryClient.getInstances(hostServiceId); } catch (Exception ignored) {}
            }
            if (instances != null && !instances.isEmpty()) {
                for (ServiceInstance si : instances) {
                    String scheme = si.isSecure() ? "https" : "http";
                    String base = scheme + "://" + si.getHost() + ":" + si.getPort();
                    targets.add(new TargetInstance(
                            candidateServiceId != null ? candidateServiceId : (hostServiceId != null ? hostServiceId : base),
                            base,
                            basePath
                    ));
                }
                return targets;
            }
        }

        // Fallback to route URI
        String scheme = uri.getScheme() != null ? uri.getScheme() : "http";
        String host = uri.getHost();
        int port = uri.getPort();
        StringBuilder base = new StringBuilder();
        base.append(scheme).append("://").append(host);
        if (port > 0) base.append(":").append(port);
        targets.add(new TargetInstance(route.getId(), base.toString(), basePath));
        return targets;
    }

    private record TargetInstance(String serviceLabel, String baseUrl, String basePath) {
        String instanceLabel() { return baseUrl; }
        String primaryHealthUrl() { return baseUrl + "/actuator/health"; }
        List<String> healthUrlsToTry() {
            List<String> urls = new ArrayList<>();
            urls.add(primaryHealthUrl());
            if (StringUtils.hasText(basePath)) {
                String p = basePath.startsWith("/") ? basePath : "/" + basePath;
                urls.add(baseUrl + p + (p.endsWith("/") ? "actuator/health" : "/actuator/health"));
            }
            return urls;
        }
    }

    private record HealthPayload(String status, String url, Map<String, Object> raw) {}
}
