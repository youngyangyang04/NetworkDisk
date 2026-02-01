<template>
  <div class="upload-button-content">
    <button 
      v-if="roundFlag" 
      class="upload-btn primary round" 
      @click="openDialog"
    >
      上传
      <i class="upload-icon">📤</i>
    </button>
    
    <button 
      v-if="circleFlag" 
      class="upload-btn circle" 
      @click="openDialog"
    >
      📤
    </button>

    <!-- 上传对话框 -->
    <div v-if="uploadDialogVisible" class="upload-dialog-overlay" @click="closeDialog">
      <div class="upload-dialog" @click.stop>
        <div class="upload-dialog-header">
          <h3>文件上传</h3>
          <button class="close-btn" @click="closeDialog">×</button>
        </div>
        
        <div class="upload-content" id="upload-content">
          <div 
            class="drag-content" 
            @click="triggerFileSelect"
            @dragover.prevent
            @drop.prevent="handleDrop"
            @dragenter.prevent
            @dragleave.prevent
          >
            <div class="drag-icon-content">
              <div class="upload-icon-large">📁</div>
            </div>
            <div class="drag-text-content">
              <span class="drag-text">将文件拖到此处，或</span>
              <span class="click-upload">点击上传</span>
            </div>
          </div>
          <!-- 隐藏的文件输入框 -->
          <input 
            ref="fileInput" 
            type="file" 
            multiple 
            style="display: none;" 
            @change="handleFileSelect"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import Uploader from 'simple-uploader.js'
import panUtil from '@/utils/common.js'
import { MD5 } from '@/utils/md5.js'
import { secUpload, mergeChunks } from '@/api/file.js'
import { useFileStore } from '@/stores/file.js'
import { useTaskStore } from '@/stores/task.js'
import { useUserStore } from '@/stores/user.js'

const props = defineProps({
  roundFlag: {
    type: Boolean,
    default: true
  },
  circleFlag: {
    type: Boolean,
    default: false
  },
  size: {
    type: String,
    default: 'medium'
  }
})

const fileStore = useFileStore()
const taskStore = useTaskStore()
const userStore = useUserStore()

const uploadDialogVisible = ref(false)
let uploader = undefined
const assignFlag = ref(false)
const fileInput = ref(null)
let isAlive = true

// 上传配置
const fileOptions = {
  target: function (file, chunk) {
    if (panUtil.getChunkUploadSwitch()) {
      return '/api/v1/files/file/chunk-upload'
    }
    return '/api/v1/files/file/upload'
  },
  singleFile: false,
  chunkSize: panUtil.getChunkSize(),
  testChunks: panUtil.getChunkUploadSwitch(),
  forceChunkSize: false,
  simultaneousUploads: 3,
  fileParameterName: 'file',
  query: function (file, chunk) {
    return {
      parentId: fileStore.currentFolderId || '0'
    }
  },
  headers: function () {
    return {
      Authorization: `${userStore.token}`
    }
  },
  checkChunkUploadedByResponse: function (chunk, message) {
    let objMessage = {}
    try {
      objMessage = JSON.parse(message)
    } catch (e) {
      // 解析失败，返回false
    }
    if (objMessage.data) {
      return (objMessage.data.uploadedChunks || []).indexOf(chunk.offset + 1) >= 0
    }
    return false
  },
  maxChunkRetries: 0,
  chunkRetryInterval: null,
  progressCallbacksInterval: 500,
  successStatuses: [200, 201, 202],
  permanentErrors: [404, 415, 500, 501],
  initialPaused: false
}

const closeDialog = () => {
  uploadDialogVisible.value = false
  // 重置绑定标志，确保下次打开时能重新绑定
  assignFlag.value = false
  
  // 清理上传器的绑定，避免重复绑定
  if (uploader) {
    if (typeof uploader.unAssignBrowse === 'function') {
      uploader.unAssignBrowse()
    }
    if (typeof uploader.unAssignDrop === 'function') {
      uploader.unAssignDrop()
    }
  }
}

