<template>
  <div class="pan-task-list-content">
    <el-button 
      type="info" 
      size="small" 
      @click="showTaskList = !showTaskList"
      :class="{ 'active': showTaskList }"
    >
      <el-icon><Upload /></el-icon>
      传输列表
      <el-badge v-if="uploadTasks.length > 0" :value="uploadTasks.length" class="task-badge" />
    </el-button>
    
    <div v-if="showTaskList" class="task-list-panel">
      <div class="task-list-header">
        <span>传输列表</span>
        <el-button type="text" size="small" @click="clearCompletedTasks">
          清除已完成
        </el-button>
      </div>
      <div class="task-list-body">
        <div v-if="uploadTasks.length === 0" class="empty-tasks">
          暂无传输任务
        </div>
        <div v-else class="task-items">
          <div 
            v-for="task in uploadTasks" 
            :key="task.filename" 
            class="task-item"
          >
            <div class="task-info">
              <div class="task-name">{{ task.filename }}</div>
              <div class="task-size">{{ task.fileSize }}</div>
            </div>
            <div class="task-progress-container">
              <div class="progress-section">
                <el-progress 
                  :percentage="task.percentage" 
                  :status="task.status === panUtil.fileStatus.FAIL.code ? 'exception' : undefined"
                  :stroke-width="4"
                />
              </div>
              <div class="actions-section">
                <el-icon 
                  v-if="task.status === panUtil.fileStatus.FAIL.code" 
                  @click="retryTask(task)"
                  class="action-icon retry-icon"
                  title="重试"
                >
                  <RefreshLeft />
                </el-icon>
                <el-icon 
                  v-if="task.status === panUtil.fileStatus.PAUSE.code" 
                  @click="resumeTask(task)"
                  class="action-icon resume-icon"
                  title="继续"
                >
                  <VideoPlay />
                </el-icon>
                <el-icon 
                  v-if="task.status === panUtil.fileStatus.UPLOADING.code" 
                  @click="pauseTask(task)"
                  class="action-icon pause-icon"
                  title="暂停"
                >
                  <VideoPause />
                </el-icon>
                <el-icon 
                  @click="removeTask(task)"
                  class="action-icon cancel-icon"
                  title="取消"
                >
                  <Close />
                </el-icon>
              </div>
            </div>
            <div class="task-status">
              <span class="status-text">{{ task.statusText }}</span>
              <span class="uploaded-size">{{ task.uploadedSize }}</span>
              <span class="speed">{{ task.speed }}</span>
              <span class="time-remaining">{{ task.timeRemaining }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { 
  Upload, 
  RefreshLeft, 
  VideoPlay, 
  VideoPause, 
  Close 
} from '@element-plus/icons-vue'
import { useTaskStore } from '@/stores/task'
import panUtil from '@/utils/common'

const taskStore = useTaskStore()
const showTaskList = ref(false)

// 监听任务列表变化，自动显示传输列表
watch(() => taskStore.taskList.length, (newLength, oldLength) => {
  if (newLength > 0 && oldLength === 0) {
    // 当有新任务添加时，自动显示传输列表
    showTaskList.value = true
  }
}, { immediate: true })

// 使用真实的上传任务数据
const uploadTasks = computed(() => taskStore.taskList)

const clearCompletedTasks = () => {
  // 清除已完成的任务
  taskStore.clear()
}

const removeTask = (task) => {
  taskStore.remove(task.filename)
}

const retryTask = (task) => {
  taskStore.retry(task.filename)
}

const pauseTask = (task) => {
  taskStore.pause(task.filename)
}

const resumeTask = (task) => {
  taskStore.resume(task.filename)
}

const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}
</script>

<style scoped>
.pan-task-list-content {
  position: relative;
}

.task-list-panel {
  position: absolute;
  top: 100%;
  right: 0;
  width: 380px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  margin-top: 8px;
  z-index: 1000;
  border: 1px solid rgba(0, 0, 0, 0.08);
  backdrop-filter: blur(10px);
}

.task-list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
  font-weight: 500;
}

.task-list-body {
  max-height: 300px;
  overflow-y: auto;
}

.empty-tasks {
  padding: 40px 16px;
  text-align: center;
  color: #999;
  font-size: 14px;
}

.task-items {
  padding: 8px 0;
}

.task-item {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.3s ease;
  border-radius: 8px;
  margin: 8px;
  background: #fafbfc;
}

.task-item:hover {
  background-color: #f0f2f5;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.task-item:last-child {
  border-bottom: none;
}

.task-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.task-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 200px;
}

.task-size {
  font-size: 12px;
  color: #999;
}

.task-progress-container {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.progress-section {
  flex: 1;
}

.actions-section {
  display: flex;
  gap: 8px;
  align-items: center;
}

:deep(.el-progress) {
  border-radius: 6px;
}

:deep(.el-progress-bar__outer) {
  background-color: #f0f0f0;
  border-radius: 6px;
}

:deep(.el-progress-bar__inner) {
  border-radius: 6px;
  transition: width 0.3s ease;
}

.task-status {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 12px;
  color: #666;
}

.status-text {
  color: #409eff;
  font-weight: 500;
}

.uploaded-size {
  color: #67c23a;
}

.speed {
  color: #e6a23c;
}

.time-remaining {
  color: #909399;
}

.task-actions {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}

.action-icon {
  font-size: 18px;
  cursor: pointer;
  padding: 6px;
  border-radius: 6px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-icon:hover {
  transform: scale(1.1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.retry-icon {
  color: #409eff;
  background: rgba(64, 158, 255, 0.1);
}

.retry-icon:hover {
  background: rgba(64, 158, 255, 0.2);
  color: #337ecc;
}

.resume-icon {
  color: #67c23a;
  background: rgba(103, 194, 58, 0.1);
}

.resume-icon:hover {
  background: rgba(103, 194, 58, 0.2);
  color: #5daf34;
}

.pause-icon {
  color: #e6a23c;
  background: rgba(230, 162, 60, 0.1);
}

.pause-icon:hover {
  background: rgba(230, 162, 60, 0.2);
  color: #cf9234;
}

.cancel-icon {
  color: #f56c6c;
  background: rgba(245, 108, 108, 0.1);
}

.cancel-icon:hover {
  background: rgba(245, 108, 108, 0.2);
  color: #dd5a5a;
}

.task-badge {
  margin-left: 4px;
}

.active {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
}
</style>

