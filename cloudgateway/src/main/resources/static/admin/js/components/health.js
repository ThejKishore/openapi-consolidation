// Health Check Component
Vue.component('health-view', {
    props: {
        routes: Array
    },
    methods: {
        formatDate(dateString) {
            if (!dateString) return 'N/A';
            return new Date(dateString).toLocaleString();
        }
    },
    template: `
        <div>
            <div class="mb-8">
                <h2 class="text-4xl font-bold mb-2">Route Health Status</h2>
                <p class="text-gray-400">Monitor the health of all routes</p>
            </div>

            <!-- Empty State -->
            <div v-if="routes.length === 0" class="text-center py-12">
                <span class="material-icons text-6xl text-gray-600 block mb-4">favorite</span>
                <h3 class="text-xl font-semibold text-gray-300 mb-2">No routes available</h3>
                <p class="text-gray-400">Create routes to check their health status</p>
            </div>

            <!-- Health Cards Grid -->
            <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                <div v-for="route in routes" :key="route.id" class="bg-gray-800 border border-gray-700 rounded-lg p-6 shadow-md hover:border-gray-600 transition-colors">
                    <div class="flex items-center justify-between mb-4">
                        <h3 class="text-lg font-bold">{{ route.id }}</h3>
                        <span v-if="route.health.status === 'UP'" class="inline-block px-3 py-1 bg-green-900 text-green-200 text-xs font-semibold rounded-full">
                            <span class="material-icons inline text-sm">check_circle</span>
                            {{ route.health.status }}
                        </span>
                        <span v-else class="inline-block px-3 py-1 bg-red-900 text-red-200 text-xs font-semibold rounded-full">
                            <span class="material-icons inline text-sm">error</span>
                            {{ route.health.status }}
                        </span>
                    </div>

                    <div class="overflow-x-auto">
                        <table class="w-full text-sm">
                            <tbody>
                                <tr class="border-b border-gray-700">
                                    <td class="px-2 py-2 font-semibold text-gray-300">URI:</td>
                                    <td class="px-2 py-2 break-all">{{ route.uri }}</td>
                                </tr>
                                <tr class="border-b border-gray-700">
                                    <td class="px-2 py-2 font-semibold text-gray-300">Response Time:</td>
                                    <td class="px-2 py-2">{{ route.health.responseTime }}ms</td>
                                </tr>
                                <tr>
                                    <td class="px-2 py-2 font-semibold text-gray-300">Last Checked:</td>
                                    <td class="px-2 py-2">{{ formatDate(route.health.lastChecked) }}</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                    <!-- Health Indicator -->
                    <div class="mt-4 pt-4 border-t border-gray-700">
                        <div v-if="route.health.status === 'UP'" class="flex items-center gap-2 text-green-400">
                            <span class="material-icons text-lg">favorite</span>
                            <span class="font-semibold">Healthy</span>
                        </div>
                        <div v-else class="flex items-center gap-2 text-red-400">
                            <span class="material-icons text-lg">heart_broken</span>
                            <span class="font-semibold">Unhealthy</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `
});

