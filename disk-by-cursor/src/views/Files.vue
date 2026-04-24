<template>
  <div class="pan-main-content files-page">
    <section class="page-top">
      <div class="pan-breadcrumb">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item class="breadcrumb-item" @click="navigateToFolder('')">
            <div class="breadcrumb-content">
              <el-icon class="breadcrumb-icon"><Folder /></el-icon>
              <span class="breadcrumb-text">根目录</span>
            </div>
          </el-breadcrumb-item>
          <el-breadcrumb-item
            v-for="item in fileStore.breadcrumbs"
            :key="item.id"
            class="breadcrumb-item"
            @click="navigateToFolder(item.id)"
          >
            <div class="breadcrumb-content">
              <el-icon class="breadcrumb-icon"><Folder /></el-icon>
              <span class="breadcrumb-text">{{ item.name }}</span>
            </div>
          </el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      <div class="toolbar-shell">
      <div class="toolbar-left">
        <UploadButton />
        <el-button class="create-folder-btn" @click="createFolder">
          <el-icon><FolderAdd /></el-icon>
          新建文件夹
        </el-button>
      </div>

      <div class="toolbar-right">
        <el-input
          v-model="searchKeyword"
          class="search-input"
          clearable
          placeholder="搜索当前类型下的文件名，回车开始"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button :loading="searchLoading" @click="handleSearch">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>

        <el-button v-if="isSearchMode" @click="clearSearch">清空搜索</el-button>
        <el-button 
          v-if="selectedFiles.length > 0" 
          type="danger" 
          @click="batchDelete"
        >
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
        <el-button 
          v-if="selectedFiles.length > 0" 
          type="success" 
          @click="batchDownload"
        >
          <el-icon><Download /></el-icon>
          批量下载
        </el-button>
      </div>
      </div>
    </section>

    <section class="file-table-shell">
      <el-table
        v-loading="loading"
        :data="fileStore.files"
        :height="tableHeight"
        class="file-table pan-table"
        tooltip-effect="dark"
        empty-text="当前目录暂无文件"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />

        <el-table-column label="文件名" prop="filename" sortable show-overflow-tooltip min-width="280">
          <template #default="{ row }">
            <div
              class="file-row-container"
              @mouseenter="showOperation($event)"
              @mouseleave="hiddenOperation($event)"
            >
              <div class="file-name-content" @click="clickFilename(row)">
                <i :class="getFileFontElement(row.fileType)" class="file-font-icon" />
                <span
                  v-if="isSearchMode && row.highlightFilename"
                  class="file-name"
                  v-html="row.highlightFilename"
                />
                <span v-else class="file-name">{{ row.filename }}</span>
              </div>

              <div class="file-operation-content">
                <div class="pan-file-operations">
                  <el-tooltip v-if="!row.folderFlag" effect="light" content="预览" placement="top">
                    <el-button icon="View" type="primary" size="small" circle @click.stop="previewFile(row)" />
                  </el-tooltip>

                  <el-tooltip v-if="!row.folderFlag" effect="light" content="下载" placement="top">
                    <el-button icon="Download" type="info" size="small" circle @click.stop="downloadFile(row)" />
                  </el-tooltip>

                  <el-tooltip v-if="supportsAi(row)" effect="light" content="AI 分析" placement="top">
                    <el-button type="primary" plain size="small" circle @click.stop="openAiDrawer(row)">
                      <el-icon><Cpu /></el-icon>
                    </el-button>
                  </el-tooltip>

                  <el-tooltip effect="light" content="重命名" placement="top">
                    <el-button icon="Edit" type="warning" size="small" circle @click.stop="renameFile(row)" />
                  </el-tooltip>

                  <el-tooltip effect="light" content="删除" placement="top">
                    <el-button icon="Delete" type="danger" size="small" circle @click.stop="deleteFile(row)" />
                  </el-tooltip>

                  <el-tooltip effect="light" content="分享" placement="top">
                    <el-button icon="Share" type="success" size="small" circle @click.stop="shareFile(row)" />
                  </el-tooltip>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="fileSizeDesc" sortable label="大小" width="120" align="center" />

        <el-table-column prop="updateTime" sortable align="center" label="修改日期" width="180">
          <template #default="{ row }">
            {{ formatDate(row.updateTime) }}
          </template>
        </el-table-column>
      </el-table>
    </section>

    <el-dialog
      v-model="showShareDialog"
      title="分享文件"
      width="500px"
      :close-on-click-modal="false"
      class="share-dialog pan-dialog"
    >
      <el-form ref="shareFormRef" :model="shareForm" :rules="shareRules" label-width="100px">
        <el-form-item label="分享名称" prop="shareName">
          <el-input v-model="shareForm.shareName" placeholder="请输入分享名称" />
        </el-form-item>

        <el-form-item label="分享类型" prop="shareType">
          <el-radio-group v-model="shareForm.shareType">
            <el-radio label="withCode">需要提取码</el-radio>
            <el-radio label="withoutCode">无需提取码</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="有效期" prop="validity">
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

    <AiFileDrawer
      v-model="showAiDrawer"
      :file="currentAiFile"
      :mode="currentAiMode"
      @open-file="continueFileAction"
    />
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Cpu, Delete, Download, Edit, Folder, FolderAdd, Search, Share } from '@element-plus/icons-vue'
import { createFolder as createFolderAPI, deleteFiles as deleteFilesAPI, downloadFile as downloadFileAPI, previewFile as previewFileAPI, renameFile as renameFileAPI, searchFiles } from '@/api/file'
import { createShare } from '@/api/share'
import { useFileStore } from '@/stores/file'
import { useUserStore } from '@/stores/user'
import AiFileDrawer from '@/components/AiFileDrawer.vue'
import UploadButton from '@/components/UploadButton.vue'

