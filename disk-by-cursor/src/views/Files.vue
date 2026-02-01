<template>
  <div class="pan-main-content">
        <!-- 面包屑导航 -->
    <div class="pan-breadcrumb">
      <el-breadcrumb separator="/">    
        <!-- 动态面包屑 -->
        <el-breadcrumb-item 
          v-for="item in fileStore.breadcrumbs" 
          :key="item.id"
          @click="navigateToFolder(item.id)"
          class="breadcrumb-item"
        >
          <div class="breadcrumb-content">
            <el-icon class="breadcrumb-icon"><Folder /></el-icon>
            <span class="breadcrumb-text">{{ item.name }}</span>
          </div>
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
        
        <!-- 工具栏 -->
    <div class="pan-toolbar">
          <div class="toolbar-left">
        <UploadButton />
        <el-button @click="createFolder" class="create-folder-btn">
              <el-icon><FolderAdd /></el-icon>
              新建文件夹
            </el-button>
          </div>
          
          <div class="toolbar-right">
        <el-button 
          v-if="selectedFiles.length > 0" 
          type="danger" 
          @click="batchDelete"
          class="batch-delete-btn"
        >
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
        <el-button 
          v-if="selectedFiles.length > 0" 
          type="success" 
          @click="batchDownload"
          class="batch-download-btn"
        >
          <el-icon><Download /></el-icon>
          批量下载
        </el-button>
          </div>
        </div>
        
    <!-- 文件表格 -->
    <div class="file-table-container">
          <el-table
        v-loading="loading"
        ref="fileTableRef"
        :data="fileStore.files"
        :height="tableHeight"
        tooltip-effect="dark"
            style="width: 100%"
            @selection-change="handleSelectionChange"
        class="file-table pan-table"
      >
        <el-table-column
          type="selection"
          width="55">
        </el-table-column>
        
        <el-table-column
          label="文件名"
          prop="filename"
          sortable
          show-overflow-tooltip
          min-width="200">
          <template #default="scope">
            <div class="file-row-container" 
                 @mouseenter="showOperation(scope.row, $event)" 
                 @mouseleave="hiddenOperation(scope.row, $event)">
              <div @click="clickFilename(scope.row)" class="file-name-content">
                <i :class="getFileFontElement(scope.row.fileType)"
                   style="margin-right: 15px; font-size: 20px; cursor: pointer;"/>
                <span style="cursor:pointer;">{{ scope.row.filename }}</span>
              </div>
              <div class="file-operation-content">
                <div class="pan-file-operations">
                  <!-- 只有文件类型才显示下载按钮 -->
                  <el-tooltip v-if="!scope.row.folderFlag" class="item" effect="light" content="下载" placement="top">
                    <el-button icon="Download" type="info" size="small" circle @click="downloadFile(scope.row)"></el-button>
                  </el-tooltip>
                  <el-tooltip class="item" effect="light" content="重命名" placement="top">
                    <el-button icon="Edit" type="warning" size="small" circle @click="renameFile(scope.row)"></el-button>
                  </el-tooltip>
                  <el-tooltip class="item" effect="light" content="删除" placement="top">
                    <el-button icon="Delete" type="danger" size="small" circle @click="deleteFile(scope.row)"></el-button>
                  </el-tooltip>
                  <el-tooltip class="item" effect="light" content="分享" placement="top">
                    <el-button icon="Share" type="success" size="small" circle @click="shareFile(scope.row)"></el-button>
                  </el-tooltip>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
            
        <el-table-column
          prop="fileSizeDesc"
          sortable
          label="大小"
          width="100"
          align="center">
          <template #default="scope">
            {{scope.row.fileSizeDesc}}
          </template>
        </el-table-column>
        
        <el-table-column
          prop="updateTime"
          sortable
          align="center"
          label="修改日期"
          width="150">
          <template #default="scope">
            {{ formatDate(scope.row.updateTime) }}
              </template>
            </el-table-column>
          </el-table>
        </div>
    


    <!-- 分享对话框 -->
    <el-dialog
      v-model="showShareDialog"
      title="分享文件"
      width="500px"
      :close-on-click-modal="false"
      class="share-dialog pan-dialog"
    >
      <el-form
        ref="shareFormRef"
        :model="shareForm"
        :rules="shareRules"
        label-width="100px"
      >
        <el-form-item label="分享名称" prop="shareName">
          <el-input v-model="shareForm.shareName" placeholder="请输入分享名称" />
        </el-form-item>
        
        <el-form-item label="分享类型" prop="shareType" required>
          <el-radio-group v-model="shareForm.shareType">
            <el-radio label="withCode">需要提取码</el-radio>
            <el-radio label="withoutCode">无需提取码</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="有效期" prop="validity" required>
          <el-select v-model="shareForm.validity" placeholder="请选择有效期">
            <el-option label="永久有效" value="permanent" />
            <el-option label="1天" value="1day" />
            <el-option label="7天" value="7days" />
            <el-option label="30天" value="30days" />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showShareDialog = false">取消</el-button>
          <el-button type="primary" @click="confirmShare">确认分享</el-button>
        </span>
      </template>
    </el-dialog>




  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Folder, 
  Upload, 
  FolderAdd, 
  Delete, 
  Download, 
  Edit, 
  Share,
  House
} from '@element-plus/icons-vue'
import { useFileStore } from '@/stores/file'
import { useUserStore } from '@/stores/user'
import { renameFile as renameFileAPI, deleteFiles as deleteFilesAPI, createFolder as createFolderAPI } from '@/api/file'
import { createShare } from '@/api/share'

