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
            <el-table-column prop="filename" label="文件名" />
            <el-table-column prop="fileSizeDesc" label="大小" width="60" />
            <el-table-column prop="updateTime" label="上传时间" width="120" />
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
            <el-table-column prop="shareName" label="分享名称" min-width="200">
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
                    <el-tooltip class="item" effect="light" content="复制链接" placement="top">
                      <el-button 
                        icon="CopyDocument" 
                        type="primary" 
                        size="small" 
                        circle 
                        @click="copyShareUrl(row)">
                      </el-button>
                    </el-tooltip>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="shareEndTime" label="过期时间" width="150" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Share, CopyDocument } from '@element-plus/icons-vue'

// mock统计数据
const stats = ref({
  totalFiles: 123,
  images: 45,
  videos: 12,
  documents: 66
})

// mock最近上传
const recentFiles = ref([
  { filename: '设计文档.pdf', fileSizeDesc: '2.3MB', updateTime: '2024-07-01 10:23:00' },
  { filename: '产品原型.png', fileSizeDesc: '1.1MB', updateTime: '2024-06-30 18:12:00' },
  { filename: '会议记录.docx', fileSizeDesc: '800KB', updateTime: '2024-06-29 09:00:00' },
  { filename: '视频演示.mp4', fileSizeDesc: '15MB', updateTime: '2024-06-28 14:45:00' },
  { filename: '数据表.xlsx', fileSizeDesc: '500KB', updateTime: '2024-06-27 16:20:00' }
])

// mock最近分享
const recentShares = ref([
  { shareName: '项目资料包', shareEndTime: '2024-08-01 23:59:59', shareUrl: 'https://disk.example.com/share/abc123' },
  { shareName: '设计图纸', shareEndTime: '2024-07-20 23:59:59', shareUrl: 'https://disk.example.com/share/def456' }
])

const copyShareUrl = (share) => {
  navigator.clipboard.writeText(share.shareUrl).then(() => {
    ElMessage.success('分享链接已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

// 显示操作按钮
const showOperation = (row, event) => {
  const container = event.currentTarget
  container.classList.add('show')
}

// 隐藏操作按钮
const hiddenOperation = (row, event) => {
  const container = event.currentTarget
  container.classList.remove('show')
}
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
  font-weight: bold;
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

/* 分享行样式 */
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
  transition: opacity 0.3s ease;
  margin-left: 8px;
}

.share-row-container.show .share-operation-content {
  opacity: 1;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .stat-card {
    margin-bottom: 12px;
  }
  
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
  
  :deep(.el-table th),
  :deep(.el-table td) {
    padding: 6px 0;
  }
}

@media (max-width: 992px) {
  .stat-card {
    margin-bottom: 8px;
  }
  
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
  
  .stat-label {
    font-size: 12px;
  }
  
  :deep(.el-table th),
  :deep(.el-table td) {
    padding: 4px 0;
  }
}

@media (max-width: 768px) {
  .stat-card {
    margin-bottom: 8px;
  }
  
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
    font-size: 11px;
  }
  
  :deep(.el-table th),
  :deep(.el-table td) {
    padding: 4px 0;
  }
}
</style> 