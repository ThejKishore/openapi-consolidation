# 🛠️ DEVELOPER QUICK REFERENCE GUIDE

## Quick Commands Reference

### Backend Setup
```bash
# Navigate to project root
cd /Users/thejkaruneegar/open-api-workspace/cloudgateway

# Build project
./gradlew clean build

# Run with tests
./gradlew bootRun

# Run skip tests
./gradlew bootRun --exclude-task test

# View dependencies
./gradlew dependencies
```

### Frontend Setup
```bash
# Navigate to admin portal
cd src/main/resources/static/admin

# Install dependencies
npm install

# Development server (with hot reload)
npm run dev

# Production build
npm run build

# Preview production build
npm run preview

# Lint code
npm run lint
```

---

## 📂 Key File Locations

### Backend
- **Controllers**: `src/main/java/com/tk/learn/cloudgateway/controller/`
- **Services**: `src/main/java/com/tk/learn/cloudgateway/service/`
- **Repositories**: `src/main/java/com/tk/learn/cloudgateway/repository/`
- **Domain Models**: `src/main/java/com/tk/learn/cloudgateway/domain/`
- **Configuration**: `src/main/java/com/tk/learn/cloudgateway/config/`
- **Database**: `src/main/resources/schema.sql`, `data.sql`
- **Config**: `src/main/resources/application.yml`

### Frontend
- **Pages**: `src/main/resources/static/admin/src/views/`
- **Components**: `src/main/resources/static/admin/src/components/`
- **State**: `src/main/resources/static/admin/src/store.js`
- **Routing**: `src/main/resources/static/admin/src/router.js`
- **API**: `src/main/resources/static/admin/src/api.js`
- **Styling**: `src/main/resources/static/admin/src/assets/style.css`

---

## 🔌 API Endpoints Quick Reference

### Routes Management
```
GET     /api/admin/routes                List all routes
GET     /api/admin/routes/{id}           Get specific route
POST    /api/admin/routes                Create route
PUT     /api/admin/routes/{id}           Update route
DELETE  /api/admin/routes/{id}           Delete route
POST    /api/admin/routes/{id}/enable    Enable route
POST    /api/admin/routes/{id}/disable   Disable route
GET     /api/admin/routes/health/status  All routes health
GET     /api/admin/routes/{id}/health    Specific route health
```

### Audit Trail
```
GET     /api/admin/audit/routes/{id}     Route audit history
GET     /api/admin/audit/users/{id}      User's changes
GET     /api/admin/audit/actions/{action} Changes by action
GET     /api/admin/audit/date-range      Date range query
GET     /api/admin/audit/versions/{id}   Version history
```

---

## 🔑 Authentication

### Backend
- **Type**: OAuth2 Resource Server with JWT
- **Configuration**: `SecurityConfig.java`
- **Token Header**: `Authorization: Bearer <token>`
- **Protected Endpoints**: `/api/admin/**`

### Frontend
- **Login Page**: `/login`
- **Token Storage**: `localStorage['authToken']`
- **Token Refresh**: Handled by Axios interceptor
- **Redirect on Auth Fail**: `/login`

---

## 📊 Database

### Connection
- **Type**: H2 (in-memory)
- **Connection String**: `jdbc:h2:mem:gwdb;DB_CLOSE_DELAY=-1`
- **Console**: `http://localhost:9000/h2-console`

### Key Tables
```sql
gw_routes              -- Gateway routes
gw_route_predicates    -- Route predicates
gw_route_filters       -- Route filters
gw_route_metadata      -- Route metadata
gw_route_audit         -- Audit trail (NEW)
```

### Initialization
- Schema: `schema.sql`
- Sample Data: `data.sql`
- Auto-initialized on startup

---

## 🧪 Adding New Features

### Backend: Add New Endpoint

1. **Create DTO** (if needed)
   ```java
   // src/main/java/com/tk/learn/cloudgateway/domain/YourDto.java
   public class YourDto {
       // fields
   }
   ```

2. **Create Service Method**
   ```java
   // In RouteService.java or new service
   public YourDto yourMethod(params) {
       // implementation
       auditService.logAction(...);
       return result;
   }
   ```

3. **Add Controller Endpoint**
   ```java
   // In AdminRouteController.java
   @PostMapping("/your-endpoint")
   @PreAuthorize("hasRole('ADMIN')")
   public ResponseEntity<YourDto> yourEndpoint(...) {
       return ResponseEntity.ok(service.yourMethod(...));
   }
   ```

### Frontend: Add New Page

