import { chunkUpload, getUploadedChunks, mergeChunks } from '@/api/file'
import { ElMessage } from 'element-plus'

export class ChunkUploader {
  constructor(options = {}) {
    this.chunkSize = options.chunkSize || 2 * 1024 * 1024 // 2MB
    this.maxConcurrent = options.maxConcurrent || 3
    this.onProgress = options.onProgress || (() => {})
    this.onSuccess = options.onSuccess || (() => {})
    this.onError = options.onError || (() => {})
  }

  async uploadFile(file, parentId) {
    try {
      // 生成文件唯一标识
      const identifier = this.generateFileIdentifier(file)
      console.log('[ChunkUploader] uploadFile: identifier', identifier, 'file:', file.name);
      
      // 检查是否已经上传过
      const uploadedChunks = await this.getUploadedChunks(identifier)
      console.log('[ChunkUploader] uploadedChunks:', uploadedChunks);
      
      // 计算分片信息
      const totalChunks = Math.ceil(file.size / this.chunkSize)
      const chunks = this.createChunks(file, totalChunks)
      console.log('[ChunkUploader] totalChunks:', totalChunks, 'chunks:', chunks.length);
      
      // 过滤已上传的分片
      const remainingChunks = chunks.filter(chunk => 
        !uploadedChunks.includes(chunk.chunkNumber)
      )
      console.log('[ChunkUploader] remainingChunks:', remainingChunks.length);
      
      if (remainingChunks.length === 0) {
        // 所有分片都已上传，直接合并
        console.log('[ChunkUploader] all chunks uploaded, merging...');
        await this.mergeFile(file, identifier, parentId)
        this.onSuccess(file)
        return
      }
      
      // 上传剩余分片
      await this.uploadChunks(remainingChunks, identifier, file)
      
      // 合并文件
      await this.mergeFile(file, identifier, parentId)
      
      this.onSuccess(file)
    } catch (error) {
      console.error('[ChunkUploader] 分片上传失败:', error)
      this.onError(error)
    }
  }

  generateFileIdentifier(file) {
    // 使用文件名、大小、修改时间生成唯一标识
    const str = `${file.name}-${file.size}-${file.lastModified}`
    let hash = 0
    for (let i = 0; i < str.length; i++) {
      const char = str.charCodeAt(i)
      hash = ((hash << 5) - hash) + char
      hash = hash & hash // 转换为32位整数
    }
    return hash.toString(36)
  }

  createChunks(file, totalChunks) {
    const chunks = []
    for (let i = 0; i < totalChunks; i++) {
      const start = i * this.chunkSize
      const end = Math.min(start + this.chunkSize, file.size)
      const chunk = file.slice(start, end)
      
      chunks.push({
        chunkNumber: i + 1,
        chunk: chunk,
        size: chunk.size
      })
    }
    return chunks
  }

  async getUploadedChunks(identifier) {
    try {
      const response = await getUploadedChunks({ identifier })
      return response.data?.uploadedChunks || []
    } catch (error) {
      console.error('获取已上传分片失败:', error)
      return []
    }
  }

  async uploadChunks(chunks, identifier, file) {
    const uploadPromises = []
    let completedChunks = 0
    const totalChunks = chunks.length

    for (let i = 0; i < chunks.length; i += this.maxConcurrent) {
      const batch = chunks.slice(i, i + this.maxConcurrent)
      const batchPromises = batch.map(chunk => 
        this.uploadChunk(chunk, identifier, file)
          .then(() => {
            completedChunks++
            this.onProgress({
              loaded: completedChunks,
              total: totalChunks,
              percent: Math.round((completedChunks / totalChunks) * 100)
            })
          })
      )
      
      await Promise.all(batchPromises)
    }
  }

  async uploadChunk(chunk, identifier, file) {
    const formData = new FormData()
    formData.append('file', chunk.chunk)
    formData.append('filename', file.name)
    formData.append('identifier', identifier)
    formData.append('totalChunks', Math.ceil(file.size / this.chunkSize))
    formData.append('chunkNumber', chunk.chunkNumber)
    formData.append('currentChunkSize', chunk.size)
    formData.append('totalSize', file.size)
    try {
      console.log('[ChunkUploader] uploadChunk:', chunk.chunkNumber, '/', Math.ceil(file.size / this.chunkSize), file.name);
      const response = await chunkUpload(formData)
      if (response.code !== 'SUCCESS') {
        throw new Error(response.message || '分片上传失败')
      }
    } catch (error) {
      console.error('[ChunkUploader] uploadChunk error:', error)
      throw new Error(`分片 ${chunk.chunkNumber} 上传失败: ${error.message}`)
    }
  }

  async mergeFile(file, identifier, parentId) {
    try {
      const data = {
        filename: file.name,
        identifier: identifier,
        parentId: parentId,
        totalSize: file.size
      }
      console.log('[ChunkUploader] mergeFile:', data);
      const response = await mergeChunks(data)
      if (response.code !== 200) {
        throw new Error(response.message || '文件合并失败')
      }
    } catch (error) {
      console.error('[ChunkUploader] mergeFile error:', error)
      throw new Error(`文件合并失败: ${error.message}`)
    }
  }
}

// 文件上传管理器
export class FileUploadManager {
  constructor() {
    this.uploadQueue = []
    this.isUploading = false
  }

  addToQueue(file, parentId, options = {}) {
    const uploader = new ChunkUploader({
      chunkSize: options.chunkSize || 2 * 1024 * 1024,
      maxConcurrent: options.maxConcurrent || 3,
      onProgress: options.onProgress || (() => {}),
      onSuccess: options.onSuccess || (() => {}),
      onError: options.onError || (() => {})
    })

    this.uploadQueue.push({
      file,
      parentId,
      uploader,
      options
    })

    this.processQueue()
  }

  async processQueue() {
    if (this.isUploading || this.uploadQueue.length === 0) {
      console.log('[uploadManager] processQueue: skip, isUploading:', this.isUploading, 'queue.length:', this.uploadQueue.length);
      return;
    }
    console.log('[uploadManager] processQueue start, queue:', this.uploadQueue);
    this.isUploading = true;
    while (this.uploadQueue.length > 0) {
      const item = this.uploadQueue.shift();
      console.log('[uploadManager] processQueue: uploading', item.file.name);
      try {
        await item.uploader.uploadFile(item.file, item.parentId);
        ElMessage.success(`${item.file.name} 上传成功`);
      } catch (error) {
        console.error('[uploadManager] processQueue: upload error', error);
        ElMessage.error(`${item.file.name} 上传失败: ${error.message}`);
      }
    }
    this.isUploading = false;
    console.log('[uploadManager] processQueue end');
  }

  clearQueue() {
    this.uploadQueue = []
  }

  getQueueLength() {
    return this.uploadQueue.length
  }
}

// 导出单例实例
export const uploadManager = new FileUploadManager() 