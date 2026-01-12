# Gateway URL Logging - Quick Reference

## What's Been Added

Complete logging infrastructure for debugging gateway URL routing:

### 1. **GatewayLoggingInterceptor** ✅
- Logs all incoming requests with method, URL, IP, user agent
- Logs all responses with status code, HTTP status text, duration
- Logs errors with full exception details
- Automatically calculates request processing time

### 2. **WebMvcConfig** ✅
- Registers the logging interceptor
- Excludes swagger UI and h2-console from logging (to reduce noise)

### 3. **GatewayRoutingLogger** ✅
- Utility class for detailed routing logs
- Formatted route configuration display
- URL transformation tracking
- Route matching/mismatching logs
- Filter execution tracking

### 4. **Enhanced Logging Config** ✅
- Updated `application.yml` with DEBUG level logging
- Pretty console output format with timestamps and thread info

## Log Output Examples

### ✅ Successful Request
```
▶ INCOMING REQUEST: GET /tla/jsonholder/todos/1 | Remote IP: 127.0.0.1 | User-Agent: HTTPie/3.1.0
✓ Route Matched: [jsonholder]
◀ RESPONSE: GET /tla/jsonholder/todos/1 | Status: 200 OK | Duration: 256ms
```

### ❌ Failed Request (404)
```
▶ INCOMING REQUEST: GET /unknown/path | Remote IP: 127.0.0.1 | User-Agent: HTTPie/3.1.0
✗ No Route Matched for Path: /unknown/path
◀ RESPONSE: GET /unknown/path | Status: 404 Not Found | Duration: 2ms
```

### ⚠️ Error Request (500)
```
▶ INCOMING REQUEST: POST /api/admin/routes | Remote IP: 127.0.0.1 | User-Agent: HTTPie/3.1.0
◀ RESPONSE: POST /api/admin/routes | Status: 500 Internal Server Error | Duration: 45ms
⚠ REQUEST FAILED: POST /api/admin/routes | Exception: Connection refused
```

## How to Use

### View Logs While Running
```bash
# Start the gateway
./gradlew bootRun

# In another terminal, watch for gateway logs
tail -f logs/app.log | grep -E "(INCOMING|RESPONSE|Route Matched)"
```

### Filter By Route
```bash
# Show only jsonholder route logs
tail -f logs/app.log | grep "jsonholder"

# Show only errors
tail -f logs/app.log | grep -E "(ERROR|FAILED|404|500)"
```

### Monitor Performance
```bash
# Show response times
tail -f logs/app.log | grep "Duration:"

# Find slow requests (> 500ms)
tail -f logs/app.log | grep -oP "Duration: \K[0-9]+" | awk '$1 > 500 {print "SLOW: " $0 "ms"}'
```

## Testing

### Before Testing
```bash
./gradlew clean build -x test
./gradlew bootRun
```

### Run Test Requests
```bash
# Test successful route
http :9000/tla/jsonholder/todos/1
# ✓ Should see: Status: 200 OK

# Test non-existent route
http :9000/invalid/path
# ✓ Should see: Status: 404 Not Found

# Watch console logs to see detailed routing information
```

## Log Levels

Current configuration uses **DEBUG** level. To adjust:

**Production (less logging)**:
```yaml
logging:
  level:
    org.springframework.cloud.gateway: INFO
    root: WARN
```

**Development (more logging)**:
```yaml
logging:
  level:
    org.springframework.cloud.gateway: TRACE
    org.springframework.web.client: TRACE
```

## Files Created/Modified

| File | Type | Purpose |
|------|------|---------|
| GatewayLoggingInterceptor.java | NEW | Request/response logging |
| WebMvcConfig.java | NEW | Interceptor registration |
| GatewayRoutingLogger.java | NEW | Routing details logging |
| application.yml | MODIFIED | Enhanced logging configuration |
| GATEWAY_LOGGING_GUIDE.md | NEW | Comprehensive documentation |

## Example Full Trace

```
[nio-9000-exec-1] INFO  ▶ INCOMING REQUEST: GET /tla/jsonholder/posts/1 | Remote IP: 127.0.0.1
[nio-9000-exec-1] DEBUG ✓ Route Matched: [jsonholder]
[nio-9000-exec-1] DEBUG   Request Path: /tla/jsonholder/posts/1
[nio-9000-exec-1] DEBUG   Backend URI:  https://jsonplaceholder.typicode.com/
[nio-9000-exec-1] DEBUG 🔄 URL TRANSFORMATION [jsonholder]
[nio-9000-exec-1] DEBUG    Original Path:    /tla/jsonholder/posts/1
[nio-9000-exec-1] DEBUG    Transformed Path: /posts/1
[nio-9000-exec-1] DEBUG    Final URL:        https://jsonplaceholder.typicode.com/posts/1
[nio-9000-exec-1] DEBUG 🔗 Filter Execution [jsonholder] - RewritePath: Path rewritten
[nio-9000-exec-1] INFO  ◀ RESPONSE: GET /tla/jsonholder/posts/1 | Status: 200 OK | Duration: 367ms
```

## Quick Debugging Checklist

- [ ] Request has correct path matching predicate?
- [ ] Backend URI is reachable?
- [ ] RewritePath filter syntax is correct?
- [ ] Check Duration - is it slow?
- [ ] Status code indicates success (2xx) or error (4xx/5xx)?
- [ ] Any exceptions in request failed logs?

Done! ✅ Your gateway now has comprehensive URL logging for debugging.

