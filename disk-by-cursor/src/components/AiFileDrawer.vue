<template>
  <el-drawer
    :model-value="modelValue"
    size="760px"
    direction="rtl"
    :destroy-on-close="false"
    class="ai-file-drawer"
    @update:model-value="emit('update:modelValue', $event)"
  >
    <template #header>
      <div class="drawer-hero">
        <div class="drawer-hero__content">
          <div class="drawer-hero__eyebrow">Document Intelligence</div>
          <div class="drawer-hero__title">{{ currentFileName }}</div>
          <div class="drawer-hero__desc">
            上传后系统会异步生成默认摘要和标签。这里优先读取后台结果，必要时也支持手动刷新。
          </div>
        </div>
        <div class="drawer-hero__meta">
          <el-tag effect="dark" type="success">{{ currentFileTypeLabel }}</el-tag>
          <el-tag type="info">{{ currentFileSize }}</el-tag>
          <el-tag :type="isSupportedFile ? 'success' : 'warning'">
            {{ isSupportedFile ? '支持 AI 解析' : '暂不支持当前文件类型' }}
          </el-tag>
        </div>
      </div>
    </template>

    <el-scrollbar class="drawer-scroll">
      <div class="ai-layout">
        <section class="status-band">
          <div class="status-band__left">
            <div class="status-band__title">
              <el-icon><Cpu /></el-icon>
              <span>AI 服务状态</span>
            </div>
            <div class="status-band__chips">
              <el-tag v-if="capability.provider" round>{{ capability.provider }}</el-tag>
              <el-tag v-if="capability.chatModel" round type="success">{{ capability.chatModel }}</el-tag>
              <el-tag v-if="capability.vectorStoreEnabled" round type="warning">向量检索已启用</el-tag>
              <el-tag v-else round type="info">向量检索未启用</el-tag>
            </div>
            <div class="status-band__hint">
              <span>摘要与标签默认复用落库结果。</span>
              <span v-if="file?.updateTime">最近修改：{{ formatDateTime(file.updateTime) }}</span>
            </div>
          </div>
          <div class="status-band__actions">
            <el-button plain :loading="capabilityLoading" @click="loadCapabilities(true)">刷新状态</el-button>
            <el-button type="primary" :disabled="!isSupportedFile" :loading="summaryLoading || tagsLoading" @click="loadInsights">
              读取 AI 结果
            </el-button>
            <el-button :disabled="!isSupportedFile" :loading="reindexLoading" @click="reindexDocument">
              重建索引
            </el-button>
          </div>
        </section>

        <el-alert
          v-if="capabilityError"
          type="warning"
          :closable="false"
          show-icon
          :title="capabilityError"
        />

        <el-alert
          v-if="!isSupportedFile && fileId"
          type="info"
          :closable="false"
          show-icon
          title="当前文件类型暂未纳入文档 AI 能力范围。建议使用 PDF、Word、Excel、PPT、TXT、CSV 或代码文本文件。"
        />

        <div class="content-grid">
          <section class="insight-panel">
            <div class="panel-head">
              <div class="panel-head__title">
                <el-icon><Document /></el-icon>
                <span>文档摘要</span>
              </div>
              <el-button text :disabled="summaryLoading || !fileId" @click="resetSummaryPrompt">
                默认摘要
              </el-button>
            </div>

            <div class="preset-row">
              <button
                v-for="preset in summaryPresets"
                :key="preset.label"
                class="preset-chip"
                type="button"
                @click="applySummaryPreset(preset.prompt)"
              >
                {{ preset.label }}
              </button>
            </div>

            <el-input
              v-model="summaryPrompt"
              type="textarea"
              :rows="4"
              resize="none"
              maxlength="300"
              show-word-limit
              placeholder="留空表示读取默认摘要；也可以补充你关心的视角，比如“请突出风险与行动项”。"
            />

            <div class="panel-toolbar">
              <el-button
                type="primary"
                :disabled="!isSupportedFile"
                :loading="summaryLoading"
                @click="loadSummary"
              >
                {{ summaryPrompt.trim() ? '按当前提示词生成' : '读取默认摘要' }}
              </el-button>
              <span class="panel-meta" v-if="summaryModel">
                {{ summaryModel }}<span v-if="summaryMocked"> · mock</span>
              </span>
            </div>

            <div class="panel-body">
              <el-skeleton v-if="summaryLoading" animated :rows="6" />
              <el-empty
                v-else-if="!summaryText && !summaryError"
                description="这里会展示文档的核心内容、关键信息和结论。"
              />
              <el-alert
                v-else-if="summaryError"
                type="error"
                :closable="false"
                show-icon
                :title="summaryError"
              />
              <div v-else class="summary-content">{{ summaryText }}</div>
            </div>
          </section>

          <section class="insight-panel">
            <div class="panel-head">
              <div class="panel-head__title">
                <el-icon><CollectionTag /></el-icon>
                <span>智能标签</span>
              </div>
              <div class="topk-group">
                <button
                  v-for="count in [5, 8, 12]"
                  :key="count"
                  class="topk-chip"
                  :class="{ 'is-active': tagTopK === count }"
                  type="button"
                  @click="tagTopK = count"
                >
                  {{ count }} 个
                </button>
              </div>
            </div>

            <div class="panel-toolbar">
              <el-button
                type="primary"
                plain
                :disabled="!isSupportedFile"
                :loading="tagsLoading"
                @click="loadTags"
              >
                读取标签
              </el-button>
              <span class="panel-meta" v-if="tagsModel">
                {{ tagsModel }}<span v-if="tagsMocked"> · mock</span>
              </span>
            </div>

            <div class="panel-body">
              <el-skeleton v-if="tagsLoading" animated :rows="4" />
              <el-empty
                v-else-if="!tagList.length && !tagsError"
                description="标签适合做分类、检索和列表卡片展示。"
              />
              <el-alert
                v-else-if="tagsError"
                type="error"
                :closable="false"
                show-icon
                :title="tagsError"
              />
              <div v-else class="tag-cloud">
                <span v-for="tag in tagList" :key="tag" class="tag-pill">{{ tag }}</span>
              </div>
            </div>
          </section>
        </div>

        <section class="qa-panel">
          <div class="panel-head">
            <div class="panel-head__title">
              <el-icon><ChatDotRound /></el-icon>
              <span>单文件问答</span>
            </div>
            <el-switch v-model="includeReferences" active-text="附带引用" />
          </div>

          <div class="preset-row">
            <button
              v-for="suggestion in questionSuggestions"
              :key="suggestion"
              class="preset-chip"
              type="button"
              @click="questionText = suggestion"
            >
              {{ suggestion }}
            </button>
          </div>

          <div class="qa-composer">
            <el-input
              v-model="questionText"
              type="textarea"
              resize="none"
              :rows="3"
              maxlength="500"
              show-word-limit
              placeholder="输入你想追问的问题，例如：这份文档的核心结论是什么？"
              @keyup.ctrl.enter="askQuestion"
            />
            <el-button
              type="primary"
              :disabled="!isSupportedFile || !questionText.trim()"
              :loading="qaLoading"
              @click="askQuestion"
            >
              提问
            </el-button>
          </div>

          <div class="qa-list">
            <el-empty v-if="!qaHistory.length && !qaLoading" description="先提一个问题，答案会结合文档内容返回。" />

            <article v-for="item in qaHistory" :key="item.id" class="qa-card">
              <div class="qa-card__meta">
                <span>{{ item.time }}</span>
                <span v-if="item.model">{{ item.model }}<span v-if="item.mocked"> · mock</span></span>
              </div>
              <div class="qa-card__question">{{ item.question }}</div>

              <el-skeleton v-if="item.loading" animated :rows="3" />
              <el-alert
                v-else-if="item.error"
                type="error"
                :closable="false"
                show-icon
                :title="item.error"
              />
              <template v-else>
                <div class="qa-card__answer">{{ item.answer }}</div>
                <div v-if="item.references?.length" class="reference-list">
                  <div class="reference-list__title">引用片段</div>
                  <div v-for="reference in item.references" :key="reference" class="reference-item">
                    {{ reference }}
                  </div>
                </div>
              </template>
            </article>
          </div>
        </section>
      </div>
    </el-scrollbar>
  </el-drawer>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { ChatDotRound, CollectionTag, Cpu, Document } from '@element-plus/icons-vue'
