package com.tk.learn.cloudgateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Custom logging interceptor to log all requests through the cloud gateway
 * Helps with debugging to see which URLs are being hit and their details
 */
@Slf4j
@Component
public class GatewayLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullUrl = queryString != null ? requestURI + "?" + queryString : requestURI;
        String remoteAddr = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        log.info("▶ INCOMING REQUEST: {} {} | Remote IP: {} | User-Agent: {}",
            method, fullUrl, remoteAddr, userAgent);

        // Store the start time for calculating request duration
        request.setAttribute("requestStartTime", System.currentTimeMillis());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullUrl = queryString != null ? requestURI + "?" + queryString : requestURI;
        int status = response.getStatus();
        String statusText = getStatusText(status);

        // Calculate request duration
        Long startTime = (Long) request.getAttribute("requestStartTime");
        long duration = startTime != null ? System.currentTimeMillis() - startTime : 0;

        // Determine log level based on status code
        if (status >= 500) {
            log.error("◀ RESPONSE: {} {} | Status: {} {} | Duration: {}ms",
                method, fullUrl, status, statusText, duration);
        } else if (status >= 400) {
            log.warn("◀ RESPONSE: {} {} | Status: {} {} | Duration: {}ms",
                method, fullUrl, status, statusText, duration);
        } else {
            log.info("◀ RESPONSE: {} {} | Status: {} {} | Duration: {}ms",
                method, fullUrl, status, statusText, duration);
        }

        if (ex != null) {
            log.error("⚠ REQUEST FAILED: {} {} | Exception: {}", method, fullUrl, ex.getMessage(), ex);
        }
    }

    private String getStatusText(int status) {
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

