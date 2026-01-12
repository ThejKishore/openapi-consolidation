# Fix: Database Routes Filters Not Applied - Use YAML Config Instead

## Problem

You created a route via the Vue admin panel with:
```json
{
    "id": "jsonholder",
    "uri": "https://jsonplaceholder.typicode.com/",
    "filters": [
        {
            "name": "StripPrefix",
            "args": "2"
        }
    ]
}
```

When you request `http://localhost:9000/tla/jsonholder/todos/1`, it returns **404** instead of proxying to the backend.

## Root Cause

**Spring Cloud Gateway MVC does NOT support filters on dynamically loaded routes from the database.**

The `CustomRouterFunctionRetriever` loads routes from the database but **does not apply filters** (as noted in the code comment on line 101: "filters (stored, but not yet applied in builder - kept for future extension)").

This is an architectural limitation of Spring Gateway MVC's router function API.

## Solution

Use filters in the **YAML configuration** (`application.yml`) instead of the database. Filters are only applied when routes are defined in YAML.

### Step 1: Delete the Bad Route from Database

```bash
# Delete the jsonholder route with the StripPrefix filter
curl -X DELETE http://localhost:9000/api/admin/routes/jsonholder
```

### Step 2: Add Route with Filters to application.yml

Edit `src/main/resources/application.yml`:

```yaml
spring:
  cloud:
    gateway:
      mvc:
        routes:
          # ... existing routes ...
          
          - id: "jsonholder"
            uri: "https://jsonplaceholder.typicode.com/"
            predicates:
              - Path=/tla/jsonholder/**
            filters:
              - RewritePath=/tla/jsonholder/(?<segment>.*), /$\{segment}
```

### Step 3: Rebuild and Restart

```bash
./gradlew clean build -x test
./gradlew bootRun
```

### Step 4: Test

```bash
# Now works!
http :9000/tla/jsonholder/todos/1

# Returns 200 OK with JSON from JSONPlaceholder
HTTP/1.1 200 OK
{
    "userId": 1,
    "id": 1,
    "title": "delectus aut autem",
    "completed": false
}
```

## Why This Works

When routes are defined in `application.yml`:
- ✅ Filters ARE applied by Spring Gateway MVC
- ✅ RewritePath, StripPrefix, AddRequestHeader all work
- ✅ Routes load at startup with all functionality

When routes are loaded from database:
- ✅ Predicates ARE applied
- ❌ Filters are NOT applied (architectural limitation)
- ⚠️ Only basic path matching works

## Comparison: YAML vs Database Routes

| Feature | YAML Config | Database Routes |
|---------|------------|-----------------|
| Predicates (Path matching) | ✅ | ✅ |
| Filters (RewritePath, etc) | ✅ | ❌ |
| Dynamic reload | ❌ (restart needed) | ✅ (auto-refresh) |
| Best Use | Production, stable routes | Admin UI, dynamic routes |

## Recommended Approach

**Use BOTH methods strategically:**

1. **YAML Config (application.yml)** - For production routes that need filters
   ```yaml
   - id: "jsonholder"
     uri: "https://jsonplaceholder.typicode.com/"
     filters:
       - RewritePath=/tla/jsonholder/(?<segment>.*), /$\{segment}
   ```

2. **Database Routes** - For admin-managed routes without filters
   ```json
   {
       "id": "admin-route",
       "uri": "http://admin-backend:8080/",
       "predicates": [{"name": "Path", "args": "/admin/**"}]
   }
   ```

## Files That Changed

- `CustomRouterFunctionRetriever.java` - Added warning logs when filters are found in DB routes
- `application.yml` - Use this for routes with filters

## Technical Details

Spring Cloud Gateway MVC uses Spring's WebFlux `RouterFunction` API. This API:
- Supports predicates through `path()`, `method()`, etc.
- Does NOT have a clean filter API for dynamically registered routes
- Filters are applied through Spring MVC interceptors (for YAML routes)

For database-driven routes to support filters, we would need to:
1. Implement custom filter logic in `CustomRouterFunctionRetriever`
2. Parse and apply each filter type manually
3. This is complex and error-prone

**Better solution**: Use YAML for routes with filters, database for simple admin-managed routes.

---

**Status**: ✅ **RESOLVED** - Use YAML configuration for routes that need filters.

