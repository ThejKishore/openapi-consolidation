// Global state management
const Store = {
    state: {
        currentView: 'login',
        currentUser: null,
        routes: [],
        auditLogs: [],
        selectedRoute: null,
        searchQuery: '',
        filterStatus: '',
        showModal: false,
        modalType: '',
        formData: {
            id: '',
            uri: '',
            order: 0,
            enabled: true,
            predicates: [],
            filters: [],
            predicateType: '',
            pathPattern: '',
            filterType: '',
            stripPath: '',
            rewritePattern: ''
        },
        alertMessage: '',
        alertType: '',
        loading: false,
        activeTab: 'list',
        loginForm: {
            username: '',
            password: ''
        }
    },

    // Check if user is logged in
    isLoggedIn() {
        return !!this.state.currentUser;
    },

    // Get filtered routes based on search and status
    getFilteredRoutes() {
        return this.state.routes.filter(route => {
            const matchesSearch = route.id.toLowerCase().includes(this.state.searchQuery.toLowerCase()) ||
                                route.uri.toLowerCase().includes(this.state.searchQuery.toLowerCase());
            const matchesStatus = !this.state.filterStatus ||
                                (this.state.filterStatus === 'enabled' && route.enabled) ||
                                (this.state.filterStatus === 'disabled' && !route.enabled);
            return matchesSearch && matchesStatus;
        });
    },

    // Format date
    formatDate(dateString) {
        if (!dateString) return 'N/A';
        return new Date(dateString).toLocaleString();
    }
};

