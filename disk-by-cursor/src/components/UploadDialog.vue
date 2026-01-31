<template>
  <el-dialog v-model="visible" title="上传文件" width="600px" @close="handleClose">
    <div class="upload-container">
      <!-- 拖拽上传区域 -->
      <el-upload
        ref="uploadRef"
        :action="uploadUrl"
        :headers="uploadHeaders"
        :data="uploadData"
        :before-upload="beforeUpload"
        :on-change="onFileChange"
        :auto-upload="false"
        :accept="'*/*'"
        multiple
        drag
        class="upload-area"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            支持大文件上传，单个文件最大100MB
          </div>
        </template>
      </el-upload>
      
      <!-- 文件列表预览 -->
      <div v-if="selectedFiles.length > 0" class="file-preview">
        <h4>已选择的文件 ({{ selectedFiles.length }})</h4>
        <div class="file-list">
          <div v-for="file in selectedFiles" :key="file.uid" class="file-item">
            <el-icon class="file-icon"><Document /></el-icon>
            <span class="file-name">{{ file.name }}</span>
            <span class="file-size">{{ formatFileSize(file.size) }}</span>
            <el-button 
              size="small" 
              type="danger" 
              @click="removeFile(file)"
              class="remove-btn"
            >
              移除
            </el-button>
          </div>
        </div>
      </div>
    </div>
    
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button 
        type="primary" 
        @click="startUpload"
        :loading="isUploading"
        :disabled="selectedFiles.length === 0"
      >
        开始上传
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  parentId: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue', 'success', 'upload-start'])

const userStore = useUserStore()
const uploadRef = ref()
const isUploading = ref(false)
const selectedFiles = ref([])

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const uploadUrl = '/api/file/upload'
const uploadHeaders = computed(() => ({
  Authorization: `${userStore.token}`
}))
const uploadData = computed(() => ({
  parentId: props.parentId
}))

// 监听对话框显示状态
watch(visible, (newVal) => {
  if (!newVal) {
    // 对话框关闭时清空文件列表
    selectedFiles.value = []
    isUploading.value = false
  }
})

const beforeUpload = (file) => {
  if (!file) {
    return false
  }
  
  const maxSize = 100 * 1024 * 1024 // 100MB
  if (file.size > maxSize) {
    ElMessage.error('文件大小不能超过100MB')
    return false
  }
  
  // 检查是否已经存在同名文件
  const existingFile = selectedFiles.value.find(item => item.name === file.name)
  if (existingFile) {
    ElMessage.warning(`文件 "${file.name}" 已存在`)
    return false
  }
  
  return false // 阻止自动上传
}

const onFileChange = (file, fileList) => {
  // 当文件选择时，添加到已选择文件列表
  const fileToAdd = file.raw || file
  if (fileToAdd && !selectedFiles.value.find(f => f.name === fileToAdd.name)) {
    selectedFiles.value.push(fileToAdd)
  }
}

const removeFile = (file) => {
  const index = selectedFiles.value.findIndex(f => f.uid === file.uid)
  if (index > -1) {
    selectedFiles.value.splice(index, 1)
  }
}

const startUpload = () => {
  if (selectedFiles.value.length === 0) {
    ElMessage.warning('请先选择要上传的文件')
    return
  }
  
  isUploading.value = true
  
  // 通知父组件开始上传
  emit('upload-start', selectedFiles.value)
  
  // 关闭对话框
  handleClose()
}



const handleClose = () => {
  visible.value = false
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
.upload-container {
  min-height: 300px;
}

.upload-area {
  margin-bottom: 20px;
}

.file-preview {
  margin-top: 20px;
}

.file-preview h4 {
  margin-bottom: 15px;
  color: #333;
}

.file-list {
  max-height: 200px;
  overflow-y: auto;
}

.file-item {
  display: flex;
  align-items: center;
  padding: 10px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  margin-bottom: 8px;
  background: #fafafa;
}

.file-icon {
  margin-right: 8px;
  color: #409eff;
}

.file-name {
  flex: 1;
  font-weight: 500;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-size {
  color: #666;
  font-size: 12px;
  margin-right: 10px;
}

.remove-btn {
  flex-shrink: 0;
}
</style> 