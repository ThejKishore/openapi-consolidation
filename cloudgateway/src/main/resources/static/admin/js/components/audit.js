// Audit Logs Component
Vue.component('audit-view', {
    props: {
        auditLogs: Array,
        loading: Boolean
    },
    methods: {
        formatDate(dateString) {
            if (!dateString) return 'N/A';
            return new Date(dateString).toLocaleString();
        },
        getActionColor(action) {
            switch (action) {
                case 'CREATE': return 'bg-green-900 text-green-200';
                case 'UPDATE': return 'bg-blue-900 text-blue-200';
                case 'DELETE': return 'bg-red-900 text-red-200';
                default: return 'bg-gray-700 text-gray-200';
            }
        }
    },
    template: `
        <div>
            <div class="mb-8">
                <h2 class="text-4xl font-bold mb-2">Audit Logs</h2>
                <p class="text-gray-400">View all route management activities</p>
            </div>

            <!-- Loading State -->
            <div v-if="loading" class="flex justify-center items-center py-12">
                <span class="inline-block w-8 h-8 border-4 border-gray-600 border-t-blue-400 rounded-full animate-spin mr-3"></span>
                <span class="text-gray-400">Loading audit logs...</span>
            </div>

            <!-- Empty State -->
            <div v-else-if="auditLogs.length === 0" class="text-center py-12">
                <span class="material-icons text-6xl text-gray-600 block mb-4">assessment</span>
                <h3 class="text-xl font-semibold text-gray-300 mb-2">No audit logs yet</h3>
                <p class="text-gray-400">Route activities will appear here</p>
            </div>

            <!-- Audit Logs Table -->
            <div v-else class="bg-gray-800 border border-gray-700 rounded-lg overflow-hidden shadow-md">
                <div class="overflow-x-auto">
                    <table class="w-full">
                        <thead class="bg-gray-900">
                            <tr>
                                <th class="px-4 py-3 text-left font-semibold">Timestamp</th>
                                <th class="px-4 py-3 text-left font-semibold">Action</th>
                                <th class="px-4 py-3 text-left font-semibold">Route ID</th>
                                <th class="px-4 py-3 text-left font-semibold">User</th>
                                <th class="px-4 py-3 text-left font-semibold">Details</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="log in auditLogs" :key="log.id" class="border-b border-gray-700 hover:bg-gray-700 transition-colors">
                                <td class="px-4 py-3 text-sm">{{ formatDate(log.timestamp) }}</td>
                                <td class="px-4 py-3">
                                    <span :class="['inline-block px-3 py-1 text-xs font-semibold rounded-full', getActionColor(log.action)]">
                                        {{ log.action }}
                                    </span>
                                </td>
                                <td class="px-4 py-3">{{ log.routeId }}</td>
                                <td class="px-4 py-3 text-sm">{{ log.userId }}</td>
                                <td class="px-4 py-3 text-sm">{{ log.details }}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    `
});

