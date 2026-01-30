// Audit Logs Component
Vue.component('audit-view', {
    props: {
        auditLogs: Array,
        loading: Boolean
    },
    data() {
        return {
            selectedAudit: null,
            showAuditDetail: false
        };
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
        },
        showDetail(audit) {
            this.selectedAudit = audit;
            this.showAuditDetail = true;
        },
        closeDetail() {
            this.showAuditDetail = false;
            this.selectedAudit = null;
        },
        parseJSON(jsonString) {
            try {
                return JSON.parse(jsonString);
            } catch (e) {
                return null;
            }
        },
        formatJSON(obj) {
            if (!obj) return 'N/A';
            return JSON.stringify(obj, null, 2);
        },
        getDifferences(oldValue, newValue) {
            const old = this.parseJSON(oldValue);
            const newVal = this.parseJSON(newValue);

            if (!old && !newVal) return 'No changes recorded';
            if (!old) return 'New route created: ' + this.formatJSON(newVal);
            if (!newVal) return 'Route deleted: ' + this.formatJSON(old);

            // Return both old and new values for UPDATE
            return { old, newVal };
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
                                <th class="px-4 py-3 text-center font-semibold">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="log in auditLogs" :key="log.auditId" class="border-b border-gray-700 hover:bg-gray-700 transition-colors">
                                <td class="px-4 py-3 text-sm">{{ formatDate(log.createdAt) }}</td>
                                <td class="px-4 py-3">
                                    <span :class="['inline-block px-3 py-1 text-xs font-semibold rounded-full', getActionColor(log.action)]">
                                        {{ log.action }}
                                    </span>
                                </td>
                                <td class="px-4 py-3">{{ log.routeId }}</td>
                                <td class="px-4 py-3 text-sm">{{ log.createdBy }}</td>
                                <td class="px-4 py-3 text-sm">{{ log.description }}</td>
                                <td class="px-4 py-3 text-center">
                                    <button @click="showDetail(log)" class="px-3 py-1 bg-blue-600 hover:bg-blue-700 text-white text-xs font-semibold rounded transition-colors">
                                        View Details
                                    </button>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Audit Detail Modal -->
            <div v-if="showAuditDetail && selectedAudit" class="fixed inset-0 bg-black bg-opacity-70 flex items-center justify-center z-50 p-4">
                <div class="bg-gray-800 border border-gray-700 rounded-lg shadow-xl max-w-3xl w-full max-h-[90vh] overflow-y-auto">
                    <!-- Header -->
                    <div class="sticky top-0 bg-gray-900 border-b border-gray-700 px-6 py-4 flex items-center justify-between">
                        <h3 class="text-2xl font-bold text-white">Audit Details</h3>
                        <button @click="closeDetail" class="text-gray-400 hover:text-gray-200 text-2xl">&times;</button>
                    </div>

                    <!-- Content -->
                    <div class="p-6 space-y-6">
                        <!-- Summary Section -->
                        <div class="grid grid-cols-2 gap-4">
                            <div class="bg-gray-700 rounded-lg p-4">
                                <label class="text-gray-400 text-sm font-semibold">Route ID</label>
                                <p class="text-white text-lg mt-2 font-mono">{{ selectedAudit.routeId }}</p>
                            </div>
                            <div class="bg-gray-700 rounded-lg p-4">
                                <label class="text-gray-400 text-sm font-semibold">Action</label>
                                <div class="mt-2">
                                    <span :class="['inline-block px-3 py-1 text-xs font-semibold rounded-full', getActionColor(selectedAudit.action)]">
                                        {{ selectedAudit.action }}
                                    </span>
                                </div>
                            </div>
                            <div class="bg-gray-700 rounded-lg p-4">
                                <label class="text-gray-400 text-sm font-semibold">Modified By</label>
                                <p class="text-white text-lg mt-2">{{ selectedAudit.createdBy }}</p>
                            </div>
                            <div class="bg-gray-700 rounded-lg p-4">
                                <label class="text-gray-400 text-sm font-semibold">When</label>
                                <p class="text-white text-lg mt-2">{{ formatDate(selectedAudit.createdAt) }}</p>
                            </div>
                        </div>

                        <!-- Version Info -->
                        <div class="bg-gray-700 rounded-lg p-4">
                            <label class="text-gray-400 text-sm font-semibold">Version</label>
                            <p class="text-white text-lg mt-2">{{ selectedAudit.version }}</p>
                        </div>

                        <!-- Description -->
                        <div class="bg-gray-700 rounded-lg p-4">
                            <label class="text-gray-400 text-sm font-semibold">Description</label>
                            <p class="text-gray-200 mt-2">{{ selectedAudit.description }}</p>
                        </div>

                        <!-- Changes Section -->
                        <div class="space-y-4">
                            <h4 class="text-lg font-semibold text-white">Changes</h4>
                            
                            <!-- Previous Value -->
                            <div v-if="selectedAudit.oldValue" class="bg-red-900 bg-opacity-30 border border-red-700 rounded-lg p-4">
                                <label class="text-red-300 text-sm font-semibold">Previous Value</label>
                                <pre class="text-red-100 text-xs mt-3 overflow-x-auto bg-black bg-opacity-40 p-3 rounded">{{ formatJSON(parseJSON(selectedAudit.oldValue)) }}</pre>
                            </div>

                            <!-- New Value -->
                            <div v-if="selectedAudit.newValue" class="bg-green-900 bg-opacity-30 border border-green-700 rounded-lg p-4">
                                <label class="text-green-300 text-sm font-semibold">New Value</label>
                                <pre class="text-green-100 text-xs mt-3 overflow-x-auto bg-black bg-opacity-40 p-3 rounded">{{ formatJSON(parseJSON(selectedAudit.newValue)) }}</pre>
                            </div>

                            <!-- No Changes -->
                            <div v-if="!selectedAudit.oldValue && !selectedAudit.newValue" class="bg-gray-700 rounded-lg p-4 text-center">
                                <p class="text-gray-300">No change details recorded</p>
                            </div>
                        </div>
                    </div>

                    <!-- Footer -->
                    <div class="bg-gray-900 border-t border-gray-700 px-6 py-4 flex justify-end gap-3">
                        <button @click="closeDetail" class="px-4 py-2 bg-gray-700 hover:bg-gray-600 text-white font-semibold rounded transition-colors">
                            Close
                        </button>
                    </div>
                </div>
            </div>
        </div>
    `
});