import UploadButton from '@/components/UploadButton.vue'

const fileStore = useFileStore()
const userStore = useUserStore()
const route = useRoute()
const router = useRouter()

const loading = ref(false)
const showShareDialog = ref(false)
const currentShareFile = ref(null)
const selectedFiles = ref([])
const tableHeight = ref(window.innerHeight - 300)

// 分享表单数据
const shareForm = ref({
  shareName: '',
  shareType: '',
  validity: ''
})

// 分享表单验证规则
const shareRules = {
  shareName: [
    { required: true, message: '请输入分享名称', trigger: 'blur' },
    { min: 1, max: 50, message: '分享名称长度在 1 到 50 个字符', trigger: 'blur' }
  ],
  shareType: [
    { required: true, message: '请选择分享类型', trigger: 'change' }
  ],
  validity: [
    { required: true, message: '请选择有效期', trigger: 'change' }
  ]
}

const shareFormRef = ref(null)

  const typeMap = {
    all: '-1',
    image: '7',
    document: '3,4,5,6,10,11,12',
    video: '9',
    music: '8',
    other: '1,2'
  }

// 监听路由参数变化，处理侧边栏点击
watch(() => route.query.type, (newType) => {
  const fileTypes = newType ? typeMap[newType] || '-1' : typeMap.all
  loading.value = true
  fileStore.loadFiles(userStore.rootFileId, fileTypes).finally(() => loading.value = false)
}, { immediate: true })

// 获取行类名，用于鼠标悬停效果
function getRowClassName({ row, rowIndex }) {
  return 'file-table-row'
}

// 打开文件夹
function openFolder(folder) {
  if (!folder.id) {
    ElMessage.error('文件夹ID无效')
    return
  }
  
  loading.value = true
  fileStore.loadFiles(folder.id, fileStore.fileTypes)
    .catch((error) => {
      console.error('[Files.vue] 打开文件夹失败:', error)
      ElMessage.error('打开文件夹失败')
    })
    .finally(() => {
      loading.value = false
    })
}

// 面包屑导航
function navigateToFolder(folderId) {
  loading.value = true
  // 如果是根目录（空字符串），使用用户根目录ID
  const targetFolderId = folderId === '' ? userStore.rootFileId : folderId
  fileStore.loadFiles(targetFolderId, fileStore.fileTypes).finally(() => loading.value = false)
}

// 文件图标
function getFileFontElement(fileType) {
  const typeMap = {
    0: 'fa fa-folder-o',
    2: 'fa fa-file-archive-o',
    3: 'fa fa-file-excel-o',
    4: 'fa fa-file-word-o',
    5: 'fa fa-file-pdf-o',
    6: 'fa fa-file-text-o',
    7: 'fa fa-file-image-o',
    8: 'fa fa-file-audio-o',
    9: 'fa fa-file-video-o',
    10: 'fa fa-file-powerpoint-o',
    11: 'fa fa-file-code-o',
    12: 'fa fa-file-code-o'
  }
  return typeMap[fileType] || 'fa fa-file'
}

// 选择变化
function handleSelectionChange(selection) {
  selectedFiles.value = selection
}

// 显示操作按钮
function showOperation(row, event) {
  const container = event.currentTarget
  const operationContent = container.querySelector('.file-operation-content')
  if (operationContent) {
    operationContent.classList.add('show')
    console.log('显示操作按钮:', row.filename, '是否为文件夹:', row.folderFlag)
  }
}

// 隐藏操作按钮
function hiddenOperation(row, event) {
  const container = event.currentTarget
  const operationContent = container.querySelector('.file-operation-content')
  if (operationContent) {
    operationContent.classList.remove('show')
  }
}

