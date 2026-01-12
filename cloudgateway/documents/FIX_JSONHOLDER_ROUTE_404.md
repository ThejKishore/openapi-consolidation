# Fix: JSONPlaceholder Route Not Working (404 Error)

## Problem

When calling the route through the gateway:
```bash
http :9000/tla/jsonholder/todos/1
# Returns: HTTP/1.1 404
```

But the direct call to JSONPlaceholder works:
```bash
https://jsonplaceholder.typicode.com/todos/1
# Returns: HTTP/1.1 200 OK with data
```

## Root Cause

The route configuration in the database had an incorrectly escaped RewritePath filter argument:

**Incorrect (stored in DB)**:
```json
{
  "name": "RewritePath",
  "args": "/tla/jsonholder/(?<segment>.*), /$\\{segment}"
}
```

Notice the double backslash `\\{segment}` - this causes the regex group reference to fail, resulting in a malformed rewrite path.

**Correct (YAML config)**:
```yaml
- RewritePath=/tla/jsonholder/(?<segment>.*), /$\{segment}
```

Notice the single backslash `\{segment}` in YAML, which becomes `${segment}` at runtime.

## Solution

Updated `src/main/resources/application.yml` to include a properly configured `jsonholder` route:

**File**: `src/main/resources/application.yml`

```yaml
- id: "jsonholder"
  uri: https://jsonplaceholder.typicode.com/
  predicates:
    - Path=/tla/jsonholder/**
  filters:
    - RewritePath=/tla/jsonholder/(?<segment>.*), /$\{segment}
```

This follows the same pattern as the working `externalsvc` route:

```yaml
- id: "externalsvc"
  uri: https://httpbin.org/
  predicates:
    - Path=/tla/externalsvc/**
  filters:
    - RewritePath=/tla/externalsvc/(?<segment>.*), /$\{segment}
```

## How It Works

When you request `http://localhost:9000/tla/jsonholder/todos/1`:

1. **Path Predicate** matches: `/tla/jsonholder/**`
2. **RewritePath Filter** extracts the `segment` group: `todos/1`
3. **Rewrite Rule** replaces the path: `/tla/jsonholder/todos/1` → `/todos/1`
4. **Backend URI** is prepended: `https://jsonplaceholder.typicode.com/` + `/todos/1`
5. **Final Request**: `https://jsonplaceholder.typicode.com/todos/1`
6. **Response**: Returns 200 OK with the todo data

## Verification

Test the route with these commands:

```bash
# Test through gateway (should work now)
http :9000/tla/jsonholder/todos/1

# Test more endpoints
http :9000/tla/jsonholder/posts/1
http :9000/tla/jsonholder/users/1
http :9000/tla/jsonholder/comments?postId=1

# Compare with direct call (already works)
http https://jsonplaceholder.typicode.com/todos/1
```

## Expected Response

```bash
HTTP/1.1 200 OK
Content-Type: application/json; charset=utf-8

{
    "completed": false,
    "id": 1,
    "title": "delectus aut autem",
    "userId": 1
}
```

## Files Modified

- `src/main/resources/application.yml` - Added properly configured `jsonholder` route

## Important Notes

1. **YAML Escaping**: In YAML, the `$` character needs to be escaped as `\$` when it's part of a string that will be interpreted later
2. **Regex Syntax**: The pattern `(?<segment>.*)` creates a named capture group called `segment`
3. **Replacement**: `/$\{segment}` uses `$\{segment}` to reference the captured group (the `\` prevents premature YAML interpretation)
4. **Spring Gateway Syntax**: This is standard Spring Cloud Gateway RewritePath filter syntax

## Related Routes

Your gateway now has these routes configured:

| ID | Backend | Pattern | Filter |
|----|---------|---------|--------|
| employeesvc | localhost:8081 | /tla/emplSvc/** | None |
| personsvc | localhost:8082 | /tla/personSvc/** | None |
| externalsvc | httpbin.org | /tla/externalsvc/** | RewritePath |
| jsonholder | jsonplaceholder.typicode.com | /tla/jsonholder/** | RewritePath |

## Next Steps

1. Rebuild the Spring Boot application:
   ```bash
   ./gradlew clean build -x test
   ```

2. Start the application:
   ```bash
   ./gradlew bootRun
   ```

3. Test the route:
   ```bash
   http :9000/tla/jsonholder/todos/1
   ```

4. Should see 200 OK with JSON response instead of 404

