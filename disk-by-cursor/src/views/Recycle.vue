<template>
  <div class="recycle-container">
    <div class="page-header">
      <h2>回收站</h2>
      <div class="header-actions">
        <el-button @click="batchRestore" :disabled="selectedFiles.length === 0">
          <el-icon><RefreshLeft /></el-icon>
          批量还原
        </el-button>
        <el-button type="danger" @click="batchDelete" :disabled="selectedFiles.length === 0">
          <el-icon><Delete /></el-icon>
          彻底删除
        </el-button>
      </div>
    </div>
    
    <el-card>
      <el-table 
        :data="recycleList" 
        style="width: 100%" 
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        
        <el-table-column label="文件名" min-width="300">
          <template #default="{ row }">
            <div class="file-row-container" 
                 @mouseenter="showOperation(row, $event)" 
                 @mouseleave="hiddenOperation(row, $event)">
              <div class="file-name-content">
                <el-icon class="file-icon" :color="getFileIconColor(row)">
                  <component :is="getFileIcon(row)" />
                </el-icon>
                <span class="file-name">{{ row.filename }}</span>
              </div>
              <div class="file-operation-content">
                <div class="pan-file-operations">
                  <el-tooltip class="item" effect="light" content="还原" placement="top">
                    <el-button icon="RefreshLeft" type="success" size="small" circle @click="restoreFile(row)"></el-button>
                  </el-tooltip>
                  <el-tooltip class="item" effect="light" content="彻底删除" placement="top">
                    <el-button icon="Delete" type="danger" size="small" circle @click="deleteFile(row)"></el-button>
                  </el-tooltip>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="fileSizeDesc" label="大小" width="100" />
        <el-table-column prop="updateTime" label="删除时间" width="180" />
        

      </el-table>
    </el-card>
    
    <!-- 空状态 -->
    <el-empty v-if="recycleList.length === 0 && !loading" description="回收站为空" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRecycleList, restoreFiles, deleteRecycleFiles } from '@/api/recycle'

// 响应式数据
const loading = ref(false)
const recycleList = ref([])
const selectedFiles = ref([])

// 方法
const loadRecycleList = async () => {
  loading.value = true
  try {
    const response = await getRecycleList()
    if (response.code === 200) {
      recycleList.value = response.data || []
    }
  } catch (error) {
    console.error('加载回收站列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSelectionChange = (selection) => {
  selectedFiles.value = selection
}

// 显示操作按钮
const showOperation = (row, event) => {
  const container = event.currentTarget
  const operationContent = container.querySelector('.file-operation-content')
  if (operationContent) {
    operationContent.classList.add('show')
  }
}

// 隐藏操作按钮
const hiddenOperation = (row, event) => {
  const container = event.currentTarget
  const operationContent = container.querySelector('.file-operation-content')
  if (operationContent) {
    operationContent.classList.remove('show')
  }
}

const getFileIcon = (file) => {
  if (file.folderFlag) return 'Folder'
  
  const typeMap = {
    7: 'Picture',
    9: 'VideoPlay',
    3: 'Document',
    4: 'Document',
    5: 'Document',
    6: 'Document',
    10: 'Document',
    11: 'Document',
    12: 'Document'
  }
  
  return typeMap[file.fileType] || 'Document'
}

const getFileIconColor = (file) => {
  if (file.folderFlag) return '#409eff'
  
  const colorMap = {
    7: '#67c23a',
    9: '#e6a23c',
    3: '#f56c6c',
    4: '#f56c6c',
    5: '#f56c6c',
    6: '#f56c6c',
    10: '#f56c6c',
    11: '#f56c6c',
    12: '#f56c6c'
  }
  
  return colorMap[file.fileType] || '#909399'
}

const restoreFile = async (file) => {
  try {
    await ElMessageBox.confirm('确定要还原这个文件吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await restoreFiles({ fileIds: file.fileId })
    ElMessage.success('还原成功')
    loadRecycleList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('还原失败')
    }
  }
}

const deleteFile = async (file) => {
  try {
    await ElMessageBox.confirm('确定要彻底删除这个文件吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteRecycleFiles({ fileIds: file.fileId })
    ElMessage.success('删除成功')
    loadRecycleList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const batchRestore = async () => {
  if (selectedFiles.value.length === 0) {
    ElMessage.warning('请选择要还原的文件')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定要还原选中的 ${selectedFiles.value.length} 个文件吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const fileIds = selectedFiles.value.map(f => f.fileId).join(',')
    await restoreFiles({ fileIds })
    ElMessage.success('批量还原成功')
    loadRecycleList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量还原失败')
    }
  }
}

const batchDelete = async () => {
  if (selectedFiles.value.length === 0) {
    ElMessage.warning('请选择要删除的文件')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定要彻底删除选中的 ${selectedFiles.value.length} 个文件吗？此操作不可恢复！`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const fileIds = selectedFiles.value.map(f => f.fileId).join(',')
    await deleteRecycleFiles({ fileIds })
    ElMessage.success('批量删除成功')
    loadRecycleList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

// 生命周期
onMounted(() => {
  loadRecycleList()
})
</script>

<style scoped>
.recycle-container {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #333;
}

.header-actions {
  display: flex;
  gap: 10px;
}

/* 文件行容器样式 */
.file-row-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.file-name-content {
  flex: 1;
  display: flex;
  align-items: center;
}

.file-operation-content {
  position: relative;
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
}

.file-operation-content.show {
  opacity: 1;
  pointer-events: auto;
}

.pan-file-operations {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
  background: rgba(255, 255, 255, 0.95);
  padding: 4px 8px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  z-index: 10;
  position: static !important;
  right: auto !important;
  top: auto !important;
}

.pan-file-operations .el-button {
  margin: 0;
  flex-shrink: 0;
  transition: all 0.2s ease;
}

.pan-file-operations .el-button:hover {
  transform: scale(1.1);
}

.file-icon {
  margin-right: 8px;
  font-size: 18px;
}

.file-name {
  color: #333;
}
</style> 