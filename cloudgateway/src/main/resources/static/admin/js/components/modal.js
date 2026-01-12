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
        },
        addPredicate() {
            if (!this.formData.newPredicateType || !this.formData.newPredicateValue) {
                alert('Please select type and enter value');
                return;
            }
            const predicate = {
                name: this.formData.newPredicateType,
                args: this.formData.newPredicateValue
            };
            this.$emit('add-predicate', predicate);
        },
        removePredicate(index) {
            this.$emit('remove-predicate', index);
        },
        addFilter() {
            if (!this.formData.newFilterType || !this.formData.newFilterValue) {
                alert('Please select filter type and enter value');
                return;
            }
            const filter = {
                name: this.formData.newFilterType,
                args: this.formData.newFilterValue
            };
            this.$emit('add-filter', filter);
        },
        removeFilter(index) {
            this.$emit('remove-filter', index);
        },
        addMetadata() {
            if (!this.formData.newMetadataKey || !this.formData.newMetadataValue) {
                alert('Please enter both key and value');
                return;
            }
            const metadata = {};
            metadata[this.formData.newMetadataKey] = this.formData.newMetadataValue;
            this.$emit('add-metadata', metadata);
        },
        removeMetadata(key) {
            this.$emit('remove-metadata', key);
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

                        <!-- Existing Predicates List -->
                        <div v-if="formData.predicates && formData.predicates.length > 0" class="mb-4">
                            <p class="text-gray-400 text-sm mb-2">Added Predicates:</p>
                            <div class="space-y-2">
                                <div v-for="(pred, idx) in formData.predicates" :key="idx" class="flex justify-between items-center bg-gray-800 p-3 rounded-lg">
                                    <div>
                                        <span class="text-white font-semibold">{{ pred.name }}</span>
                                        <span class="text-gray-400 text-sm ml-2">{{ JSON.stringify(pred.args) }}</span>
                                    </div>
                                    <button @click="removePredicate(idx)" class="text-red-400 hover:text-red-300 transition-colors">
                                        <span class="material-icons text-lg">delete</span>
                                    </button>
                                </div>
                            </div>
                        </div>

                        <!-- Add New Predicate -->
                        <div class="border-t border-gray-700 pt-4">
                            <p class="text-gray-300 font-semibold mb-3">Add New Predicate</p>
                            
                            <div class="mb-3">
                                <label class="block text-gray-300 font-semibold mb-2">Predicate Type</label>
                                <select 
                                    :value="formData.newPredicateType || ''"
                                    @input="updateField('newPredicateType', \$event.target.value)"
                                    class="w-full px-4 py-2 bg-gray-700 border-2 border-gray-600 rounded-lg text-white focus:outline-none focus:border-blue-400 transition-colors">
                                    <option value="">Select predicate type</option>
                                    <option value="Path">Path Pattern</option>
                                    <option value="Method">HTTP Method</option>
                                    <option value="Header">Header</option>
                                </select>
                            </div>

                            <div class="mb-3">
                                <label class="block text-gray-300 font-semibold mb-2">Predicate Value</label>
                                <input 
                                    :value="formData.newPredicateValue || ''"
                                    @input="updateField('newPredicateValue', \$event.target.value)"
                                    type="text" 
                                    placeholder="e.g., /api/** or GET or Accept=application/json"
                                    class="w-full px-4 py-2 bg-gray-700 border-2 border-gray-600 rounded-lg text-white focus:outline-none focus:border-blue-400 transition-colors">
                            </div>

                            <button @click="addPredicate" class="w-full px-4 py-2 bg-green-600 hover:bg-green-700 text-white font-semibold rounded-lg transition-colors flex items-center justify-center gap-2">
                                <span class="material-icons">add</span>
                                Add Predicate
                            </button>
                        </div>
                    </div>

                    <!-- Filters Section -->
                    <div class="mb-6 p-4 bg-gray-900 border border-gray-700 rounded-lg">
                        <h4 class="text-lg font-bold text-blue-400 mb-4 flex items-center gap-2">
                            <span class="material-icons">tune</span>
                            Route Filters
                        </h4>

                        <!-- Existing Filters List -->
                        <div v-if="formData.filters && formData.filters.length > 0" class="mb-4">
                            <p class="text-gray-400 text-sm mb-2">Added Filters:</p>
                            <div class="space-y-2">
                                <div v-for="(filter, idx) in formData.filters" :key="idx" class="flex justify-between items-center bg-gray-800 p-3 rounded-lg">
                                    <div>
                                        <span class="text-white font-semibold">{{ filter.name }}</span>
                                        <span class="text-gray-400 text-sm ml-2">{{ JSON.stringify(filter.args) }}</span>
                                    </div>
                                    <button @click="removeFilter(idx)" class="text-red-400 hover:text-red-300 transition-colors">
                                        <span class="material-icons text-lg">delete</span>
                                    </button>
                                </div>
                            </div>
                        </div>

                        <!-- Add New Filter -->
                        <div class="border-t border-gray-700 pt-4">
                            <p class="text-gray-300 font-semibold mb-3">Add New Filter</p>
                            
                            <div class="mb-3">
                                <label class="block text-gray-300 font-semibold mb-2">Filter Type</label>
                                <select 
                                    :value="formData.newFilterType || ''"
                                    @input="updateField('newFilterType', \$event.target.value)"
                                    class="w-full px-4 py-2 bg-gray-700 border-2 border-gray-600 rounded-lg text-white focus:outline-none focus:border-blue-400 transition-colors">
                                    <option value="">Select filter type</option>
                                    <option value="StripPrefix">Strip Prefix</option>
                                    <option value="RewritePath">Rewrite Path</option>
                                    <option value="AddRequestHeader">Add Header</option>
                                    <option value="AddResponseHeader">Add Response Header</option>
                                    <option value="RemoveRequestHeader">Remove Request Header</option>
                                </select>
                            </div>

                            <div class="mb-3">
                                <label class="block text-gray-300 font-semibold mb-2">Filter Value</label>
                                <input 
                                    :value="formData.newFilterValue || ''"
                                    @input="updateField('newFilterValue', \$event.target.value)"
                                    type="text" 
                                    placeholder="e.g., 1 or /path,/newpath or X-Custom-Header,value"
                                    class="w-full px-4 py-2 bg-gray-700 border-2 border-gray-600 rounded-lg text-white focus:outline-none focus:border-blue-400 transition-colors">
                            </div>

                            <button @click="addFilter" class="w-full px-4 py-2 bg-green-600 hover:bg-green-700 text-white font-semibold rounded-lg transition-colors flex items-center justify-center gap-2">
                                <span class="material-icons">add</span>
                                Add Filter
                            </button>
                        </div>
                    </div>

                    <!-- Metadata Section -->
                    <div class="mb-6 p-4 bg-gray-900 border border-gray-700 rounded-lg">
                        <h4 class="text-lg font-bold text-blue-400 mb-4 flex items-center gap-2">
                            <span class="material-icons">label</span>
                            Route Metadata
                        </h4>

                        <!-- Existing Metadata List -->
                        <div v-if="formData.metadata && Object.keys(formData.metadata).length > 0" class="mb-4">
                            <p class="text-gray-400 text-sm mb-2">Added Metadata:</p>
                            <div class="space-y-2">
                                <div v-for="(value, key) in formData.metadata" :key="key" class="flex justify-between items-center bg-gray-800 p-3 rounded-lg">
                                    <div>
                                        <span class="text-white font-semibold">{{ key }}</span>
                                        <span class="text-gray-400 text-sm ml-2">{{ value }}</span>
                                    </div>
                                    <button @click="removeMetadata(key)" class="text-red-400 hover:text-red-300 transition-colors">
                                        <span class="material-icons text-lg">delete</span>
                                    </button>
                                </div>
                            </div>
                        </div>

                        <!-- Add New Metadata -->
                        <div class="border-t border-gray-700 pt-4">
                            <p class="text-gray-300 font-semibold mb-3">Add New Metadata</p>
                            
                            <div class="mb-3">
                                <label class="block text-gray-300 font-semibold mb-2">Key</label>
                                <input 
                                    :value="formData.newMetadataKey || ''"
                                    @input="updateField('newMetadataKey', \$event.target.value)"
                                    type="text" 
                                    placeholder="e.g., environment, version, team"
                                    class="w-full px-4 py-2 bg-gray-700 border-2 border-gray-600 rounded-lg text-white focus:outline-none focus:border-blue-400 transition-colors">
                            </div>

                            <div class="mb-3">
                                <label class="block text-gray-300 font-semibold mb-2">Value</label>
                                <input 
                                    :value="formData.newMetadataValue || ''"
                                    @input="updateField('newMetadataValue', \$event.target.value)"
                                    type="text" 
                                    placeholder="e.g., production, v1.0.0, platform-team"
                                    class="w-full px-4 py-2 bg-gray-700 border-2 border-gray-600 rounded-lg text-white focus:outline-none focus:border-blue-400 transition-colors">
                            </div>

                            <button @click="addMetadata" class="w-full px-4 py-2 bg-green-600 hover:bg-green-700 text-white font-semibold rounded-lg transition-colors flex items-center justify-center gap-2">
                                <span class="material-icons">add</span>
                                Add Metadata
                            </button>
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

