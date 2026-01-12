<template>
  <div class="p-8 ml-64">
    <div>
      <h1 class="text-4xl font-bold text-white mb-2">Audit Log</h1>
      <p class="text-gray-400">View complete history of all changes made to routes</p>
    </div>

    <!-- Tabs -->
    <div class="bg-gray-800 border border-gray-700 rounded-lg overflow-hidden mt-8">
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
        <!-- Change History Tab -->
        <div v-show="activeTab === 'history'" class="space-y-4">
          <div class="flex gap-4 mb-6 flex-wrap">
            <input
              v-model="filters.routeId"
              type="text"
              placeholder="Filter by route ID..."
              class="flex-1 min-w-xs px-4 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:border-blue-500"
            />
            <select
              v-model="filters.action"
              class="px-4 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:outline-none focus:border-blue-500"
            >
              <option value="">All Actions</option>
              <option value="CREATE">Create</option>
              <option value="UPDATE">Update</option>
              <option value="DELETE">Delete</option>
            </select>
            <input
              v-model="filters.user"
              type="text"
              placeholder="Filter by user..."
              class="px-4 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:border-blue-500"
            />
            <button
              @click="clearFilters"
              class="px-4 py-2 bg-gray-600 hover:bg-gray-500 text-white rounded-lg transition-colors"
            >
              Clear
            </button>
          </div>

          <div v-if="filteredAudits.length === 0" class="text-center text-gray-400 py-8">
            No audit entries found
          </div>

          <div
            v-for="(audit, idx) in filteredAudits"
            :key="idx"
            class="bg-gray-700 rounded-lg p-4 border border-gray-600"
          >
            <div class="flex items-start justify-between">
              <div class="flex-1">
                <div class="flex items-center gap-3 mb-2">
                  <span
                    :class="[
                      'px-3 py-1 rounded-full text-white text-xs font-bold',
                      audit.action === 'CREATE' ? 'bg-green-600' :
                      audit.action === 'UPDATE' ? 'bg-blue-600' :
                      'bg-red-600'
                    ]"
                  >
                    {{ audit.action }}
                  </span>
                  <h3 class="text-lg font-bold text-white">{{ audit.routeId }}</h3>
                  <span class="text-gray-400 text-sm">v{{ audit.version }}</span>
                </div>

                <div class="grid grid-cols-2 gap-4 mt-3 text-sm">
                  <div>
                    <p class="text-gray-400">User</p>
                    <p class="text-white font-medium">{{ audit.createdBy }}</p>
                  </div>
                  <div>
                    <p class="text-gray-400">Time</p>
                    <p class="text-white font-medium">{{ formatDateTime(audit.createdAt) }}</p>
                  </div>
                </div>

                <div class="mt-3 text-sm text-gray-300">
                  <p>{{ audit.description }}</p>
                </div>

                <div v-if="audit.oldValue || audit.newValue" class="mt-4">
                  <button
                    @click="toggleDiff(idx)"
                    class="text-blue-400 hover:text-blue-300 text-sm font-medium"
                  >
                    {{ expandedDiffs.has(idx) ? '▼ Hide Changes' : '▶ Show Changes' }}
                  </button>

                  <div v-if="expandedDiffs.has(idx)" class="mt-3 space-y-2">
                    <div v-if="audit.oldValue" class="bg-red-900 rounded p-3">
                      <p class="text-red-200 text-xs font-bold mb-1">Previous Value:</p>
                      <pre class="text-red-100 text-xs overflow-auto">{{ formatJson(audit.oldValue) }}</pre>
                    </div>
                    <div v-if="audit.newValue" class="bg-green-900 rounded p-3">
                      <p class="text-green-200 text-xs font-bold mb-1">New Value:</p>
                      <pre class="text-green-100 text-xs overflow-auto">{{ formatJson(audit.newValue) }}</pre>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Timeline View Tab -->
        <div v-show="activeTab === 'timeline'" class="space-y-6">
          <div v-if="groupedAudits.length === 0" class="text-center text-gray-400 py-8">
            No audit entries found
          </div>

          <div v-for="(group, idx) in groupedAudits" :key="idx" class="relative">
            <div class="flex items-start gap-4">
              <div class="relative flex flex-col items-center">
                <div class="w-4 h-4 bg-blue-500 rounded-full mt-2"></div>
                <div v-if="idx < groupedAudits.length - 1" class="w-0.5 h-24 bg-gray-700 mt-2"></div>
              </div>

              <div class="flex-1 pb-8">
                <p class="text-gray-400 text-sm font-medium">{{ formatDateTime(group.createdAt) }}</p>
                <div class="bg-gray-700 rounded-lg p-4 mt-2 border border-gray-600">
                  <h4 class="text-white font-bold">{{ group.action }} - {{ group.routeId }}</h4>
                  <p class="text-gray-300 text-sm mt-1">By {{ group.createdBy }}</p>
                  <span
                    :class="[
                      'inline-block mt-2 px-2 py-1 rounded text-white text-xs',
                      group.action === 'CREATE' ? 'bg-green-600' :
                      group.action === 'UPDATE' ? 'bg-blue-600' :
                      'bg-red-600'
                    ]"
                  >
                    Version {{ group.version }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Version Control Tab -->
        <div v-show="activeTab === 'versions'" class="space-y-4">
          <div class="mb-6">
            <label class="block text-sm font-medium text-gray-300 mb-2">
              Select Route
            </label>
            <select
              v-model="selectedRoute"
              class="w-full px-4 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:outline-none focus:border-blue-500"
            >
              <option value="">Choose a route...</option>
              <option v-for="route in routes" :key="route.id" :value="route.id">
                {{ route.id }}
              </option>
            </select>
          </div>

          <div v-if="selectedRoute && routeVersions.length > 0" class="space-y-3">
            <div
              v-for="(version, idx) in routeVersions"
              :key="idx"
              class="bg-gray-700 rounded-lg p-4 border border-gray-600"
            >
              <div class="flex items-center justify-between">
                <div>
                  <span class="text-white font-bold">Version {{ version.version }}</span>
                  <span class="text-gray-400 text-sm ml-2">by {{ version.createdBy }}</span>
                </div>
                <div class="flex gap-2">
                  <button
                    @click="compareVersions(idx)"
                    class="px-3 py-1 bg-blue-600 hover:bg-blue-700 text-white text-sm rounded"
                  >
                    Compare
                  </button>
                  <button
                    @click="rollbackVersion(version)"
                    class="px-3 py-1 bg-green-600 hover:bg-green-700 text-white text-sm rounded"
                  >
                    Rollback
                  </button>
                </div>
              </div>
              <p class="text-gray-400 text-sm mt-2">{{ formatDateTime(version.createdAt) }}</p>
            </div>
          </div>

          <div v-else-if="selectedRoute" class="text-center text-gray-400 py-8">
            No version history for this route
          </div>
        </div>

        <!-- Export/Analysis Tab -->
        <div v-show="activeTab === 'export'" class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <button
              @click="exportAuditLog"
              class="px-4 py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors"
            >
              📥 Export Audit Log (JSON)
            </button>
            <button
              @click="exportAuditCSV"
              class="px-4 py-3 bg-green-600 hover:bg-green-700 text-white rounded-lg transition-colors"
            >
              📄 Export as CSV
            </button>
            <button
              @click="printAuditLog"
              class="px-4 py-3 bg-purple-600 hover:bg-purple-700 text-white rounded-lg transition-colors"
            >
              🖨️ Print Audit Log
            </button>
            <button
              @click="clearAuditLog"
              class="px-4 py-3 bg-red-600 hover:bg-red-700 text-white rounded-lg transition-colors"
            >
              🗑️ Clear Old Entries
            </button>
          </div>

          <div class="bg-gray-700 rounded-lg p-4 mt-4">
            <h4 class="text-white font-bold mb-3">Audit Statistics</h4>
            <div class="grid grid-cols-3 gap-4">
              <div>
                <p class="text-gray-400 text-sm">Total Entries</p>
                <p class="text-2xl font-bold text-white">{{ allAudits.length }}</p>
              </div>
              <div>
                <p class="text-gray-400 text-sm">Create Actions</p>
                <p class="text-2xl font-bold text-green-400">{{ countByAction('CREATE') }}</p>
              </div>
              <div>
                <p class="text-gray-400 text-sm">Update Actions</p>
                <p class="text-2xl font-bold text-blue-400">{{ countByAction('UPDATE') }}</p>
              </div>
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
  name: 'AuditLog',
  setup() {
    const store = useStore()
    const activeTab = ref('history')
    const expandedDiffs = ref(new Set())
    const filters = ref({
      routeId: '',
      action: '',
      user: ''
    })
    const selectedRoute = ref('')

    const tabs = [
      { id: 'history', name: '📋 Change History' },
      { id: 'timeline', name: '📊 Timeline View' },
      { id: 'versions', name: '🔄 Version Control' },
      { id: 'export', name: '💾 Export & Analysis' }
    ]

    const routes = computed(() => store.state.routes)
    const allAudits = computed(() => store.state.auditHistory)

    const filteredAudits = computed(() => {
      return allAudits.value.filter(audit => {
        const matchesRoute = !filters.value.routeId || audit.routeId.toLowerCase().includes(filters.value.routeId.toLowerCase())
        const matchesAction = !filters.value.action || audit.action === filters.value.action
        const matchesUser = !filters.value.user || audit.createdBy.toLowerCase().includes(filters.value.user.toLowerCase())
        return matchesRoute && matchesAction && matchesUser
      })
    })

    const groupedAudits = computed(() => {
      const sorted = [...filteredAudits.value].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
      return sorted.slice(0, 10)
    })

    const routeVersions = computed(() => {
      if (!selectedRoute.value) return []
      return allAudits.value.filter(a => a.routeId === selectedRoute.value).reverse()
    })

    const formatDateTime = (datetime) => {
      if (!datetime) return 'N/A'
      const date = new Date(datetime)
      return date.toLocaleString()
    }

    const formatJson = (json) => {
      try {
        if (typeof json === 'string') {
          return JSON.stringify(JSON.parse(json), null, 2)
        }
        return JSON.stringify(json, null, 2)
      } catch {
        return json
      }
    }

    const toggleDiff = (idx) => {
      if (expandedDiffs.value.has(idx)) {
        expandedDiffs.value.delete(idx)
      } else {
        expandedDiffs.value.add(idx)
      }
    }

    const clearFilters = () => {
      filters.value = { routeId: '', action: '', user: '' }
    }

    const compareVersions = (idx) => {
      alert('Version comparison feature coming soon')
    }

    const rollbackVersion = (version) => {
      if (confirm(`Are you sure you want to rollback to version ${version.version}?`)) {
        alert('Rollback feature coming soon')
      }
    }

    const exportAuditLog = () => {
      const data = JSON.stringify(filteredAudits.value, null, 2)
      const blob = new Blob([data], { type: 'application/json' })
      const url = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `audit-log-${new Date().toISOString().split('T')[0]}.json`
      link.click()
    }

    const exportAuditCSV = () => {
      const headers = ['Timestamp', 'Route', 'Action', 'Version', 'User', 'Description']
      const rows = filteredAudits.value.map(a => [
        a.createdAt,
        a.routeId,
        a.action,
        a.version,
        a.createdBy,
        a.description
      ])

      const csv = [headers, ...rows].map(row => row.map(cell => `"${cell}"`).join(',')).join('\n')
      const blob = new Blob([csv], { type: 'text/csv' })
      const url = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `audit-log-${new Date().toISOString().split('T')[0]}.csv`
      link.click()
    }

    const printAuditLog = () => {
      window.print()
    }

    const clearAuditLog = () => {
      alert('Clear functionality coming soon')
    }

    const countByAction = (action) => {
      return allAudits.value.filter(a => a.action === action).length
    }

    onMounted(() => {
      store.dispatch('fetchRoutes')
      // Fetch audit history for all routes
      routes.value.forEach(route => {
        store.dispatch('fetchAuditHistory', route.id)
      })
    })

    return {
      activeTab,
      tabs,
      expandedDiffs,
      filters,
      selectedRoute,
      routes,
      allAudits,
      filteredAudits,
      groupedAudits,
      routeVersions,
      formatDateTime,
      formatJson,
      toggleDiff,
      clearFilters,
      compareVersions,
      rollbackVersion,
      exportAuditLog,
      exportAuditCSV,
      printAuditLog,
      clearAuditLog,
      countByAction
    }
  }
}
</script>

