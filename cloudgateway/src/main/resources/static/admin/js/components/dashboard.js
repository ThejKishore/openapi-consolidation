// Dashboard Component
Vue.component('dashboard-view', {
    props: {
        routes: Array,
        currentUser: String
    },
    methods: {
        openCreateModal() {
            this.$emit('open-modal');
        },
        navigateTo(view) {
            this.$emit('navigate', view);
        }
    },
    template: `
        <div>
            <div class="mb-8">
                <h2 class="text-4xl font-bold mb-2">Dashboard</h2>
                <p class="text-gray-400">Welcome to Cloud Gateway Admin Portal</p>
            </div>

            <!-- Stats Grid -->
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
                <!-- Total Routes Card -->
                <div class="bg-gray-800 border border-gray-700 rounded-lg p-6 shadow-md">
                    <div class="flex items-center justify-between mb-4">
                        <h3 class="text-lg font-semibold">Total Routes</h3>
                        <span class="material-icons text-blue-400">route</span>
                    </div>
                    <div class="text-4xl font-bold text-blue-400 mb-2">{{ routes.length }}</div>
                    <p class="text-gray-400 text-sm">Active routes in the gateway</p>
                </div>

                <!-- Enabled Routes Card -->
                <div class="bg-gray-800 border border-gray-700 rounded-lg p-6 shadow-md">
                    <div class="flex items-center justify-between mb-4">
                        <h3 class="text-lg font-semibold">Enabled Routes</h3>
                        <span class="material-icons text-green-400">check_circle</span>
                    </div>
                    <div class="text-4xl font-bold text-green-400 mb-2">{{ routes.filter(r => r.enabled).length }}</div>
                    <p class="text-gray-400 text-sm">Currently active and serving</p>
                </div>

                <!-- Disabled Routes Card -->
                <div class="bg-gray-800 border border-gray-700 rounded-lg p-6 shadow-md">
                    <div class="flex items-center justify-between mb-4">
                        <h3 class="text-lg font-semibold">Disabled Routes</h3>
                        <span class="material-icons text-red-400">pause_circle</span>
                    </div>
                    <div class="text-4xl font-bold text-red-400 mb-2">{{ routes.filter(r => !r.enabled).length }}</div>
                    <p class="text-gray-400 text-sm">Currently disabled</p>
                </div>

                <!-- Current User Card -->
                <div class="bg-gray-800 border border-gray-700 rounded-lg p-6 shadow-md">
                    <div class="flex items-center justify-between mb-4">
                        <h3 class="text-lg font-semibold">Current User</h3>
                        <span class="material-icons text-cyan-400">account_circle</span>
                    </div>
                    <div class="text-2xl font-bold text-cyan-400 mb-2">{{ currentUser }}</div>
                    <p class="text-gray-400 text-sm">Logged in as Administrator</p>
                </div>
            </div>

            <!-- Quick Actions -->
            <div class="bg-gray-800 border border-gray-700 rounded-lg p-6 shadow-md">
                <h3 class="text-lg font-semibold mb-4 flex items-center gap-2">
                    <span class="material-icons">lightning_on</span>
                    Quick Actions
                </h3>
                <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                    <button @click="openCreateModal" class="flex items-center justify-center gap-2 bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 px-4 rounded-lg transition-colors">
                        <span class="material-icons">add</span>
                        Create Route
                    </button>
                    <button @click="navigateTo('routes')" class="flex items-center justify-center gap-2 bg-gray-700 hover:bg-gray-600 text-white font-semibold py-3 px-4 rounded-lg transition-colors">
                        <span class="material-icons">list</span>
                        View Routes
                    </button>
                    <button @click="navigateTo('audit')" class="flex items-center justify-center gap-2 bg-gray-700 hover:bg-gray-600 text-white font-semibold py-3 px-4 rounded-lg transition-colors">
                        <span class="material-icons">history</span>
                        Audit Logs
                    </button>
                    <button @click="navigateTo('health')" class="flex items-center justify-center gap-2 bg-gray-700 hover:bg-gray-600 text-white font-semibold py-3 px-4 rounded-lg transition-colors">
                        <span class="material-icons">favorite</span>
                        Health Status
                    </button>
                </div>
            </div>
        </div>
    `
});

