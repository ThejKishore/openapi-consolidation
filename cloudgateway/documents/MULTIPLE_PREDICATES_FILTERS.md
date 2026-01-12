# Multiple Predicates, Filters, and Metadata Support

## Overview

The route creation and editing modal has been enhanced to support:
- **Multiple Predicates** - Add as many path predicates as needed
- **Multiple Filters** - Add as many transformation filters as needed
- **Route Metadata** - Add custom key-value metadata to routes

## How to Use

### Adding Multiple Predicates

1. Open the route creation/editing modal
2. Scroll to **"Path Predicates"** section
3. Select a **Predicate Type**:
   - **Path Pattern** - Match incoming request paths (e.g., `/api/**`)
   - **HTTP Method** - Match specific HTTP methods (e.g., `GET`, `POST`)
   - **Header** - Match specific headers (e.g., `Accept=application/json`)

4. Enter the **Predicate Value** - the actual pattern or condition
5. Click **"Add Predicate"** button
6. Repeat steps 2-5 to add more predicates
7. Each added predicate appears in the list above with a delete button

**Example:**
```
Predicate 1: Type=Path, Value=/api/**
Predicate 2: Type=Method, Value=GET
Predicate 3: Type=Header, Value=Accept=application/json
```

### Adding Multiple Filters

1. Open the route creation/editing modal
2. Scroll to **"Route Filters"** section
3. Select a **Filter Type**:
   - **StripPrefix** - Remove prefix from path (e.g., `1` removes first segment)
   - **RewritePath** - Rewrite path pattern (e.g., `/path,/newpath`)
   - **AddRequestHeader** - Add header to request (e.g., `X-Custom,value`)
   - **AddResponseHeader** - Add header to response
   - **RemoveRequestHeader** - Remove header from request

4. Enter the **Filter Value** - the transformation details
5. Click **"Add Filter"** button
6. Repeat steps 2-5 to add more filters
7. Each added filter appears in the list above with a delete button

**Example:**
```
Filter 1: Type=StripPrefix, Value=1
Filter 2: Type=AddRequestHeader, Value=X-Forwarded-For,192.168.1.1
Filter 3: Type=AddResponseHeader, Value=X-Custom-Header,route-value
```

### Adding Route Metadata

1. Open the route creation/editing modal
2. Scroll to **"Route Metadata"** section
3. Enter a **Key** - metadata property name (e.g., `environment`, `version`, `owner`)
4. Enter a **Value** - metadata property value (e.g., `production`, `v1.0.0`, `platform-team`)
5. Click **"Add Metadata"** button
6. Repeat steps 3-5 to add more metadata
7. Each metadata entry appears in the list above with a delete button

**Example:**
```
Metadata 1: Key=environment, Value=production
Metadata 2: Key=version, Value=v1.0.0
Metadata 3: Key=owner, Value=platform-team
Metadata 4: Key=team, Value=API-Gateway
```

## Form Structure

### Route Information Section
- Route ID (required, disabled when editing)
- Backend URI (required)
- Route Order

### Status Section
- Route Enabled (checkbox)

### Path Predicates Section
- List of added predicates with delete buttons
- Add New Predicate form:
  - Predicate Type dropdown
  - Predicate Value input
  - Add Predicate button

### Route Filters Section
- List of added filters with delete buttons
- Add New Filter form:
  - Filter Type dropdown
  - Filter Value input
  - Add Filter button

### Route Metadata Section
- List of added metadata with delete buttons
- Add New Metadata form:
  - Key input
  - Value input
  - Add Metadata button

## API Payload Format

When a route is created or updated, the payload includes:

```json
{
  "id": "my-route",
  "uri": "https://backend.example.com",
  "order": 0,
  "enabled": true,
  "predicates": [
    {
      "name": "Path",
      "args": "/api/**"
    },
    {
      "name": "Method",
      "args": "GET"
    }
  ],
  "filters": [
    {
      "name": "StripPrefix",
      "args": "1"
    },
    {
      "name": "AddRequestHeader",
      "args": "X-Custom,value"
    }
  ],
  "metadata": {
    "environment": "production",
    "version": "v1.0.0",
    "owner": "team-name"
  }
}
```

