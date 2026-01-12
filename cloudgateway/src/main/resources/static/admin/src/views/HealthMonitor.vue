<template>
  <div class="p-8 ml-64">
    <div class="flex justify-between items-center mb-8">
      <div>
        <h1 class="text-4xl font-bold text-white mb-2">Health Monitor</h1>
        <p class="text-gray-400">Real-time health status of all gateway routes</p>
      </div>
      <button
        @click="refreshHealth"
        :disabled="refreshing"
        class="px-6 py-2 bg-blue-600 hover:bg-blue-700 disabled:bg-gray-600 text-white font-medium rounded-lg transition-colors"
      >
        {{ refreshing ? 'Refreshing...' : '🔄 Refresh' }}
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
        <!-- Status Overview Tab -->
        <div v-show="activeTab === 'overview'" class="space-y-4">
          <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
            <div class="bg-gray-700 rounded-lg p-6 border-l-4 border-green-500">
              <p class="text-gray-400 text-sm">Healthy Routes</p>
              <p class="text-3xl font-bold text-green-400 mt-2">{{ healthyCount }}</p>
              <p class="text-gray-500 text-xs mt-2">All systems operational</p>
            </div>

            <div class="bg-gray-700 rounded-lg p-6 border-l-4 border-red-500">
              <p class="text-gray-400 text-sm">Unhealthy Routes</p>
              <p class="text-3xl font-bold text-red-400 mt-2">{{ unhealthyCount }}</p>
              <p class="text-gray-500 text-xs mt-2">Immediate attention needed</p>
            </div>

            <div class="bg-gray-700 rounded-lg p-6 border-l-4 border-yellow-500">
              <p class="text-gray-400 text-sm">Slow Routes</p>
              <p class="text-3xl font-bold text-yellow-400 mt-2">{{ slowCount }}</p>
              <p class="text-gray-500 text-xs mt-2">Response time > 5s</p>
            </div>
          </div>

          <div class="space-y-3">
            <div
              v-for="route in routes"
              :key="route.id"
              class="bg-gray-700 rounded-lg p-4 border border-gray-600"
            >
              <div class="flex items-center justify-between">
                <div class="flex-1">
                  <h3 class="text-lg font-bold text-white">{{ route.id }}</h3>
                  <p class="text-gray-400 text-sm mt-1">{{ route.uri }}</p>
                  <div class="flex items-center gap-4 mt-3">
                    <div class="flex items-center gap-2">
                      <span
                        :class="[
                          'w-3 h-3 rounded-full',
                          getHealthColor(route.health?.status)
                        ]"
                      ></span>
                      <span class="text-white font-medium">{{ route.health?.status || 'UNKNOWN' }}</span>
                    </div>
                    <span class="text-gray-400">⏱️ {{ route.health?.responseTime || 0 }}ms</span>
                    <span class="text-gray-400 text-xs">Last: {{ formatTime(route.health?.lastChecked) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Detailed Metrics Tab -->
        <div v-show="activeTab === 'metrics'" class="space-y-4">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div class="bg-gray-700 rounded-lg p-4">
              <h4 class="text-white font-bold mb-4">Average Response Times</h4>
              <div class="space-y-3">
                <div v-for="route in routes" :key="route.id" class="flex items-center justify-between">
                  <span class="text-gray-300 text-sm">{{ route.id }}</span>
                  <span class="text-white font-medium">{{ route.health?.responseTime || 0 }}ms</span>
                </div>
              </div>
            </div>

            <div class="bg-gray-700 rounded-lg p-4">
              <h4 class="text-white font-bold mb-4">Uptime (24h)</h4>
              <div class="space-y-3">
                <div v-for="route in routes" :key="route.id" class="flex items-center justify-between">
                  <span class="text-gray-300 text-sm">{{ route.id }}</span>
                  <div class="flex items-center gap-2">
                    <div class="w-32 bg-gray-600 rounded-full h-2">
                      <div
                        class="bg-green-500 h-2 rounded-full"
                        :style="{ width: getUptimePercentage(route.health?.status) + '%' }"
                      ></div>
                    </div>
                    <span class="text-white text-sm font-medium">{{ getUptimePercentage(route.health?.status) }}%</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Alerts Tab -->
        <div v-show="activeTab === 'alerts'" class="space-y-4">
          <div v-if="alerts.length === 0" class="text-center text-gray-400 py-8">
            No active alerts
          </div>

          <div
            v-for="alert in alerts"
            :key="alert.id"
            :class="[
              'p-4 rounded-lg border-l-4',
              alert.level === 'critical' ? 'bg-red-900 border-red-500 text-red-200' : 'bg-yellow-900 border-yellow-500 text-yellow-200'
            ]"
          >
            <div class="flex items-start justify-between">
              <div>
                <h4 class="font-bold">{{ alert.title }}</h4>
                <p class="text-sm mt-1">{{ alert.message }}</p>
                <p class="text-xs mt-2 opacity-75">{{ alert.time }}</p>
              </div>
              <button
                @click="dismissAlert(alert.id)"
                class="text-sm hover:underline"
              >
                Dismiss
              </button>
            </div>
          </div>
        </div>

        <!-- History Tab -->
        <div v-show="activeTab === 'history'" class="space-y-4">
          <div class="flex gap-4 mb-6">
            <button
              @click="timeRange = '24h'"
              :class="[
                'px-4 py-2 rounded-lg transition-colors',
                timeRange === '24h' ? 'bg-blue-600 text-white' : 'bg-gray-700 text-gray-300 hover:bg-gray-600'
              ]"
            >
              Last 24h
            </button>
            <button
              @click="timeRange = '7d'"
              :class="[
                'px-4 py-2 rounded-lg transition-colors',
                timeRange === '7d' ? 'bg-blue-600 text-white' : 'bg-gray-700 text-gray-300 hover:bg-gray-600'
              ]"
            >
              Last 7 days
            </button>
            <button
              @click="timeRange = '30d'"
              :class="[
                'px-4 py-2 rounded-lg transition-colors',
                timeRange === '30d' ? 'bg-blue-600 text-white' : 'bg-gray-700 text-gray-300 hover:bg-gray-600'
              ]"
            >
              Last 30 days
            </button>
          </div>

          <div class="bg-gray-700 rounded-lg p-4">
            <p class="text-gray-400 text-center py-8">
              Historical health data chart would be displayed here
            </p>
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
  name: 'HealthMonitor',
  setup() {
    const store = useStore()
    const activeTab = ref('overview')
    const refreshing = ref(false)
    const timeRange = ref('24h')
    const alerts = ref([
      {
        id: 1,
        level: 'critical',
        title: 'Route unavailable',
        message: 'External service is currently unreachable',
        time: '5 minutes ago'
      }
    ])

    const tabs = [
      { id: 'overview', name: '📊 Overview' },
      { id: 'metrics', name: '📈 Metrics' },
      { id: 'alerts', name: '⚠️ Alerts' },
      { id: 'history', name: '📅 History' }
    ]

    const routes = computed(() => store.state.routes)
    const healthStatus = computed(() => store.state.healthStatus)

    const healthyCount = computed(() => {
      return routes.value.filter(r => healthStatus.value[r.id]?.status === 'UP').length
    })

    const unhealthyCount = computed(() => {
      return routes.value.filter(r => healthStatus.value[r.id]?.status === 'DOWN').length
    })

    const slowCount = computed(() => {
      return routes.value.filter(r => healthStatus.value[r.id]?.status === 'SLOW').length
    })

    const getHealthColor = (status) => {
      switch (status) {
        case 'UP':
          return 'bg-green-500'
        case 'DOWN':
          return 'bg-red-500'
        case 'SLOW':
          return 'bg-yellow-500'
        default:
          return 'bg-gray-500'
      }
    }

    const getUptimePercentage = (status) => {
      switch (status) {
        case 'UP':
          return 99.9
        case 'DOWN':
          return 0
        case 'SLOW':
          return 95
        default:
          return 50
      }
    }

    const formatTime = (timestamp) => {
      if (!timestamp) return 'Never'
      const date = new Date(timestamp)
      return date.toLocaleTimeString()
    }

    const refreshHealth = async () => {
      refreshing.value = true
      try {
        await store.dispatch('fetchHealthStatus')
      } catch (error) {
        console.error('Error refreshing health:', error)
      } finally {
        refreshing.value = false
      }
    }

    const dismissAlert = (id) => {
      alerts.value = alerts.value.filter(a => a.id !== id)
    }

    onMounted(() => {
      store.dispatch('fetchRoutes')
      store.dispatch('fetchHealthStatus')
    })

    return {
      activeTab,
      tabs,
      refreshing,
      timeRange,
      alerts,
      routes,
      healthyCount,
      unhealthyCount,
      slowCount,
      getHealthColor,
      getUptimePercentage,
      formatTime,
      refreshHealth,
      dismissAlert
    }
  }
}
</script>

