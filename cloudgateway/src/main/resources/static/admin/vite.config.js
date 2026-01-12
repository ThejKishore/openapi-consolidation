import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:9000',
        changeOrigin: true
      }
    }
  },
  build: {
    outDir: '../../../build/resources/main/static/admin/dist',
    emptyOutDir: true,
    minify: 'terser',
    sourcemap: true
  }
})

