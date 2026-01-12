<template>
  <div class="p-8 ml-64">
    <router-link to="/routes" class="text-blue-400 hover:text-blue-300 text-sm mb-4 inline-block">
      ← Back to Routes
    </router-link>

    <div v-if="route" class="space-y-6">
      <div>
        <h1 class="text-4xl font-bold text-white mb-2">{{ route.id }}</h1>
        <p class="text-gray-400">{{ route.uri }}</p>
      </div>

      <!-- Status Cards -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div class="bg-gray-800 border border-gray-700 rounded-lg p-4">
          <p class="text-gray-400 text-sm">Status</p>
          <p class="text-2xl font-bold mt-2" :class="route.enabled ? 'text-green-400' : 'text-red-400'">
            {{ route.enabled ? 'Enabled' : 'Disabled' }}
          </p>
        </div>
        <div class="bg-gray-800 border border-gray-700 rounded-lg p-4">
          <p class="text-gray-400 text-sm">Version</p>
          <p class="text-2xl font-bold text-blue-400 mt-2">{{ route.version }}</p>
        </div>
        <div class="bg-gray-800 border border-gray-700 rounded-lg p-4">
          <p class="text-gray-400 text-sm">Health</p>
          <p class="text-2xl font-bold mt-2" :class="getHealthColor(route.health?.status)">
            {{ route.health?.status || 'UNKNOWN' }}
          </p>
        </div>
        <div class="bg-gray-800 border border-gray-700 rounded-lg p-4">
          <p class="text-gray-400 text-sm">Response Time</p>
          <p class="text-2xl font-bold text-white mt-2">{{ route.health?.responseTime || 0 }}ms</p>
        </div>
      </div>

      <!-- Tabs -->
      <div class="bg-gray-800 border border-gray-700 rounded-lg overflow-hidden">
        <div class="flex border-b border-gray-700">
          <button
            v-for="tab in tabs"
            :key="tab.id"
            @click="activeTab = tab.id"
            :class="[
              'px-6 py-4 font-medium transition-colors border-b-2',
              activeTab === tab.id
                ? 'text-blue-400 border-blue-500'
                : 'text-gray-400 border-transparent hover:text-gray-300'
            ]"
          >
            {{ tab.name }}
          </button>
        </div>

        <div class="p-6">
          <!-- Configuration Tab -->
          <div v-show="activeTab === 'config'" class="space-y-4">
            <div class="grid grid-cols-2 gap-4">
              <div class="bg-gray-700 rounded-lg p-4">
                <p class="text-gray-400 text-sm">Created By</p>
                <p class="text-white font-medium mt-2">{{ route.createdBy }}</p>
              </div>
              <div class="bg-gray-700 rounded-lg p-4">
                <p class="text-gray-400 text-sm">Created At</p>
                <p class="text-white font-medium mt-2">{{ formatDateTime(route.createdAt) }}</p>
              </div>
              <div class="bg-gray-700 rounded-lg p-4">
                <p class="text-gray-400 text-sm">Updated By</p>
                <p class="text-white font-medium mt-2">{{ route.updatedBy }}</p>
              </div>
              <div class="bg-gray-700 rounded-lg p-4">
                <p class="text-gray-400 text-sm">Updated At</p>
                <p class="text-white font-medium mt-2">{{ formatDateTime(route.updatedAt) }}</p>
              </div>
            </div>

            <div class="bg-gray-700 rounded-lg p-4">
              <p class="text-gray-400 text-sm mb-3">Configuration (JSON)</p>
              <pre class="bg-gray-800 rounded p-3 text-gray-300 text-xs overflow-auto">{{ JSON.stringify(route, null, 2) }}</pre>
            </div>
          </div>

          <!-- Predicates Tab -->
          <div v-show="activeTab === 'predicates'" class="space-y-3">
            <div v-if="route.predicates?.length === 0" class="text-center text-gray-400 py-8">
              No predicates configured
            </div>
            <div
              v-for="(pred, idx) in route.predicates"
              :key="idx"
              class="bg-gray-700 rounded-lg p-4 border border-gray-600"
            >
              <p class="text-white font-bold">{{ pred.name }}</p>
              <p class="text-gray-400 text-sm mt-2">{{ pred.args }}</p>
            </div>
          </div>

          <!-- Filters Tab -->
          <div v-show="activeTab === 'filters'" class="space-y-3">
            <div v-if="route.filters?.length === 0" class="text-center text-gray-400 py-8">
              No filters configured
            </div>
            <div
              v-for="(filter, idx) in route.filters"
              :key="idx"
              class="bg-gray-700 rounded-lg p-4 border border-gray-600"
            >
              <p class="text-white font-bold">{{ filter.name }}</p>
              <p class="text-gray-400 text-sm mt-2">{{ filter.args }}</p>
            </div>
          </div>

          <!-- Metadata Tab -->
          <div v-show="activeTab === 'metadata'" class="space-y-3">
            <div v-if="!route.metadata || Object.keys(route.metadata).length === 0" class="text-center text-gray-400 py-8">
              No metadata
            </div>
            <div
              v-for="(value, key) in route.metadata"
              :key="key"
              class="bg-gray-700 rounded-lg p-4 border border-gray-600"
            >
              <p class="text-gray-400 text-sm">{{ key }}</p>
              <p class="text-white font-medium mt-2">{{ value }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Action Buttons -->
      <div class="flex gap-3">
        <router-link
          to="/routes"
          class="flex-1 px-4 py-2 bg-gray-700 hover:bg-gray-600 text-white font-medium rounded-lg transition-colors text-center"
        >
          Back
        </router-link>
        <button
          @click="editRoute"
          class="flex-1 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition-colors"
        >
          Edit
        </button>
        <button
          @click="toggleRoute"
          :class="[
            'flex-1 px-4 py-2 text-white font-medium rounded-lg transition-colors',
            route.enabled ? 'bg-red-600 hover:bg-red-700' : 'bg-green-600 hover:bg-green-700'
          ]"
        >
          {{ route.enabled ? 'Disable' : 'Enable' }}
        </button>
        <button
          @click="deleteRoute"
          class="flex-1 px-4 py-2 bg-red-600 hover:bg-red-700 text-white font-medium rounded-lg transition-colors"
        >
          Delete
        </button>
      </div>
    </div>

    <div v-else class="text-center text-gray-400 py-8">
      Route not found
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'