import {
  askSingleFileQuestion,
  generateFileTags,
  getAiCapabilities,
  indexAiFile,
  summarizeFile
} from '@/api/ai'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  file: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:modelValue'])

const SUPPORTED_FILE_TYPES = new Set([3, 4, 5, 6, 10, 11, 12])

const FILE_TYPE_LABELS = {
  1: '文件',
  2: '压缩包',
  3: 'Excel',
  4: 'Word',
  5: 'PDF',
  6: '文本',
  7: '图片',
  8: '音频',
  9: '视频',
  10: 'PPT',
  11: '代码',
  12: 'CSV'
}

const summaryPresets = [
  { label: '默认', prompt: '' },
  { label: '结构化重点', prompt: '请按主题、关键数据、核心结论、建议四部分输出结构化摘要。' },
  { label: '行动项', prompt: '请聚焦文档中的待办事项、负责人线索、时间节点和风险点。' },
  { label: '管理视角', prompt: '请从管理层视角概括背景、收益、风险和下一步建议。' }
]

const questionSuggestions = [
  '这份文档主要讲了什么？',
  '请提炼三个最重要的结论。',
  '这份文档有哪些风险、限制或待办事项？'
]

const capability = ref({})
const capabilityLoading = ref(false)
const capabilityError = ref('')

