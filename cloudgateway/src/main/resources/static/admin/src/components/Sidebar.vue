<template>
  <aside class="w-64 bg-gray-800 border-r border-gray-700 h-screen overflow-y-auto fixed left-0 top-16">
    <nav class="p-4 space-y-2">
      <router-link
        to="/"
        active-class="bg-blue-600 text-white"
        class="flex items-center space-x-3 px-4 py-3 rounded-lg text-gray-300 hover:bg-gray-700 transition-colors"
      >
        <span class="text-xl">📊</span>
        <span>Dashboard</span>
      </router-link>

      <router-link
        to="/routes"
        active-class="bg-blue-600 text-white"
        class="flex items-center space-x-3 px-4 py-3 rounded-lg text-gray-300 hover:bg-gray-700 transition-colors"
      >
        <span class="text-xl">🛣️</span>
        <span>Routes</span>
      </router-link>

      <router-link
        to="/health"
        active-class="bg-blue-600 text-white"
        class="flex items-center space-x-3 px-4 py-3 rounded-lg text-gray-300 hover:bg-gray-700 transition-colors"
      >
        <span class="text-xl">💚</span>
        <span>Health Monitor</span>
      </router-link>

      <router-link
        to="/audit"
        active-class="bg-blue-600 text-white"
        class="flex items-center space-x-3 px-4 py-3 rounded-lg text-gray-300 hover:bg-gray-700 transition-colors"
      >
        <span class="text-xl">📋</span>
        <span>Audit Log</span>
      </router-link>
    </nav>
  </aside>
</template>

<script>
export default {
  name: 'Sidebar'
}
</script>

<style scoped>
aside {
  margin-left: 0;
}
</style>