export default {
  name: 'RouteDetail',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const store = useStore()
    const activeTab = ref('config')

    const tabs = [
      { id: 'config', name: '⚙️ Configuration' },
      { id: 'predicates', name: '🔍 Predicates' },
      { id: 'filters', name: '📝 Filters' },
      { id: 'metadata', name: '🏷️ Metadata' }
    ]

    const routeDetail = computed(() => {
      return store.state.routes.find(r => r.id === route.params.id)
    })

    const formatDateTime = (datetime) => {
      if (!datetime) return 'N/A'
      return new Date(datetime).toLocaleString()
    }

    const getHealthColor = (status) => {
      switch (status) {
        case 'UP':
          return 'text-green-400'
        case 'DOWN':
          return 'text-red-400'
        case 'SLOW':
          return 'text-yellow-400'
        default:
          return 'text-gray-400'
      }
    }

    const editRoute = () => {
      router.push('/routes')
    }

    const toggleRoute = async () => {
      try {
        await store.dispatch('toggleRouteStatus', {
          id: routeDetail.value.id,
          enable: !routeDetail.value.enabled
        })
      } catch (error) {
        console.error('Error toggling route:', error)
      }
    }

    const deleteRoute = async () => {
      if (confirm('Are you sure you want to delete this route?')) {
        try {
          await store.dispatch('deleteRoute', routeDetail.value.id)
          router.push('/routes')
        } catch (error) {
          console.error('Error deleting route:', error)
        }
      }
    }

    onMounted(() => {
      store.dispatch('fetchRoutes')
    })

    return {
      route: routeDetail,
      activeTab,
      tabs,
      formatDateTime,
      getHealthColor,
      editRoute,
      toggleRoute,
      deleteRoute
    }
  }
}
</script>

