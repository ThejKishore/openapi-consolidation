# Gateway URL Logging and Debugging Guide

## Overview

Comprehensive logging has been added to the Cloud Gateway to help debug and track which URLs are being hit through the gateway. This includes request/response logging, route configuration display, and filter execution tracking.

## What Gets Logged

### 1. **Request/Response Logging** (via GatewayLoggingInterceptor)

Every request and response through the gateway is logged with:
- ▶ **INCOMING REQUEST**: Method, Full URL, Remote IP, User-Agent
- ◀ **RESPONSE**: Status code, Status text, Duration in milliseconds
- ⚠ **REQUEST FAILED**: Exception details if request fails

**Example Log Output**:
```
14:23:45.123 [nio-9000-exec-1] INFO GatewayLoggingInterceptor - ▶ INCOMING REQUEST: GET /tla/jsonholder/todos/1 | Remote IP: 127.0.0.1 | User-Agent: HTTPie/3.1.0
14:23:45.456 [nio-9000-exec-1] INFO GatewayLoggingInterceptor - ◀ RESPONSE: GET /tla/jsonholder/todos/1 | Status: 200 OK | Duration: 333ms
```

### 2. **Route Configuration Logging** (via GatewayRoutingLogger)

Gateway displays all configured routes on startup with:
- Route ID
- Backend URI
- Predicates (matching rules)
- Filters (transformations)
- Total number of routes

**Example Log Output**:
```
╔════════════════════════════════════════════════════════════════╗
║                 CLOUD GATEWAY ROUTES CONFIGURATION              ║
╠════════════════════════════════════════════════════════════════╣
║ Route ID: jsonholder
║   URI: https://jsonplaceholder.typicode.com/
║   Predicates:
║     - Path /tla/jsonholder/**
║   Filters:
║     - RewritePath /tla/jsonholder/(?<segment>.*), /$\{segment}
║────────────────────────────────────────────────────────────────║
║ Total Routes: 4
╚════════════════════════════════════════════════════════════════╝
```

### 3. **Framework Debug Logging**

Additional debug logging from Spring Cloud Gateway components:
- Gateway handler mapping
- Filter chain execution
- Route predicates matching
- URL rewrites
- Web client requests

## Files Added

### 1. **GatewayLoggingInterceptor.java**
Location: `src/main/java/com/tk/learn/cloudgateway/filter/GatewayLoggingInterceptor.java`

- Intercepts all HTTP requests/responses
- Logs incoming requests with method, URL, IP, and user agent
- Logs response status, HTTP status text, and duration
- Logs errors with full exception details
- Excludes static resources and swagger docs from logging

### 2. **WebMvcConfig.java**
Location: `src/main/java/com/tk/learn/cloudgateway/config/WebMvcConfig.java`

- Registers the GatewayLoggingInterceptor
- Excludes swagger and h2-console from logging to reduce noise

### 3. **GatewayRoutingLogger.java**
Location: `src/main/java/com/tk/learn/cloudgateway/util/GatewayRoutingLogger.java`

- Logs route configuration with formatted tables
- Logs URL transformations (RewritePath details)
- Logs route matching/mismatching
- Logs filter execution details

### 4. **application.yml** - Enhanced Logging Configuration

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

## Debugging Use Cases

### 1. **Verify Route is Being Hit**

When a request doesn't work, check the logs:
```bash
# You make this request:
http :9000/tla/jsonholder/todos/1

# You'll see in logs:
▶ INCOMING REQUEST: GET /tla/jsonholder/todos/1 | Remote IP: 127.0.0.1
✓ Route Matched: [jsonholder]
  Request Path: /tla/jsonholder/todos/1
  Backend URI:  https://jsonplaceholder.typicode.com/
◀ RESPONSE: GET /tla/jsonholder/todos/1 | Status: 200 OK | Duration: 256ms
```

### 2. **Check URL Rewriting**

If path rewriting fails, look for:
```
🔄 URL TRANSFORMATION [jsonholder]
   Original Path:    /tla/jsonholder/todos/1
   Transformed Path: /todos/1
   Backend URI:      https://jsonplaceholder.typicode.com/
   Final URL:        https://jsonplaceholder.typicode.com/todos/1
```

