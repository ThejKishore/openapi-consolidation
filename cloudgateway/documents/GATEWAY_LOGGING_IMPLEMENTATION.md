# Gateway URL Logging Implementation Summary

## Overview

Complete logging infrastructure has been added to the Cloud Gateway to track and debug all URLs being hit through the gateway. This includes request/response tracking, route matching details, and performance monitoring.

## Components Added

### 1. **GatewayLoggingInterceptor** ✅
**File**: `src/main/java/com/tk/learn/cloudgateway/filter/GatewayLoggingInterceptor.java`

Intercepts all HTTP requests and responses:
- Logs incoming requests with: Method, URL, Remote IP, User-Agent
- Logs outgoing responses with: Status code, HTTP status text, Duration (ms)
- Logs errors with full exception details
- Auto-calculates request processing time
- Uses emoji symbols for easy visual scanning

### 2. **WebMvcConfig** ✅
**File**: `src/main/java/com/tk/learn/cloudgateway/config/WebMvcConfig.java`

Configuration class:
- Registers the GatewayLoggingInterceptor
- Excludes static resources from logging (swagger, h2-console)
- Reduces noise in logs

### 3. **GatewayRoutingLogger** ✅
**File**: `src/main/java/com/tk/learn/cloudgateway/util/GatewayRoutingLogger.java`

Utility component for detailed routing logs:
- URL transformation tracking
- Route matching/mismatching logs
- Filter execution details
- Backend call logging
- HTTP status text conversion

### 4. **Enhanced Logging Configuration** ✅
**File**: `src/main/resources/application.yml`

Added comprehensive logging levels:
```yaml
logging:
  level:
    org.springframework.cloud.gateway: DEBUG
    org.springframework.cloud.gateway.server.mvc: DEBUG
    org.springframework.cloud.gateway.server.mvc.handler: DEBUG
    org.springframework.cloud.gateway.server.mvc.filter: DEBUG
    org.springframework.web.servlet.mvc.method.annotation: DEBUG
    org.springframework.web.client: DEBUG
    root: INFO
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
```

## Log Output Format

### Successful Request Example
```
14:23:45.123 [nio-9000-exec-1] INFO  ▶ INCOMING REQUEST: GET /tla/jsonholder/todos/1 | Remote IP: 127.0.0.1 | User-Agent: HTTPie/3.1.0
14:23:45.456 [nio-9000-exec-1] INFO  ◀ RESPONSE: GET /tla/jsonholder/todos/1 | Status: 200 OK | Duration: 333ms
```

### Failed Request Example
```
14:24:10.123 [nio-9000-exec-1] INFO  ▶ INCOMING REQUEST: GET /unknown/path | Remote IP: 127.0.0.1 | User-Agent: HTTPie/3.1.0
14:24:10.125 [nio-9000-exec-1] WARN  ◀ RESPONSE: GET /unknown/path | Status: 404 Not Found | Duration: 2ms
```

### Error Request Example
```
14:25:30.123 [nio-9000-exec-1] INFO  ▶ INCOMING REQUEST: POST /api/admin/routes | Remote IP: 127.0.0.1 | User-Agent: HTTPie/3.1.0
14:25:30.456 [nio-9000-exec-1] ERROR ◀ RESPONSE: POST /api/admin/routes | Status: 500 Internal Server Error | Duration: 333ms
14:25:30.457 [nio-9000-exec-1] ERROR ⚠ REQUEST FAILED: POST /api/admin/routes | Exception: Connection refused
```

## Visual Log Indicators

| Symbol | Meaning | Example |
|--------|---------|---------|
| ▶ | Incoming request | ▶ INCOMING REQUEST |
| ◀ | Response returned | ◀ RESPONSE |
| ✓ | Route matched | ✓ Route Matched |
| ✗ | Route not matched | ✗ No Route Matched |
| 🔄 | URL transformation | 🔄 URL TRANSFORMATION |
| 📤 | Backend call | 📤 BACKEND CALL |
| 🔗 | Filter execution | 🔗 Filter Execution |
| ⚠ | Error/warning | ⚠ REQUEST FAILED |

## How to Use

### Build and Run
```bash
# Build with logging enabled
./gradlew clean build -x test

# Start the gateway
./gradlew bootRun

# Watch logs in another terminal
tail -f logs/app.log
```

### Example Test Requests
```bash
# Successful route
http :9000/tla/jsonholder/todos/1
# ✓ Shows: Status: 200 OK | Duration: XXXms

# Non-existent route
http :9000/invalid/path
# Shows: Status: 404 Not Found | Duration: XXms

# Watch real-time logs
tail -f logs/app.log | grep -E "(INCOMING|RESPONSE|Route Matched)"
```

