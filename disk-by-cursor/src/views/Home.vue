<template>
  <div class="home-container pan-main-content">
    <el-row :gutter="12">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#409eff"><Folder /></el-icon>
            <div class="stat-info">
              <div class="stat-number">{{ stats.totalFiles }}</div>
              <div class="stat-label">总文件数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#67c23a"><Picture /></el-icon>
            <div class="stat-info">
              <div class="stat-number">{{ stats.images }}</div>
              <div class="stat-label">图片文件</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#e6a23c"><VideoPlay /></el-icon>
            <div class="stat-info">
              <div class="stat-number">{{ stats.videos }}</div>
              <div class="stat-label">视频文件</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#f56c6c"><Document /></el-icon>
            <div class="stat-info">
              <div class="stat-number">{{ stats.documents }}</div>
              <div class="stat-label">文档文件</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="12" style="margin-top: 12px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最近上传</span>
            </div>
          </template>
          <div v-if="recentFiles.length === 0" class="empty-state">
            <el-empty description="暂无最近上传的文件" />
          </div>
          <el-table v-else :data="recentFiles" style="width: 100%">
            <el-table-column prop="filename" label="文件名" min-width="200" />
            <el-table-column prop="fileSizeDesc" label="大小" width="100" />
            <el-table-column label="上传时间" width="180">
              <template #default="{ row }">
                <span>{{ formatDateTime(row.updateTime) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>我的分享</span>
            </div>
          </template>
          <div v-if="recentShares.length === 0" class="empty-state">
            <el-empty description="暂无分享文件" />
          </div>
          <el-table v-else :data="recentShares" style="width: 100%">
            <el-table-column prop="shareName" label="分享名称" min-width="220">
              <template #default="{ row }">
                <div
                  class="share-row-container"
                  @mouseenter="showOperation($event)"
                  @mouseleave="hiddenOperation($event)"
                >
                  <div class="share-name-content">
                    <el-icon class="share-icon" color="#409eff">
                      <Share />
                    </el-icon>
                    <span class="share-name">{{ row.shareName }}</span>
                  </div>
                  <div class="share-operation-content">
                    <el-tooltip effect="light" content="复制链接" placement="top">
                      <el-button
                        :icon="CopyDocument"
                        type="primary"
                        size="small"
                        circle
                        @click="copyShareUrl(row)"
                      />
                    </el-tooltip>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="过期时间" width="180">
              <template #default="{ row }">
                <span>{{ formatDateTime(row.shareEndTime, '永久有效') }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { CopyDocument, Document, Folder, Picture, Share, VideoPlay } from '@element-plus/icons-vue'
import { getHomeOverview } from '@/api/file'
import { getShareList } from '@/api/share'
import { useRoute } from 'vue-router'

const stats = ref({
  totalFiles: 0,
  images: 0,
  videos: 0,
  documents: 0
})

const recentFiles = ref([])
const recentShares = ref([])
const route = useRoute()

const toNumber = (value) => {
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : 0
}

const parseDate = (value) => {
  if (!value) return null
  if (value instanceof Date) return value
  const date = new Date(value)
  if (!Number.isNaN(date.getTime())) return date
  return null
}

const formatDateTime = (value, emptyText = '--') => {
  const date = parseDate(value)
  if (!date) return emptyText
  return date.toLocaleString('zh-CN')
}

const sortByRecent = (items) => {
  return [...items].sort((a, b) => {
    const left = parseDate(a.createTime || a.gmtCreate || a.shareEndTime)?.getTime() || 0
    const right = parseDate(b.createTime || b.gmtCreate || b.shareEndTime)?.getTime() || 0
    return right - left
  })
}

const loadOverview = async () => {
  const response = await getHomeOverview()
  if (response.code !== 'SUCCESS') {
    throw new Error(response.message || '加载首页概览失败')
  }
  const data = response.data || {}
  stats.value = {
    totalFiles: toNumber(data.totalFiles),
    images: toNumber(data.images),
    videos: toNumber(data.videos),
    documents: toNumber(data.documents)
  }
  recentFiles.value = Array.isArray(data.recentFiles) ? data.recentFiles : []
}

const loadShares = async () => {
  const response = await getShareList()
  if (response.code !== 'SUCCESS') {
    throw new Error(response.message || '加载分享列表失败')
  }
  const list = Array.isArray(response.data) ? response.data : []
  recentShares.value = sortByRecent(list).slice(0, 5)
}

const loadHomeData = async () => {
  const [overviewResult, shareResult] = await Promise.allSettled([loadOverview(), loadShares()])
  if (overviewResult.status === 'rejected') {
    console.error('加载首页概览失败:', overviewResult.reason)
    ElMessage.error('加载首页概览失败')
  }
  if (shareResult.status === 'rejected') {
    console.error('加载分享数据失败:', shareResult.reason)
    ElMessage.error('加载分享数据失败')
  }
}

const copyShareUrl = async (share) => {
  if (!share?.shareUrl) {
    ElMessage.warning('当前分享没有可复制的链接')
    return
  }
  try {
    await navigator.clipboard.writeText(share.shareUrl)
    ElMessage.success('分享链接已复制到剪贴板')
  } catch (error) {
    ElMessage.error('复制失败')
  }
}

const showOperation = (event) => {
  event.currentTarget.classList.add('show')
}

const hiddenOperation = (event) => {
  event.currentTarget.classList.remove('show')
}

onMounted(() => {
  loadHomeData()
})

watch(
  () => route.fullPath,
  (newPath) => {
    if (newPath === '/') {
      loadHomeData()
    }
  }
)
</script>

<style scoped>
.home-container {
  padding: 0;
}

.stat-card {
  margin-bottom: 12px;
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-light);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-medium);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 12px;
}

.stat-icon {
  font-size: 40px;
  margin-right: 16px;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.stat-label {
  color: var(--text-secondary);
  font-size: 14px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: var(--text-primary);
}

.empty-state {
  padding: 40px 0;
}

:deep(.el-card) {
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-light);
  transition: all 0.3s ease;
}

:deep(.el-card:hover) {
  box-shadow: var(--shadow-medium);
}

:deep(.el-card__header) {
  border-bottom: 1px solid var(--border-color);
  background: var(--bg-light);
}

:deep(.el-table) {
  border-radius: var(--border-radius);
}

:deep(.el-table th) {
  background: var(--bg-light);
  color: var(--text-primary);
  font-weight: 600;
  padding: 6px 0;
}

:deep(.el-table td) {
  padding: 6px 0;
}

.share-row-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.share-name-content {
  display: flex;
  align-items: center;
  flex: 1;
}

.share-icon {
  margin-right: 8px;
  font-size: 16px;
}

.share-name {
  color: var(--text-primary);
  font-size: 14px;
}

.share-operation-content {
  opacity: 0;
  transition: opacity 0.2s ease;
  margin-left: 8px;
}

.share-row-container.show .share-operation-content {
  opacity: 1;
}

@media (max-width: 1200px) {
  .stat-content {
    padding: 12px;
  }

  .stat-icon {
    font-size: 36px;
    margin-right: 12px;
  }

  .stat-number {
    font-size: 24px;
  }
}

@media (max-width: 992px) {
  .stat-content {
    padding: 8px;
  }

  .stat-icon {
    font-size: 32px;
    margin-right: 8px;
  }

  .stat-number {
    font-size: 20px;
  }
}

@media (max-width: 768px) {
  .stat-content {
    padding: 8px;
  }

  .stat-icon {
    font-size: 28px;
    margin-right: 8px;
  }

  .stat-number {
    font-size: 18px;
  }

  .stat-label {
    font-size: 12px;
  }
}
</style>
