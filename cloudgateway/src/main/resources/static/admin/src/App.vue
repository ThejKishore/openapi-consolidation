<template>
  <div id="app" class="min-h-screen bg-gray-900">
    <Header v-if="isLoggedIn" />
    <div v-if="isLoggedIn" class="flex h-full">
      <Sidebar />
      <main class="flex-1 overflow-auto">
        <router-view />
      </main>
    </div>
    <div v-else>
      <router-view />
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useStore } from 'vuex'
import Header from './components/Header.vue'
import Sidebar from './components/Sidebar.vue'

export default {
  name: 'App',
  components: {
    Header,
    Sidebar
  },
  setup() {
    const store = useStore()
    const isLoggedIn = computed(() => store.state.auth.isLoggedIn)

    return {
      isLoggedIn
    }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
}
</style>

