<template>
  <div class="pan-content">
    <pan-header />
    <div class="pan-main-wrapper">
      <div class="sidebar-container" :class="{ 'sidebar-collapsed': isSidebarCollapsed }">
        <pan-navbar :is-collapsed="isSidebarCollapsed" />
        <div class="sidebar-toggle" @click="toggleSidebar">
          <el-icon><ArrowLeft v-if="!isSidebarCollapsed" /><ArrowRight v-else /></el-icon>
        </div>
      </div>
      <pan-app-main />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import PanHeader from '@/components/header/index.vue'
import PanNavbar from '@/components/navbar/index.vue'
import PanAppMain from '@/components/app-main/index.vue'

const isSidebarCollapsed = ref(false)

const toggleSidebar = () => {
  isSidebarCollapsed.value = !isSidebarCollapsed.value
}
</script>

<style scoped>
.pan-content {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8f0 100%);
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  transition: all 0.3s ease;
}

.pan-main-wrapper {
  display: flex;
  flex: 1;
  margin-top: 62px;
  padding: 8px;
  gap: 8px;
  min-width: 0;
  position: relative;
  height: calc(100vh - 62px);
}

.sidebar-container {
  position: relative;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.sidebar-container.sidebar-collapsed {
  width: 50px;
}

.sidebar-container:not(.sidebar-collapsed) {
  width: 160px;
}

.sidebar-toggle {
  position: absolute;
  right: -12px;
  top: 50%;
  transform: translateY(-50%);
  width: 24px;
  height: 24px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 10;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.sidebar-toggle:hover {
  background: #f5f7fa;
  transform: translateY(-50%) scale(1.1);
}

.pan-main-wrapper > pan-app-main {
  flex: 1;
  min-width: 0;
  overflow: auto;
}

/* 响应式布局 */
@media (max-width: 1200px) {
  .pan-main-wrapper {
    gap: 8px;
    padding: 8px;
  }
}

@media (max-width: 992px) {
  .pan-main-wrapper {
    gap: 6px;
    padding: 6px;
  }
}
</style> 