const fileStore = useFileStore()
const userStore = useUserStore()
const route = useRoute()
const router = useRouter()

const AI_SUPPORTED_FILE_TYPES = new Set([3, 4, 5, 6, 10, 11, 12])

const loading = ref(false)
const searchLoading = ref(false)
const isSearchMode = ref(false)
const searchKeyword = ref('')
const selectedFiles = ref([])
const tableHeight = ref(calcTableHeight())

const showShareDialog = ref(false)
const currentShareFile = ref(null)
const shareFormRef = ref(null)
const shareForm = ref({
  shareName: '',
  shareType: '',
  validity: ''
})

const showAiDrawer = ref(false)
const currentAiFile = ref(null)
const currentAiMode = ref('insight')

const shareRules = {
  shareName: [
    { required: true, message: '请输入分享名称', trigger: 'blur' },
    { min: 1, max: 50, message: '分享名称长度应在 1 到 50 个字符之间', trigger: 'blur' }
  ],
  shareType: [{ required: true, message: '请选择分享类型', trigger: 'change' }],
  validity: [{ required: true, message: '请选择有效期', trigger: 'change' }]
}

const typeMap = {
  all: '-1',
  image: '7',
  document: '3,4,5,6,10,11,12',
  video: '9',
  music: '8',
  other: '1,2'
}

watch(
  [() => route.query.type, () => userStore.rootFileId],
  async ([newType, rootFileId]) => {
    if (!rootFileId) {
      return
    }
    isSearchMode.value = false
    searchKeyword.value = ''
    loading.value = true
    try {
      const fileTypes = newType ? typeMap[newType] || '-1' : typeMap.all
      await fileStore.loadFiles(rootFileId, fileTypes)
      selectedFiles.value = []
    } finally {
      loading.value = false
    }
  },
  { immediate: true }
)

onMounted(() => {
  window.addEventListener('resize', updateTableHeight)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', updateTableHeight)
})

function calcTableHeight() {
  return Math.max(window.innerHeight - 330, 320)
}

function updateTableHeight() {
  tableHeight.value = calcTableHeight()
}

function handleSelectionChange(selection) {
  selectedFiles.value = Array.isArray(selection) ? selection : []
}

function showOperation(event) {
  const operationContent = event.currentTarget?.querySelector('.file-operation-content')
  operationContent?.classList.add('show')
}

function hiddenOperation(event) {
  const operationContent = event.currentTarget?.querySelector('.file-operation-content')
  operationContent?.classList.remove('show')
}

function getFileId(file) {
  return file?.id || file?.fileId || file?.file_id || ''
}

