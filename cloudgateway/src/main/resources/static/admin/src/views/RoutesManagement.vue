<template>
  <div class="p-8 ml-64">
    <div class="flex justify-between items-center mb-8">
      <div>
        <h1 class="text-4xl font-bold text-white mb-2">Route Management</h1>
        <p class="text-gray-400">Manage and configure gateway routes</p>
      </div>
      <button
        @click="showAddRoute = true"
        class="px-6 py-2 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition-colors"
      >
        + New Route
      </button>
    </div>

    <!-- Tabs -->
    <div class="bg-gray-800 border border-gray-700 rounded-lg overflow-hidden mb-8">
      <div class="flex border-b border-gray-700">
        <button
          v-for="tab in tabs"
          :key="tab.id"
          @click="activeTab = tab.id"
          :class="[
            'px-6 py-4 font-medium transition-colors border-b-2',
            activeTab === tab.id
              ? 'text-blue-400 border-blue-500 bg-gray-750'
              : 'text-gray-400 border-transparent hover:text-gray-300'
          ]"
        >
          {{ tab.name }}
        </button>
      </div>

      <div class="p-6">
        <!-- Routes List Tab -->
        <div v-show="activeTab === 'list'" class="space-y-4">
          <div class="flex gap-4 mb-6">
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Search routes..."
              class="flex-1 px-4 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:border-blue-500"
            />
            <select
              v-model="filterStatus"
              class="px-4 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:outline-none focus:border-blue-500"
            >
              <option value="">All Status</option>
              <option value="enabled">Enabled</option>
              <option value="disabled">Disabled</option>
            </select>
          </div>

          <div v-if="filteredRoutes.length === 0" class="text-center text-gray-400 py-8">
            No routes found
          </div>

          <div v-for="route in filteredRoutes" :key="route.id" class="bg-gray-700 rounded-lg p-4 border border-gray-600">
            <div class="flex items-start justify-between">
              <div class="flex-1">
                <h3 class="text-lg font-bold text-white">{{ route.id }}</h3>
                <p class="text-gray-400 text-sm mt-1">URI: {{ route.uri }}</p>
                <div class="flex gap-2 mt-3">
                  <span v-if="route.enabled" class="inline-block px-2 py-1 bg-green-900 text-green-200 text-xs rounded">
                    ✓ Enabled
                  </span>
                  <span v-else class="inline-block px-2 py-1 bg-red-900 text-red-200 text-xs rounded">
                    ✗ Disabled
                  </span>
                  <span class="inline-block px-2 py-1 bg-blue-900 text-blue-200 text-xs rounded">
                    v{{ route.version }}
                  </span>
                </div>
              </div>

              <div class="flex gap-2">
                <router-link
                  :to="`/routes/${route.id}`"
                  class="px-3 py-2 bg-blue-600 hover:bg-blue-700 text-white text-sm rounded transition-colors"
                >
                  View
                </router-link>
                <button
                  @click="editRoute(route)"
                  class="px-3 py-2 bg-gray-600 hover:bg-gray-500 text-white text-sm rounded transition-colors"
                >
                  Edit
                </button>
                <button
                  @click="toggleRoute(route)"
                  :class="[
                    'px-3 py-2 text-white text-sm rounded transition-colors',
                    route.enabled ? 'bg-red-600 hover:bg-red-700' : 'bg-green-600 hover:bg-green-700'
                  ]"
                >
                  {{ route.enabled ? 'Disable' : 'Enable' }}
                </button>
                <button
                  @click="deleteRoute(route.id)"
                  class="px-3 py-2 bg-red-600 hover:bg-red-700 text-white text-sm rounded transition-colors"
                >
                  Delete
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Bulk Operations Tab -->
        <div v-show="activeTab === 'bulk'" class="space-y-4">
          <div class="bg-gray-700 p-4 rounded-lg">
            <h3 class="text-lg font-bold text-white mb-4">Bulk Operations</h3>
            <div class="grid grid-cols-2 gap-4">
              <button
                @click="enableAllRoutes"
                class="px-4 py-3 bg-green-600 hover:bg-green-700 text-white rounded-lg transition-colors"
              >
                Enable All Routes
              </button>
              <button
                @click="disableAllRoutes"
                class="px-4 py-3 bg-red-600 hover:bg-red-700 text-white rounded-lg transition-colors"
              >
                Disable All Routes
              </button>
              <button
                @click="refreshAllHealth"
                class="px-4 py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors"
              >
                Refresh Health Status
              </button>
              <button
                @click="exportRoutes"
                class="px-4 py-3 bg-purple-600 hover:bg-purple-700 text-white rounded-lg transition-colors"
              >
                Export Routes (JSON)
              </button>
            </div>
          </div>
        </div>

        <!-- Add/Edit Form Tab -->
        <div v-show="activeTab === 'form'" class="space-y-4">
          <RouteForm
            :route="editingRoute"
            @submit="saveRoute"
            @cancel="editingRoute = null"
          />
        </div>
      </div>
    </div>

    <!-- Modal for add route -->
    <div v-if="showAddRoute" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-gray-800 border border-gray-700 rounded-lg p-6 max-w-2xl w-full mx-4">
        <h2 class="text-2xl font-bold text-white mb-4">Create New Route</h2>
        <RouteForm
          @submit="createNewRoute"
          @cancel="showAddRoute = false"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import RouteForm from '../components/RouteForm.vue'

