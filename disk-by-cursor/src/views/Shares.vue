<template>
  <div class="shares-container">
    <!-- 页面标题和工具栏 -->
    <div class="page-header">
      <h2>我的分享</h2>
      <div class="header-actions">
      <el-button type="primary" @click="showCreateShareDialog = true">
        <el-icon><Share /></el-icon>
        创建分享
      </el-button>
      </div>
    </div>
    
    <!-- 分享表格 -->
    <el-card>
      <el-table 
        :data="shareList" 
        style="width: 100%" 
        v-loading="loading"
      >
        <el-table-column label="分享名称" min-width="200">
          <template #default="{ row }">
            <div class="share-row-container" 
                 @mouseenter="showOperation(row, $event)" 
                 @mouseleave="hiddenOperation(row, $event)">
              <div class="share-name-content">
                <el-icon class="share-icon" color="#409eff">
                  <Share />
                </el-icon>
                <span class="share-name">{{ row.shareName }}</span>
              </div>
              <div class="share-operation-content">
                <div class="pan-share-operations">
                  <el-tooltip class="item" effect="light" content="查看详情" placement="top">
                    <el-button icon="View" type="info" size="small" circle @click="viewShareDetail(row)"></el-button>
                  </el-tooltip>
                  <el-tooltip class="item" effect="light" content="续期" placement="top">
                    <el-button icon="Refresh" type="warning" size="small" circle @click="extendShare(row)"></el-button>
                  </el-tooltip>
                  <el-tooltip class="item" effect="light" content="取消分享" placement="top">
                    <el-button icon="Delete" type="danger" size="small" circle @click="cancelShare(row)"></el-button>
                  </el-tooltip>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="shareUrl" label="分享链接" min-width="300" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="share-url-cell">
              <div class="share-url-content">
                <el-icon class="link-icon"><Link /></el-icon>
                <span class="share-url-text">{{ row.shareUrl }}</span>
              </div>
              <el-button 
                @click="copyShareUrl(row)" 
                size="small"
                type="primary"
                class="copy-btn"
                :icon="CopyDocument"
              >
                复制
              </el-button>
            </div>
                </template>
        </el-table-column>
        
        <el-table-column prop="shareCode" label="分享码" width="120" align="center">
          <template #default="{ row }">
            <div class="share-code-display" @click="copyShareCode(row)" title="点击复制分享码">
              <el-icon class="code-icon"><Key /></el-icon>
              <span class="share-code-text">{{ row.shareCode }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="shareEndTime" label="过期时间" width="150" align="center">
          <template #default="{ row }">
            <span class="expire-time">{{ formatExpireTime(row.shareEndTime) }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="shareStatus" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.shareStatus)" size="small">
              {{ getStatusText(row.shareStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        

      </el-table>
    </el-card>
    
    <!-- 空状态 -->
    <el-empty v-if="shareList.length === 0 && !loading" description="暂无分享" />
    
    <!-- 创建分享对话框 -->
    <el-dialog 
      v-model="showCreateShareDialog" 
      title="创建分享" 
      width="600px"
      :close-on-click-modal="false"
      class="pan-dialog"
    >
      <el-form :model="shareForm" :rules="shareRules" ref="shareFormRef" label-width="100px">
        <el-form-item label="分享名称" prop="shareName">
          <el-input v-model="shareForm.shareName" placeholder="请输入分享名称" />
        </el-form-item>
        
        <el-form-item label="分享文件" prop="shareFileIds">
          <el-transfer
            v-model="shareForm.shareFileIds"
            :data="fileList"
            :titles="['可选文件', '已选文件']"
            :props="{
              key: 'fileId',
              label: 'filename'
            }"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="分享类型" prop="shareType">
          <el-radio-group v-model="shareForm.shareType">
            <el-radio :label="0">公开分享</el-radio>
            <el-radio :label="1">私密分享</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="过期时间" prop="shareDayType">
          <el-select v-model="shareForm.shareDayType" placeholder="请选择过期时间">
            <el-option label="1天" :value="1" />
            <el-option label="7天" :value="7" />
            <el-option label="30天" :value="30" />
            <el-option label="永久" :value="-1" />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
        <el-button @click="showCreateShareDialog = false">取消</el-button>
        <el-button type="primary" @click="createShare">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 分享详情对话框 -->
    <el-dialog 
      v-model="showDetailDialog" 
      title="分享详情" 
      width="800px"
      class="pan-dialog"
    >
      <div v-if="currentShare" class="share-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="分享名称">{{ currentShare.shareName }}</el-descriptions-item>
          <el-descriptions-item label="分享码">{{ currentShare.shareCode }}</el-descriptions-item>
          <el-descriptions-item label="分享链接">{{ currentShare.shareUrl }}</el-descriptions-item>
          <el-descriptions-item label="过期时间">{{ formatExpireTime(currentShare.shareEndTime) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentShare.shareStatus)">
              {{ getStatusText(currentShare.shareStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDate(currentShare.createTime) }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Link, Key, CopyDocument, Share } from '@element-plus/icons-vue'
import { getShareList, createShare as createShareApi, cancelShare as cancelShareApi, getShareDetail } from '@/api/share'

// 响应式数据
const shareList = ref([])
const loading = ref(false)
const showCreateShareDialog = ref(false)
const showDetailDialog = ref(false)
const currentShare = ref(null)
const tableHeight = ref(400)

// 表单数据
const shareForm = ref({
  shareName: '',
  shareFileIds: [],
  shareType: 0,
  shareDayType: 7
})

const shareRules = {
  shareName: [
    { required: true, message: '请输入分享名称', trigger: 'blur' }
  ],
  shareFileIds: [
    { required: true, message: '请选择要分享的文件', trigger: 'change' }
  ]
}

const fileList = ref([])

// 计算表格高度
const calculateTableHeight = () => {
  const windowHeight = window.innerHeight
  const offset = 200 // 其他元素的高度
  tableHeight.value = windowHeight - offset
}

// 显示操作按钮
const showOperation = (row, event) => {
  const container = event.currentTarget
  const operationContent = container.querySelector('.share-operation-content')
  if (operationContent) {
    operationContent.classList.add('show')
  }
}

// 隐藏操作按钮
const hiddenOperation = (row, event) => {
  const container = event.currentTarget
  const operationContent = container.querySelector('.share-operation-content')
  if (operationContent) {
    operationContent.classList.remove('show')
  }
}

// 格式化过期时间
const formatExpireTime = (time) => {
  if (!time) return '永久有效'
  const date = new Date(time)
  return date.toLocaleDateString('zh-CN')
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

// 方法
const loadShareList = async () => {
  loading.value = true
  try {
    const response = await getShareList()
    if (response.code === 'SUCCESS') {
      shareList.value = response.data || []
    }
  } catch (error) {
    console.error('加载分享列表失败:', error)
    ElMessage.error('加载分享列表失败')
  } finally {
    loading.value = false
  }
}

const copyShareUrl = (share) => {
  navigator.clipboard.writeText(share.shareUrl).then(() => {
    ElMessage.success('分享链接已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

const copyShareCode = (share) => {
  navigator.clipboard.writeText(share.shareCode).then(() => {
    ElMessage.success('分享码已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

const getStatusType = (status) => {
  const typeMap = {
    0: 'success',
    1: 'warning',
    2: 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    0: '正常',
    1: '即将过期',
    2: '已过期'
  }
  return textMap[status] || '未知'
}

const viewShareDetail = async (share) => {
  try {
    const response = await getShareDetail(share.id)
    if (response.code === 'SUCCESS') {
      currentShare.value = response.data
      showDetailDialog.value = true
    }
  } catch (error) {
    ElMessage.error('获取分享详情失败')
  }
}

const extendShare = (share) => {
  ElMessage.info('续期功能开发中...')
}

const cancelShare = async (share) => {
  try {
    await ElMessageBox.confirm('确定要取消这个分享吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await cancelShareApi({ shareId: share.id.toString() })
    ElMessage.success('取消分享成功')
    loadShareList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消分享失败:', error)
      ElMessage.error('取消分享失败')
    }
  }
}

const createShare = async () => {
  try {
    const data = {
      shareName: shareForm.value.shareName,
      shareFileIds: shareForm.value.shareFileIds.join(','),
      shareType: shareForm.value.shareType,
      shareDayType: shareForm.value.shareDayType
    }
    
    const response = await createShareApi(data)
    if (response.code === 'SUCCESS') {
    ElMessage.success('创建分享成功')
    showCreateShareDialog.value = false
    
    // 重置表单
      shareForm.value.shareName = ''
      shareForm.value.shareFileIds = []
      shareForm.value.shareType = 0
      shareForm.value.shareDayType = 7
    
    loadShareList()
    } else {
      ElMessage.error(response.message || '创建分享失败')
    }
  } catch (error) {
    console.error('创建分享失败:', error)
    ElMessage.error('创建分享失败')
  }
}

// 生命周期
onMounted(() => {
  loadShareList()
  calculateTableHeight()
  window.addEventListener('resize', calculateTableHeight)
})

onUnmounted(() => {
  window.removeEventListener('resize', calculateTableHeight)
})
</script>

<style scoped>
.shares-container {
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

.share-item {
  display: flex;
  align-items: center;
}

.share-icon {
  margin-right: 8px;
  font-size: 18px;
}

.share-name {
  color: #333;
}

.shares-table {
  width: 100% !important;
  table-layout: fixed;
}

:deep(.el-table) {
  border-radius: var(--border-radius);
  width: 100% !important;
  table-layout: fixed;
}

:deep(.el-table__body-wrapper) {
  overflow-x: auto;
}

:deep(.el-table__header-wrapper) {
  overflow: hidden;
}

:deep(.el-table th) {
  background: var(--bg-light);
  color: var(--text-primary);
  font-weight: 600;
}

:deep(.el-table td) {
  padding: 12px 0;
}

/* 分享URL单元格样式 */
.share-url-cell {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  padding: 8px 12px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 8px;
  border: 1px solid #e9ecef;
  transition: all 0.3s ease;
}

.share-url-cell:hover {
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
  border-color: #2196f3;
  box-shadow: 0 2px 8px rgba(33, 150, 243, 0.15);
}

.share-url-content {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-grow: 1;
  min-width: 0;
}

.share-url-text {
  font-weight: 500;
  color: #1976d2;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 13px;
}

.link-icon {
  color: #2196f3;
  font-size: 16px;
  flex-shrink: 0;
}

.copy-btn {
  flex-shrink: 0;
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.copy-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(33, 150, 243, 0.3);
}

/* 分享码显示样式 */
.share-code-display {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 8px 12px;
  background: linear-gradient(135deg, #fff3e0 0%, #ffe0b2 100%);
  border-radius: 8px;
  border: 1px solid #ffcc80;
  transition: all 0.3s ease;
  cursor: pointer;
}

.share-code-display:hover {
  background: linear-gradient(135deg, #ffe0b2 0%, #ffcc80 100%);
  border-color: #ff9800;
  box-shadow: 0 2px 8px rgba(255, 152, 0, 0.15);
  transform: translateY(-1px);
}

.share-code-text {
  font-weight: 600;
  color: #f57c00;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 14px;
  letter-spacing: 1px;
  user-select: all;
}

.code-icon {
  color: #ff9800;
  font-size: 16px;
  flex-shrink: 0;
}

/* 分享行容器样式 */
.share-row-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.share-name-content {
  flex: 1;
  display: flex;
  align-items: center;
}

.share-operation-content {
  position: relative;
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
}

.share-operation-content.show {
  opacity: 1;
  pointer-events: auto;
}

.pan-share-operations {
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

.pan-share-operations .el-button {
  margin: 0;
  flex-shrink: 0;
  transition: all 0.2s ease;
}

.pan-share-operations .el-button:hover {
  transform: scale(1.1);
}

.share-detail {
  padding: 20px;
}

.share-detail .el-descriptions {
  margin-bottom: 20px;
}

.expire-time {
  color: var(--text-secondary);
  font-size: 0.9em;
}

/* 卡片样式 */
.el-card {
  border: none;
  box-shadow: var(--shadow-light);
  transition: all 0.3s ease;
}

.el-card:hover {
  box-shadow: var(--shadow-medium);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .pan-toolbar {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
  
  .toolbar-right .pan-btn-primary {
    width: 100%;
    margin-left: 0;
  }

  .shares-table-container {
    overflow-x: visible;
  }
  
  :deep(.el-table) {
    font-size: 13px;
  }
  
  :deep(.el-table th),
  :deep(.el-table td) {
    padding: 10px 6px;
  }
}

@media (max-width: 992px) {
  .pan-toolbar {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
  
  .toolbar-right .pan-btn-primary {
    width: 100%;
    margin-left: 0;
  }

  .shares-table-container {
    overflow-x: visible;
  }
  
  :deep(.el-table) {
    font-size: 12px;
  }
  
  :deep(.el-table th),
  :deep(.el-table td) {
    padding: 8px 4px;
  }
}

@media (max-width: 768px) {
  .pan-main-content {
    padding: 10px;
  }
  
  .pan-toolbar {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
  
  .toolbar-right .pan-btn-primary {
    width: 100%;
    margin-left: 0;
  }

  .shares-table-container {
    overflow-x: visible;
  }

  :deep(.el-table) {
    font-size: 12px;
  }
  
  :deep(.el-table th),
  :deep(.el-table td) {
    padding: 8px 4px;
  }
  
  .share-row-container {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .share-operation-content {
    align-self: flex-end;
  }
  
  /* 移动端分享链接和分享码优化 */
  .share-url-cell {
    flex-direction: column;
    align-items: stretch;
    gap: 8px;
    padding: 10px;
  }
  
  .share-url-content {
    justify-content: center;
  }
  
  .copy-btn {
    align-self: center;
  }
  
  .share-url-text {
    font-size: 12px;
  }
  
  .share-code-display {
    padding: 8px;
  }
  
  .share-code-text {
    font-size: 12px;
  }
}
</style> 