// 点击文件名
function clickFilename(file) {
  if (file.folderFlag) {
    openFolder(file)
  } else {
    // 根据文件类型进行预览或下载
    downloadFile(file)
  }
}

// 下载文件
function downloadFile(file) {
  console.log('[Files.vue] 下载文件:', file)
  // TODO: 实现下载逻辑
  ElMessage.info('下载功能开发中...')
}

// 重命名文件
async function renameFile(file) {
  console.log('[Files.vue] 重命名文件:', file)
  try {
    const { value: newName } = await ElMessageBox.prompt('请输入新的文件名', '重命名', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPlaceholder: '请输入文件名',
      inputValue: file.filename,
      inputValidator: (value) => {
        if (!value) {
          return '文件名不能为空'
        }
        if (value.length > 255) {
          return '文件名不能超过255个字符'
        }
        return true
      }
    })

    if (newName) {
      await renameFileAPI({
        fileId: file.id.toString(),
        newFilename: newName
      })
      ElMessage.success('文件重命名成功')
      loading.value = true
      fileStore.loadFiles(fileStore.parentId, fileStore.fileTypes).finally(() => loading.value = false)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('重命名失败:', error)
      ElMessage.error('文件重命名失败')
    }
  }
}

// 删除文件
async function deleteFile(file) {
  try {
    await ElMessageBox.confirm(`确定要删除文件 "${file.filename}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    
    await deleteFilesAPI({
      fileIds: [file.id.toString()]
    })
    ElMessage.success('文件删除成功')
    loading.value = true
    fileStore.loadFiles(fileStore.parentId, fileStore.fileTypes).finally(() => loading.value = false)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 创建文件夹
async function createFolder() {
  try {
    const { value: folderName } = await ElMessageBox.prompt('请输入文件夹名称', '新建文件夹', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPlaceholder: '请输入文件夹名称',
      inputValidator: (value) => {
        if (!value) {
          return '文件夹名称不能为空'
        }
        if (value.length > 255) {
          return '文件夹名称不能超过255个字符'
        }
        const invalidChars = /[<>:"/\\|?*]/
        if (invalidChars.test(value)) {
          return '文件夹名称不能包含以下字符: < > : " / \\ | ? *'
        }
        return true
      }
    })

    if (folderName) {
      await createFolderAPI({
        parentId: fileStore.parentId,
        folderName: folderName
      })
      ElMessage.success('文件夹创建成功')
      loading.value = true
      fileStore.loadFiles(fileStore.parentId, fileStore.fileTypes).finally(() => loading.value = false)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('创建文件夹失败:', error)
      ElMessage.error('文件夹创建失败')
    }
  }
}

// 分享文件
function shareFile(file) {
  if (!file.id && !file.fileId && !file.file_id) {
    ElMessage.error('文件ID无效')
    return
  }
  
  currentShareFile.value = file
  shareForm.value = {
    shareName: '',
    shareType: '',
    validity: ''
  }
  showShareDialog.value = true
}

// 确认分享
async function confirmShare() {
  if (!shareFormRef.value) return
  
  try {
    await shareFormRef.value.validate()
    
    const fileId = currentShareFile.value.id || currentShareFile.value.fileId || currentShareFile.value.file_id
    
    // 转换分享类型：withCode -> 1, withoutCode -> 0
    const shareType = shareForm.value.shareType === 'withCode' ? 1 : 0
    
    // 转换有效期：permanent -> '永久有效', 1day -> '1天', 7days -> '7天', 30days -> '30天'
    const validityMap = {
      'permanent': '永久有效',
      '1day': '1天',
      '7days': '7天',
      '30days': '30天'
    }
    const shareDayType = validityMap[shareForm.value.validity]
    
    console.log('[Files.vue] 确认分享:', {
      fileId,
      file: currentShareFile.value,
      form: shareForm.value,
      shareType,
      shareDayType
    })
    
    const shareData = {
      shareName: shareForm.value.shareName,
      shareType: shareType,
      shareDayType: shareDayType,
      shareFileIds: [fileId.toString()]
    }
    
    const response = await createShare(shareData)
    console.log('[Files.vue] 分享API响应:', response)
    
    // 检查API响应格式：{ code: "SUCCESS", success: true, message: null, data: null }
    if (response && (response.code === 'SUCCESS' || response.success === true)) {
      showShareDialog.value = false
      ElMessage.success('分享创建成功！请前往"我的分享"查看分享详情')
      
      // 可选：自动跳转到分享页面
      setTimeout(() => {
        router.push('/shares')
      }, 1500)
    } else {
      const errorMessage = response?.message || '分享创建失败'
      ElMessage.error(errorMessage)
    }
  } catch (error) {
    console.error('[Files.vue] 分享失败:', error)
    if (error.response && error.response.data && error.response.data.message) {
      ElMessage.error(`分享失败: ${error.response.data.message}`)
    } else {
      ElMessage.error('分享失败，请重试')
    }
  }
}

// 批量下载
function batchDownload() {
  if (selectedFiles.value.length === 0) {
    ElMessage.warning('请选择要下载的文件')
    return
  }
  ElMessage.info('批量下载功能开发中...')
}

// 批量删除
async function batchDelete() {
  if (selectedFiles.value.length === 0) {
    ElMessage.warning('请选择要删除的文件')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedFiles.value.length} 个文件吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    const fileIds = selectedFiles.value.map(file => file.id.toString())
    await deleteFilesAPI({
      fileIds: fileIds
    })
    ElMessage.success('文件批量删除成功')
    loading.value = true
    fileStore.loadFiles(fileStore.parentId, fileStore.fileTypes).finally(() => loading.value = false)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('文件批量删除失败')
    }
  }
}







// 格式化日期
function formatDate(dateString) {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  console.log('[Files.vue] 组件挂载')
  loading.value = true
  fileStore.initRoot().finally(() => loading.value = false)
})
</script>

<style scoped>
.pan-main-content {
  width: 100%;
  height: 100%;
  padding: 0;
}



.breadcrumb-item {
  cursor: pointer !important;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
}

.breadcrumb-item:hover {
  color: #667eea;
}

.breadcrumb-content {
  display: flex;
  align-items: center;
  height: 100%;
  cursor: pointer !important;
}

.breadcrumb-content:hover {
  color: #667eea;
}

.breadcrumb-icon {
  margin-right: 8px;
  color: #667eea;
  display: flex;
  align-items: center;
  font-size: 16px;
  cursor: pointer;
}

.breadcrumb-text {
  font-weight: 500;
  display: flex;
  align-items: center;
  line-height: 1;
  cursor: pointer;
}



.toolbar-left {
  display: flex;
  gap: 12px;
}

.toolbar-right {
  display: flex;
  gap: 12px;
}



.create-folder-btn {
  background: #67c23a;
  border-color: #67c23a;
  color: #fff;
  transition: all 0.3s ease;
}

.create-folder-btn:hover {
  background: #85ce61;
  border-color: #85ce61;
  transform: translateY(-2px);
}

.batch-delete-btn {
  background: #f56c6c;
  border-color: #f56c6c;
  transition: all 0.3s ease;
}

.batch-delete-btn:hover {
  background: #f78989;
  border-color: #f78989;
  transform: translateY(-2px);
}

.batch-download-btn {
  background: #409eff;
  border-color: #409eff;
  transition: all 0.3s ease;
}

.batch-download-btn:hover {
  background: #66b1ff;
  border-color: #66b1ff;
  transform: translateY(-2px);
}



.file-name-content {
  display: flex;
  align-items: center;
  padding: 8px 0;
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

.task-progress {
  margin-bottom: 8px;
}

.task-status {
  display: flex;
  justify-content: space-between;
  align-items: center;
}



.task-actions {
  display: flex;
  gap: 8px;
}



.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}



/* 表格容器优化 */
.file-table-container {
  width: 100%;
  overflow: hidden;
}

/* 表格布局优化 */
:deep(.el-table) {
  border-radius: var(--border-radius);
  width: 100% !important;
  table-layout: fixed;
}

:deep(.el-table__body-wrapper) {
  overflow: hidden;
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

/* 文件操作按钮优化 */
.file-row-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
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

/* 响应式设计 */
@media (max-width: 1200px) {
  .pan-file-operations {
    flex-direction: column;
    gap: 2px;
  }
  
  .pan-file-operations .el-button {
    width: 100%;
  }
}

@media (max-width: 992px) {
  .pan-main-content {
    padding: 0;
  }
  
  .pan-toolbar {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .toolbar-left,
  .toolbar-right {
    justify-content: center;
  }
  
  :deep(.el-table) {
    font-size: 13px;
  }
  
  :deep(.el-table th),
  :deep(.el-table td) {
    padding: 10px 6px;
  }
}

@media (max-width: 768px) {
  .pan-main-content {
    padding: 0;
  }
  
  .pan-toolbar {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .toolbar-left,
  .toolbar-right {
    justify-content: center;
  }
  
  .pan-task-list {
    width: calc(100vw - 40px);
    right: 20px;
    left: 20px;
  }
  
  .pan-file-operations {
    right: 20px;
    top: 8px;
  }
  
  :deep(.el-table) {
    font-size: 12px;
  }
  
  :deep(.el-table th),
  :deep(.el-table td) {
    padding: 8px 4px;
  }
}
</style> 