1. **Create Vue Component**
   ```vue
   <!-- src/views/YourPage.vue -->
   <template>
       <div><!-- Your template --></div>
   </template>
   
   <script>
   export default {
       name: 'YourPage'
   }
   </script>
   ```

2. **Add Route**
   ```javascript
   // In router.js
   {
       path: '/your-path',
       name: 'YourPage',
       component: () => import('./views/YourPage.vue'),
       meta: { requiresAuth: true }
   }
   ```

3. **Add Navigation Link**
   ```vue
   <!-- In Sidebar.vue -->
   <router-link to="/your-path">
       Your Link
   </router-link>
   ```

---

## 🐛 Common Issues & Solutions

### Backend Won't Start
```bash
# Check if port 9000 is in use
lsof -i :9000

# Build failed
./gradlew clean build -x test

# Check logs for detailed errors
```

### Frontend Won't Load
```bash
# Check if port 5173 is in use
lsof -i :5173

# Clear dependencies and reinstall
rm -rf node_modules package-lock.json
npm install

# Check API base URL in src/api.js
```

### API Calls Failing
- Check backend is running on :9000
- Verify JWT token in localStorage
- Check CORS configuration in SecurityConfig
- Review network tab in browser DevTools

### Database Issues
```bash
# Reset database (clears H2)
# Delete h2 database files or restart backend
./gradlew bootRun

# View H2 console
http://localhost:9000/h2-console
```

---

## 📝 Code Style

### Backend
- Java 17
- Lombok for boilerplate
- Spring conventions
- Camel case for methods/variables

### Frontend
- Vue 3 Composition API
- Tailwind CSS utilities
- Kebab case for component files
- Camel case for JS variables

---

## 🔍 Debugging

### Backend
```bash
# Enable debug logging
# In application.yml:
logging:
  level:
    root: DEBUG
    com.tk.learn.cloudgateway: DEBUG

# Or run with debug flag
./gradlew bootRun --debug
```

### Frontend
```javascript
// Browser console
// View Vuex store state
console.$store.state

// View route info
console.$router.currentRoute

// Network requests in DevTools Network tab
```

---

## 📦 Dependencies

### Backend Key Dependencies
- Spring Boot 3.4.3
- Spring Cloud Gateway MVC
- Spring Security + OAuth2
- Spring Data JPA
- Jackson (JSON processing)
- Lombok (code generation)
- H2 Database

### Frontend Key Dependencies
- Vue.js 3
- Vue Router 4
- Vuex 4
- Axios
- Tailwind CSS
- Vite (build tool)

---

## 🚀 Performance Tips

### Backend
- Health check caching: 30 seconds
- Use indexes on audit queries
- Transaction management for route changes

### Frontend
- Lazy load route components
- Debounce search input
- Cache API responses
- Minimize bundle size

---

## 📋 Testing

### Backend Tests
```bash
./gradlew test

# Run specific test
./gradlew test --tests HealthAggregatorControllerUnitTest
```

### Frontend Tests (To be added)
```bash
npm run test
npm run test:unit
npm run test:e2e
```

---

## 🔧 Configuration

### Backend Properties
- **API Port**: 9000
- **Database**: H2 (in-memory)
- **OAuth2 Issuer**: Configurable in application.yml
- **CORS Origins**: Localhost:3000, 5173

### Frontend Environment
- **Dev API Proxy**: http://localhost:9000
- **Dev Port**: 5173
- **Prod Build Output**: `build/resources/main/static/admin/dist/`

---

## 📚 Additional Resources

### Documentation Files
- `PROJECT_COMPLETION_SUMMARY.md` - Full project overview
- `PHASE_COMPLETION_SUMMARY.md` - Phase details
- `FRONTEND_IMPLEMENTATION_COMPLETE.md` - Frontend specifics
- `FRONTEND_QUICK_START.md` - Getting started guide

### Code Documentation
- Javadoc comments in backend classes
- JSDoc comments in Vue components
- Inline comments for complex logic
- API endpoint documentation via Swagger

---

## 🎯 Development Workflow

1. **Modify Backend**
   - Edit Java files
   - Run `./gradlew build`
   - Backend auto-recompiles during bootRun

2. **Modify Frontend**
   - Edit Vue files
   - Hot reload automatically
   - Check console for errors

3. **Database Changes**
   - Edit schema.sql
   - Restart backend to reinitialize

4. **Test Changes**
   - Use Postman for API testing
   - Use browser DevTools for frontend
   - Check console logs for errors

5. **Commit Code**
   - Follow conventional commits
   - Include issue reference if applicable

---

**Keep this guide handy for development!** 📌