## Key Features

✅ **Add multiple predicates** - No limit on number of predicates
✅ **Add multiple filters** - No limit on number of filters
✅ **Add custom metadata** - Store any key-value data with routes
✅ **Easy to manage** - Add and remove items with buttons
✅ **Clear display** - Lists show all added items with delete buttons
✅ **Input validation** - Alerts if type or value is missing
✅ **Auto-clear fields** - Input fields clear after adding item
✅ **Edit support** - Existing predicates, filters, and metadata load when editing routes
✅ **Responsive design** - Works on mobile, tablet, and desktop
✅ **Visual feedback** - Clear sections with icons and descriptions

## Component Changes

### Modal Component (modal.js)
- Added methods for managing predicates, filters, and metadata
- Enhanced template with sections for listing existing items
- Added forms for adding new items
- Delete buttons for removing items

### App Component (app.js)
- Updated `formData` initialization
- Added methods: `addPredicate`, `removePredicate`, `addFilter`, `removeFilter`, `addMetadata`, `removeMetadata`
- Updated `editRoute` to properly initialize new fields
- Updated `openCreateModal` to reset all new fields
- Updated `closeModal` to reset all new fields
- Added event bindings in route-modal component

## Data Flow

1. **Create/Edit Route**
   - User opens modal
   - Forms display existing data (for edit)
   - User adds predicates, filters, metadata
   - User clicks Create/Update
   - Data sent to backend API

2. **Display Predicates**
   - Predicate array stored in `formData.predicates`
   - `v-for` loop displays each predicate
   - Delete button removes from array

3. **Display Filters**
   - Filter array stored in `formData.filters`
   - `v-for` loop displays each filter
   - Delete button removes from array

4. **Display Metadata**
   - Metadata object stored in `formData.metadata`
   - `v-for` loop displays each entry
   - Delete button removes key from object

## Validation

- Both type and value must be provided before adding
- Empty input fields trigger alert
- Fields auto-clear after successful add

## Styling

All new sections use:
- Consistent dark theme (bg-gray-900)
- Material Design icons for actions
- Green buttons for adding items
- Red delete buttons
- Clear labels and descriptions
- Responsive flex layouts

## Examples

### Example 1: Simple Path-Based Route
```
Predicates: Path = /api/**
Filters: StripPrefix = 1
Metadata: 
  - environment: development
  - version: v1.0.0
```

### Example 2: Complex Route with Headers
```
Predicates:
  - Path = /admin/**
  - Method = POST
Filters:
  - AddRequestHeader = Authorization,Bearer-token
  - AddRequestHeader = X-Route-ID,admin
Metadata:
  - environment: production
  - owner: admin-team
  - security-level: high
```

### Example 3: Multi-Service Route
```
Predicates:
  - Path = /service/**
  - Header = X-Service-Version=v2
Filters:
  - StripPrefix = 1
  - AddRequestHeader = X-Forwarded-Service,service-name
  - AddResponseHeader = X-Service-Version,2.0.0
Metadata:
  - environment: staging
  - service: multi-tenant
  - version: v2.0.0
  - team: services
```

## Troubleshooting

**Issue: Predicate/Filter not appearing in list**
- Check that you selected a type and entered a value
- Click the "Add" button (not just pressing Enter)

**Issue: Metadata not saving**
- Ensure both key and value are provided
- Keys must be unique (cannot have duplicate keys)

**Issue: Changes lost when switching routes**
- Changes are only saved when you click Create/Update
- Don't navigate away without saving

## Future Enhancements

- Predicate/filter templates for common patterns
- Metadata suggestions based on history
- Bulk import/export of predicates and filters
- Predicate/filter testing interface
- Metadata validation rules


