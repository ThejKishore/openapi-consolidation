<template>
  <div class="min-h-screen bg-gradient-to-br from-gray-900 to-gray-800 flex items-center justify-center p-4">
    <div class="max-w-md w-full bg-gray-800 rounded-lg shadow-xl p-8 border border-gray-700">
      <div class="text-center mb-8">
        <div class="w-16 h-16 bg-blue-600 rounded-lg flex items-center justify-center mx-auto mb-4">
          <span class="text-white font-bold text-2xl">GW</span>
        </div>
        <h1 class="text-2xl font-bold text-white">Gateway Admin</h1>
        <p class="text-gray-400 text-sm mt-2">Cloud Gateway Management Portal</p>
      </div>

      <form @submit.prevent="handleLogin" class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-300 mb-2">
            Username
          </label>
          <input
            v-model="credentials.username"
            type="text"
            placeholder="Enter username"
            class="w-full px-4 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-300 mb-2">
            Password
          </label>
          <input
            v-model="credentials.password"
            type="password"
            placeholder="Enter password"
            class="w-full px-4 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
          />
        </div>

        <button
          type="submit"
          :disabled="isLoading"
          class="w-full px-4 py-2 bg-blue-600 hover:bg-blue-700 disabled:bg-gray-600 text-white font-medium rounded-lg transition-colors"
        >
          {{ isLoading ? 'Logging in...' : 'Login' }}
        </button>
      </form>

      <div v-if="error" class="mt-4 p-3 bg-red-900 border border-red-700 rounded-lg text-red-200 text-sm">
        {{ error }}
      </div>

      <div class="mt-6 pt-6 border-t border-gray-700">
        <p class="text-xs text-gray-400 text-center">
          For demo: Use any username/password (OAuth2 integration needed)
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import { reactive, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'

export default {
  name: 'Login',
  setup() {
    const store = useStore()
    const router = useRouter()
    const isLoading = computed(() => store.state.loading)
    const error = computed(() => store.state.error)

    const credentials = reactive({
      username: 'admin',
      password: 'admin'
    })

    const handleLogin = async () => {
      // Auto-login for development - no OAuth2 required
      try {
        const mockToken = 'dev-token-' + Date.now()
        const mockUser = {
          name: 'Developer',
          roles: ['ADMIN']
        }

        await store.dispatch('login', {
          token: mockToken,
          user: mockUser
        })

        router.push('/')
      } catch (err) {
        console.error('Login failed:', err)
      }
    }

    // Auto-login on page load for development
    onMounted(() => {
      handleLogin()
    })

    return {
      credentials,
      isLoading,
      error,
      handleLogin
    }
  }
}
</script>