function getFileFontElement(fileType) {
  const fileTypeMap = {
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
  return fileTypeMap[fileType] || 'fa fa-file'
}

function supportsAi(file) {
  return !file?.folderFlag && AI_SUPPORTED_FILE_TYPES.has(Number(file?.fileType))
}

function clickFilename(file) {
  if (file?.folderFlag) {
    openFolder(file)
    return
  }
  if (supportsAi(file)) {
    openAiDrawer(file, 'insight')
    return
  }
  continueFileAction(file)
}

function continueFileAction(file) {
  if (canInlinePreview(file?.fileType)) {
    previewFile(file)
    return
  }
  downloadFile(file)
}

async function openFolder(folder) {
  const folderId = getFileId(folder)
  if (!folderId) {
    ElMessage.error('文件夹 ID 无效')
    return
  }
  isSearchMode.value = false
  loading.value = true
  try {
    await fileStore.loadFiles(folderId, fileStore.fileTypes)
    selectedFiles.value = []
  } catch (error) {
    ElMessage.error(error?.message || '打开文件夹失败')
  } finally {
    loading.value = false
  }
}

async function navigateToFolder(folderId) {
  const targetFolderId = folderId || userStore.rootFileId
  if (!targetFolderId) {
    return
  }
  isSearchMode.value = false
  loading.value = true
  try {
    await fileStore.loadFiles(targetFolderId, fileStore.fileTypes)
    selectedFiles.value = []
  } finally {
    loading.value = false
  }
}

async function refreshCurrentFolder() {
  const targetFolderId = fileStore.parentId || userStore.rootFileId
  if (!targetFolderId) {
    return
  }
  loading.value = true
  try {
    await fileStore.loadFiles(targetFolderId, fileStore.fileTypes)
    selectedFiles.value = []
  } finally {
    loading.value = false
  }
}

async function handleSearch() {
  const keyword = searchKeyword.value?.trim()
  if (!keyword) {
    await clearSearch()
    return
  }

  searchLoading.value = true
  loading.value = true
  try {
    const response = await searchFiles({
      keyword,
      fileTypes: fileStore.fileTypes
    })
    if (response?.success) {
      fileStore.files = Array.isArray(response.data) ? response.data : []
      selectedFiles.value = []
      isSearchMode.value = true
    }
  } catch (error) {
    ElMessage.error(error?.message || '搜索失败')
  } finally {
    searchLoading.value = false
    loading.value = false
  }
}

async function clearSearch() {
  if (!isSearchMode.value && !searchKeyword.value) {
    return
  }
  searchKeyword.value = ''
  isSearchMode.value = false
  await refreshCurrentFolder()
}

async function previewFile(file) {
  if (file?.folderFlag) {
    ElMessage.warning('文件夹暂不支持预览')
    return
  }
  try {
    await previewSingleFile(file)
  } catch (error) {
    ElMessage.error(error?.message || '文件预览失败')
  }
}

async function downloadFile(file) {
  if (file?.folderFlag) {
    ElMessage.warning('文件夹暂不支持下载')
    return
  }
  try {
    await downloadSingleFile(file)
    ElMessage.success('文件下载已开始')
  } catch (error) {
    ElMessage.error(error?.message || '文件下载失败')
  }
}

async function renameFile(file) {
  try {
    const { value: newName } = await ElMessageBox.prompt('请输入新的文件名', '重命名', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputValue: file?.filename || '',
      inputPlaceholder: '请输入文件名',
      inputValidator: (value) => {
        if (!value) {
          return '文件名不能为空'
        }
        if (value.length > 255) {
          return '文件名不能超过 255 个字符'
        }
        return true
      }
    })

    if (!newName) {
      return
    }

    await renameFileAPI({
      fileId: String(getFileId(file)),
      newFilename: newName
    })
    ElMessage.success('文件重命名成功')
    await refreshCurrentFolder()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error?.message || '文件重命名失败')
    }
  }
}

