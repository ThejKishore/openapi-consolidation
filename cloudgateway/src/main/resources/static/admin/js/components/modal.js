// Modal Component
Vue.component('route-modal', {
    props: {
        visible: Boolean,
        modalType: String,
        formData: Object,
        loading: Boolean
    },
    methods: {
        handleClose() {
            this.$emit('close');
        },
        handleSubmit() {
            if (this.modalType === 'create') {
                this.$emit('create');
            } else {
                this.$emit('update');
            }
        },
        updateField(field, value) {
            this.$emit('update-field', { field, value });
        }
    },
    template: `
        <div v-if="visible" class="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50 animate-fadeIn">
            <div class="bg-gray-800 border border-gray-700 rounded-lg max-w-2xl w-full mx-4 max-h-[90vh] overflow-y-auto shadow-2xl animate-slideUp">
                <!-- Modal Header -->
                <div class="flex justify-between items-center px-6 py-4 border-b border-gray-700 sticky top-0 bg-gray-800">
                    <h3 class="text-2xl font-bold flex items-center gap-2">
                        <span class="material-icons">{{ modalType === 'create' ? 'add_circle' : 'edit' }}</span>
                        {{ modalType === 'create' ? 'Create New Route' : 'Edit Route' }}
                    </h3>
                    <button @click="handleClose" class="text-gray-400 hover:text-white transition-colors">
                        <span class="material-icons text-3xl">close</span>
                    </button>
                </div>

                <!-- Modal Content -->
                <div class="px-6 py-6">
                    <!-- Route Information Section -->
                    <div class="mb-6">
                        <h4 class="text-lg font-bold text-blue-400 mb-4 flex items-center gap-2">
                            <span class="material-icons">info</span>
                            Route Information
                        </h4>

                        <div class="mb-4">
                            <label class="block text-gray-300 font-semibold mb-2">Route ID *</label>
                            <input 
                                :value="formData.id"
                                @input="updateField('id', \$event.target.value)"
                                type="text" 
                                placeholder="e.g., jsonholder, myapi"
                                :disabled="modalType === 'edit'"
                                class="w-full px-4 py-2 bg-gray-700 border-2 border-gray-600 rounded-lg text-white focus:outline-none focus:border-blue-400 transition-colors disabled:opacity-50 disabled:cursor-not-allowed">
                            <p class="text-gray-400 text-sm mt-1">Unique identifier for this route (cannot be changed after creation)</p>
                        </div>

                        <div class="mb-4">
                            <label class="block text-gray-300 font-semibold mb-2">Backend URI *</label>
                            <input 
                                :value="formData.uri"
                                @input="updateField('uri', \$event.target.value)"
                                type="text" 
                                placeholder="e.g., https://jsonplaceholder.typicode.com/"
                                class="w-full px-4 py-2 bg-gray-700 border-2 border-gray-600 rounded-lg text-white focus:outline-none focus:border-blue-400 transition-colors">
                            <p class="text-gray-400 text-sm mt-1">The target backend service URL</p>
                        </div>

                        <div class="mb-4">
                            <label class="block text-gray-300 font-semibold mb-2">Route Order</label>
                            <input 
                                :value="formData.order"
                                @input="updateField('order', \$event.target.value)"
                                type="number" 
                                placeholder="0" 
                                min="0"
                                class="w-full px-4 py-2 bg-gray-700 border-2 border-gray-600 rounded-lg text-white focus:outline-none focus:border-blue-400 transition-colors">
                            <p class="text-gray-400 text-sm mt-1">Priority order for route matching (lower number = higher priority)</p>
                        </div>
                    </div>

                    <!-- Status Section -->
                    <div class="mb-6 p-4 bg-gray-900 border border-gray-700 rounded-lg">
                        <h4 class="text-lg font-bold text-blue-400 mb-4 flex items-center gap-2">
                            <span class="material-icons">toggle_on</span>
                            Status
                        </h4>
                        <label class="flex items-center cursor-pointer">
                            <input 
                                :checked="formData.enabled"
                                @input="updateField('enabled', \$event.target.checked)"
                                type="checkbox"
                                class="mr-3 w-5 h-5">
                            <span class="text-gray-300 font-semibold">Route Enabled</span>
                        </label>
                        <p class="text-gray-400 text-sm mt-2">Enable or disable this route without deleting it</p>
                    </div>

                    <!-- Path Predicates Section -->
                    <div class="mb-6 p-4 bg-gray-900 border border-gray-700 rounded-lg">
                        <h4 class="text-lg font-bold text-blue-400 mb-4 flex items-center gap-2">
                            <span class="material-icons">location_on</span>
                            Path Predicates
                        </h4>

                        <div class="mb-4">
                            <label class="block text-gray-300 font-semibold mb-2">Predicate Type</label>
                            <select 
                                :value="formData.predicateType"
                                @input="updateField('predicateType', \$event.target.value)"
                                class="w-full px-4 py-2 bg-gray-700 border-2 border-gray-600 rounded-lg text-white focus:outline-none focus:border-blue-400 transition-colors">
                                <option value="">Select predicate type</option>
                                <option value="Path">Path Pattern</option>
                                <option value="Method">HTTP Method</option>
                                <option value="Header">Header</option>
                            </select>
                            <p class="text-gray-400 text-sm mt-1">Condition to match incoming requests</p>
                        </div>

                        <div v-if="formData.predicateType === 'Path'" class="mb-4">
                            <label class="block text-gray-300 font-semibold mb-2">Path Pattern</label>
                            <input 
                                :value="formData.pathPattern"
                                @input="updateField('pathPattern', \$event.target.value)"
                                type="text" 
                                placeholder="e.g., /api/** or /users/{id}"
                                class="w-full px-4 py-2 bg-gray-700 border-2 border-gray-600 rounded-lg text-white focus:outline-none focus:border-blue-400 transition-colors">
                        </div>
                    </div>

                    <!-- Filters Section -->
                    <div class="mb-6 p-4 bg-gray-900 border border-gray-700 rounded-lg">
                        <h4 class="text-lg font-bold text-blue-400 mb-4 flex items-center gap-2">
                            <span class="material-icons">tune</span>
                            Route Filters
                        </h4>

                        <div class="mb-4">
                            <label class="block text-gray-300 font-semibold mb-2">Filter Type</label>
                            <select 
                                :value="formData.filterType"
                                @input="updateField('filterType', \$event.target.value)"
                                class="w-full px-4 py-2 bg-gray-700 border-2 border-gray-600 rounded-lg text-white focus:outline-none focus:border-blue-400 transition-colors">
                                <option value="">Select filter type</option>
                                <option value="StripPrefix">Strip Prefix</option>
                                <option value="RewritePath">Rewrite Path</option>
                                <option value="AddRequestHeader">Add Header</option>
                            </select>
                            <p class="text-gray-400 text-sm mt-1">Transformation to apply to requests/responses</p>
                        </div>

                        <div v-if="formData.filterType === 'StripPrefix'" class="mb-4">
                            <label class="block text-gray-300 font-semibold mb-2">Strip Path</label>
                            <input 
                                :value="formData.stripPath"
                                @input="updateField('stripPath', \$event.target.value)"
                                type="text" 
                                placeholder="e.g., 1 (number of segments to strip)"
                                class="w-full px-4 py-2 bg-gray-700 border-2 border-gray-600 rounded-lg text-white focus:outline-none focus:border-blue-400 transition-colors">
                        </div>

                        <div v-if="formData.filterType === 'RewritePath'" class="mb-4">
                            <label class="block text-gray-300 font-semibold mb-2">Rewrite Pattern</label>
                            <input 
                                :value="formData.rewritePattern"
                                @input="updateField('rewritePattern', \$event.target.value)"
                                type="text" 
                                placeholder="e.g., /api/(?<segment>.*), /v1/\${segment}"
                                class="w-full px-4 py-2 bg-gray-700 border-2 border-gray-600 rounded-lg text-white focus:outline-none focus:border-blue-400 transition-colors">
                        </div>
                    </div>
                </div>

                <!-- Modal Footer -->
                <div class="flex gap-3 justify-end px-6 py-4 border-t border-gray-700 sticky bottom-0 bg-gray-800">
                    <button 
                        @click="handleClose" 
                        class="px-6 py-2 bg-gray-700 hover:bg-gray-600 text-white font-semibold rounded-lg transition-colors flex items-center gap-2">
                        <span class="material-icons">close</span>
                        Cancel
                    </button>
                    <button 
                        @click="handleSubmit" 
                        :disabled="loading || !formData.id || !formData.uri"
                        class="px-6 py-2 bg-blue-600 hover:bg-blue-700 disabled:bg-gray-600 disabled:cursor-not-allowed text-white font-semibold rounded-lg transition-colors flex items-center gap-2">
                        <span v-if="loading" class="inline-block w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin mr-2"></span>
                        <span v-if="!loading" class="material-icons">{{ modalType === 'create' ? 'add_circle' : 'save' }}</span>
                        {{ modalType === 'create' ? (loading ? 'Creating...' : 'Create Route') : (loading ? 'Updating...' : 'Update Route') }}
                    </button>
                </div>
            </div>
        </div>
    `
});

