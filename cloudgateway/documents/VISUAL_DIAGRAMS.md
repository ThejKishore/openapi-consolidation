# Visual Diagram: How the Fix Works

## Request Flow Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                        CLIENT REQUEST                               │
│                 http://localhost:9000/tla/externalsvc/get            │
└──────────────────────────┬──────────────────────────────────────────┘
                           │
                           ▼
        ┌──────────────────────────────────────────┐
        │    SPRING CLOUD GATEWAY MVC                │
        │     (Port 9000)                           │
        │                                            │
        │  1. Check Path Predicate                  │
        │     Path=/tla/externalsvc/**              │
        │     Input: /tla/externalsvc/get           │
        │     Match: ✓ YES                          │
        │                                            │
        │  2. Apply Filters (IN ORDER)              │
        │                                            │
        │  Filter #1: RewritePath                   │
        │  ┌────────────────────────────────────┐  │
        │  │ Pattern: /tla/externalsvc/(?<s>.*) │  │
        │  │ Input:   /tla/externalsvc/get      │  │
        │  │ Capture: s = "get"                 │  │
        │  │ Replace: /$\{s} = /get             │  │
        │  │ Output:  /get                      │  │
        │  └────────────────────────────────────┘  │
        │                                            │
        │  Filter #2: AddRequestHeader              │
        │  ┌────────────────────────────────────┐  │
        │  │ Add header:                        │  │
        │  │ X-Request-Foo: Bar                 │  │
        │  └────────────────────────────────────┘  │
        │                                            │
        │  3. Forward to Backend                    │
        │     Base URI: https://httpbin.org/        │
        │     Path:    /get                         │
        │     Final:   https://httpbin.org/get      │
        │                                            │
        └──────────────────────────┬────────────────┘
                                   │
                                   ▼
        ┌──────────────────────────────────────────┐
        │   HTTPBIN.ORG (External Service)          │
        │                                            │
        │  GET https://httpbin.org/get              │
        │  Headers:                                 │
        │    Host: httpbin.org                      │
        │    X-Request-Foo: Bar  (added by gateway) │
        │    User-Agent: ...                        │
        │    ...                                    │
        │                                            │
        │  Response:                                │
        │  200 OK                                   │
        │  Content-Type: application/json           │
        │  {                                        │
        │    "args": {},                            │
        │    "headers": {                           │
        │      "X-Request-Foo": "Bar",  ← Verify!  │
        │      ...                                  │
        │    },                                     │
        │    "url": "https://httpbin.org/get"      │
        │  }                                        │
        │                                            │
        └──────────────────────────┬────────────────┘
                                   │
                                   ▼
        ┌──────────────────────────────────────────┐
        │    RESPONSE BACK TO CLIENT                 │
        │                                            │
        │  HTTP/1.1 200 OK                          │
        │  Content-Type: application/json           │
        │  {                                        │
        │    "args": {},                            │
        │    "headers": {                           │
        │      "Host": "httpbin.org",               │
        │      "X-Request-Foo": "Bar",  ✓ SUCCESS   │
        │      ...                                  │
        │    },                                     │
        │    "origin": "YOUR_IP",                   │
        │    "url": "https://httpbin.org/get"      │
        │  }                                        │
        │                                            │
        └──────────────────────────────────────────┘
```

---

## Path Transformation Details

```
┌─────────────────────────────────────────────────────┐
│          REGEX PATTERN MATCHING                      │
├─────────────────────────────────────────────────────┤
│                                                     │
│  Pattern: /tla/externalsvc/(?<segment>.*)           │
│           ↑                                         │
│           Regex syntax                             │
│                                                     │
│  Components:                                        │
│  /tla/externalsvc/  →  Literal match               │
│  (?<segment>.*)     →  Capture group named 'segment'│
│                         . = any character           │
│                         * = zero or more            │
│                                                     │
│  Examples:                                          │
│  ┌──────────────────────────────────────────────┐  │
│  │ Input: /tla/externalsvc/get                 │  │
│  │        ↓                                     │  │
│  │        /tla/externalsvc/ matches ✓         │  │
│  │        get captured as 'segment' ✓         │  │
│  │        ↓                                     │  │
│  │ Output: /get                                │  │
│  └──────────────────────────────────────────────┘  │
│                                                     │
│  ┌──────────────────────────────────────────────┐  │
│  │ Input: /tla/externalsvc/post                │  │
│  │        ↓                                     │  │
│  │        /tla/externalsvc/ matches ✓         │  │
│  │        post captured as 'segment' ✓        │  │
│  │        ↓                                     │  │
│  │ Output: /post                               │  │
│  └──────────────────────────────────────────────┘  │
│                                                     │
│  ┌──────────────────────────────────────────────┐  │
│  │ Input: /tla/externalsvc/status/418          │  │
│  │        ↓                                     │  │
│  │        /tla/externalsvc/ matches ✓         │  │
│  │        status/418 captured as 'segment' ✓  │  │
│  │        ↓                                     │  │
│  │ Output: /status/418                         │  │
│  └──────────────────────────────────────────────┘  │
│                                                     │
│  Replacement Template: /$\{segment}                 │
│                       ↑           ↑                │
│                       Literal /   Insert 'segment' │
│                                                     │
└─────────────────────────────────────────────────────┘
```

---

## Filter Processing Order

```
┌──────────────────────────────────────────────────────────────┐
│                    REQUEST FILTERS                            │
│              (Applied in order: top to bottom)                │
├──────────────────────────────────────────────────────────────┤
│                                                               │
│  INPUT: /tla/externalsvc/get + Request Headers               │
│                                                               │
│  ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼  │
│  │                                                    │        │
│  │  Filter 1: RewritePath                           │        │
│  │  ┌────────────────────────────────────────────┐  │        │
│  │  │ Transform path:                            │  │        │
│  │  │ /tla/externalsvc/get → /get                │  │        │
│  │  └────────────────────────────────────────────┘  │        │
│  │                                                    │        │
│  │  ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼   │
│  │  │                                            │  │        │
│  │  │  Filter 2: AddRequestHeader                │  │        │
│  │  │  ┌────────────────────────────────────────┐  │  │      │
│  │  │  │ Add header to request:                 │  │  │      │
│  │  │  │ X-Request-Foo: Bar                     │  │  │      │
│  │  │  └────────────────────────────────────────┘  │  │      │
│  │  │                                            │  │  │      │
│  │  │  ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼  │  │  │     │
│  │  │  │                                     │  │  │  │     │
│  │  │  │  REQUEST READY FOR UPSTREAM        │  │  │  │     │
│  │  │  │  Path: /get                        │  │  │  │     │
│  │  │  │  Host: httpbin.org                 │  │  │  │     │
│  │  │  │  X-Request-Foo: Bar                │  │  │  │     │
│  │  │  │  ...other headers...               │  │  │  │     │
│  │  │  │                                     │  │  │  │     │
│  │  │  └─────────────────────────────────────┘  │  │  │     │
│  │  │                                            │  │  │     │
│  │  └────────────────────────────────────────────┘  │  │     │
│  │                                                    │        │
│  └────────────────────────────────────────────────────┘        │
│                                                                 │
│  OUTPUT: Request to https://httpbin.org/get with all headers   │
│                                                                 │
└──────────────────────────────────────────────────────────────┘
```

---

## Before vs After: Visual Comparison

### BEFORE (StripPrefix=2)

```
┌─────────────────────────────────────────┐
│  /tla/externalsvc/get                   │
│  ↓                                       │
│  StripPrefix=2 Filter                   │
│  (Remove segments blindly)               │
│  ↓                                       │
│  Segment 1: /tla        REMOVED ✗       │
│  Segment 2: /externalsvc REMOVED ✗      │
│  ↓                                       │
│  /get                                   │
│  ↓                                       │
│  https://httpbin.org/get                │
│  ↓                                       │
│  200 OK ✓                               │
│                                          │
│  ⚠️  Works but:                          │
│      - Not explicit about intent        │
│      - Positional/index-based            │
│      - Less flexible                     │
│      - Harder to debug                   │
└─────────────────────────────────────────┘
```

### AFTER (RewritePath Regex)

```
┌──────────────────────────────────────────────┐
│  /tla/externalsvc/get                        │
│  ↓                                            │
│  RewritePath Filter (Regex-based)            │
│  Pattern: /tla/externalsvc/(?<segment>.*)    │
│  ↓                                            │
│  Match: /tla/externalsvc/                    │
│  Capture: segment = "get"                    │
│  ↓                                            │
│  Replace: /$\{segment} = /get                │
│  ↓                                            │
│  /get                                        │
│  ↓                                            │
│  https://httpbin.org/get                     │
│  ↓                                            │
│  200 OK ✓                                    │
│                                               │
│  ✅ Better because:                          │
│     - Explicit pattern matching               │
│     - Clear intent visible in config          │
│     - Regex-based (more flexible)             │
│     - Easier to debug and maintain            │
│     - Industry best practice                  │
│     - Captures and reuses path segments       │
└──────────────────────────────────────────────┘
```

---

## Common Test Scenarios

```
┌─────────────────────────────────────────────────────────┐
│  SCENARIO 1: Valid Endpoint (/get)                      │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  Request:   /tla/externalsvc/get                        │
│  Rewrite:   /get                                        │
│  httpbin:   https://httpbin.org/get ✓ EXISTS           │
│  Response:  200 OK ✓                                    │
│                                                          │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│  SCENARIO 2: Invalid Endpoint (/gets)                   │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  Request:   /tla/externalsvc/gets                       │
│  Rewrite:   /gets                                       │
│  httpbin:   https://httpbin.org/gets ✗ DOESN'T EXIST   │
│  Response:  404 Not Found (CORRECT) ✓                  │
│                                                          │
│  NOTE: This is NOT a gateway issue, but a client error  │
│        You requested wrong endpoint!                     │
│                                                          │
└─────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────┐
│  SCENARIO 3: Complex Path (/status/418)                 │
├──────────────────────────────────────────────────────────┤
│                                                           │
│  Request:   /tla/externalsvc/status/418                 │
│  Rewrite:   /status/418                                 │
│  httpbin:   https://httpbin.org/status/418 ✓ EXISTS    │
│  Response:  418 I'm a teapot ✓                          │
│                                                           │
└──────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────┐
│  SCENARIO 4: POST Request                               │
├──────────────────────────────────────────────────────────┤
│                                                           │
│  Request:   POST /tla/externalsvc/post                  │
│  Rewrite:   /post                                       │
│  httpbin:   https://httpbin.org/post ✓ EXISTS          │
│  Response:  200 OK with echoed data ✓                   │
│                                                           │
└──────────────────────────────────────────────────────────┘
```

---

## Summary

### The Fix in One Picture

```
           CONFIGURATION CHANGE
                    │
                    ▼
        ┌───────────────────────────────────────┐
        │  OLD: StripPrefix=2                   │
        │  NEW: RewritePath=...(?<segment>.*) │
        └───────────────────────────────────────┘
                    │
                    ▼
        ┌───────────────────────────────────────┐
        │  SAME PATH TRANSFORMATION              │
        │  /tla/externalsvc/X → /X               │
        └───────────────────────────────────────┘
                    │
                    ▼
        ┌───────────────────────────────────────┐
        │  BETTER IMPLEMENTATION                │
        │  - Explicit regex pattern              │
        │  - Industry best practice              │
        │  - More flexible                       │
        │  - Easier to maintain                  │
        └───────────────────────────────────────┘
```

---

## Test This Fix Now

```
START APP:        java -jar build/libs/...jar
TEST ENDPOINT:    http :9000/tla/externalsvc/get
EXPECT RESULT:    HTTP/1.1 200 OK + JSON
VERIFY HEADER:    Look for X-Request-Foo: Bar in response
```

✅ That's it! The fix is complete and ready to use! 🚀

