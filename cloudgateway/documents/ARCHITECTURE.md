# Admin Portal Architecture Diagram

## Application Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                     Browser / User                              │
└──────────────────────────────┬──────────────────────────────────┘
                               │
                               ↓
┌─────────────────────────────────────────────────────────────────┐
│                    index.html (Entry Point)                      │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │ CDN Libraries:                                            │   │
│  │ • Tailwind CSS (Styling)                                 │   │
│  │ • Material Icons (Icons)                                 │   │
│  │ • Vue.js 2.6.14 (Framework)                             │   │
│  │ • Axios (HTTP Client)                                   │   │
│  └──────────────────────────────────────────────────────────┘   │
└──────────────────────────────┬──────────────────────────────────┘
                               │
                ┌──────────────┼──────────────┐
                ↓              ↓              ↓
        ┌──────────────┐  ┌──────────┐  ┌──────────────┐
        │ store.js     │  │ app.js   │  │ components/  │
        │ (State)      │  │ (Logic)  │  │ (UI)         │
        └──────────────┘  └──────────┘  └──────────────┘
                               │
                ┌──────────────┴──────────────┐
                │                             │
         ┌──────▼──────┐            ┌────────▼───────┐
         │ Components  │            │ API Endpoints  │
         ├─────────────┤            ├────────────────┤
         │ header.js   │            │ GET /routes    │
         │ sidebar.js  │            │ POST /routes   │
         │ login.js    │            │ PUT /routes    │
         │ dashboard.js│            │ DELETE /routes │
         │ routes.js   │            │ GET /audit     │
         │ route-...js │            │ ...            │
         │ audit.js    │            └────────────────┘
         │ health.js   │                    │
         │ modal.js    │                    ↓
         └─────────────┘            ┌────────────────┐
                │                   │ Backend Server │
                │                   │ :9000          │
                │                   └────────────────┘
                └─────────────────────────────────────┘
```

## Component Hierarchy

```
┌─ Root Vue Instance (app.js)
│
├─ Layout Structure
│  ├─ app-header (header.js)
│  │  ├─ User Info Display
│  │  └─ Logout Button
│  │
│  ├─ Main Container
│  │  ├─ app-sidebar (sidebar.js)
│  │  │  ├─ Dashboard Link
│  │  │  ├─ Routes Link
│  │  │  ├─ Audit Link
│  │  │  └─ Health Link
│  │  │
│  │  └─ Content Area
│  │     ├─ login-view (login.js)
│  │     ├─ dashboard-view (dashboard.js)
│  │     ├─ routes-view (routes.js)
│  │     ├─ route-details-view (route-details.js)
│  │     ├─ audit-view (audit.js)
│  │     └─ health-view (health.js)
│  │
│  └─ Modals
│     └─ route-modal (modal.js)
│        ├─ Route Info Section
│        ├─ Status Section
│        ├─ Predicates Section
│        └─ Filters Section
│
└─ Global State (store.js)
   ├─ routes[]
   ├─ auditLogs[]
   ├─ currentUser
   ├─ formData{}
   └─ Helper Methods
```

## Data Flow

```
User Action (Click/Input)
        │
        ↓
   Component Event
   (@click, @input)
        │
        ↓
   Method in app.js
   (login, fetchRoutes, etc.)
        │
        ├──→ Update State (store.js)
        │        │
        │        ↓
        │   Vue Reactivity
        │        │
        │        ↓
        │   Re-render Components
        │
        └──→ API Call (Axios)
                 │
                 ↓
            Backend Response
                 │
                 ↓
            Update State
                 │
                 ↓
            Components Re-render
                 │
                 ↓
            User Sees Update
```

## File Organization

```
admin/
├── index.html                    ← Entry Point
│
├── js/
│   ├── store.js                  ← Global State
│   │   ├── state object
│   │   ├── isLoggedIn()
│   │   ├── getFilteredRoutes()
│   │   └── formatDate()
│   │
│   ├── app.js                    ← Main Application
│   │   ├── Vue Instance
│   │   ├── Data Properties
│   │   ├── Methods (login, fetchRoutes, etc.)
│   │   ├── Computed Properties
│   │   └── Main Template
│   │
│   └── components/               ← UI Components
│       ├── header.js             (400+ lines)
│       ├── sidebar.js            (Layout)
│       ├── login.js              (Auth)
│       ├── dashboard.js          (Overview)
│       ├── routes.js             (List & Search)
│       ├── route-details.js      (View Details)
│       ├── audit.js              (Logs)
│       ├── health.js             (Monitoring)
│       └── modal.js              (Forms)
│
├── QUICKSTART.md                 ← Quick Start Guide
├── STRUCTURE.md                  ← Architecture Docs
├── REFACTORING_SUMMARY.md        ← Change Summary
└── VERIFICATION_CHECKLIST.md     ← Testing Checklist
```

## State Management Flow

```
Store (store.js)
├── currentUser: string
├── currentView: 'login' | 'dashboard' | 'routes' | 'details' | 'audit' | 'health'
├── routes: Route[]
├── selectedRoute: Route | null
├── auditLogs: AuditLog[]
├── formData: RouteForm
├── searchQuery: string
├── filterStatus: 'enabled' | 'disabled' | ''
├── showModal: boolean
├── modalType: 'create' | 'edit'
├── alertMessage: string
├── alertType: 'success' | 'danger' | 'info'
└── loading: boolean

