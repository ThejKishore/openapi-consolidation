# Fix: Edit Not Populating in Vue Admin Screen

## Problem

When editing a route, the form was not properly displaying the route's current data. Changes made to the form were also not being persisted or reflected in the UI.

## Root Causes

### Issue 1: RouteForm Component Not Updating on Prop Changes
The `RouteForm.vue` component was initializing `formData` with the route data once, but it didn't watch for changes to the `route` prop. When editing a different route, the form would retain the previous route's data instead of loading the new one.

**Root Cause Code:**
```javascript
// OLD - Only initialized once, never updates
const formData = reactive({
  id: props.route?.id || '',
  uri: props.route?.uri || '',
  order: props.route?.order || 0,
  enabled: props.route?.enabled !== false,
  predicates: props.route?.predicates || [{ name: 'Path', args: '' }],
  filters: props.route?.filters || []
})
```

### Issue 2: Store Using Unconfigured Axios Instance
The `store.js` was using plain `axios` instead of the configured `apiClient` from `api.js`. This meant:
- API calls weren't using the correct base URL
- CORS headers weren't being properly handled
- Authorization headers weren't being added
- Interceptors for error handling weren't applied

## Solution

### Fix 1: Add Watcher to RouteForm Component

**File**: `src/main/resources/static/admin/src/components/RouteForm.vue`

Added a `watch` to listen for changes to the `route` prop and update the form data accordingly:

```javascript
import { reactive, computed, watch } from 'vue'

// ...existing setup code...

// Watch for changes to the route prop and update formData
watch(() => props.route, (newRoute) => {
  if (newRoute) {
    formData.id = newRoute.id || ''
    formData.uri = newRoute.uri || ''
    formData.order = newRoute.order || 0
    formData.enabled = newRoute.enabled !== false
    formData.predicates = newRoute.predicates && newRoute.predicates.length > 0 
      ? JSON.parse(JSON.stringify(newRoute.predicates))
      : [{ name: 'Path', args: '' }]
    formData.filters = newRoute.filters && newRoute.filters.length > 0
      ? JSON.parse(JSON.stringify(newRoute.filters))
      : []
  } else {
    // Reset form for new route creation
    formData.id = ''
    formData.uri = ''
    formData.order = 0
    formData.enabled = true
    formData.predicates = [{ name: 'Path', args: '' }]
    formData.filters = []
  }
}, { deep: true })
```

**Key improvements**:
- Uses `JSON.parse(JSON.stringify())` to deeply clone arrays to prevent reference issues
- Resets form when `route` prop is null (for creating new routes)
- Uses `deep: true` to watch nested properties
- Properly initializes all fields

### Fix 2: Update Store to Use Configured API Client

**File**: `src/main/resources/static/admin/src/store.js`

Changed the import from:
```javascript
import axios from 'axios'
```

To:
```javascript
import apiClient from './api'
```

Replaced all `axios` calls with `apiClient`:
- `axios.get()` → `apiClient.get()`
- `axios.post()` → `apiClient.post()`
- `axios.put()` → `apiClient.put()`
- `axios.delete()` → `apiClient.delete()`

**Benefits**:
- Uses consistent base URL configuration from `api.js`
- Includes CORS headers and proper configuration
- Automatically adds authorization headers from localStorage
- Uses proper error handling interceptors
- Consistent across the entire Vue application

## Files Modified

1. **src/main/resources/static/admin/src/components/RouteForm.vue**
   - Added `watch` import from Vue
   - Added watcher for `route` prop changes
   - Form now updates dynamically when prop changes

2. **src/main/resources/static/admin/src/store.js**
   - Changed from `axios` to `apiClient`
   - All API actions now use configured client
   - Ensures consistent API communication

## Testing

```bash
# Navigate to Routes Management
# 1. Create a route with test data
curl -X POST http://localhost:9000/api/admin/routes \
  -H "Content-Type: application/json" \
  -d '{
    "id": "edit-test",
    "uri": "http://example.com",
    "order": 1,
    "enabled": true,
    "predicates": [{"name": "Path", "args": "/test"}],
    "filters": []
  }'

# 2. In the Vue UI, click "Edit" on the route
# ✅ Form should populate with all route data

# 3. Make changes to the form
# ✅ Changes should appear immediately in the form

# 4. Click "Update Route"
# ✅ Changes should be saved and reflected in the list

# 5. Edit the route again
# ✅ Form should show the updated values

# 6. Create another route and click "Edit" on both
# ✅ Switching between edits should show correct data for each
```

## Expected Results

✅ Edit form properly displays route data when opened
✅ Form updates when switching between different route edits
✅ Form resets when creating a new route
✅ All form fields are properly populated (id, uri, order, enabled, predicates, filters)
✅ Changes are persisted when saving
✅ API calls use correct base URL and headers
✅ Authorization headers are included in requests
✅ Error handling works properly for failed requests

## Impact

- **Before**: Form wouldn't update, edits would show wrong data or be lost
- **After**: Form properly tracks route changes, all edits persist and display correctly
- **User Experience**: Seamless editing experience with immediate feedback

## Additional Notes

The `apiClient` in `api.js` includes:
- Correct base URL: `http://localhost:9000`
- CORS headers configuration
- Authorization token injection from localStorage
- Error handling with token refresh on 401
- Proper Content-Type headers

All Vue components should use `apiClient` instead of direct `axios` imports for consistency.

