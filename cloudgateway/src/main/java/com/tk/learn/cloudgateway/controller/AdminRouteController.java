package com.tk.learn.cloudgateway.controller;

import com.tk.learn.cloudgateway.domain.RouteRequest;
import com.tk.learn.cloudgateway.domain.RouteResponse;
import com.tk.learn.cloudgateway.service.HealthCheckService;
import com.tk.learn.cloudgateway.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.lang.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin/routes")
@RequiredArgsConstructor
@Tag(name = "Route Management", description = "Admin APIs for managing gateway routes")
@SecurityRequirement(name = "bearer-jwt")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRouteController {

    private final RouteService routeService;
    private final HealthCheckService healthCheckService;

    @GetMapping
    @Operation(summary = "List all routes", description = "Retrieve all configured routes")
    public ResponseEntity<List<RouteResponse>> listAllRoutes() {
        try {
            List<RouteResponse> routes = routeService.listAllRoutes();
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            log.error("Failed to list routes: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get route by ID", description = "Retrieve details of a specific route")
    public ResponseEntity<RouteResponse> getRouteById(@PathVariable String id) {
        try {
            RouteResponse route = routeService.getRouteById(id);
            if (route == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(route);
        } catch (Exception e) {
            log.error("Failed to get route {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @Operation(summary = "Create new route", description = "Create a new gateway route")
    public ResponseEntity<RouteResponse> createRoute(
            @Valid @RequestBody RouteRequest request,
            @Nullable Authentication authentication) {
        try {
            log.info("📝 Creating new route: {} -> {}", request.getId(), request.getUri());
            String createdBy = authentication != null ? authentication.getName() : "system";
            RouteResponse route = routeService.createRoute(request, createdBy);
            log.info("✅ Route created successfully: {} (enabled: {})", route.getId(), route.getEnabled());
            log.info("🔄 Gateway routes have been refreshed to include the new route");
            return ResponseEntity.status(HttpStatus.CREATED).body(route);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid route request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Failed to create route: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update route", description = "Update an existing gateway route")
    public ResponseEntity<RouteResponse> updateRoute(
            @PathVariable String id,
            @Valid @RequestBody RouteRequest request,
            @Nullable Authentication authentication) {
        try {
            log.info("🔧 Updating route: {} -> {}", id, request.getUri());
            String updatedBy = authentication != null ? authentication.getName() : "system";
            RouteResponse route = routeService.updateRoute(id, request, updatedBy);
            log.info("✅ Route updated successfully: {} (enabled: {})", route.getId(), route.getEnabled());
            log.info("🔄 Gateway routes have been refreshed with the updated route configuration");
            return ResponseEntity.ok(route);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid route update request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Failed to update route {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete route", description = "Delete a gateway route")
    public ResponseEntity<Void> deleteRoute(
            @PathVariable String id,
            @Nullable Authentication authentication) {
        try {
            log.info("🗑️ Deleting route: {}", id);
            String deletedBy = authentication != null ? authentication.getName() : "system";
            routeService.deleteRoute(id, deletedBy);
            log.info("✅ Route deleted successfully: {}", id);
            log.info("🔄 Gateway routes have been refreshed - route {} is no longer available", id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("Route not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Failed to delete route {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/enable")
    @Operation(summary = "Enable route", description = "Enable a disabled route")
    public ResponseEntity<RouteResponse> enableRoute(
            @PathVariable String id,
            @Nullable Authentication authentication) {
        try {
            String updatedBy = authentication != null ? authentication.getName() : "system";
            RouteResponse route = routeService.enableRoute(id, updatedBy);
            return ResponseEntity.ok(route);
        } catch (Exception e) {
            log.error("Failed to enable route {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/disable")
    @Operation(summary = "Disable route", description = "Disable a route without deleting it")
    public ResponseEntity<RouteResponse> disableRoute(
            @PathVariable String id,
            @Nullable Authentication authentication) {
        try {
            String updatedBy = authentication != null ? authentication.getName() : "system";
            RouteResponse route = routeService.disableRoute(id, updatedBy);
            return ResponseEntity.ok(route);
        } catch (Exception e) {
            log.error("Failed to disable route {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/health/status")
    @Operation(summary = "Get health status for all routes", description = "Check health of all gateway routes")
    public ResponseEntity<List<RouteResponse>> getAllRoutesWithHealth() {
        try {
            List<RouteResponse> routes = routeService.listAllRoutes();

            // Check health for each route
            for (RouteResponse route : routes) {
                healthCheckService.checkRouteHealth(route.getId(), route.getUri());
            }

            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            log.error("Failed to get routes health: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/health")
    @Operation(summary = "Get health status for specific route", description = "Check health of a specific gateway route")
    public ResponseEntity<Map<String, Object>> getRouteHealth(@PathVariable String id) {
        try {
            RouteResponse route = routeService.getRouteById(id);
            if (route == null) {
                return ResponseEntity.notFound().build();
            }

            healthCheckService.checkRouteHealth(id, route.getUri());

            Map<String, Object> response = new HashMap<>();
            response.put("routeId", id);
            response.put("health", route.getHealth());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get route health {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

