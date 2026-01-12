<template>
  <div class="p-8 ml-64">
    <div class="mb-8">
      <h1 class="text-4xl font-bold text-white mb-2">Dashboard</h1>
      <p class="text-gray-400">Welcome to Cloud Gateway Admin Portal</p>
    </div>

    <!-- Statistics Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
      <div class="bg-gray-800 border border-gray-700 rounded-lg p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-gray-400 text-sm">Total Routes</p>
            <p class="text-3xl font-bold text-white mt-2">{{ totalRoutes }}</p>
          </div>
          <span class="text-4xl">🛣️</span>
        </div>
      </div>

      <div class="bg-gray-800 border border-gray-700 rounded-lg p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-gray-400 text-sm">Healthy Routes</p>
            <p class="text-3xl font-bold text-green-400 mt-2">{{ healthyCount }}</p>
          </div>
          <span class="text-4xl">✅</span>
        </div>
      </div>

      <div class="bg-gray-800 border border-gray-700 rounded-lg p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-gray-400 text-sm">Unhealthy Routes</p>
            <p class="text-3xl font-bold text-red-400 mt-2">{{ unhealthyCount }}</p>
          </div>
          <span class="text-4xl">❌</span>
        </div>
      </div>

      <div class="bg-gray-800 border border-gray-700 rounded-lg p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-gray-400 text-sm">Recent Changes</p>
            <p class="text-3xl font-bold text-blue-400 mt-2">{{ recentChanges }}</p>
          </div>
          <span class="text-4xl">📊</span>
        </div>
      </div>
    </div>

    <!-- Tabs Section -->
    <div class="bg-gray-800 border border-gray-700 rounded-lg overflow-hidden">
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
        <!-- Quick Stats Tab -->
        <div v-show="activeTab === 'stats'" class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div class="bg-gray-700 rounded-lg p-4">
              <p class="text-gray-400 text-sm">Avg Response Time</p>
              <p class="text-2xl font-bold text-white mt-1">145ms</p>
            </div>
            <div class="bg-gray-700 rounded-lg p-4">
              <p class="text-gray-400 text-sm">Total Requests (24h)</p>
              <p class="text-2xl font-bold text-white mt-1">12.4K</p>
            </div>
            <div class="bg-gray-700 rounded-lg p-4">
              <p class="text-gray-400 text-sm">Success Rate</p>
              <p class="text-2xl font-bold text-green-400 mt-1">99.8%</p>
            </div>
            <div class="bg-gray-700 rounded-lg p-4">
              <p class="text-gray-400 text-sm">Uptime</p>
              <p class="text-2xl font-bold text-white mt-1">99.99%</p>
            </div>
          </div>
        </div>

        <!-- Recent Activity Tab -->
        <div v-show="activeTab === 'activity'" class="space-y-3">
          <div v-if="recentActivity.length === 0" class="text-center text-gray-400 py-8">
            No recent activity
          </div>
          <div
            v-for="activity in recentActivity"
            :key="activity.id"
            class="flex items-center justify-between bg-gray-700 p-4 rounded-lg"
          >
            <div>
              <p class="text-white font-medium">{{ activity.action }}</p>
              <p class="text-gray-400 text-sm">{{ activity.route }}</p>
            </div>
            <span class="text-gray-400 text-sm">{{ activity.time }}</span>
          </div>
        </div>

        <!-- System Health Tab -->
        <div v-show="activeTab === 'system'" class="space-y-4">
          <div class="bg-gray-700 rounded-lg p-4">
            <div class="flex items-center justify-between mb-2">
              <span class="text-gray-300">Database Connection</span>
              <span class="text-green-400 font-medium">Connected</span>
            </div>
            <div class="w-full bg-gray-600 rounded-full h-2">
              <div class="bg-green-500 h-2 rounded-full" style="width: 100%"></div>
            </div>
          </div>

          <div class="bg-gray-700 rounded-lg p-4">
            <div class="flex items-center justify-between mb-2">
              <span class="text-gray-300">Memory Usage</span>
              <span class="text-blue-400 font-medium">45%</span>
            </div>
            <div class="w-full bg-gray-600 rounded-full h-2">
              <div class="bg-blue-500 h-2 rounded-full" style="width: 45%"></div>
            </div>
          </div>

          <div class="bg-gray-700 rounded-lg p-4">
            <div class="flex items-center justify-between mb-2">
              <span class="text-gray-300">CPU Usage</span>
              <span class="text-yellow-400 font-medium">62%</span>
            </div>
            <div class="w-full bg-gray-600 rounded-full h-2">
              <div class="bg-yellow-500 h-2 rounded-full" style="width: 62%"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'

export default {
  name: 'Dashboard',
  setup() {
    const store = useStore()
    const activeTab = ref('stats')
    const recentActivity = ref([
      { id: 1, action: 'Route Created', route: 'employeesvc', time: '2 minutes ago' },
      { id: 2, action: 'Route Updated', route: 'personsvc', time: '5 minutes ago' },
      { id: 3, action: 'Route Enabled', route: 'externalsvc', time: '10 minutes ago' }
    ])

    const routes = computed(() => store.state.routes)
    const healthStatus = computed(() => store.state.healthStatus)

    const totalRoutes = computed(() => routes.value.length)
    const healthyCount = computed(() => {
      return Object.values(healthStatus.value).filter(h => h?.status === 'UP').length
    })
    const unhealthyCount = computed(() => {
      return Object.values(healthStatus.value).filter(h => h?.status === 'DOWN').length
    })
    const recentChanges = computed(() => recentActivity.value.length)

    const tabs = [
      { id: 'stats', name: '📊 Quick Stats' },
      { id: 'activity', name: '📝 Recent Activity' },
      { id: 'system', name: '🔧 System Health' }
    ]

    onMounted(() => {
      store.dispatch('fetchRoutes')
      store.dispatch('fetchHealthStatus')
    })

    return {
      activeTab,
      tabs,
      totalRoutes,
      healthyCount,
      unhealthyCount,
      recentChanges,
      recentActivity
    }
  }
}
</script>

