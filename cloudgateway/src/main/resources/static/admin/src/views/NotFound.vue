<template>
  <div class="min-h-screen bg-gradient-to-br from-gray-900 to-gray-800 flex items-center justify-center p-4">
    <div class="text-center max-w-md">
      <div class="text-6xl mb-4">🚫</div>
      <h1 class="text-4xl font-bold text-white mb-2">Page Not Found</h1>
      <p class="text-gray-400 mb-8">The page you're looking for doesn't exist.</p>
      <router-link
        to="/"
        class="inline-block px-6 py-3 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition-colors"
      >
        Back to Dashboard
      </router-link>
    </div>
  </div>
</template>

<script>
export default {
  name: 'NotFound'
}
</script>

