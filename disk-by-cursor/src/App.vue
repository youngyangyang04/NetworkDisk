<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

onMounted(async () => {
  // 初始化用户信息
  await userStore.initUserInfo()
})
</script>

<style>
/* 全局样式已在 theme.css 中定义 */
</style> 