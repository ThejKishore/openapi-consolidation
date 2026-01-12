# Cloud Gateway Admin Portal - Implementation Plan

## Project Overview
A comprehensive Admin Portal for managing Cloud Gateway routes with auditing, health monitoring, and OAuth2 authentication.

---

## Phase 1: Database & Backend Infrastructure

### 1.1 Audit Table Schema ✅ IN PROGRESS
**File**: `src/main/resources/schema.sql`
**Changes**:
- [ ] Create `gw_route_audit` table with columns:
  - audit_id (PK, auto-increment)
  - route_id (FK to gw_routes)
  - action (CREATE, UPDATE, DELETE)
  - created_by (username)
  - created_at (timestamp)
  - version (integer)
  - old_value (JSON - previous route config)
  - new_value (JSON - current route config)
  - description (text)

### 1.2 Route Versioning ✅ IN PROGRESS
**File**: `src/main/resources/schema.sql`
**Changes**:
- [ ] Add `version` column to `gw_routes` table (default 1)
- [ ] Add `created_by` column to `gw_routes` table
- [ ] Add `updated_by` column to `gw_routes` table
- [ ] Add `created_at` column to `gw_routes` table (default CURRENT_TIMESTAMP)
- [ ] Add `updated_at` column to `gw_routes` table (default CURRENT_TIMESTAMP)

### 1.3 Dependency Updates ✅ IN PROGRESS
**File**: `build.gradle`
**Changes**:
- [ ] Add Spring Security OAuth2 Resource Server: `org.springframework.boot:spring-boot-starter-oauth2-resource-server`
- [ ] Add Spring Security: `org.springframework.boot:spring-boot-starter-security`
- [ ] Add JSON processing: `com.fasterxml.jackson.core:jackson-databind`
- [ ] Add H2 Web Console (for testing)
- [ ] Add Spring Data REST for automatic REST APIs

---

## Phase 2: Backend API Implementation

### 2.1 Domain Models ✅ IN PROGRESS
**File**: `src/main/java/com/tk/learn/cloudgateway/domain/`
**Create New Classes**:
- [ ] `RouteAudit.java` - JPA Entity for audit logging
- [ ] `RouteResponse.java` - DTO for API responses
- [ ] `RouteRequest.java` - DTO for API requests
- [ ] `AuditResponse.java` - DTO for audit history
- [ ] `HealthStatus.java` - DTO for route health status

### 2.2 Repository Layer ✅ IN PROGRESS
**File**: `src/main/java/com/tk/learn/cloudgateway/repository/`
**Create New Classes**:
- [ ] `RouteRepository.java` - JPA Repository for routes
- [ ] `RouteAuditRepository.java` - JPA Repository for audit logs
- [ ] `RouteAuditRepository` with custom query methods:
  - Find audit history by route_id
  - Find all audits by action type
  - Find audits by date range

### 2.3 Service Layer ✅ IN PROGRESS
**File**: `src/main/java/com/tk/learn/cloudgateway/service/`
**Create New Classes**:
- [ ] `RouteService.java` - Business logic for route management
  - listAllRoutes()
  - getRouteById(id)
  - createRoute(RouteRequest)
  - updateRoute(id, RouteRequest)
  - deleteRoute(id)
  - enableRoute(id)
  - disableRoute(id)
  
- [ ] `AuditService.java` - Audit logging logic
  - logAction(routeId, action, oldValue, newValue, createdBy)
  - getAuditHistory(routeId)
  - getAuditsByUser(userId)
  - getAuditsByDateRange(start, end)
  
- [ ] `HealthCheckService.java` - Health monitoring
  - checkRouteHealth(uri) - Test if backend is reachable
  - getHealthStatus(routeId)
  - checkAllRoutesHealth()

