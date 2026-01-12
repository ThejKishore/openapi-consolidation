<template>
  <form @submit.prevent="handleSubmit" class="space-y-6">
    <!-- Route ID -->
    <div>
      <label class="block text-sm font-medium text-gray-300 mb-2">
        Route ID <span class="text-red-400">*</span>
      </label>
      <input
        v-model="formData.id"
        type="text"
        :disabled="isEdit"
        placeholder="e.g., employeesvc"
        class="w-full px-4 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white placeholder-gray-500 disabled:bg-gray-800 disabled:text-gray-500 focus:outline-none focus:border-blue-500"
      />
    </div>

    <!-- URI -->
    <div>
      <label class="block text-sm font-medium text-gray-300 mb-2">
        Backend URI <span class="text-red-400">*</span>
      </label>
      <input
        v-model="formData.uri"
        type="text"
        placeholder="e.g., http://localhost:8081"
        class="w-full px-4 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:border-blue-500"
      />
    </div>

    <!-- Order -->
    <div>
      <label class="block text-sm font-medium text-gray-300 mb-2">
        Order (Priority)
      </label>
      <input
        v-model.number="formData.order"
        type="number"
        placeholder="0"
        class="w-full px-4 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:outline-none focus:border-blue-500"
      />
    </div>

    <!-- Predicates Section -->
    <div class="border-t border-gray-700 pt-6">
      <div class="flex justify-between items-center mb-4">
        <h3 class="text-lg font-bold text-white">Predicates</h3>
        <button
          type="button"
          @click="addPredicate"
          class="px-3 py-1 bg-blue-600 hover:bg-blue-700 text-white text-sm rounded"
        >
          + Add
        </button>
      </div>

      <div class="space-y-3">
        <div v-for="(pred, idx) in formData.predicates" :key="idx" class="flex gap-2">
          <select
            v-model="pred.name"
            class="flex-1 px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:outline-none focus:border-blue-500"
          >
            <option>Path</option>
            <option>Host</option>
            <option>Method</option>
            <option>Header</option>
          </select>
          <input
            v-model="pred.args"
            type="text"
            placeholder="Arguments"
            class="flex-1 px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:border-blue-500"
          />
          <button
            type="button"
            @click="removePredicate(idx)"
            class="px-3 py-2 bg-red-600 hover:bg-red-700 text-white text-sm rounded"
          >
            ✕
          </button>
        </div>
      </div>
    </div>

    <!-- Filters Section -->
    <div class="border-t border-gray-700 pt-6">
      <div class="flex justify-between items-center mb-4">
        <h3 class="text-lg font-bold text-white">Filters</h3>
        <button
          type="button"
          @click="addFilter"
          class="px-3 py-1 bg-blue-600 hover:bg-blue-700 text-white text-sm rounded"
        >
          + Add
        </button>
      </div>

      <div class="space-y-3">
        <div v-for="(filter, idx) in formData.filters" :key="idx" class="flex gap-2">
          <select
            v-model="filter.name"
            class="flex-1 px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:outline-none focus:border-blue-500"
          >
            <option>StripPrefix</option>
            <option>RewritePath</option>
            <option>AddRequestHeader</option>
            <option>RemoveRequestHeader</option>
            <option>AddResponseHeader</option>
          </select>
          <input
            v-model="filter.args"
            type="text"
            placeholder="Arguments"
            class="flex-1 px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:border-blue-500"
          />
          <button
            type="button"
            @click="removeFilter(idx)"
            class="px-3 py-2 bg-red-600 hover:bg-red-700 text-white text-sm rounded"
          >
            ✕
          </button>
        </div>
      </div>
    </div>

    <!-- Enabled Toggle -->
    <div class="border-t border-gray-700 pt-6">
      <label class="flex items-center space-x-3">
        <input
          v-model="formData.enabled"
          type="checkbox"
          class="w-5 h-5 bg-gray-700 border border-gray-600 rounded focus:ring-blue-500"
        />
        <span class="text-gray-300">Enable this route</span>
      </label>
    </div>

    <!-- Actions -->
    <div class="flex gap-3 pt-6 border-t border-gray-700">
      <button
        type="submit"
        class="flex-1 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition-colors"
      >
        {{ isEdit ? 'Update Route' : 'Create Route' }}
      </button>
      <button
        type="button"
        @click="$emit('cancel')"
        class="flex-1 px-4 py-2 bg-gray-700 hover:bg-gray-600 text-white font-medium rounded-lg transition-colors"
      >
        Cancel
      </button>
    </div>
  </form>
</template>

<script>
import { reactive, computed, watch } from 'vue'

export default {
  name: 'RouteForm',
  props: {
    route: {
      type: Object,
      default: null
    }
  },
  emits: ['submit', 'cancel'],
  setup(props, { emit }) {
    const isEdit = computed(() => !!props.route)

    const formData = reactive({
      id: props.route?.id || '',
      uri: props.route?.uri || '',
      order: props.route?.order || 0,
      enabled: props.route?.enabled !== false,
      predicates: props.route?.predicates || [{ name: 'Path', args: '' }],
      filters: props.route?.filters || []
    })

    // Watch for changes to the route prop and update formData
    watch(() => props.route, (newRoute) => {
      if (newRoute) {
        formData.id = newRoute.id || ''
        formData.uri = newRoute.uri || ''
        formData.order = newRoute.order || 0
        formData.enabled = newRoute.enabled !== false
        formData.predicates = newRoute.predicates && newRoute.predicates.length > 0
          ? JSON.parse(JSON.stringify(newRoute.predicates))
          : [{ name: 'Path', args: '' }]
        formData.filters = newRoute.filters && newRoute.filters.length > 0
          ? JSON.parse(JSON.stringify(newRoute.filters))
          : []
      } else {
        // Reset form for new route creation
        formData.id = ''
        formData.uri = ''
        formData.order = 0
        formData.enabled = true
        formData.predicates = [{ name: 'Path', args: '' }]
        formData.filters = []
      }
    }, { deep: true })

    const addPredicate = () => {
      formData.predicates.push({ name: 'Path', args: '' })
    }

    const removePredicate = (index) => {
      formData.predicates.splice(index, 1)
    }

    const addFilter = () => {
      formData.filters.push({ name: 'StripPrefix', args: '' })
    }

    const removeFilter = (index) => {
      formData.filters.splice(index, 1)
    }

    const handleSubmit = () => {
      if (!formData.id || !formData.uri) {
        alert('Please fill in all required fields')
        return
      }

      emit('submit', {
        id: formData.id,
        uri: formData.uri,
        order: formData.order,
        enabled: formData.enabled,
        predicates: formData.predicates.filter(p => p.name),
        filters: formData.filters.filter(f => f.name)
      })
    }

    return {
      formData,
      isEdit,
      addPredicate,
      removePredicate,
      addFilter,
      removeFilter,
      handleSubmit
    }
  }
}
</script>

