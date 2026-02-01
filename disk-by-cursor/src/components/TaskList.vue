<template>
  <div v-if="taskStore.viewFlag" class="task-list">
    <div class="task-header">
      <h3>上传任务 ({{ taskStore.uploadTaskNum }})</h3>
      <button class="clear-btn" @click="clearAll">清空</button>
    </div>
    
    <div class="task-items">
      <div 
        v-for="task in taskStore.taskList" 
        :key="task.filename" 
        class="task-item"
      >
        <div class="task-info">
          <div class="filename">{{ task.filename }}</div>
          <div class="file-size">{{ task.fileSize }}</div>
        </div>
        
        <div class="task-progress">
          <div class="progress-bar">
            <div 
              class="progress-fill" 
              :style="{ width: task.percentage + '%' }"
            ></div>
          </div>
          <div class="progress-text">{{ task.percentage }}%</div>
        </div>
        
        <div class="task-status">
          <div class="status-text">{{ task.statusText }}</div>
          <div class="uploaded-size">{{ task.uploadedSize }}</div>
          <div class="speed">{{ task.speed }}</div>
          <div class="time-remaining">{{ task.timeRemaining }}</div>
        </div>
        
        <div class="task-actions">
          <button 
            v-if="task.status === fileStatus.UPLOADING.code" 
            class="action-btn pause-btn" 
            @click="pauseTask(task.filename)"
          >
            暂停
          </button>
          <button 
            v-if="task.status === fileStatus.PAUSE.code" 
            class="action-btn resume-btn" 
            @click="resumeTask(task.filename)"
          >
            继续
          </button>
          <button 
            v-if="task.status === fileStatus.FAIL.code" 
            class="action-btn retry-btn" 
            @click="retryTask(task.filename)"
          >
            重试
          </button>
          <button 
            class="action-btn cancel-btn" 
            @click="cancelTask(task.filename)"
          >
            取消
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useTaskStore } from '@/stores/task.js'
import panUtil from '@/utils/common.js'

const taskStore = useTaskStore()
const fileStatus = panUtil.fileStatus

const pauseTask = (filename) => {
  taskStore.pause(filename)
}

const resumeTask = (filename) => {
  taskStore.resume(filename)
}

const retryTask = (filename) => {
  taskStore.retry(filename)
}

const cancelTask = (filename) => {
  taskStore.cancel(filename)
}

const clearAll = () => {
  taskStore.clear()
}
</script>

<style scoped>
.task-list {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 400px;
  max-height: 500px;
  background: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  overflow: hidden;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #f5f5f5;
  border-bottom: 1px solid #ddd;
}

.task-header h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.clear-btn {
  background: #ff4757;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.clear-btn:hover {
  background: #ff3742;
}

.task-items {
  max-height: 400px;
  overflow-y: auto;
}

.task-item {
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
}

.task-item:last-child {
  border-bottom: none;
}

.task-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.filename {
  font-weight: 500;
  color: #333;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-size {
  color: #666;
  font-size: 12px;
  margin-left: 10px;
}

.task-progress {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.progress-bar {
  flex: 1;
  height: 6px;
  background: #e0e0e0;
  border-radius: 3px;
  overflow: hidden;
  margin-right: 10px;
}

.progress-fill {
  height: 100%;
  background: #409eff;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 12px;
  color: #666;
  min-width: 40px;
  text-align: right;
}

.task-status {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 12px;
  color: #666;
}

.status-text {
  color: #409eff;
  font-weight: 500;
}

.task-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  border: none;
  padding: 4px 8px;
  border-radius: 3px;
  cursor: pointer;
  font-size: 11px;
  transition: all 0.2s;
}

.pause-btn {
  background: #ffa502;
  color: white;
}

.pause-btn:hover {
  background: #ff9500;
}

.resume-btn {
  background: #2ed573;
  color: white;
}

.resume-btn:hover {
  background: #26d0ce;
}

.retry-btn {
  background: #5352ed;
  color: white;
}

.retry-btn:hover {
  background: #3742fa;
}

.cancel-btn {
  background: #ff4757;
  color: white;
}

.cancel-btn:hover {
  background: #ff3742;
}
</style>
