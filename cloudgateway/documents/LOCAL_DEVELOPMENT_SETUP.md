# 🔓 LOCAL DEVELOPMENT MODE - SECURITY DISABLED

## What Changed

Security has been disabled for local development. You can now:

✅ Access all APIs without authentication
✅ Frontend auto-logs in on page load
✅ No OAuth2 required
✅ Test freely without tokens

---

## 🚀 Quick Start

### Backend
```bash
cd /Users/thejkaruneegar/open-api-workspace/cloudgateway
./gradlew clean build
./gradlew bootRun
```

**Backend**: http://localhost:9000
**H2 Console**: http://localhost:9000/h2-console

### Frontend
```bash
cd src/main/resources/static/admin
npm install
npm run dev
```

**Frontend**: http://localhost:5173

---

## 🧪 Testing

### Test API with curl (No token required)
```bash
# List routes
curl http://localhost:9000/api/admin/routes

# Create route
curl -X POST http://localhost:9000/api/admin/routes \
  -H "Content-Type: application/json" \
  -d '{
    "id":"testroute",
    "uri":"http://localhost:8080",
    "enabled":true,
    "predicates":[{"name":"Path","args":"/api/**"}],
    "filters":[]
  }'

# Get audit history
curl http://localhost:9000/api/admin/audit/routes/testroute

# Check health
curl http://localhost:9000/api/admin/routes/health/status
```

### Test Frontend
- Access: http://localhost:5173
- Auto-logged in as "Developer"
- All features available immediately

---

## 📝 What's Disabled

✅ **Backend Security**
- No OAuth2 validation
- No JWT token check
- No role verification
- All endpoints accessible

✅ **Frontend Security**
- Auto-login on page load
- No credentials required
- No token refresh needed

---

## ⚠️ IMPORTANT: For Production

**DO NOT use this configuration in production!**

When ready for production:

1. **Re-enable OAuth2** in `SecurityConfig.java`
2. **Configure OAuth2 provider** in `application.yml`
3. **Update frontend** login to use real OAuth2
4. **Remove auto-login** from Login.vue

---

## 🔐 Re-enabling Security

To restore security for production:

### 1. Update SecurityConfig.java
```java
.authorizeHttpRequests(authz -> authz
    .requestMatchers("/health", "/actuator/health", "/h2-console/**").permitAll()
    .requestMatchers("/api/gateway/**").permitAll()
    .requestMatchers("/api/admin/**").authenticated()
    .requestMatchers("/api/actuator/**").hasRole("ADMIN")
    .anyRequest().permitAll()
)
.oauth2ResourceServer(oauth2 -> oauth2
    .jwt(jwt -> jwt
        .jwtAuthenticationConverter(jwtAuthenticationConverter())
    )
)
```

### 2. Update application.yml
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://your-oauth2-provider.com
          jwk-set-uri: https://your-oauth2-provider.com/.well-known/jwks.json
```

### 3. Update Login.vue
```javascript
// Remove auto-login
// Remove onMounted hook
// Restore credential-based login
```

---

## 📋 Current Configuration

| Component | Status | Details |
|-----------|--------|---------|
| Backend OAuth2 | ❌ Disabled | All requests allowed |
| Frontend Auth | ❌ Disabled | Auto-login enabled |
| CORS | ✅ Enabled | Frontend can call backend |
| H2 Console | ✅ Enabled | Database access available |
| API Endpoints | ✅ All Open | No authentication needed |

---

## 🎯 Development Workflow

1. **Make Backend Changes**
   ```bash
   # Edit Java files
   ./gradlew clean build
   ./gradlew bootRun
   ```

2. **Make Frontend Changes**
   ```bash
   # Frontend hot-reloads automatically
   npm run dev
   ```

3. **Test API**
   ```bash
   # Use curl or Postman - no auth needed
   curl http://localhost:9000/api/admin/routes
   ```

4. **Access Portal**
   ```bash
   # Auto-logs in immediately
   http://localhost:5173
   ```

---

## ✅ Testing Checklist

- [ ] Backend starts without OAuth2 errors
- [ ] Frontend auto-logs in
- [ ] Can access dashboard
- [ ] Can list routes via API
- [ ] Can create route via API
- [ ] Can create route via UI
- [ ] Can edit route
- [ ] Can delete route
- [ ] Health monitor works
- [ ] Audit log displays changes

---

## 💡 Tips

✅ Use `curl` for quick API testing  
✅ Use browser DevTools Network tab to debug  
✅ Check logs for any errors  
✅ Use H2 console to inspect database  
✅ Keep security disabled only during development  

---

**Remember**: This is for LOCAL DEVELOPMENT ONLY ⚠️

All security is restored for production deployment!

