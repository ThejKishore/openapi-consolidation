// Main Vue Application
new Vue({
    el: '#app',
    data: Store.state,
    computed: {
        isLoggedIn() {
            return Store.isLoggedIn();
        },
        filteredRoutes() {
            return Store.getFilteredRoutes();
        }
    },
    mounted() {
        // Check if user is already logged in
        const token = localStorage.getItem('authToken');
        if (token) {
            this.currentUser = localStorage.getItem('currentUser') || 'Admin';
            this.currentView = 'dashboard';
            this.fetchRoutes();
        }
    },
    methods: {
        // Login
        login() {
            if (!this.loginForm.username || !this.loginForm.password) {
                this.showAlert('Please enter username and password', 'danger');
                return;
            }

            this.loading = true;
            // Simulate login (in real app, call auth API)
            setTimeout(() => {
                this.currentUser = this.loginForm.username;
                localStorage.setItem('authToken', 'mock-token-' + Date.now());
                localStorage.setItem('currentUser', this.loginForm.username);
                this.loginForm = { username: '', password: '' };
                this.currentView = 'dashboard';
                this.fetchRoutes();
                this.loading = false;
            }, 500);
        },

        logout() {
            if (confirm('Are you sure you want to logout?')) {
                this.currentUser = null;
                this.routes = [];
                this.auditLogs = [];
                localStorage.removeItem('authToken');
                localStorage.removeItem('currentUser');
                this.currentView = 'login';
                this.loginForm = { username: '', password: '' };
            }
        },

        // Navigation
        navigate(view) {
            this.currentView = view;
            if (view === 'routes') {
                this.fetchRoutes();
            } else if (view === 'audit') {
                this.fetchAuditLogs();
            }
        },

        // Routes API
        fetchRoutes() {
            this.loading = true;
            axios.get('/api/admin/routes', {
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem('authToken')
                }
            })
            .then(response => {
                this.routes = response.data || [];
                this.loading = false;
            })
            .catch(error => {
                console.error('Error fetching routes:', error);
                this.showAlert('Failed to fetch routes', 'danger');
                this.loading = false;
            });
        },

        createRoute() {
            if (!this.formData.id || !this.formData.uri) {
                this.showAlert('Route ID and URI are required', 'danger');
                return;
            }

            this.loading = true;
            axios.post('/api/admin/routes', this.formData, {
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem('authToken')
                }
            })
            .then(response => {
                this.routes.push(response.data);
                this.showAlert('Route created successfully', 'success');
                this.closeModal();
                this.loading = false;
            })
            .catch(error => {
                console.error('Error creating route:', error);
                this.showAlert('Failed to create route: ' + (error.response?.data?.message || error.message), 'danger');
                this.loading = false;
            });
        },

        editRoute(route) {
            this.formData = JSON.parse(JSON.stringify(route));
            this.modalType = 'edit';
            this.showModal = true;
        },

        updateRoute() {
            if (!this.formData.id || !this.formData.uri) {
                this.showAlert('Route ID and URI are required', 'danger');
                return;
            }

            this.loading = true;
            axios.put(`/api/admin/routes/${this.formData.id}`, this.formData, {
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem('authToken')
                }
            })
            .then(response => {
                const index = this.routes.findIndex(r => r.id === this.formData.id);
                if (index !== -1) {
                    this.$set(this.routes, index, response.data);
                }
                this.showAlert('Route updated successfully', 'success');
                this.closeModal();
                this.loading = false;
            })
            .catch(error => {
                console.error('Error updating route:', error);
                this.showAlert('Failed to update route: ' + (error.response?.data?.message || error.message), 'danger');
                this.loading = false;
            });
        },

        deleteRoute(routeId) {
            if (!confirm('Are you sure you want to delete this route?')) {
                return;
            }

            this.loading = true;
            axios.delete(`/api/admin/routes/${routeId}`, {
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem('authToken')
                }
            })
            .then(() => {
                this.routes = this.routes.filter(r => r.id !== routeId);
                this.showAlert('Route deleted successfully', 'success');
                this.loading = false;
            })
            .catch(error => {
                console.error('Error deleting route:', error);
                this.showAlert('Failed to delete route: ' + (error.response?.data?.message || error.message), 'danger');
                this.loading = false;
            });
        },

        toggleRoute(route) {
            this.loading = true;
            const updatedRoute = { ...route, enabled: !route.enabled };
            axios.put(`/api/admin/routes/${route.id}`, updatedRoute, {
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem('authToken')
                }
            })
            .then(response => {
                const index = this.routes.findIndex(r => r.id === route.id);
                if (index !== -1) {
                    this.$set(this.routes, index, response.data);
                }
                this.showAlert(`Route ${updatedRoute.enabled ? 'enabled' : 'disabled'}`, 'success');
                this.loading = false;
            })
            .catch(error => {
                console.error('Error toggling route:', error);
                this.showAlert('Failed to toggle route', 'danger');
                this.loading = false;
            });
        },

        viewRoute(route) {
            this.selectedRoute = route;
            this.currentView = 'details';
        },

        // Audit Logs
        fetchAuditLogs() {
            this.loading = true;
            axios.get('/api/audit/logs', {
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem('authToken')
                }
            })
            .then(response => {
                this.auditLogs = response.data || [];
                this.loading = false;
            })
            .catch(error => {
                console.error('Error fetching audit logs:', error);
                this.auditLogs = [];
                this.loading = false;
            });
        },

        // Utility Methods
        showAlert(message, type = 'info') {
            this.alertMessage = message;
            this.alertType = type;
            setTimeout(() => {
                this.alertMessage = '';
            }, 3000);
        },

        openCreateModal() {
            this.formData = {
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
            };
            this.modalType = 'create';
            this.showModal = true;
        },

        closeModal() {
            this.showModal = false;
            this.modalType = '';
            this.formData = {
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
            };
        },

        exportRoutes() {
            const dataStr = JSON.stringify(this.routes, null, 2);
            const dataBlob = new Blob([dataStr], { type: 'application/json' });
            const url = URL.createObjectURL(dataBlob);
            const link = document.createElement('a');
            link.href = url;
            link.download = `routes-${new Date().toISOString().split('T')[0]}.json`;
            link.click();
        },

        updateFormField(payload) {
            this.formData[payload.field] = payload.value;
        }
    },
    template: `
        <div class="h-screen bg-gray-900 text-gray-100 flex flex-col">
            <!-- Login View -->
            <login-view 
                v-if="currentView === 'login'"
                :loading="loading"
                :loginForm="loginForm"
                @login="login">
            </login-view>

            <!-- Main App -->
            <div v-else class="flex flex-col h-full">
                <!-- Header -->
                <app-header 
                    :currentUser="currentUser"
                    @logout="logout">
                </app-header>

                <!-- Main Container -->
                <div class="flex flex-1 overflow-hidden">
                    <!-- Sidebar -->
                    <app-sidebar 
                        :currentView="currentView"
                        @navigate="navigate">
                    </app-sidebar>

                    <!-- Content Area -->
                    <div class="flex-1 overflow-y-auto p-6 sm:p-8">
                        <!-- Alert Messages -->
                        <div v-if="alertMessage" :class="['mb-6 px-4 py-3 rounded-lg flex items-center gap-3 animate-slideUp', 
                                                           alertType === 'success' ? 'bg-green-900 text-green-200 border border-green-700' : 
                                                           alertType === 'danger' ? 'bg-red-900 text-red-200 border border-red-700' :
                                                           'bg-blue-900 text-blue-200 border border-blue-700']">
                            <span class="material-icons">{{ alertType === 'success' ? 'check_circle' : alertType === 'danger' ? 'error' : 'info' }}</span>
                            {{ alertMessage }}
                        </div>

                        <!-- Dashboard View -->
                        <dashboard-view 
                            v-if="currentView === 'dashboard'"
                            :routes="routes"
                            :currentUser="currentUser"
                            @open-modal="openCreateModal"
                            @navigate="navigate">
                        </dashboard-view>

                        <!-- Routes View -->
                        <routes-view 
                            v-if="currentView === 'routes'"
                            :routes="routes"
                            :filteredRoutes="filteredRoutes"
                            :searchQuery="searchQuery"
                            :filterStatus="filterStatus"
                            :loading="loading"
                            @open-modal="openCreateModal"
                            @edit-route="editRoute"
                            @delete-route="deleteRoute"
                            @toggle-route="toggleRoute"
                            @view-route="viewRoute"
                            @export-routes="exportRoutes"
                            @update-search="(v) => searchQuery = v"
                            @update-filter="(v) => filterStatus = v">
                        </routes-view>

                        <!-- Route Details View -->
                        <route-details-view 
                            v-if="currentView === 'details' && selectedRoute"
                            :route="selectedRoute"
                            @back="currentView = 'routes'"
                            @edit-route="editRoute"
                            @toggle-route="toggleRoute"
                            @delete-route="deleteRoute">
                        </route-details-view>

                        <!-- Audit Logs View -->
                        <audit-view 
                            v-if="currentView === 'audit'"
                            :auditLogs="auditLogs"
                            :loading="loading">
                        </audit-view>

                        <!-- Health Check View -->
                        <health-view 
                            v-if="currentView === 'health'"
                            :routes="routes">
                        </health-view>
                    </div>
                </div>
            </div>

            <!-- Route Modal -->
            <route-modal 
                :visible="showModal"
                :modalType="modalType"
                :formData="formData"
                :loading="loading"
                @close="closeModal"
                @create="createRoute"
                @update="updateRoute"
                @update-field="updateFormField">
            </route-modal>
        </div>
    `
});

