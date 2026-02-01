<template>
  <div class="pan-header-content">
    <div class="pan-header-content-wrapper">
      <div class="pan-title-font-content">
        <span class="pan-title-font" @click="goHome">卡码网盘</span>
      </div>
      <div class="pan-upload-task-list-content">
        <pan-task-list />
      </div>
      <div class="pan-user-info-content">
        <pan-user-info />
      </div>
    </div>
  </div>
</template>

<script setup>
import PanUserInfo from '@/components/user-info/index.vue'
import PanTaskList from '@/components/task-list/index.vue'

import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>

<style scoped>
.pan-header-content {
  top: 0;
  left: 0;
  width: 100%;
  z-index: 41;
  position: fixed;
}

.pan-header-content .pan-header-content-wrapper {
  height: 62px;
  display: flex;
  align-items: center;
  position: relative;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  padding: 0 20px;
}

.pan-header-content .pan-header-content-wrapper .pan-title-font-content {
  flex: 1;
}

.pan-header-content-wrapper .pan-title-font-content .pan-title-font {
  font-size: 24px;
  font-weight: 600;
  cursor: pointer;
  color: #fff;
  letter-spacing: 1px;
  transition: all 0.3s ease;
}

.pan-header-content-wrapper .pan-title-font-content .pan-title-font:hover {
  transform: scale(1.02);
}

.pan-header-content-wrapper .pan-user-info-content {
  margin-left: auto;
}

.pan-header-content-wrapper .pan-upload-task-list-content {
  margin-right: 20px;
}
</style>

