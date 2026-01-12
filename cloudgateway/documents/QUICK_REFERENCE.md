# Quick Reference - Multiple Predicates, Filters & Metadata

## 🚀 Quick Start

### Adding a Predicate
1. Click "Create Route" or "Edit Route"
2. Scroll to "Path Predicates" section
3. Select a **Type**: Path, Method, or Header
4. Enter a **Value**: e.g., `/api/**`, `GET`, `Accept=application/json`
5. Click **"Add Predicate"** button
6. Repeat for more predicates

### Adding a Filter
1. Click "Create Route" or "Edit Route"
2. Scroll to "Route Filters" section
3. Select a **Type**: StripPrefix, RewritePath, AddRequestHeader, etc.
4. Enter a **Value**: e.g., `1`, `/path,/newpath`, `X-Custom,value`
5. Click **"Add Filter"** button
6. Repeat for more filters

### Adding Metadata
1. Click "Create Route" or "Edit Route"
2. Scroll to "Route Metadata" section
3. Enter a **Key**: e.g., `environment`, `version`, `owner`
4. Enter a **Value**: e.g., `production`, `v1.0.0`, `my-team`
5. Click **"Add Metadata"** button
6. Repeat for more metadata

## 📋 Predicate Types

| Type | Example Value | Purpose |
|------|---------------|---------|
| Path | `/api/**` | Match URL paths |
| Method | `GET`, `POST`, `PUT` | Match HTTP methods |
| Header | `Accept=application/json` | Match request headers |

## 🔧 Filter Types

| Type | Example Value | Purpose |
|------|---------------|---------|
| StripPrefix | `1` | Remove path prefix segments |
| RewritePath | `/old,/new` | Rewrite path patterns |
| AddRequestHeader | `X-Header,value` | Add request headers |
| AddResponseHeader | `X-Header,value` | Add response headers |
| RemoveRequestHeader | `X-Header` | Remove request headers |

## 🏷️ Metadata Examples

Common metadata keys:
- `environment` → `production`, `staging`, `development`
- `version` → `v1.0.0`, `v2.1.0`
- `owner` → `team-name`, `admin-team`
- `service` → `api-gateway`, `auth-service`
- `security-level` → `high`, `medium`, `low`
- `compliance` → `hipaa`, `pci`, `gdpr`

## ✅ Form Rules

**Before adding an item:**
- ✅ Select a type (required)
- ✅ Enter a value (required)
- ❌ Don't leave fields empty

**After adding:**
- ✅ Item appears in list
- ✅ Input fields clear automatically
- ✅ Can add more items
- ✅ Can remove items with delete button

## 🎯 Common Use Cases

### Scenario 1: API with Path-based Routing
```
Predicates:
  - Type: Path, Value: /api/**

Filters:
  - Type: StripPrefix, Value: 1
  - Type: AddRequestHeader, Value: X-API-Version,2.0

Metadata:
  - Key: environment, Value: production
  - Key: version, Value: v2.0.0
```

### Scenario 2: Admin Portal with Method Filtering
```
Predicates:
  - Type: Path, Value: /admin/**
  - Type: Method, Value: POST

Filters:
  - Type: AddRequestHeader, Value: X-Admin-Route,true
  - Type: AddResponseHeader, Value: X-Admin-Version,1.0

Metadata:
  - Key: security-level, Value: high
  - Key: owner, Value: admin-team
```

### Scenario 3: Multi-Tenant Service
```
Predicates:
  - Type: Path, Value: /service/**
  - Type: Header, Value: X-Tenant-ID=.*

Filters:
  - Type: StripPrefix, Value: 1
  - Type: AddRequestHeader, Value: X-Forwarded-Service,service
  - Type: AddResponseHeader, Value: X-Service-Version,3.0

Metadata:
  - Key: service, Value: multi-tenant
  - Key: version, Value: v3.0.0
  - Key: compliance, Value: pci
  - Key: team, Value: services-team
```

## 🐛 Troubleshooting

**Problem: Predicate not added**
- ✅ Check that you selected a type
- ✅ Check that you entered a value
- ✅ Click the "Add" button (not just Enter key)

**Problem: Filter not appearing**
- ✅ Verify filter type is selected
- ✅ Verify filter value is entered
- ✅ Check that "Add Filter" button was clicked

**Problem: Metadata not saving**
- ✅ Ensure key is provided
- ✅ Ensure value is provided
- ✅ Keys must be unique

**Problem: Changes lost**
- ✅ Click "Create Route" or "Update Route" to save
- ✅ Don't navigate away without saving

## 🔄 Editing Routes

When editing a route:
1. All existing predicates, filters, and metadata load
2. You can add more items
3. You can remove items with delete buttons
4. Click "Update Route" to save changes

## 📱 Mobile & Responsive

The form works on:
- ✅ Desktop (full view)
- ✅ Tablet (responsive layout)
- ✅ Mobile (stacked layout)

All buttons and inputs are touch-friendly.

## 🔐 Notes

- All data validated on client side
- Server should validate on backend too
- No sensitive data exposed
- Same security as before

## 💡 Tips

1. **Start simple** - Add basic predicates/filters, then add metadata
2. **Use consistent naming** - Keep metadata key names consistent
3. **Document metadata** - Use metadata to document route purpose
4. **Test incrementally** - Add items one at a time and test

## 📞 Need Help?

See full documentation:
- `MULTIPLE_PREDICATES_FILTERS.md` - Complete user guide
- `IMPLEMENTATION_SUMMARY.md` - Technical details