export default {
  name: 'RoutesManagement',
  components: {
    RouteForm
  },
  setup() {
    const store = useStore()
    const activeTab = ref('list')
    const showAddRoute = ref(false)
    const editingRoute = ref(null)
    const searchQuery = ref('')
    const filterStatus = ref('')

    const tabs = [
      { id: 'list', name: '📋 Routes List' },
      { id: 'bulk', name: '⚙️ Bulk Operations' },
      { id: 'form', name: '➕ Add Route' }
    ]

    const routes = computed(() => store.state.routes)

    const filteredRoutes = computed(() => {
      return routes.value.filter(route => {
        const matchesSearch = route.id.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
                              route.uri.toLowerCase().includes(searchQuery.value.toLowerCase())
        const matchesStatus = !filterStatus.value ||
                              (filterStatus.value === 'enabled' && route.enabled) ||
                              (filterStatus.value === 'disabled' && !route.enabled)
        return matchesSearch && matchesStatus
      })
    })

    const editRoute = (route) => {
      editingRoute.value = { ...route }
      activeTab.value = 'form'
    }

    const saveRoute = async (routeData) => {
      try {
        if (editingRoute.value?.id) {
          await store.dispatch('updateRoute', {
            id: editingRoute.value.id,
            routeData
          })
        } else {
          await store.dispatch('createRoute', routeData)
        }
        editingRoute.value = null
        activeTab.value = 'list'
      } catch (error) {
        console.error('Error saving route:', error)
      }
    }

    const createNewRoute = async (routeData) => {
      try {
        await store.dispatch('createRoute', routeData)
        showAddRoute.value = false
      } catch (error) {
        console.error('Error creating route:', error)
      }
    }

    const deleteRoute = async (routeId) => {
      if (confirm('Are you sure you want to delete this route?')) {
        try {
          await store.dispatch('deleteRoute', routeId)
        } catch (error) {
          console.error('Error deleting route:', error)
        }
      }
    }

    const toggleRoute = async (route) => {
      try {
        await store.dispatch('toggleRouteStatus', {
          id: route.id,
          enable: !route.enabled
        })
      } catch (error) {
        console.error('Error toggling route:', error)
      }
    }

    const enableAllRoutes = async () => {
      for (const route of routes.value) {
        if (!route.enabled) {
          try {
            await store.dispatch('toggleRouteStatus', {
              id: route.id,
              enable: true
            })
          } catch (error) {
            console.error('Error enabling route:', error)
          }
        }
      }
    }

    const disableAllRoutes = async () => {
      for (const route of routes.value) {
        if (route.enabled) {
          try {
            await store.dispatch('toggleRouteStatus', {
              id: route.id,
              enable: false
            })
          } catch (error) {
            console.error('Error disabling route:', error)
          }
        }
      }
    }

    const refreshAllHealth = async () => {
      try {
        await store.dispatch('fetchHealthStatus')
      } catch (error) {
        console.error('Error refreshing health:', error)
      }
    }

    const exportRoutes = () => {
      const dataStr = JSON.stringify(routes.value, null, 2)
      const dataBlob = new Blob([dataStr], { type: 'application/json' })
      const url = URL.createObjectURL(dataBlob)
      const link = document.createElement('a')
      link.href = url
      link.download = `routes-${new Date().toISOString().split('T')[0]}.json`
      link.click()
    }

    onMounted(() => {
      store.dispatch('fetchRoutes')
    })

    return {
      activeTab,
      tabs,
      showAddRoute,
      editingRoute,
      searchQuery,
      filterStatus,
      filteredRoutes,
      editRoute,
      saveRoute,
      createNewRoute,
      deleteRoute,
      toggleRoute,
      enableAllRoutes,
      disableAllRoutes,
      refreshAllHealth,
      exportRoutes
    }
  }
}
</script>

