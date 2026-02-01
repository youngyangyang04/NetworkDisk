import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getFileList, getBreadcrumbs } from '@/api/file'
import { useUserStore } from './user'

export const useFileStore = defineStore('file', () => {
  const parentId = ref('')
  const currentFolderId = ref('') // 当前文件夹ID，用于上传
  const fileTypes = ref('-1')
  const files = ref([])
  const breadcrumbs = ref([])

  // 支持传fileTypes参数
  async function loadFiles(newParentId, newFileTypes) {
    if (typeof newFileTypes !== 'undefined') fileTypes.value = newFileTypes
    if (typeof newParentId !== 'undefined') {
      parentId.value = newParentId
      currentFolderId.value = newParentId // 更新当前文件夹ID
    }
    console.log('[fileStore] loadFiles 调用', { parentId: parentId.value, fileTypes: fileTypes.value })
    
    try {
      const res = await getFileList({ parentId: parentId.value, fileTypes: fileTypes.value })
      console.log('[fileStore] 文件列表API响应:', res)
      files.value = res.data || []
      
      const bcRes = await getBreadcrumbs({ fileId: parentId.value })
      console.log('[fileStore] 面包屑API响应:', bcRes)
      breadcrumbs.value = bcRes.data || []
      
      console.log('[fileStore] 加载完成，文件数量:', files.value.length, '面包屑数量:', breadcrumbs.value.length)
    } catch (error) {
      console.error('[fileStore] loadFiles 失败:', error)
      throw error
    }
  }

  // 初始化（登录后调用）
  async function initRoot() {
    const userStore = useUserStore()
    if (userStore.rootFileId) {
      parentId.value = userStore.rootFileId
      currentFolderId.value = userStore.rootFileId // 设置初始文件夹ID
      fileTypes.value = '-1'
      await loadFiles(userStore.rootFileId, '-1')
    }
  }

  // 刷新文件列表（上传完成后调用）
  async function loadFileList() {
    await loadFiles(currentFolderId.value, fileTypes.value)
  }

  return { 
    parentId, 
    currentFolderId, 
    fileTypes, 
    files, 
    breadcrumbs, 
    loadFiles, 
    initRoot,
    loadFileList
  }
})