// 打开对话框时重新绑定上传器
const openDialog = () => {
  // 如果对话框已经打开，则不再重复打开
  if (uploadDialogVisible.value) {
    return
  }
  
  // 确保绑定标志为false，允许重新绑定
  assignFlag.value = false
  uploadDialogVisible.value = true
  
  // 等待DOM更新后重新绑定上传器
  nextTick(() => {
    rebindUploader()
  })
}

// 触发文件选择
const triggerFileSelect = () => {
  // 防止重复触发
  if (fileInput.value) {
    fileInput.value.click()
  }
}

// 处理文件选择
const handleFileSelect = (event) => {
  const files = Array.from(event.target.files)
  if (files.length > 0 && uploader) {
    // 将选择的文件添加到上传器
    uploader.addFiles(files)
    // 清空文件输入框，允许重复选择相同文件
    event.target.value = ''
  }
}

// 处理文件拖拽
const handleDrop = (event) => {
  event.preventDefault()
  const files = Array.from(event.dataTransfer.files)
  if (files.length > 0 && uploader) {
    // 将拖拽的文件添加到上传器
    uploader.addFiles(files)
  }
}

const rebindUploader = () => {
  if (uploader && !uploader.support) {
    alert('本浏览器不支持simple-uploader，请更换浏览器重试')
    return
  }
  if (uploader && !assignFlag.value) {
    const uploadContent = document.getElementById('upload-content')
    if (uploadContent) {
      // 先解绑之前的绑定，避免重复绑定
      if (typeof uploader.unAssignBrowse === 'function') {
        uploader.unAssignBrowse()
      }
      if (typeof uploader.unAssignDrop === 'function') {
        uploader.unAssignDrop()
      }
      
      // 重新绑定
      uploader.assignBrowse(uploadContent)
      uploader.assignDrop(uploadContent)
      assignFlag.value = true
    }
  }
}

const filesAdded = (files, fileList, event) => {
  if (!isAlive || !uploader) return false
  uploadDialogVisible.value = false
  
  try {
    files.forEach((f) => {
      f.pause()
      if (f.size > panUtil.getMaxFileSize()) {
        throw new Error('文件：' + f.name + '大小超过了最大上传文件的限制（' + panUtil.translateFileSize(panUtil.getMaxFileSize()) + '）')
      }
      
      let taskItem = {
        target: f,
        filename: f.name,
        fileSize: panUtil.translateFileSize(f.size),
        uploadedSize: panUtil.translateFileSize(0),
        status: panUtil.fileStatus.PARSING.code,
        statusText: panUtil.fileStatus.PARSING.text,
        timeRemaining: panUtil.translateTime(Number.POSITIVE_INFINITY),
        speed: panUtil.translateSpeed(f.averageSpeed),
        percentage: 0,
        parentId: String(fileStore.currentFolderId || '0')
      }
      
      // 添加到任务列表
      taskStore.add(taskItem)
      
      // 自动显示传输列表
      taskStore.updateViewFlag(true)
      
      // 计算MD5
      MD5(f.file, (e, md5) => {
         f['uniqueIdentifier'] = md5
         secUpload({
           filename: f.name,
           identifier: md5,
           parentId: fileStore.currentFolderId || '0'
         }).then(res => {
           if (res.code === 0 || res.success === true) {
             f.cancel()
             taskStore.remove(f.name)
            fileStore.loadFileList()
            if (uploader && Array.isArray(uploader.files) && uploader.files.length === 0) {
               taskStore.updateViewFlag(false)
             }
           } else {
             f.resume()
             taskStore.updateStatus({
               filename: f.name,
               status: panUtil.fileStatus.WAITING.code,
               statusText: panUtil.fileStatus.WAITING.text
             })
           }
         }).catch(err => {
           f.resume()
           taskStore.updateStatus({
             filename: f.name,
             status: panUtil.fileStatus.WAITING.code,
             statusText: panUtil.fileStatus.WAITING.text
           })
         })
       })
    })
  } catch (e) {
    alert(e.message)
    if (uploader && typeof uploader.cancel === 'function') {
      try { uploader.cancel() } catch (err) {}
    }
    taskStore.clear()
    return false
  }
  
  taskStore.updateViewFlag(true)
  return true
}