const summaryPrompt = ref('')
const summaryText = ref('')
const summaryModel = ref('')
const summaryMocked = ref(false)
const summaryLoading = ref(false)
const summaryError = ref('')

const tagTopK = ref(5)
const tagList = ref([])
const tagsModel = ref('')
const tagsMocked = ref(false)
const tagsLoading = ref(false)
const tagsError = ref('')

const questionText = ref('')
const includeReferences = ref(true)
const qaLoading = ref(false)
const qaHistory = ref([])

const reindexLoading = ref(false)
const autoLoadedFileKey = ref('')

const fileId = computed(() => props.file?.id || props.file?.fileId || props.file?.file_id || '')
const currentFileKey = computed(() => (fileId.value ? String(fileId.value) : ''))
const currentFileName = computed(() => props.file?.filename || '文档智能分析')
const currentFileSize = computed(() => props.file?.fileSizeDesc || '未知大小')
const currentFileTypeLabel = computed(() => FILE_TYPE_LABELS[Number(props.file?.fileType)] || '未知类型')
const isSupportedFile = computed(() => Boolean(fileId.value) && !props.file?.folderFlag && SUPPORTED_FILE_TYPES.has(Number(props.file?.fileType)))

watch(
  () => props.modelValue,
  (visible) => {
    if (visible) {
      handleDrawerOpen()
    }
  }
)

watch(currentFileKey, () => {
  resetDocumentState()
  if (props.modelValue) {
    handleDrawerOpen()
  }
})

function resetDocumentState() {
  summaryPrompt.value = ''
  summaryText.value = ''
  summaryModel.value = ''
  summaryMocked.value = false
  summaryLoading.value = false
  summaryError.value = ''

  tagTopK.value = 5
  tagList.value = []
  tagsModel.value = ''
  tagsMocked.value = false
  tagsLoading.value = false
  tagsError.value = ''

  questionText.value = ''
  includeReferences.value = true
  qaLoading.value = false
  qaHistory.value = []

  reindexLoading.value = false
  autoLoadedFileKey.value = ''
}

async function handleDrawerOpen() {
  if (!fileId.value) {
    return
  }
  void loadCapabilities()
  if (isSupportedFile.value && autoLoadedFileKey.value !== currentFileKey.value) {
    autoLoadedFileKey.value = currentFileKey.value
    void loadInsights()
  }
}

