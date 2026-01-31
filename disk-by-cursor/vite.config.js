import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    proxy: {
      '/api/v1/auth': {
        target: 'http://localhost:8089',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/api\/v1\/auth/, '/api/v1/auth')
      },
      '/api/v1/files': {
        target: 'http://localhost:8082',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/api\/v1\/files/, '/api/v1/files')
      },
      '/api/v1/shares': {
        target: 'http://localhost:8085',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/api\/v1\/shares/, '/api/v1/shares')
      },
      '/api/v1/users': {
        target: 'http://localhost:8086',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/api\/v1\/users/, '/api/v1/users')
      }
    }
  }
}) 