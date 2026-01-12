<template>
  <header class="bg-gray-800 border-b border-gray-700 sticky top-0 z-40">
    <div class="flex items-center justify-between h-16 px-6">
      <div class="flex items-center space-x-3">
        <div class="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center">
          <span class="text-white font-bold text-lg">GW</span>
        </div>
        <h1 class="text-xl font-bold text-white">Gateway Admin</h1>
      </div>

      <div class="flex items-center space-x-6">
        <div class="text-gray-300 text-sm">
          <span class="text-gray-400">User:</span>
          <span class="ml-2 font-medium">{{ currentUser?.name || 'Admin' }}</span>
        </div>

        <button
          @click="logout"
          class="px-4 py-2 bg-red-600 hover:bg-red-700 text-white rounded-lg transition-colors"
        >
          Logout
        </button>
      </div>
    </div>
  </header>
</template>

<script>
import { computed } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'

export default {
  name: 'Header',
  setup() {
    const store = useStore()
    const router = useRouter()
    const currentUser = computed(() => store.state.auth.user)

    const logout = () => {
      store.dispatch('logout')
      router.push('/login')
    }

    return {
      currentUser,
      logout
    }
  }
}
</script>