### Filter Logs By Route
```bash
# Show only jsonholder route logs
tail -f logs/app.log | grep "jsonholder"

# Show only errors
tail -f logs/app.log | grep -E "(ERROR|FAILED|⚠)"

# Show only slow requests (> 500ms)
tail -f logs/app.log | grep "Duration:" | grep -v "Duration: [0-4][0-9][0-9]ms"
```

## Logging Levels

### DEBUG (Current - Development)
Maximum detail, shows:
- All requests/responses
- URL transformations
- Filter execution
- Backend calls
- Framework debug messages

### INFO (Can be changed)
Important messages:
- Successful requests/responses
- Route matches
- Status summaries

### WARN (Can be changed)
Warnings and issues:
- Unmatched routes (404)
- Client errors (4xx)

### ERROR (Can be changed)
Errors only:
- Server errors (5xx)
- Exceptions

## Adjusting Log Levels

To reduce logging for production, edit `application.yml`:

```yaml
logging:
  level:
    org.springframework.cloud.gateway: INFO  # Change from DEBUG
    root: WARN                                # Change from INFO
```

## What Gets Logged

✅ All HTTP requests (method, path, query params, IP, user agent)
✅ All HTTP responses (status code, duration)
✅ Request/response errors with exceptions
✅ Gateway framework operations
✅ Route predicates matching
✅ Filter execution
✅ Backend URL calls

❌ Static resources (swagger, h2-console) - excluded by design
❌ Passwords or sensitive headers - not logged for security

## Performance Impact

- ✅ Minimal - logging uses conditional level checks
- ✅ No blocking operations
- ✅ Async logger for performance
- ✅ Safe for production when set to INFO level

## Troubleshooting

| Issue | Debug Step |
|-------|-----------|
| Route not matching | Check incoming path matches predicate pattern |
| Wrong backend called | Verify URI and RewritePath filter in logs |
| Slow responses | Monitor "Duration:" values |
| Authorization failures | Look for 401/403 status codes |
| URL rewriting broken | Check "URL TRANSFORMATION" logs |

## Files Modified/Created

| File | Type | Lines | Purpose |
|------|------|-------|---------|
| GatewayLoggingInterceptor.java | NEW | 85 | Request/response logging |
| WebMvcConfig.java | NEW | 27 | Interceptor registration |
| GatewayRoutingLogger.java | NEW | 110 | Routing detail logs |
| application.yml | MODIFIED | +11 | Logging configuration |
| GATEWAY_LOGGING_GUIDE.md | NEW | 300+ | Comprehensive documentation |
| GATEWAY_LOGGING_QUICK_REFERENCE.md | NEW | 150+ | Quick reference guide |

## Next Steps

1. Build the application (logging automatically enabled)
2. Run the gateway
3. Make test requests
4. Monitor console output for detailed logging
5. Adjust log levels as needed for your environment

## Example Full Request Trace

```
09:15:23.456 [nio-9000-exec-1] INFO  ▶ INCOMING REQUEST: GET /tla/jsonholder/todos/1 | Remote IP: 127.0.0.1 | User-Agent: HTTPie/3.1.0
09:15:23.457 [nio-9000-exec-1] INFO  ✓ Route Matched: [jsonholder]
09:15:23.457 [nio-9000-exec-1] INFO    Request Path: /tla/jsonholder/todos/1
09:15:23.457 [nio-9000-exec-1] INFO    Backend URI:  https://jsonplaceholder.typicode.com/
09:15:23.458 [nio-9000-exec-1] DEBUG 🔄 URL TRANSFORMATION [jsonholder]
09:15:23.458 [nio-9000-exec-1] DEBUG    Original Path:    /tla/jsonholder/todos/1
09:15:23.458 [nio-9000-exec-1] DEBUG    Transformed Path: /todos/1
09:15:23.458 [nio-9000-exec-1] DEBUG    Final URL:        https://jsonplaceholder.typicode.com/todos/1
09:15:23.459 [nio-9000-exec-1] DEBUG 📤 BACKEND CALL [jsonholder]: GET https://jsonplaceholder.typicode.com/todos/1
09:15:23.823 [nio-9000-exec-1] INFO  ◀ RESPONSE: GET /tla/jsonholder/todos/1 | Status: 200 OK | Duration: 367ms
```

---

**Status**: ✅ Complete! Your gateway now has comprehensive logging enabled for debugging URL routing.