const uploadProgress = (rootFile, file, chunk) => {
  if (!isAlive) return
  let uploadTaskItem = taskStore.getUploadTask(file.name)
  if (file.isUploading()) {
    if (uploadTaskItem.status !== panUtil.fileStatus.UPLOADING.code) {
      taskStore.updateStatus({
        filename: file.name,
        status: panUtil.fileStatus.UPLOADING.code,
        statusText: panUtil.fileStatus.UPLOADING.text
      })
    }
    taskStore.updateProcess({
      filename: file.name,
      speed: panUtil.translateSpeed(file.averageSpeed),
      percentage: Math.floor(file.progress() * 100),
      uploadedSize: panUtil.translateFileSize(file.sizeUploaded()),
      timeRemaining: panUtil.translateTime(file.timeRemaining())
    })
  }
}

const doMerge = (file) => {
  if (!isAlive) return
  let uploadTaskItem = taskStore.getUploadTask(file.name)
  taskStore.updateStatus({
    filename: file.name,
    status: panUtil.fileStatus.MERGE.code,
    statusText: panUtil.fileStatus.MERGE.text
  })
  taskStore.updateProcess({
    filename: file.name,
    speed: panUtil.translateSpeed(file.averageSpeed),
    percentage: 99,
    uploadedSize: panUtil.translateFileSize(file.sizeUploaded()),
    timeRemaining: panUtil.translateTime(file.timeRemaining())
  })
  
  mergeChunks({
    filename: uploadTaskItem.filename,
    identifier: uploadTaskItem.target.uniqueIdentifier,
    parentId: uploadTaskItem.parentId,
    totalSize: uploadTaskItem.target.size
  }).then(res => {
    if (res.code === 0 || res.success === true) {
      if (uploader && typeof uploader.removeFile === 'function') {
        uploader.removeFile(file)
      }
      fileStore.loadFileList()
      taskStore.updateStatus({
        filename: file.name,
        status: panUtil.fileStatus.SUCCESS.code,
        statusText: panUtil.fileStatus.SUCCESS.text
      })
      taskStore.remove(file.name)
      if (uploader && Array.isArray(uploader.files) && uploader.files.length === 0) {
        taskStore.updateViewFlag(false)
      }
    } else {
      file.pause()
      taskStore.updateStatus({
        filename: file.name,
        status: panUtil.fileStatus.FAIL.code,
        statusText: panUtil.fileStatus.FAIL.text
      })
    }
  }).catch(err => {
    file.pause()
    taskStore.updateStatus({
      filename: file.name,
      status: panUtil.fileStatus.FAIL.code,
      statusText: panUtil.fileStatus.FAIL.text
    })
  })
}

const fileUploaded = (rootFile, file, message, chunk) => {
  if (!isAlive) return
  let res = {}
  try {
    res = JSON.parse(message)
  } catch (e) {
    // 解析失败
  }
  
  if (res.code === 0 || res.success === true) {
    if (res.data) {
      if (res.data.mergeFlag) {
        doMerge(file)
      } else if (res.data.uploadedChunks && res.data.uploadedChunks.length === file.chunks.length) {
        doMerge(file)
      }
    } else {
      if (uploader && typeof uploader.removeFile === 'function') {
        uploader.removeFile(file)
      }
      fileStore.loadFileList()
      taskStore.updateStatus({
        filename: file.name,
        status: panUtil.fileStatus.SUCCESS.code,
        statusText: panUtil.fileStatus.SUCCESS.text
      })
      taskStore.remove(file.name)
      if (uploader && Array.isArray(uploader.files) && uploader.files.length === 0) {
        taskStore.updateViewFlag(false)
      }
    }
  } else {
    file.pause()
    taskStore.updateStatus({
      filename: file.name,
      status: panUtil.fileStatus.FAIL.code,
      statusText: panUtil.fileStatus.FAIL.text
    })
  }
}