### 2.4 REST Controller ✅ IN PROGRESS
**File**: `src/main/java/com/tk/learn/cloudgateway/controller/`
**Create New Classes**:
- [ ] `AdminRouteController.java` - REST endpoints for route management
  - GET /api/admin/routes - List all routes
  - GET /api/admin/routes/{id} - Get route details
  - POST /api/admin/routes - Create new route
  - PUT /api/admin/routes/{id} - Update route
  - DELETE /api/admin/routes/{id} - Delete route
  - POST /api/admin/routes/{id}/enable - Enable route
  - POST /api/admin/routes/{id}/disable - Disable route
  - GET /api/admin/routes/health/status - Get all routes health
  - GET /api/admin/routes/{id}/health - Get specific route health
  
- [ ] `AdminAuditController.java` - REST endpoints for audit
  - GET /api/admin/audit/routes/{id} - Audit history for route
  - GET /api/admin/audit/users/{userId} - Audits by user
  - GET /api/admin/audit/actions/{action} - Audits by action

### 2.5 Security Configuration ✅ IN PROGRESS
**File**: `src/main/java/com/tk/learn/cloudgateway/config/`
**Create New Classes**:
- [ ] `SecurityConfig.java` - OAuth2 & Security configuration
  - Configure OAuth2 Resource Server
  - Setup role-based access control (ADMIN role required)
  - Configure CORS for frontend
  - Protect all /api/admin/* endpoints
  - Allow public access to gateway routes
  
- [ ] `JwtAuthenticationConverter.java` - JWT token conversion
  - Extract roles from JWT token
  - Convert to Spring authorities

### 2.6 Application Properties ✅ IN PROGRESS
**File**: `src/main/resources/application.yml`
**Changes**:
- [ ] Configure OAuth2 Resource Server (issuer-uri, jwk-set-uri)
- [ ] Configure database connection
- [ ] Configure logging levels
- [ ] Add server servlet context path for admin portal: `/admin`

---

## Phase 3: Frontend Implementation

### 3.1 Project Setup ✅ IN PROGRESS
**Directory**: `src/main/resources/static/admin/`
**Create**:
- [ ] `package.json` - Vue.js project configuration
- [ ] `.env` - Environment variables (API_BASE_URL, OAuth2 config)
- [ ] `vite.config.js` - Build configuration
- [ ] `.eslintrc.js` - Linting configuration

### 3.2 Vue.js Core Files ✅ IN PROGRESS
**Directory**: `src/main/resources/static/admin/src/`
**Create**:
- [ ] `main.js` - Entry point
- [ ] `App.vue` - Root component
- [ ] `router.js` - Vue Router configuration
- [ ] `store.js` - Pinia state management
- [ ] `api.js` - Axios HTTP client with OAuth2 interceptor

### 3.3 Pages/Views ✅ IN PROGRESS
**Directory**: `src/main/resources/static/admin/src/views/`
**Create**:
- [ ] `Login.vue` - OAuth2 login page (redirect to auth server)
- [ ] `Dashboard.vue` - Admin dashboard landing page
- [ ] `RoutesList.vue` - List all routes with search/filter
- [ ] `RouteDetail.vue` - View route details
- [ ] `RouteForm.vue` - Create/Edit route form
- [ ] `HealthMonitor.vue` - Health status dashboard
- [ ] `AuditLog.vue` - Audit history and versioning view
- [ ] `NotAuthorized.vue` - 403 error page
- [ ] `NotFound.vue` - 404 error page

### 3.4 Components ✅ IN PROGRESS
**Directory**: `src/main/resources/static/admin/src/components/`
**Create**:
- [ ] `RouteTable.vue` - Reusable table component for routes
- [ ] `PredicatesEditor.vue` - Add/edit route predicates
- [ ] `FiltersEditor.vue` - Add/edit route filters
- [ ] `MetadataEditor.vue` - Key-value editor for metadata
- [ ] `HealthBadge.vue` - Display health status badge
- [ ] `AuditTimeline.vue` - Timeline view of changes
- [ ] `VersionHistory.vue` - Version comparison view
- [ ] `ConfirmDialog.vue` - Reusable confirmation dialog
- [ ] `LoadingSpinner.vue` - Loading indicator
- [ ] `Toast.vue` - Notification component
- [ ] `Header.vue` - Navigation header
- [ ] `Sidebar.vue` - Navigation sidebar

### 3.5 Styling & Assets ✅ IN PROGRESS
**Directory**: `src/main/resources/static/admin/src/assets/`
**Create**:
- [ ] `style.css` - Global styles (Tailwind CSS or Bootstrap)
- [ ] `variables.css` - CSS custom properties
- [ ] Logo and favicon files

### 3.6 Build & Deployment ✅ IN PROGRESS
**File**: `pom.xml` or modify `build.gradle`
**Changes**:
- [ ] Add Maven/Gradle plugin to build Vue.js frontend
- [ ] Configure output directory: `src/main/resources/static/admin/dist/`
- [ ] Add npm/yarn build commands

---

## Phase 4: Admin Portal Features

### 4.1 Route Management Dashboard ✅ IN PROGRESS
**Features**:
- [ ] Display all routes in a searchable, sortable table
- [ ] Show route details: id, uri, predicates, filters, enabled status
- [ ] Quick enable/disable toggle
- [ ] View full route configuration as JSON
- [ ] Export route configuration (JSON/YAML)

### 4.2 Create Route ✅ IN PROGRESS
**Features**:
- [ ] Form to input route details
- [ ] Dynamic predicates editor (add multiple)
- [ ] Dynamic filters editor (add multiple)
- [ ] Metadata key-value editor
- [ ] Validation (required fields, URI format, etc.)
- [ ] Test predicate button (check if pattern is valid)
- [ ] Preview final route configuration

### 4.3 Edit Route ✅ IN PROGRESS
**Features**:
- [ ] Pre-populate form with existing route data
- [ ] Show version history in sidebar
- [ ] Compare with previous version
- [ ] Show "last updated by" information
- [ ] Revert to previous version option

### 4.4 Delete Route ✅ IN PROGRESS
**Features**:
- [ ] Confirmation dialog with affected routes
- [ ] Audit trail shows deletion
- [ ] Soft delete option (disable instead of delete)
- [ ] Restore from audit history option

### 4.5 Health Monitoring ✅ IN PROGRESS
**Features**:
- [ ] Real-time health status for each route
- [ ] Color-coded status indicators (green=healthy, red=down, yellow=slow)
- [ ] Response time metrics
- [ ] Last checked timestamp
- [ ] Refresh all button
- [ ] Filter by health status
- [ ] Historical health data (last 24 hours, 7 days, 30 days)

### 4.6 Audit & Versioning ✅ IN PROGRESS
**Features**:
- [ ] Audit log showing all changes (create, update, delete)
- [ ] Display: who changed it, when, what action, version
- [ ] Version history view for each route
- [ ] Side-by-side diff view of version changes
- [ ] Revert to specific version (creates new audit entry)
- [ ] Filter audit by: date range, user, action type, route
- [ ] Export audit log as CSV

### 4.7 OAuth2 Authentication & Authorization ✅ IN PROGRESS
**Features**:
- [ ] Redirect to OAuth2 provider for login
- [ ] Extract JWT token and store in localStorage/sessionStorage
- [ ] Auto-refresh token before expiry
- [ ] Role-based access control (require ADMIN role)
- [ ] Display logged-in user info
- [ ] Logout functionality
- [ ] Handle 401/403 errors gracefully

---

## Phase 5: Testing

### 5.1 Unit Tests ✅ IN PROGRESS
**Files**:
- [ ] `RouteServiceTest.java` - Test route CRUD operations
- [ ] `AuditServiceTest.java` - Test audit logging
- [ ] `HealthCheckServiceTest.java` - Test health checks

### 5.2 Integration Tests ✅ IN PROGRESS
**Files**:
- [ ] `AdminRouteControllerTest.java` - Test REST endpoints
- [ ] `AdminAuditControllerTest.java` - Test audit endpoints
- [ ] Security tests for OAuth2

### 5.3 Frontend Tests ✅ IN PROGRESS
**Files**:
- [ ] `RoutesList.spec.js` - Test route listing component
- [ ] `RouteForm.spec.js` - Test form validation
- [ ] `AuditLog.spec.js` - Test audit display
- [ ] API client tests with mocked HTTP

---

## Phase 6: Deployment & Documentation

### 6.1 Deployment Configuration ✅ IN PROGRESS
**Files**:
- [ ] Dockerfile for containerization
- [ ] docker-compose.yml for local development
- [ ] Kubernetes manifests (optional)
- [ ] Deployment guide

### 6.2 Documentation ✅ IN PROGRESS
**Files**:
- [ ] API documentation (Swagger/OpenAPI)
- [ ] Frontend development guide
- [ ] Admin Portal user guide
- [ ] OAuth2 setup guide
- [ ] Database schema documentation

---

## Technical Architecture

```
┌─────────────────────────────────────────────────────────┐
│                   Admin Portal (Vue.js SPA)              │
│  ┌────────────────────────────────────────────────────┐ │
│  │ Routes | Health Monitor | Audit Log | User Info    │ │
│  └────────────────────────────────────────────────────┘ │
│                         ▲                                 │
│                         │ HTTPS + OAuth2 Token            │
│                         ▼                                 │
├─────────────────────────────────────────────────────────┤
│              Spring Cloud Gateway (Port 9000)             │
│                                                          │
│  ┌────────────────────────────────────────────────────┐ │
│  │            Admin REST API Controllers               │ │
│  │  (/api/admin/routes, /api/admin/audit, etc.)      │ │
│  └────────────────────────────────────────────────────┘ │
│                         ▲ ▼                              │
│  ┌──────────────┬──────┴──┴────────┬─────────────────┐ │
│  │   Services   │   Controllers    │  Repositories   │ │
│  │              │                  │                  │ │
│  │ RouteService │ AdminRoute       │ RouteRepository │ │
│  │AuditService  │ Controller       │RouteAuditRepo   │ │
│  │HealthCheck   │                  │                  │ │
│  └──────────────┴──────────────────┴─────────────────┘ │
│                         ▲ ▼                              │
│                    ┌─────────────┐                       │
│                    │   Database   │                      │
│                    │   (H2/PostgreSQL)                  │
│                    │              │                      │
│                    │ gw_routes    │                      │
│                    │ gw_route_    │                      │
│                    │  predicates  │                      │
│                    │ gw_route_    │                      │
│                    │  filters     │                      │
│                    │ gw_route_    │                      │
│                    │  audit       │                      │
│                    │ gw_route_    │                      │
│                    │  metadata    │                      │
│                    └─────────────┘                       │
└─────────────────────────────────────────────────────────┘
                         ▲
                         │ Route Traffic
                         ▼
              Backend Services & APIs
```

---

## Key Implementation Details

### Database Schema Changes
```sql
-- Extend existing gw_routes table
ALTER TABLE gw_routes ADD COLUMN version INT DEFAULT 1;
ALTER TABLE gw_routes ADD COLUMN created_by VARCHAR(255);
ALTER TABLE gw_routes ADD COLUMN updated_by VARCHAR(255);
ALTER TABLE gw_routes ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE gw_routes ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- New audit table
CREATE TABLE gw_route_audit (
    audit_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    route_id VARCHAR(100) NOT NULL,
    action VARCHAR(20) NOT NULL,
    version INT NOT NULL,
    created_by VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    old_value CLOB,
    new_value CLOB,
    description TEXT,
    FOREIGN KEY (route_id) REFERENCES gw_routes(id)
);
```

### API Response Format
```json
{
  "id": "route1",
  "uri": "http://backend:8080",
  "enabled": true,
  "version": 3,
  "createdBy": "admin",
  "createdAt": "2026-01-01T10:00:00Z",
  "updatedBy": "admin2",
  "updatedAt": "2026-01-11T15:30:00Z",
  "predicates": [
    {"name": "Path", "args": "/api/**"}
  ],
  "filters": [
    {"name": "StripPrefix", "args": "1"}
  ],
  "metadata": {},
  "health": {
    "status": "UP",
    "responseTime": 45,
    "lastChecked": "2026-01-11T15:35:00Z"
  }
}
```

---

## Development Priority & Sequence

1. **Phase 1 - Database** (Week 1)
   - Schema changes
   - Dependency updates

2. **Phase 2 - Backend** (Week 2-3)
   - Domain models
   - Repositories
   - Services (Route, Audit, HealthCheck)
   - Controllers
   - Security config

3. **Phase 3 - Frontend Setup** (Week 3-4)
   - Project structure
   - Core files
   - Basic router & state management

4. **Phase 4 - Features** (Week 4-6)
   - Route management
   - Health monitoring
   - Audit & versioning
   - OAuth2 integration

5. **Phase 5 - Testing** (Week 6-7)
   - Unit tests
   - Integration tests
   - Frontend tests

6. **Phase 6 - Deployment** (Week 7-8)
   - Docker setup
   - Documentation
   - Deployment guide

---

## Completion Checklist

### Backend
- [ ] Database schema updated with audit tables and versioning
- [ ] All dependencies added to build.gradle
- [ ] Domain models created (RouteAudit, RouteResponse, AuditResponse)
- [ ] Repositories implemented with query methods
- [ ] RouteService with full CRUD operations
- [ ] AuditService with logging functionality
- [ ] HealthCheckService for monitoring
- [ ] AdminRouteController with all endpoints
- [ ] AdminAuditController with audit endpoints
- [ ] SecurityConfig with OAuth2
- [ ] Application.yml configured
- [ ] Unit tests written
- [ ] Integration tests written

### Frontend
- [ ] Vue.js project initialized
- [ ] Pages created (Dashboard, Routes, Audit, etc.)
- [ ] Components created (RouteTable, Editors, etc.)
- [ ] API client with OAuth2 token handling
- [ ] State management setup (Pinia)
- [ ] Router configured
- [ ] Authentication flow implemented
- [ ] Styling applied
- [ ] Frontend tests written

### Operations
- [ ] Swagger/OpenAPI documentation
- [ ] Admin Portal user guide
- [ ] Deployment guide
- [ ] Docker setup
- [ ] Database migrations documented

---

## Success Criteria

1. ✅ Admin Portal accessible at `/admin` with OAuth2 protection
2. ✅ List all routes with real-time health status
3. ✅ Create/update/delete routes without gateway restart
4. ✅ Full audit trail showing who/when/what for all changes
5. ✅ Version history with rollback capability
6. ✅ Role-based access control (ADMIN users only)
7. ✅ Responsive Vue.js SPA interface
8. ✅ All endpoints documented
9. ✅ Comprehensive test coverage
10. ✅ Production-ready deployment

---

## File Structure
```
cloudgateway/
├── src/
│   ├── main/
│   │   ├── java/com/tk/learn/cloudgateway/
│   │   │   ├── domain/          (New)
│   │   │   ├── repository/       (New)
│   │   │   ├── service/          (New)
│   │   │   ├── controller/       (New)
│   │   │   ├── config/           (New/Modified)
│   │   │   └── ...existing...
│   │   └── resources/
│   │       ├── static/admin/     (New - Vue.js SPA)
│   │       │   └── src/
│   │       │       ├── views/
│   │       │       ├── components/
│   │       │       ├── assets/
│   │       │       └── ...
│   │       ├── schema.sql        (Modified)
│   │       └── application.yml   (Modified)
│   └── test/
│       └── java/...tests...      (New test classes)
└── build.gradle                  (Modified)
```

---

## Next Steps
1. Review and approve this plan
2. Start Phase 1: Database schema changes
3. Create sample implementation for feedback
4. Iterate based on feedback
5. Complete remaining phases

---

**Status**: PLAN CREATED - Ready for implementation
**Last Updated**: January 11, 2026
**Estimated Timeline**: 8 weeks