async function loadCapabilities(force = false) {
  if (capabilityLoading.value) {
    return
  }
  if (!force && capability.value?.chatModel) {
    return
  }

  capabilityLoading.value = true
  capabilityError.value = ''
  try {
    const response = await getAiCapabilities()
    capability.value = response?.data || {}
  } catch (error) {
    capabilityError.value = error?.message || 'AI 服务状态读取失败'
  } finally {
    capabilityLoading.value = false
  }
}

async function loadInsights() {
  await Promise.allSettled([loadSummary(), loadTags()])
}

function buildFilePayload() {
  return {
    fileId: String(fileId.value),
    filename: props.file?.filename || ''
  }
}

function applySummaryPreset(prompt) {
  summaryPrompt.value = prompt
}

function resetSummaryPrompt() {
  summaryPrompt.value = ''
}

async function loadSummary() {
  if (!isSupportedFile.value) {
    return
  }
  summaryLoading.value = true
  summaryError.value = ''
  try {
    const response = await summarizeFile({
      ...buildFilePayload(),
      prompt: summaryPrompt.value.trim() || undefined
    })
    const data = response?.data || {}
    summaryText.value = data.summary || ''
    summaryModel.value = data.model || ''
    summaryMocked.value = Boolean(data.mocked)
  } catch (error) {
    summaryError.value = error?.message || '摘要读取失败'
    summaryText.value = ''
  } finally {
    summaryLoading.value = false
  }
}

async function loadTags() {
  if (!isSupportedFile.value) {
    return
  }
  tagsLoading.value = true
  tagsError.value = ''
  try {
    const response = await generateFileTags({
      ...buildFilePayload(),
      topK: tagTopK.value
    })
    const data = response?.data || {}
    tagList.value = Array.isArray(data.tags) ? data.tags : []
    tagsModel.value = data.model || ''
    tagsMocked.value = Boolean(data.mocked)
  } catch (error) {
    tagsError.value = error?.message || '标签读取失败'
    tagList.value = []
  } finally {
    tagsLoading.value = false
  }
}

async function reindexDocument() {
  if (!isSupportedFile.value) {
    return
  }
  reindexLoading.value = true
  try {
    await indexAiFile({
      ...buildFilePayload(),
      forceReindex: true
    })
    ElMessage.success('文档索引已重建，正在刷新摘要与标签')
    await loadInsights()
  } catch (error) {
    ElMessage.error(error?.message || '重建索引失败')
  } finally {
    reindexLoading.value = false
  }
}

async function askQuestion() {
  const question = questionText.value.trim()
  if (!question || !isSupportedFile.value) {
    return
  }

  qaLoading.value = true
  const historyItem = {
    id: `${Date.now()}-${Math.random().toString(16).slice(2)}`,
    time: formatDateTime(new Date()),
    question,
    answer: '',
    references: [],
    model: '',
    mocked: false,
    loading: true,
    error: ''
  }
  qaHistory.value = [historyItem, ...qaHistory.value]
  questionText.value = ''

  try {
    const response = await askSingleFileQuestion({
      ...buildFilePayload(),
      question,
      includeReferences: includeReferences.value
    })
    const data = response?.data || {}
    historyItem.answer = data.answer || ''
    historyItem.references = Array.isArray(data.references) ? data.references : []
    historyItem.model = data.model || ''
    historyItem.mocked = Boolean(data.mocked)
  } catch (error) {
    historyItem.error = error?.message || '问答失败'
  } finally {
    historyItem.loading = false
    qaLoading.value = false
    qaHistory.value = [...qaHistory.value]
  }
}

