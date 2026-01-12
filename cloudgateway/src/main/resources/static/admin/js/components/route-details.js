// Route Details Component
Vue.component('route-details-view', {
    props: {
        route: Object
    },
    methods: {
        goBack() {
            this.$emit('back');
        },
        editRoute() {
            this.$emit('edit-route', this.route);
        },
        toggleRoute() {
            this.$emit('toggle-route', this.route);
        },
        deleteRoute() {
            this.$emit('delete-route', this.route.id);
        },
        formatDate(dateString) {
            if (!dateString) return 'N/A';
            return new Date(dateString).toLocaleString();
        }
    },
    template: `
        <div>
            <button @click="goBack" class="flex items-center gap-2 bg-gray-700 hover:bg-gray-600 text-white font-semibold py-2 px-4 rounded-lg transition-colors mb-6">
                <span class="material-icons">arrow_back</span>
                Back to Routes
            </button>

            <!-- Route Header Card -->
            <div class="bg-gray-800 border border-gray-700 rounded-lg p-6 mb-6 shadow-md">
                <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-4">
                    <h3 class="text-2xl font-bold">Route Details: {{ route.id }}</h3>
                    <div class="flex gap-2">
                        <span v-if="route.enabled" class="inline-block px-4 py-2 bg-green-900 text-green-200 font-semibold rounded-lg">
                            <span class="material-icons inline text-sm">check_circle</span>
                            Enabled
                        </span>
                        <span v-else class="inline-block px-4 py-2 bg-red-900 text-red-200 font-semibold rounded-lg">
                            <span class="material-icons inline text-sm">cancel</span>
                            Disabled
                        </span>
                    </div>
                </div>

                <div class="overflow-x-auto">
                    <table class="w-full">
                        <tbody>
                            <tr class="border-b border-gray-700">
                                <td class="px-4 py-3 font-semibold text-gray-300 w-40">Route ID:</td>
                                <td class="px-4 py-3">{{ route.id }}</td>
                            </tr>
                            <tr class="border-b border-gray-700">
                                <td class="px-4 py-3 font-semibold text-gray-300">Backend URI:</td>
                                <td class="px-4 py-3">{{ route.uri }}</td>
                            </tr>
                            <tr class="border-b border-gray-700">
                                <td class="px-4 py-3 font-semibold text-gray-300">Order:</td>
                                <td class="px-4 py-3">{{ route.order }}</td>
                            </tr>
                            <tr class="border-b border-gray-700">
                                <td class="px-4 py-3 font-semibold text-gray-300">Version:</td>
                                <td class="px-4 py-3">{{ route.version }}</td>
                            </tr>
                            <tr class="border-b border-gray-700">
                                <td class="px-4 py-3 font-semibold text-gray-300">Created By:</td>
                                <td class="px-4 py-3">{{ route.createdBy }}</td>
                            </tr>
                            <tr class="border-b border-gray-700">
                                <td class="px-4 py-3 font-semibold text-gray-300">Created At:</td>
                                <td class="px-4 py-3">{{ formatDate(route.createdAt) }}</td>
                            </tr>
                            <tr class="border-b border-gray-700">
                                <td class="px-4 py-3 font-semibold text-gray-300">Updated By:</td>
                                <td class="px-4 py-3">{{ route.updatedBy }}</td>
                            </tr>
                            <tr>
                                <td class="px-4 py-3 font-semibold text-gray-300">Updated At:</td>
                                <td class="px-4 py-3">{{ formatDate(route.updatedAt) }}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Path Predicates Card -->
            <div v-if="route.predicates && route.predicates.length > 0" class="bg-gray-800 border border-gray-700 rounded-lg p-6 mb-6 shadow-md">
                <h3 class="text-xl font-bold mb-4 flex items-center gap-2">
                    <span class="material-icons">location_on</span>
                    Path Predicates
                </h3>
                <div class="overflow-x-auto">
                    <table class="w-full">
                        <thead class="bg-gray-900">
                            <tr>
                                <th class="px-4 py-3 text-left font-semibold">Name</th>
                                <th class="px-4 py-3 text-left font-semibold">Arguments</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="(p, idx) in route.predicates" :key="idx" class="border-b border-gray-700">
                                <td class="px-4 py-3">{{ p.name }}</td>
                                <td class="px-4 py-3"><code class="bg-gray-900 px-2 py-1 rounded">{{ JSON.stringify(p.args) }}</code></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Filters Card -->
            <div v-if="route.filters && route.filters.length > 0" class="bg-gray-800 border border-gray-700 rounded-lg p-6 mb-6 shadow-md">
                <h3 class="text-xl font-bold mb-4 flex items-center gap-2">
                    <span class="material-icons">tune</span>
                    Route Filters
                </h3>
                <div class="overflow-x-auto">
                    <table class="w-full">
                        <thead class="bg-gray-900">
                            <tr>
                                <th class="px-4 py-3 text-left font-semibold">Name</th>
                                <th class="px-4 py-3 text-left font-semibold">Arguments</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="(f, idx) in route.filters" :key="idx" class="border-b border-gray-700">
                                <td class="px-4 py-3">{{ f.name }}</td>
                                <td class="px-4 py-3"><code class="bg-gray-900 px-2 py-1 rounded">{{ JSON.stringify(f.args) }}</code></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Health Status Card -->
            <div class="bg-gray-800 border border-gray-700 rounded-lg p-6 mb-6 shadow-md">
                <h3 class="text-xl font-bold mb-4 flex items-center gap-2">
                    <span class="material-icons">favorite</span>
                    Health Status
                </h3>
                <div class="overflow-x-auto">
                    <table class="w-full">
                        <tbody>
                            <tr class="border-b border-gray-700">
                                <td class="px-4 py-3 font-semibold text-gray-300 w-40">Status:</td>
                                <td class="px-4 py-3">
                                    <span v-if="route.health.status === 'UP'" class="inline-block px-3 py-1 bg-green-900 text-green-200 font-semibold rounded-full">
                                        {{ route.health.status }}
                                    </span>
                                    <span v-else class="inline-block px-3 py-1 bg-red-900 text-red-200 font-semibold rounded-full">
                                        {{ route.health.status }}
                                    </span>
                                </td>
                            </tr>
                            <tr class="border-b border-gray-700">
                                <td class="px-4 py-3 font-semibold text-gray-300">Response Time:</td>
                                <td class="px-4 py-3">{{ route.health.responseTime }}ms</td>
                            </tr>
                            <tr>
                                <td class="px-4 py-3 font-semibold text-gray-300">Last Checked:</td>
                                <td class="px-4 py-3">{{ formatDate(route.health.lastChecked) }}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Actions Card -->
            <div class="bg-gray-800 border border-gray-700 rounded-lg p-6 shadow-md">
                <h3 class="text-xl font-bold mb-4 flex items-center gap-2">
                    <span class="material-icons">play_circle_outline</span>
                    Actions
                </h3>
                <div class="flex flex-wrap gap-3">
                    <button @click="editRoute" class="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 px-4 rounded-lg transition-colors">
                        <span class="material-icons">edit</span>
                        Edit Route
                    </button>
                    <button @click="toggleRoute" :class="route.enabled ? 'bg-orange-600 hover:bg-orange-700' : 'bg-green-600 hover:bg-green-700'" class="flex items-center gap-2 text-white font-semibold py-2 px-4 rounded-lg transition-colors">
                        <span class="material-icons">{{ route.enabled ? 'block' : 'check_circle' }}</span>
                        {{ route.enabled ? 'Disable Route' : 'Enable Route' }}
                    </button>
                    <button @click="deleteRoute" class="flex items-center gap-2 bg-red-600 hover:bg-red-700 text-white font-semibold py-2 px-4 rounded-lg transition-colors">
                        <span class="material-icons">delete</span>
                        Delete Route
                    </button>
                </div>
            </div>
        </div>
    `
});