async function deleteFile(file) {
  try {
    await ElMessageBox.confirm(`确定删除 "${file.filename}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteFilesAPI({
      fileIds: [String(getFileId(file))]
    })
    ElMessage.success('文件删除成功')
    await refreshCurrentFolder()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error?.message || '删除失败')
    }
  }
}

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
          return '文件夹名称不能超过 255 个字符'
        }
        if (/[<>:"/\\|?*]/.test(value)) {
          return '文件夹名称不能包含以下字符: < > : " / \\ | ? *'
        }
        return true
      }
    })

    if (!folderName) {
      return
    }

    await createFolderAPI({
      parentId: fileStore.parentId,
      folderName
    })
    ElMessage.success('文件夹创建成功')
    await refreshCurrentFolder()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error?.message || '文件夹创建失败')
    }
  }
}

function shareFile(file) {
  if (!getFileId(file)) {
    ElMessage.error('文件 ID 无效')
    return
  }
  currentShareFile.value = file
  shareForm.value = {
    shareName: file?.filename || '',
    shareType: '',
    validity: ''
  }
  showShareDialog.value = true
}

async function confirmShare() {
  if (!shareFormRef.value || !currentShareFile.value) {
    return
  }

  try {
    await shareFormRef.value.validate()

    const shareType = shareForm.value.shareType === 'withCode' ? 1 : 0
    const validityMap = {
      permanent: '永久有效',
      '1day': '1天',
      '7days': '7天',
      '30days': '30天'
    }

    await createShare({
      shareName: shareForm.value.shareName,
      shareType,
      shareDayType: validityMap[shareForm.value.validity],
      shareFileIds: [String(getFileId(currentShareFile.value))]
    })

    showShareDialog.value = false
    ElMessage.success('分享创建成功，正在跳转到我的分享')
    setTimeout(() => {
      router.push('/shares')
    }, 800)
  } catch (error) {
    ElMessage.error(error?.message || '分享失败，请重试')
  }
}

function openAiDrawer(file, mode = 'qa') {
  if (!supportsAi(file)) {
    ElMessage.warning('当前文件类型暂不支持 AI 解析')
    return
  }
  currentAiFile.value = file
  currentAiMode.value = mode
  showAiDrawer.value = true
}

async function batchDownload() {
  if (!selectedFiles.value.length) {
    ElMessage.warning('请选择要下载的文件')
    return
  }

  const targetFiles = selectedFiles.value.filter((file) => !file.folderFlag)
  if (!targetFiles.length) {
    ElMessage.warning('当前选中项都是文件夹，无法下载')
    return
  }

  let successCount = 0
  for (const file of targetFiles) {
    try {
      await downloadSingleFile(file)
      successCount++
    } catch (error) {
      console.error('[Files.vue] batch download failed', file, error)
    }
  }

  if (!successCount) {
    ElMessage.error('批量下载失败')
    return
  }
  ElMessage.success(`批量下载完成，成功 ${successCount}/${targetFiles.length}`)
}

async function batchDelete() {
  if (!selectedFiles.value.length) {
    ElMessage.warning('请选择要删除的文件')
    return
  }

  try {
    await ElMessageBox.confirm(`确定删除选中的 ${selectedFiles.value.length} 个文件吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteFilesAPI({
      fileIds: selectedFiles.value.map((file) => String(getFileId(file)))
    })
    ElMessage.success('文件批量删除成功')
    await refreshCurrentFolder()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error?.message || '文件批量删除失败')
    }
  }
}

function canInlinePreview(fileType) {
  return [5, 6, 7, 8, 9, 11, 12].includes(Number(fileType))
}

function isTextPreviewType(fileType) {
  return [6, 11, 12].includes(Number(fileType))
}

function getPreviewMimeType(fileType) {
  const normalized = Number(fileType)
  if (normalized === 5) return 'application/pdf'
  if ([6, 11, 12].includes(normalized)) return 'text/plain;charset=utf-8'
  if (normalized === 7) return 'image/*'
  if (normalized === 8) return 'audio/*'
  if (normalized === 9) return 'video/*'
  return 'application/octet-stream'
}

function parseFilenameFromDisposition(disposition, fallbackName = 'download') {
  if (!disposition) {
    return fallbackName
  }
  const utf8Match = disposition.match(/filename\*\s*=\s*UTF-8''([^;]+)/i)
  if (utf8Match?.[1]) {
    try {
      return decodeURIComponent(utf8Match[1])
    } catch (_) {
      return utf8Match[1]
    }
  }
  const normalMatch = disposition.match(/filename\s*=\s*"?([^";]+)"?/i)
  return normalMatch?.[1] || fallbackName
}

function saveBlobToLocal(blob, filename) {
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  window.URL.revokeObjectURL(url)
}

async function extractBlobFromResponse(response, defaultErrorMessage) {
  const blob = response?.data instanceof Blob ? response.data : response
  if (!(blob instanceof Blob)) {
    throw new Error(defaultErrorMessage)
  }
  if (blob.type?.includes('application/json')) {
    const text = await blob.text()
    try {
      const result = JSON.parse(text)
      throw new Error(result?.message || defaultErrorMessage)
    } catch (error) {
      if (error instanceof SyntaxError) {
        throw new Error(defaultErrorMessage)
      }
      throw error
    }
  }
  return blob
}

function escapeHtml(rawText = '') {
  return rawText
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')
}

async function decodeTextBlob(blob) {
  const bytes = await blob.arrayBuffer()
  try {
    return new TextDecoder('utf-8', { fatal: true }).decode(bytes)
  } catch (_) {
    try {
      return new TextDecoder('gb18030').decode(bytes)
    } catch (_) {
      return new TextDecoder().decode(bytes)
    }
  }
}

function renderTextPreview(previewWindow, text, filename) {
  const safeTitle = escapeHtml(filename || '文本预览')
  const safeText = escapeHtml(text)
  previewWindow.document.open()
  previewWindow.document.write(`<!doctype html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>${safeTitle}</title>
  <style>
    body { margin: 0; background: #f7f8fa; color: #1f2329; font-family: Consolas, Monaco, Menlo, monospace; }
    .wrap { padding: 16px; }
    pre { margin: 0; white-space: pre-wrap; word-break: break-word; line-height: 1.6; font-size: 14px; }
  </style>
</head>
<body>
  <div class="wrap"><pre>${safeText}</pre></div>
</body>
</html>`)
  previewWindow.document.close()
}