### 3. **Identify Route Mismatches**

If a request doesn't match any route:
```
✗ No Route Matched for Path: /unknown/path
◀ RESPONSE: GET /unknown/path | Status: 404 Not Found | Duration: 2ms
```

### 4. **Monitor Performance**

Check request duration to identify slow routes:
```
◀ RESPONSE: GET /tla/jsonholder/todos/1 | Status: 200 OK | Duration: 500ms
◀ RESPONSE: GET /tla/externalsvc/status | Status: 200 OK | Duration: 1200ms
```

### 5. **Track Authorization Issues**

Unauthorized requests are logged:
```
◀ RESPONSE: POST /api/admin/routes | Status: 401 Unauthorized | Duration: 5ms
```

## Configurable Log Levels

Adjust logging verbosity in `application.yml`:

### Maximum Debugging (DEBUG level)
```yaml
logging:
  level:
    org.springframework.cloud.gateway: DEBUG
    org.springframework.web.client: DEBUG
```

### Production (INFO level)
```yaml
logging:
  level:
    org.springframework.cloud.gateway: INFO
    org.springframework.web.client: INFO
    root: WARN
```

### Minimal Logging (WARN level)
```yaml
logging:
  level:
    root: WARN
```

## Example: Full Request Flow Log

```
09:15:23.456 [nio-9000-exec-1] INFO GatewayLoggingInterceptor - ▶ INCOMING REQUEST: GET /tla/jsonholder/posts/1 | Remote IP: 127.0.0.1 | User-Agent: curl/7.64.1

09:15:23.457 [nio-9000-exec-1] INFO GatewayRoutingLogger - ✓ Route Matched: [jsonholder]
  Request Path: /tla/jsonholder/posts/1
  Backend URI:  https://jsonplaceholder.typicode.com/

09:15:23.458 [nio-9000-exec-1] DEBUG GatewayRoutingLogger - 🔄 URL TRANSFORMATION [jsonholder]
   Original Path:    /tla/jsonholder/posts/1
   Transformed Path: /posts/1
   Backend URI:      https://jsonplaceholder.typicode.com/
   Final URL:        https://jsonplaceholder.typicode.com/posts/1

09:15:23.459 [nio-9000-exec-1] DEBUG GatewayRoutingLogger - 🔗 Filter Execution [jsonholder] - RewritePath: Path rewritten successfully

09:15:23.678 [nio-9000-exec-1] DEBUG org.springframework.web.client - REST call: GET https://jsonplaceholder.typicode.com/posts/1

09:15:23.823 [nio-9000-exec-1] INFO GatewayLoggingInterceptor - ◀ RESPONSE: GET /tla/jsonholder/posts/1 | Status: 200 OK | Duration: 367ms
```

## Testing the Logging

```bash
# Watch logs with:
tail -f logs/application.log | grep -E "(INCOMING|RESPONSE|Route Matched|URL TRANSFORMATION)"

# Or filter for errors:
tail -f logs/application.log | grep -E "(ERROR|FAILED|404|500)"

# Or filter for specific route:
tail -f logs/application.log | grep "jsonholder"
```

## Performance Considerations

- ✅ Logging is efficient with conditional level checks
- ✅ Sensitive paths (swagger, h2-console) are excluded
- ✅ No performance impact on request processing
- ✅ Logs are formatted with millisecond precision timestamps

## Troubleshooting Tips

1. **Route not matching?** - Check if request path matches the predicate pattern
2. **Wrong backend called?** - Verify the URI and RewritePath filter
3. **Slow responses?** - Monitor the Duration values in response logs
4. **Authorization failures?** - Look for 401/403 status codes
5. **Filter issues?** - Search logs for the filter name and route ID

## Next Steps

1. Rebuild the application:
   ```bash
   ./gradlew clean build -x test
   ```

2. Start and monitor logs:
   ```bash
   ./gradlew bootRun
   ```

3. Make requests and observe the detailed logging:
   ```bash
   http :9000/tla/jsonholder/todos/1
   ```

4. Check console output for request/response logs and route matching details

