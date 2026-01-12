# Fix: "Not a managed type" JPA Error

## Problem

The application was failing to start with the following error:

```
Error creating bean with name 'routeRepository' defined in 
com.tk.learn.cloudgateway.repository.RouteRepository defined in 
@EnableJpaRepositories declared on JpaRepositoriesRegistrar.EnableJpaRepositoriesConfiguration: 
Not a managed type: class com.tk.learn.cloudgateway.dynamic.DbRouteModels$DbRoute
```

## Root Cause

The `DbRoute` class was being used as a JPA entity in `RouteRepository`, but it was not annotated with proper JPA annotations:
- Missing `@Entity` annotation
- Missing `@Table` annotation  
- Missing `@Id` annotation
- Missing `@Column` annotations for database field mappings
- Fields like `metadata`, `predicates`, and `filters` need to be marked as `@Transient` since they're loaded from separate tables

## Solution

### File: `src/main/java/com/tk/learn/cloudgateway/dynamic/DbRouteModels.java`

Added JPA annotations to the `DbRoute` class:

```java
@Entity
@Table(name = "gw_routes")
public static class DbRoute {
    @Id
    public String id;
    
    @Column(name = "uri")
    public String uri;
    
    @Column(name = "order_no")
    public Integer order;
    
    @Column(name = "enabled")
    public boolean enabled;
    
    @Transient
    public Map<String, String> metadata = new LinkedHashMap<>();
    
    @Transient
    public List<PredicateRow> predicates = new ArrayList<>();
    
    @Transient
    public List<FilterRow> filters = new ArrayList<>();
}
```

### Key Changes:
1. **`@Entity`**: Marks the class as a JPA managed entity
2. **`@Table(name = "gw_routes")`**: Maps to the database table `gw_routes`
3. **`@Id`**: Designates the `id` field as the primary key
4. **`@Column(name = "...")`**: Maps fields to corresponding database columns
5. **`@Transient`**: Marks fields that are NOT persisted in the main table (they're stored in separate tables):
   - `metadata` is stored in `gw_route_metadata` table
   - `predicates` are stored in `gw_route_predicates` table
   - `filters` are stored in `gw_route_filters` table

### File: `src/main/resources/application.yml`

Fixed OAuth2 configuration for local development by providing proper JWT configuration values:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8081/oauth2
          jwk-set-uri: http://localhost:8081/oauth2/jwks
```

## Result

✅ Application now starts successfully
✅ Spring Data JPA recognizes `DbRoute` as a managed entity
✅ `RouteRepository` can be properly instantiated
✅ All beans are wired correctly

## Testing

Start the application with:
```bash
./gradlew bootRun
```

The application should start successfully on port 9000 with the message:
```
Tomcat started on port 9000 (http) with context path '/'
Started CloudgatewayApplication in X.XXX seconds
```