const uploadComplete = () => {
  // 上传完成
}

const uploadError = (rootFile, file, message, chunk) => {
  if (!isAlive) return
  taskStore.updateStatus({
    filename: file.name,
    status: panUtil.fileStatus.FAIL.code,
    statusText: panUtil.fileStatus.FAIL.text
  })
  taskStore.updateProcess({
    filename: file.name,
    speed: panUtil.translateSpeed(0),
    percentage: 0,
    uploadedSize: panUtil.translateFileSize(0),
    timeRemaining: panUtil.translateTime(Number.POSITIVE_INFINITY)
  })
}

const initUploader = () => {
  taskStore.clear()
  // 实例化上传对象
  uploader = new Uploader(fileOptions)
  
  if (!uploader.support) {
    alert('本浏览器不支持simple-uploader，请更换浏览器重试')
    return
  }
  
  // 绑定事件
  uploader.on("filesAdded", filesAdded)
  uploader.on("fileProgress", uploadProgress)
  uploader.on("fileSuccess", fileUploaded)
  uploader.on("complete", uploadComplete)
  uploader.on("fileError", uploadError)
}

onMounted(() => {
  initUploader()
})

onUnmounted(() => {
  isAlive = false
  if (uploader) {
    // 清理所有绑定
    if (typeof uploader.cancel === 'function') {
      try { uploader.cancel() } catch (e) {}
    }
    if (typeof uploader.unAssignBrowse === 'function') {
      uploader.unAssignBrowse()
    }
    if (typeof uploader.unAssignDrop === 'function') {
      uploader.unAssignDrop()
    }
    if (typeof uploader.off === 'function') {
      uploader.off()
    }
    uploader = undefined
  }
  // 重置状态
  assignFlag.value = false
  uploadDialogVisible.value = false
})
</script>

<style scoped>
.upload-button-content {
  display: inline-block;
  margin-right: 10px;
}

.upload-btn {
  border: none;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.upload-btn.primary {
  background-color: #409eff;
  color: white;
  padding: 8px 16px;
  border-radius: 4px;
}

.upload-btn.primary:hover {
  background-color: #66b1ff;
}

.upload-btn.round {
  border-radius: 20px;
}

.upload-btn.circle {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #409eff;
  color: white;
  font-size: 18px;
}

.upload-btn.circle:hover {
  background-color: #66b1ff;
}

.upload-icon {
  margin-left: 5px;
}

.upload-dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.upload-dialog {
  background: white;
  border-radius: 8px;
  width: 500px;
  max-width: 90vw;
  max-height: 90vh;
  overflow: hidden;
}

.upload-dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.upload-dialog-header h3 {
  margin: 0;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
}

.close-btn:hover {
  color: #333;
}

.upload-content {
  width: 100%;
  height: 300px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.drag-content {
  border: 2px dashed #dcdfe6;
  border-radius: 8px;
  width: 80%;
  height: 250px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  cursor: pointer;
}

.drag-content:hover {
  border-color: #409eff;
  background-color: rgba(64, 158, 255, 0.05);
}

.drag-content.dragover {
  border-color: #409eff;
  background-color: rgba(64, 158, 255, 0.1);
  transform: scale(1.02);
}

.drag-icon-content {
  margin-bottom: 20px;
}

.upload-icon-large {
  font-size: 64px;
  color: #dcdfe6;
}

.drag-text-content {
  text-align: center;
}

.drag-text {
  color: #909399;
  margin-right: 5px;
}

.click-upload {
  color: #409eff;
  cursor: pointer;
}

.click-upload:hover {
  text-decoration: underline;
}
</style>