async function previewSingleFile(file) {
  const fileId = getFileId(file)
  if (!fileId) {
    throw new Error('文件 ID 无效')
  }

  const previewWindow = window.open('', '_blank')
  if (!previewWindow) {
    throw new Error('浏览器拦截了预览窗口，请允许弹窗后重试')
  }

  try {
    const response = await previewFileAPI({ fileId: String(fileId) })
    const rawBlob = await extractBlobFromResponse(response, '文件预览失败')
    if (isTextPreviewType(file?.fileType)) {
      const decodedText = await decodeTextBlob(rawBlob)
      renderTextPreview(previewWindow, decodedText, file?.filename)
      return
    }
    const previewType = rawBlob.type || getPreviewMimeType(file?.fileType)
    const previewBlob = rawBlob.type ? rawBlob : new Blob([rawBlob], { type: previewType })
    const previewUrl = window.URL.createObjectURL(previewBlob)
    previewWindow.location.href = previewUrl
    setTimeout(() => window.URL.revokeObjectURL(previewUrl), 60000)
  } catch (error) {
    previewWindow.close()
    throw error
  }
}

async function downloadSingleFile(file) {
  const fileId = getFileId(file)
  if (!fileId) {
    throw new Error('文件 ID 无效')
  }
  const response = await downloadFileAPI({ fileId: String(fileId) })
  const blob = await extractBlobFromResponse(response, '文件下载失败')
  const disposition = response?.headers?.['content-disposition'] || response?.headers?.['Content-Disposition']
  const filename = parseFilenameFromDisposition(disposition, file?.filename || 'download')
  saveBlobToLocal(blob, filename)
}

function formatDate(value) {
  if (!value) {
    return ''
  }
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return ''
  }
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<style scoped>
.files-page {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.page-top {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 16px 20px;
  border-radius: 24px;
  background: #fff;
  border: 1px solid rgba(226, 232, 240, 0.9);
  box-shadow: 0 16px 36px rgba(15, 23, 42, 0.06);
}

.breadcrumb-item {
  cursor: pointer;
}

.breadcrumb-content {
  display: flex;
  align-items: center;
  gap: 6px;
}

.breadcrumb-icon {
  color: #2563eb;
}

.breadcrumb-text {
  color: #334155;
  font-weight: 500;
}

.file-table-shell {
  border-radius: 24px;
  background: #fff;
  border: 1px solid rgba(226, 232, 240, 0.9);
  box-shadow: 0 16px 36px rgba(15, 23, 42, 0.06);
}

.toolbar-shell {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding-top: 16px;
  border-top: 1px solid rgba(226, 232, 240, 0.9);
}

.toolbar-left,
.toolbar-right {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.search-input {
  width: 340px;
}

.create-folder-btn {
  background: linear-gradient(135deg, #22c55e, #16a34a);
  border-color: transparent;
  color: #fff;
}

.batch-delete-btn {
  border-color: transparent;
}

.batch-download-btn {
  border-color: transparent;
}

.file-table-shell {
  padding: 10px 14px 16px;
}

:deep(.el-table) {
  width: 100%;
  border-radius: 18px;
}

:deep(.el-table th) {
  background: #f8fafc;
  color: #0f172a;
  font-weight: 700;
}

.file-row-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.file-name-content {
  display: flex;
  align-items: center;
  min-width: 0;
  cursor: pointer;
}

.file-font-icon {
  margin-right: 14px;
  font-size: 20px;
  color: #3b82f6;
}

.file-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

:deep(.search-highlight) {
  background: #fef3c7;
  color: #92400e;
  border-radius: 4px;
  padding: 0 2px;
  font-weight: 700;
}

.file-operation-content {
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.2s ease;
}

.file-operation-content.show {
  opacity: 1;
  pointer-events: auto;
}

.pan-file-operations {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  padding: 4px 8px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.94);
  backdrop-filter: blur(12px);
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.12);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@media (max-width: 1100px) {
  .toolbar-shell {
    flex-direction: column;
    align-items: stretch;
  }

  .search-input {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .page-top {
    padding: 18px;
  }

  .toolbar-right,
  .toolbar-left {
    flex-direction: column;
    align-items: stretch;
  }

  .file-table-shell {
    padding: 10px;
  }
}
</style>