function formatDateTime(value) {
  if (!value) {
    return ''
  }
  const date = value instanceof Date ? value : new Date(value)
  if (Number.isNaN(date.getTime())) {
    return ''
  }
  return date.toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<style scoped>
.drawer-scroll {
  height: 100%;
}

.ai-layout {
  display: flex;
  flex-direction: column;
  gap: 18px;
  padding-bottom: 24px;
}

.drawer-hero {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  width: 100%;
  padding: 18px 20px;
  border-radius: 22px;
  background:
    radial-gradient(circle at top left, rgba(43, 120, 228, 0.28), transparent 40%),
    linear-gradient(135deg, #0f172a, #1d4ed8 58%, #38bdf8);
  color: #fff;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.26);
}

.drawer-hero__content {
  min-width: 0;
}

.drawer-hero__eyebrow {
  margin-bottom: 8px;
  font-size: 12px;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  opacity: 0.82;
}

.drawer-hero__title {
  font-size: 24px;
  font-weight: 700;
  line-height: 1.3;
  word-break: break-word;
}

.drawer-hero__desc {
  margin-top: 10px;
  max-width: 520px;
  font-size: 13px;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.86);
}

.drawer-hero__meta {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 8px;
  max-width: 220px;
}

.status-band,
.insight-panel,
.qa-panel {
  border: 1px solid rgba(148, 163, 184, 0.18);
  border-radius: 22px;
  background: linear-gradient(180deg, #ffffff, #f8fbff);
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.06);
}

.status-band {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  padding: 18px 20px;
}

.status-band__title,
.panel-head__title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: 700;
  color: #0f172a;
}

.status-band__chips,
.status-band__actions,
.preset-row,
.topk-group,
.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.status-band__hint {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 12px;
  font-size: 13px;
  color: #64748b;
}

.status-band__actions {
  align-items: flex-start;
  justify-content: flex-end;
}

.content-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
}

.insight-panel,
.qa-panel {
  padding: 18px 20px 20px;
}

.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
}

.preset-row {
  margin-bottom: 14px;
}

.preset-chip,
.topk-chip {
  border: 0;
  border-radius: 999px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.preset-chip {
  padding: 8px 12px;
  background: #edf4ff;
  color: #1d4ed8;
}

.preset-chip:hover {
  background: #dbeafe;
  transform: translateY(-1px);
}

.topk-chip {
  padding: 6px 12px;
  background: #f1f5f9;
  color: #475569;
}

.topk-chip.is-active,
.topk-chip:hover {
  background: #dbeafe;
  color: #1d4ed8;
}

.panel-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin: 14px 0 16px;
}

.panel-meta {
  font-size: 12px;
  color: #64748b;
}

.panel-body {
  min-height: 180px;
}

.summary-content,
.qa-card__answer {
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.8;
  color: #1e293b;
}

.tag-cloud {
  padding-top: 6px;
}

.tag-pill {
  padding: 8px 14px;
  border-radius: 999px;
  background: linear-gradient(135deg, #eff6ff, #dbeafe);
  color: #1d4ed8;
  font-size: 13px;
  font-weight: 600;
}

.qa-composer {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 12px;
  margin-bottom: 18px;
}

.qa-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.qa-card {
  padding: 16px;
  border-radius: 18px;
  background:
    radial-gradient(circle at top right, rgba(56, 189, 248, 0.12), transparent 36%),
    #f8fafc;
  border: 1px solid rgba(148, 163, 184, 0.18);
}

.qa-card__meta {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
  font-size: 12px;
  color: #64748b;
}

.qa-card__question {
  margin-bottom: 12px;
  font-size: 15px;
  font-weight: 700;
  color: #0f172a;
}

.reference-list {
  margin-top: 14px;
  padding-top: 14px;
  border-top: 1px dashed rgba(148, 163, 184, 0.5);
}

.reference-list__title {
  margin-bottom: 10px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  color: #64748b;
  text-transform: uppercase;
}

.reference-item {
  padding: 10px 12px;
  border-radius: 12px;
  background: #fff;
  color: #334155;
  font-size: 13px;
  line-height: 1.6;
}

@media (max-width: 960px) {
  .content-grid {
    grid-template-columns: 1fr;
  }

  .status-band,
  .drawer-hero,
  .qa-composer {
    grid-template-columns: 1fr;
    flex-direction: column;
  }

  .status-band__actions {
    justify-content: flex-start;
  }

  .drawer-hero__meta {
    justify-content: flex-start;
    max-width: none;
  }

  .qa-composer {
    display: flex;
    flex-direction: column;
  }
}
</style>