Computed Properties
├── isLoggedIn → checks currentUser
├── filteredRoutes → filters by search + status
└── formatDate() → utility function
```

## Authentication Flow

```
1. User opens app.js
   │
   ├─ Check localStorage.authToken
   │
   ├─ IF token exists
   │  ├─ Set currentUser
   │  ├─ Set currentView = 'dashboard'
   │  └─ Fetch routes
   │
   └─ ELSE
      └─ Show login view

2. User clicks Login
   │
   ├─ Validate form
   ├─ Set loading = true
   ├─ Simulate login (500ms)
   │
   ├─ Save token to localStorage
   ├─ Set currentUser
   ├─ Set currentView = 'dashboard'
   ├─ Fetch routes
   │
   └─ Set loading = false

3. User clicks Logout
   │
   ├─ Clear localStorage
   ├─ Clear state (currentUser, routes, etc.)
   └─ Set currentView = 'login'
```

## API Integration

```
Frontend (Components)
        ↓ Axios Request
        │ (GET /api/admin/routes)
        │
        ↓
Backend (Spring Boot)
        │ Validates JWT Token
        │ Fetches from Database
        │ Returns JSON Response
        │
        ↓ JSON Data
Frontend (app.js)
        ↓ Update store.state.routes
        │
        ↓
Components (vue reactivity)
        ↓
User (Browser)
```

## Component Communication Pattern

```
                    Parent Component
                   (app.js template)
                         │
            ┌────────────┼────────────┐
            │            │            │
            ↓            ↓            ↓
        Child 1      Child 2      Child 3
        (header)    (sidebar)    (routes)
            │            │            │
         Props         Props        Props
            ↓            ↓            ↓
        Events       Events       Events
            ↑            ↑            ↑
            │            │            │
            └────────────┼────────────┘
                         │
                         ↓
                  Parent Methods
                  (login, navigate,
                   fetchRoutes, etc.)
                         │
                         ↓
                  Update state.js
```

## View Routing (Client-side)

```
Routes:
├─ login
│  └─ Shows: LoginView
│     └─ Emits: @login
│        └─ Calls: login()
│
├─ dashboard
│  └─ Shows: DashboardView
│     └─ Emits: @open-modal, @navigate
│
├─ routes
│  └─ Shows: RoutesView
│     └─ Search/Filter functionality
│     └─ Emits: @edit-route, @delete-route, etc.
│
├─ details
│  └─ Shows: RouteDetailsView
│     └─ Display: Route information
│     └─ Emits: @edit-route, @back
│
├─ audit
│  └─ Shows: AuditView
│     └─ Display: Audit logs table
│
└─ health
   └─ Shows: HealthView
      └─ Display: Health status cards
```

## Modal Workflow

```
User clicks "New Route"
        ↓
openCreateModal()
        ↓
Reset formData
Set modalType = 'create'
Set showModal = true
        ↓
Modal displays (route-modal.js)
        ↓
User fills form
        ↓
User clicks Create/Update
        ↓
Modal emits @create/@update
        ↓
createRoute() / updateRoute()
        ↓
API POST/PUT request
        ↓
Response received
        ↓
Update store.routes
Show alert
Close modal
        ↓
Components re-render
```

## CSS Styling Architecture

```
Tailwind CSS (CDN)
├─ Utility Classes
│  ├─ Colors: bg-gray-900, text-blue-400, etc.
│  ├─ Layout: flex, grid, gap, etc.
│  ├─ Spacing: p-4, m-2, etc.
│  ├─ Typography: text-lg, font-bold, etc.
│  └─ Responsive: sm:, md:, lg:, etc.
│
├─ Dark Theme
│  ├─ Background: bg-gray-900, bg-gray-800
│  ├─ Text: text-gray-100, text-gray-400
│  └─ Borders: border-gray-700
│
├─ Material Design Colors
│  ├─ Primary: blue-600, blue-400
│  ├─ Success: green-600, green-400
│  ├─ Danger: red-600, red-400
│  └─ Info: cyan-400, blue-400
│
└─ Custom Inline (index.html)
   ├─ Scrollbar styling
   ├─ Animations (fadeIn, slideUp)
   └─ Animation classes
```

## Performance Optimization

```
Loading Strategy:
1. index.html loads (2.3 KB)
   │
   ├─ CDN libraries load async
   │  ├─ Tailwind CSS
   │  ├─ Material Icons
   │  ├─ Vue.js
   │  └─ Axios
   │
   ├─ JavaScript files load sequential
   │  ├─ store.js (1 KB)
   │  ├─ Components (8 × 8 KB avg)
   │  └─ app.js (15 KB)
   │
   └─ Vue app initializes
      └─ Renders based on state
         └─ User sees UI

Lazy Rendering:
├─ Only active view renders
├─ Modal renders only when shown
├─ Components use v-if
└─ Minimal DOM nodes at start
```

## Deployment Architecture

```
Production Server (:9000)
│
├─ /api/                    (Backend APIs)
│  ├─ /admin/routes
│  ├─ /audit/logs
│  └─ ...
│
└─ /static/admin/           (Frontend - This Portal)
   ├─ index.html
   ├─ js/
   │  ├─ store.js
   │  ├─ app.js
   │  └─ components/
   └─ CDN Links (external)
      ├─ Tailwind CSS
      ├─ Material Icons
      ├─ Vue.js
      └─ Axios
```

---

## Summary

- **Modular Architecture**: Separation of concerns with store, app, and components
- **One-way Data Flow**: State → Components → User → Events → Methods → State
- **Vue Reactivity**: Automatic re-rendering on state changes
- **Component-based**: Reusable, testable, maintainable components
- **No Build Step**: Direct CDN loading, no transpilation needed
- **Scalable Design**: Easy to add new views, components, and features


