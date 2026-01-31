<template>
  <div class="pan-app-main-content">
    <router-view :key="$route.fullPath" />
  </div>
</template>

<script setup>
// 主要内容区域组件
</script>

<style scoped>
.pan-app-main-content {
  width: 100%;
  height: 100%;
  padding: 8px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  box-sizing: border-box;
  max-width: 100%;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .pan-app-main-content {
    padding: 6px;
  }
}

@media (max-width: 992px) {
  .pan-app-main-content {
    padding: 4px;
  }
}
</style>

