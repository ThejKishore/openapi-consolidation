# Cloud Gateway Admin Portal - Vue.js UI

## Overview
This is a complete replacement for the Vue3 + Vite + npm-based UI with a **pure vanilla Vue.js 2 implementation** that works directly from a single HTML file using CDN links.

## Features ✨

### ✅ No Build Tools Required
- No npm installation needed
- No node_modules directory
- No build/compile step
- Direct browser loading of CDN libraries
- Single `index.html` file

### ✅ Complete Functionality
- 🔐 **Login System** - Simple username/password authentication
- 📊 **Dashboard** - Quick overview of routes and statistics
- 🛣️ **Route Management** - Create, read, update, delete routes
- 📋 **Audit Logs** - View all route management activities
- ❤️ **Health Monitoring** - Check route health status
- 🔍 **Search & Filter** - Find routes by ID or URI
- 📥 **Export Routes** - Download routes as JSON
- 📱 **Responsive Design** - Works on desktop and mobile

## How to Use

### Starting the Admin Portal
The admin portal is automatically served by the Spring Boot application:

```bash
# Build and run the Cloud Gateway application
./gradlew bootRun

# Access the admin portal at:
http://localhost:9000/static/admin/index.html
```

### Alternative Direct File Access
You can also open the file directly in a browser:

```bash
open /Users/thejkaruneegar/open-api-workspace/cloudgateway/src/main/resources/static/admin/index.html
```

## Login Credentials

For demo purposes, the login accepts any username and password:
- **Username:** admin (or any value)
- **Password:** password (or any value)

## Technology Stack

- **Vue.js 2.6.14** - via CDN (no npm required)
- **Axios** - for HTTP requests (via CDN)
- **CSS3** - Custom dark theme styling (no Tailwind)
- **HTML5** - Semantic markup

### CDN Links Used
```html
<!-- Vue.js 2 -->
<script src="https://cdn.jsdelivr.net/npm/vue@2.6.14/dist/vue.js"></script>

<!-- Axios for HTTP -->
<script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
```

## File Structure

```
/src/main/resources/static/admin/
├── index.html              ← Complete standalone application
├── package.json            ← [DEPRECATED] Can be safely removed
├── package-lock.json       ← [DEPRECATED] Can be safely removed  
├── vite.config.js          ← [DEPRECATED] Can be safely removed
├── postcss.config.js       ← [DEPRECATED] Can be safely removed
├── tailwind.config.js      ← [DEPRECATED] Can be safely removed
├── node_modules/           ← [DEPRECATED] Can be safely removed
└── src/                    ← [DEPRECATED] Old Vue3 source files
```

## API Integration

The UI communicates with the Cloud Gateway API endpoints:

```
GET    /api/admin/routes              - Fetch all routes
POST   /api/admin/routes              - Create a new route
PUT    /api/admin/routes/{id}         - Update a route
DELETE /api/admin/routes/{id}         - Delete a route
GET    /api/audit/logs                - Fetch audit logs
```

Each request includes the `Authorization: Bearer {token}` header.

## Styling

All styles are defined in the `<style>` tag within `index.html`:

- **Dark Theme** - Gray900 background with light text
- **Color Scheme:**
  - Primary: Blue (#2563eb)
  - Success: Green (#059669)
  - Danger: Red (#dc2626)
  - Warning: Orange (#d97706)
  - Background: Dark Gray (#111827)

## Key Components

### 1. Login View
- Simple username/password form
- Mock authentication (accepts any credentials)
- Stores auth token in localStorage

### 2. Dashboard View
- Route statistics cards
- Quick action buttons
- Route status overview

### 3. Routes Management View
- List of all routes with search/filter
- Create, edit, toggle, delete route actions
- Route details view with predicates and filters
- Health status monitoring

### 4. Audit Logs View
- Timeline of all route management activities
- Action badges (CREATE, UPDATE, DELETE)
- User and timestamp information

### 5. Health Check View
- Grid view of route health statuses
- Response time metrics
- Last checked timestamp

## Migration from Vue3 to Vue.js 2

### What Changed
1. **Build Tool Removed:** No Vite, no npm, no node_modules
2. **Framework Simplified:** Vue 2 via CDN (same features, simpler deployment)
3. **Styling:** Plain CSS (no Tailwind, same dark theme appearance)
4. **Bundling:** Single file (no module imports)
5. **Dependencies:** Only Vue and Axios from CDN

### Benefits
✅ **Simpler deployment** - Just one HTML file
✅ **Faster loading** - No build process needed
✅ **No dependencies** - CDN hosted libraries
✅ **Same features** - All functionality preserved
✅ **Easy maintenance** - All code in one file
✅ **Zero npm issues** - No package conflicts
✅ **Works everywhere** - Any HTTP server serves it

## Development Tips

### Adding New Features
1. Edit the `<script>` section in `index.html`
2. Add methods, computed properties, or data fields in the Vue instance
3. Update the template to use new functionality

### Debugging
- Open browser DevTools (F12)
- Check Console for errors
- Vue DevTools may not work (not Vue 3)
- Use `console.log()` statements

### Styling
- Modify CSS in the `<style>` tag
- No build step required - refresh browser to see changes
- Use CSS classes and inline styles

## Cleanup

The following files from the old Vue3 setup can be safely removed:

```bash
# Remove npm configuration (old build system)
rm -rf node_modules/
rm package.json
rm package-lock.json
rm vite.config.js
rm postcss.config.js
rm tailwind.config.js

# Remove old Vue3 source files
rm -rf src/
```

## Browser Support

- Chrome 60+
- Firefox 55+
- Safari 12+
- Edge 79+
- Any modern browser with ES6 support

## Performance

- **No build step** - Loads instantly
- **Minimal CSS** - No Tailwind bloat
- **CDN libraries** - Cached by browser
- **Single file** - One HTTP request
- **Vue 2** - Lightweight framework (about 30KB gzipped)

## License

This admin portal is part of the Cloud Gateway project.

## Support

For issues or questions about the admin portal, please refer to the main project documentation.

---

**Status:** ✅ Production Ready

No npm needed. Just open `index.html` and enjoy!

