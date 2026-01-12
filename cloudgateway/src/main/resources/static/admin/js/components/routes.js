// Routes Component
Vue.component('routes-view', {
    props: {
        routes: Array,
        filteredRoutes: Array,
        searchQuery: String,
        filterStatus: String,
        loading: Boolean
    },
    methods: {
        openCreateModal() {
            this.$emit('open-modal');
        },
        editRoute(route) {
            this.$emit('edit-route', route);
        },
        deleteRoute(routeId) {
            this.$emit('delete-route', routeId);
        },
        toggleRoute(route) {
            this.$emit('toggle-route', route);
        },
        viewRoute(route) {
            this.$emit('view-route', route);
        },
        exportRoutes() {
            this.$emit('export-routes');
        },
        updateSearch(value) {
            this.$emit('update-search', value);
        },
        updateFilter(value) {
            this.$emit('update-filter', value);
        }
    },
    template: `
        <div>
            <div class="mb-8">
                <h2 class="text-4xl font-bold mb-2">Route Management</h2>
                <p class="text-gray-400">Manage and configure gateway routes</p>
            </div>

            <!-- Action Buttons -->
            <div class="flex gap-4 mb-6">
                <button @click="openCreateModal" class="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 px-4 rounded-lg transition-colors">
                    <span class="material-icons">add</span>
                    New Route
                </button>
                <button @click="exportRoutes" class="flex items-center gap-2 bg-gray-700 hover:bg-gray-600 text-white font-semibold py-2 px-4 rounded-lg transition-colors">
                    <span class="material-icons">download</span>
                    Export Routes
                </button>
            </div>

            <!-- Search and Filter -->
            <div class="flex flex-col sm:flex-row gap-4 mb-6">
                <input 
                    :value="searchQuery"
                    @input="updateSearch(\$event.target.value)"
                    type="text" 
                    placeholder="Search routes by ID or URI..."
                    class="flex-1 px-4 py-2 bg-gray-700 border-2 border-gray-600 rounded-lg text-white focus:outline-none focus:border-blue-400 transition-colors">
                <select 
                    :value="filterStatus"
                    @input="updateFilter(\$event.target.value)"
                    class="px-4 py-2 bg-gray-700 border-2 border-gray-600 rounded-lg text-white focus:outline-none focus:border-blue-400 transition-colors">
                    <option value="">All Status</option>
                    <option value="enabled">Enabled</option>
                    <option value="disabled">Disabled</option>
                </select>
            </div>

            <!-- Loading State -->
            <div v-if="loading" class="flex justify-center items-center py-8">
                <span class="inline-block w-8 h-8 border-4 border-gray-600 border-t-blue-400 rounded-full animate-spin mr-3"></span>
                <span class="text-gray-400">Loading routes...</span>
            </div>

            <!-- Empty State -->
            <div v-else-if="filteredRoutes.length === 0" class="text-center py-12">
                <span class="material-icons text-6xl text-gray-600 block mb-4">search_off</span>
                <h3 class="text-xl font-semibold text-gray-300 mb-2">No routes found</h3>
                <p class="text-gray-400">Create your first route to get started</p>
            </div>

            <!-- Routes List -->
            <div v-else class="space-y-4">
                <div v-for="route in filteredRoutes" :key="route.id" class="bg-gray-800 border border-gray-700 rounded-lg p-4 hover:border-gray-600 transition-colors shadow-md">
                    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-4">
                        <div class="flex-1">
                            <h3 class="text-lg font-semibold text-white mb-1">{{ route.id }}</h3>
                            <p class="text-gray-400 text-sm">{{ route.uri }}</p>
                        </div>
                        <div class="flex gap-2 flex-wrap">
                            <span v-if="route.enabled" class="inline-block px-3 py-1 bg-green-900 text-green-200 text-xs font-semibold rounded-full">
                                <span class="material-icons inline text-sm">check_circle</span>
                                Enabled
                            </span>
                            <span v-else class="inline-block px-3 py-1 bg-red-900 text-red-200 text-xs font-semibold rounded-full">
                                <span class="material-icons inline text-sm">cancel</span>
                                Disabled
                            </span>
                            <span class="inline-block px-3 py-1 bg-blue-900 text-blue-200 text-xs font-semibold rounded-full">
                                v{{ route.version }}
                            </span>
                        </div>
                    </div>

                    <!-- Action Buttons -->
                    <div class="flex flex-wrap gap-2">
                        <button @click="viewRoute(route)" class="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white text-sm font-semibold rounded-lg transition-colors flex items-center gap-1">
                            <span class="material-icons text-sm">visibility</span>
                            View
                        </button>
                        <button @click="editRoute(route)" class="px-4 py-2 bg-gray-700 hover:bg-gray-600 text-white text-sm font-semibold rounded-lg transition-colors flex items-center gap-1">
                            <span class="material-icons text-sm">edit</span>
                            Edit
                        </button>
                        <button @click="toggleRoute(route)" :class="route.enabled ? 'bg-orange-600 hover:bg-orange-700' : 'bg-green-600 hover:bg-green-700'" class="px-4 py-2 text-white text-sm font-semibold rounded-lg transition-colors flex items-center gap-1">
                            <span class="material-icons text-sm">{{ route.enabled ? 'block' : 'check_circle' }}</span>
                            {{ route.enabled ? 'Disable' : 'Enable' }}
                        </button>
                        <button @click="deleteRoute(route.id)" class="px-4 py-2 bg-red-600 hover:bg-red-700 text-white text-sm font-semibold rounded-lg transition-colors flex items-center gap-1">
                            <span class="material-icons text-sm">delete</span>
                            Delete
                        </button>
                    </div>
                </div>
            </div>
        </div>
    `
});

