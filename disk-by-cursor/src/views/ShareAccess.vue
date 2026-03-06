<template>
  <div class="share-access-page">
    <el-card class="share-access-card" v-loading="loadingShareInfo">
      <template #header>
        <div class="card-header">分享访问</div>
      </template>

      <el-result
        v-if="errorMessage"
        icon="error"
        title="链接不可用"
        :sub-title="errorMessage"
      />

      <template v-else-if="shareInfo">
        <div class="share-info">
          <h2 class="share-title">{{ shareInfo.shareName }}</h2>
          <p>状态：{{ shareInfo.shareStatus === 0 ? '正常' : '异常' }}</p>
          <p>过期时间：{{ formatDateTime(shareInfo.shareEndTime, '永久有效') }}</p>
        </div>

        <div v-if="isPrivateShare && !shareVerified" class="share-code-panel">
          <el-alert type="warning" show-icon :closable="false" title="该分享需要提取码" />
          <el-input
            v-model="shareCodeInput"
            maxlength="12"
            placeholder="请输入提取码"
            class="share-code-input"
            @keyup.enter="handleCheckCode"
          />
          <el-button type="primary" :loading="checkingCode" @click="handleCheckCode">验证提取码</el-button>
        </div>

        <div v-else class="share-files-panel">
          <div class="panel-title">分享文件</div>
          <el-table :data="shareFiles" v-loading="loadingFiles" style="width: 100%">
            <el-table-column prop="filename" label="文件名" min-width="260" />
            <el-table-column prop="fileSizeDesc" label="大小" width="120" />
            <el-table-column label="更新时间" width="180">
              <template #default="{ row }">
                {{ formatDateTime(row.updateTime, '--') }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button
                  type="primary"
                  link
                  :disabled="Number(row.folderFlag) === 1"
                  @click="handleDownload(row)"
                >
                  下载
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!loadingFiles && shareFiles.length === 0" description="该分享暂无可下载文件" />
        </div>
      </template>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { checkShareCode, downloadShareFile, getShareFiles, getShareSimpleDetail } from '@/api/share'

const route = useRoute()
const loadingShareInfo = ref(false)
const loadingFiles = ref(false)
const checkingCode = ref(false)
const errorMessage = ref('')
const shareInfo = ref(null)
const shareFiles = ref([])
const shareCodeInput = ref('')
const shareVerified = ref(false)

const shareId = computed(() => route.query.shareId || '')
const isPrivateShare = computed(() => Number(shareInfo.value?.shareType) === 1)

function formatDateTime(value, emptyText = '--') {
  if (!value) return emptyText
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  return date.toLocaleString('zh-CN')
}

function parseFilenameFromDisposition(disposition, fallbackName = 'download') {
  if (!disposition) return fallbackName
  const utf8Match = disposition.match(/filename\*=UTF-8''([^;]+)/i)
  if (utf8Match && utf8Match[1]) {
    try {
      return decodeURIComponent(utf8Match[1]).replace(/["']/g, '')
    } catch (_) {
      return utf8Match[1].replace(/["']/g, '')
    }
  }
  const filenameMatch = disposition.match(/filename="?([^\";]+)"?/i)
  return filenameMatch?.[1] || fallbackName
}

function saveBlob(blob, filename) {
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  window.URL.revokeObjectURL(url)
}

async function loadShareInfo() {
  if (!shareId.value) {
    errorMessage.value = '缺少 shareId 参数'
    return
  }
  loadingShareInfo.value = true
  try {
    const response = await getShareSimpleDetail({ shareId: shareId.value })
    if (!response?.success || !response?.data) {
      errorMessage.value = response?.message || '分享不存在或已失效'
      return
    }
    shareInfo.value = response.data
    if (!isPrivateShare.value) {
      shareVerified.value = true
      await loadShareFiles()
    }
  } catch (error) {
    errorMessage.value = error?.message || '加载分享信息失败'
  } finally {
    loadingShareInfo.value = false
  }
}

async function loadShareFiles() {
  if (!shareId.value) return
  loadingFiles.value = true
  try {
    const params = { shareId: shareId.value }
    if (isPrivateShare.value) {
      params.shareCode = shareCodeInput.value.trim()
    }
    const response = await getShareFiles(params)
    if (response?.success) {
      shareFiles.value = Array.isArray(response.data) ? response.data : []
      return
    }
    shareFiles.value = []
    ElMessage.error(response?.message || '加载分享文件失败')
  } catch (error) {
    shareFiles.value = []
    ElMessage.error(error?.message || '加载分享文件失败')
  } finally {
    loadingFiles.value = false
  }
}

async function handleCheckCode() {
  const shareCode = shareCodeInput.value.trim()
  if (!shareCode) {
    ElMessage.warning('请输入提取码')
    return
  }
  checkingCode.value = true
  try {
    const response = await checkShareCode({
      shareId: shareId.value,
      shareCode
    })
    if (!response?.success) {
      ElMessage.error(response?.message || '提取码验证失败')
      return
    }
    shareVerified.value = true
    ElMessage.success('提取码验证成功')
    await loadShareFiles()
  } catch (error) {
    ElMessage.error(error?.message || '提取码验证失败')
  } finally {
    checkingCode.value = false
  }
}

async function handleDownload(file) {
  if (Number(file?.folderFlag) === 1) {
    ElMessage.warning('暂不支持从分享页直接下载文件夹')
    return
  }
  try {
    const params = {
      shareId: shareId.value,
      fileId: file.fileId
    }
    if (isPrivateShare.value) {
      params.shareCode = shareCodeInput.value.trim()
    }
    const response = await downloadShareFile(params)
    const blob = response?.data
    if (!blob) {
      throw new Error('下载失败')
    }
    const disposition = response?.headers?.['content-disposition'] || response?.headers?.['Content-Disposition']
    const filename = parseFilenameFromDisposition(disposition, file.filename || 'download')
    saveBlob(blob, filename)
    ElMessage.success('下载开始')
  } catch (error) {
    ElMessage.error(error?.message || '下载失败')
  }
}

onMounted(() => {
  loadShareInfo()
})
</script>

<style scoped>
.share-access-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: #f5f7fa;
}

.share-access-card {
  width: 100%;
  max-width: 900px;
}

.card-header {
  font-size: 18px;
  font-weight: 600;
}

.share-info {
  margin-bottom: 16px;
}

.share-title {
  margin: 0 0 12px;
}

.share-code-panel {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.share-code-input {
  max-width: 320px;
}

.share-files-panel {
  margin-top: 12px;
}

.panel-title {
  margin-bottom: 12px;
  font-weight: 600;
}
</style>
