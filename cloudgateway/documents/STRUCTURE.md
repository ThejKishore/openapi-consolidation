# Cloud Gateway Admin Portal - File Structure

## Overview
The admin portal has been refactored into a modular, component-based architecture using Vue.js 2 with Tailwind CSS and Material Design icons.

## Directory Structure

```
admin/
├── index.html                 # Main HTML file (entry point)
├── js/
│   ├── app.js                # Main Vue application instance
│   ├── store.js              # Global state management
│   └── components/
│       ├── header.js         # Header component
│       ├── sidebar.js        # Sidebar navigation component
│       ├── login.js          # Login view component
│       ├── dashboard.js      # Dashboard view component
│       ├── routes.js         # Routes management view component
│       ├── route-details.js  # Route details view component
│       ├── audit.js          # Audit logs view component
│       ├── health.js         # Health check view component
│       └── modal.js          # Route creation/editing modal component
├── README.md                 # This file
└── MIGRATION.md             # Migration notes

```

## File Descriptions

### Core Files

#### index.html
- Clean HTML5 entry point with minimal inline styles
- Includes Tailwind CSS and Material Icons CDN links
- Loads all JavaScript files in the correct order
- No inline CSS, all styling done with Tailwind classes

### JavaScript Files

#### store.js
Global state management object containing:
- Application state (currentView, currentUser, routes, etc.)
- Helper methods for state operations
- Computed properties for filtering and formatting

#### app.js
Main Vue application that:
- Initializes the Vue instance
- Manages all data and computed properties
- Implements all business logic methods (CRUD operations, auth, etc.)
- Defines the main application template
- Handles routing between views

### Components

#### header.js
Header component that displays:
- Application logo and title
- Current user info
- Logout button
- Uses Material Icons for visual elements

#### sidebar.js
Navigation sidebar with:
- Dashboard link
- Routes management link
- Audit logs link
- Health check link
- Active state highlighting
- Material Icons for menu items

#### login.js
Login view component featuring:
- Username/password form
- Login button with loading state
- Demo credentials info
- Responsive design

#### dashboard.js
Dashboard view displaying:
- Statistics cards (total routes, enabled, disabled, current user)
- Quick action buttons
- Material Icons for visual hierarchy
- Responsive grid layout

#### routes.js
Routes management view with:
- Search and filter functionality
- Create new route button
- Export routes feature
- Route list with action buttons
- Status badges
- Loading and empty states

#### route-details.js
Detailed route information view showing:
- Basic route details table
- Path predicates table
- Filters table
- Health status information
- Action buttons (edit, toggle, delete)
- Back navigation

#### audit.js
Audit logs view displaying:
- Table of all route management activities
- Timestamp, action, route ID, user, details columns
- Action-based color coding
- Loading and empty states

#### health.js
Health monitoring view with:
- Health status grid layout
- Per-route health information
- Response time and last checked data
- Status indicators (UP/DOWN)
- Heart icon indicators

#### modal.js
Modal dialog component for:
- Creating new routes
- Editing existing routes
- Route information section
- Status section
- Path predicates configuration
- Route filters configuration
- Form validation and loading states

## Styling Approach

### Tailwind CSS
All styling uses Tailwind CSS utility classes:
- Dark theme (bg-gray-900, bg-gray-800, etc.)
- Material Design color scheme
- Responsive breakpoints (sm, md, lg)
- Transition and animation classes
- Focus and hover states

### Material Icons
Vector icons from Google Material Icons:
- Dashboard, route, history, favorite icons for navigation
- Edit, delete, add, visibility icons for actions
- Status indicators (check_circle, cancel, error, etc.)

## Features

### Authentication
- Login form with username/password
- Token-based authentication
- Logout functionality
- Session persistence via localStorage

### Route Management
- View all routes in a formatted list
- Create new routes with configuration
- Edit existing routes
- Delete routes with confirmation
- Toggle route enabled/disabled status
- Export routes to JSON
- Search and filter routes

### Audit Trail
- View all route management activities
- Timestamp tracking
- User activity logging
- Action type (CREATE, UPDATE, DELETE)

### Health Monitoring
- Monitor health status of all routes
- Response time tracking
- Last checked timestamp
- Visual status indicators

## Component Communication

The application uses Vue's built-in event system:
- Parent-to-child: Props
- Child-to-parent: Events (@event handlers)
- Global state: Store object

## API Integration

The application communicates with backend APIs:
- `GET /api/admin/routes` - Fetch all routes
- `POST /api/admin/routes` - Create new route
- `PUT /api/admin/routes/{id}` - Update route
- `DELETE /api/admin/routes/{id}` - Delete route
- `GET /api/audit/logs` - Fetch audit logs

## Development Notes

### Adding a New View
1. Create a new component file in `js/components/`
2. Use Tailwind classes for styling
3. Use Material Icons for icons
4. Register component in the template
5. Add navigation in sidebar.js if needed

### Adding a New Feature
1. Add state to store.js if needed
2. Add methods to app.js
3. Create/update components as needed
4. Test with the API endpoints

### Customization
- Colors: Modify Tailwind classes (gray-900, blue-600, etc.)
- Icons: Replace Material Icon names
- Layout: Adjust Tailwind grid/flex classes
- Animations: Add Tailwind animation utilities

## Browser Compatibility
- Modern browsers with ES6 support
- Vue.js 2.6.14
- Material Icons support
- CSS Grid and Flexbox support

## Performance Considerations
- Vue.js is loaded from CDN
- Axios for HTTP requests
- Lazy loading of routes
- Efficient state management
- No build step required


