package com.tk.learn.cloudgateway.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Utility class for logging gateway route information and URL transformations
 */
@Slf4j
@Component
public class GatewayRoutingLogger {

    /**
     * Log URL transformation details (useful for debugging RewritePath)
     */
    public void logUrlTransformation(String routeId, String originalPath, String transformedPath, String backendUri) {
        log.debug("🔄 URL TRANSFORMATION [{}]", routeId);
        log.debug("   Original Path:    {}", originalPath);
        log.debug("   Transformed Path: {}", transformedPath);
        log.debug("   Backend URI:      {}", backendUri);
        log.debug("   Final URL:        {}{}", backendUri, transformedPath);
    }

    /**
     * Log route matching information
     */
    public void logRouteMatch(String routeId, String path, String backendUri) {
        log.info("✓ Route Matched: [{}]", routeId);
        log.info("  Request Path: {}", path);
        log.info("  Backend URI:  {}", backendUri);
    }

    /**
     * Log route mismatch information
     */
    public void logRouteNotMatched(String path) {
        log.warn("✗ No Route Matched for Path: {}", path);
    }

    /**
     * Log filter execution details
     */
    public void logFilterExecution(String routeId, String filterName, String details) {
        log.debug("🔗 Filter Execution [{}] - {}: {}", routeId, filterName, details);
    }

    /**
     * Log incoming gateway request with full details
     */
    public void logIncomingRequest(String method, String path, String queryParams, String remoteAddr, String userAgent) {
        String fullPath = queryParams != null && !queryParams.isEmpty()
            ? path + "?" + queryParams
            : path;
        log.info("▶ INCOMING REQUEST: {} {} | Remote IP: {} | User-Agent: {}",
            method, fullPath, remoteAddr, userAgent);
    }

    /**
     * Log outgoing request to backend
     */
    public void logBackendCall(String method, String backendUrl, String routeId) {
        log.debug("📤 BACKEND CALL [{}]: {} {}", routeId, method, backendUrl);
    }

    /**
     * Log response received from backend
     */
    public void logBackendResponse(String method, String path, int statusCode, long duration) {
        String statusText = getHttpStatusText(statusCode);
        if (statusCode >= 500) {
            log.error("◀ RESPONSE: {} {} | Status: {} {} | Duration: {}ms",
                method, path, statusCode, statusText, duration);
        } else if (statusCode >= 400) {
            log.warn("◀ RESPONSE: {} {} | Status: {} {} | Duration: {}ms",
                method, path, statusCode, statusText, duration);
        } else {
            log.info("◀ RESPONSE: {} {} | Status: {} {} | Duration: {}ms",
                method, path, statusCode, statusText, duration);
        }
    }

    /**
     * Convert HTTP status code to human-readable text
     */
    private String getHttpStatusText(int status) {
        return switch (status) {
            case 200 -> "OK";
            case 201 -> "Created";
            case 204 -> "No Content";
            case 301 -> "Moved Permanently";
            case 302 -> "Found";
            case 304 -> "Not Modified";
            case 400 -> "Bad Request";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 405 -> "Method Not Allowed";
            case 500 -> "Internal Server Error";
            case 502 -> "Bad Gateway";
            case 503 -> "Service Unavailable";
            default -> "HTTP " + status;
        };
    }
}

