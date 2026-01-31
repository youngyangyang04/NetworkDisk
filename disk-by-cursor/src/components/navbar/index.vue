<template>
  <div class="pan-nav-content-wrapper" :class="{ 'collapsed': isCollapsed }">
    <div class="pan-nav-content">
      <ul class="pan-nav-home">
        <li class="pan-nav-home-li">
          <a @click="handleChange('Home')" :class="{'checked': active === 'Home'}" href="javascript:void(0);">
            <span class="text">
              <el-icon class="icon"><House /></el-icon>
              <span v-show="!isCollapsed">首页</span>
            </span>
          </a>
        </li>
      </ul>
      <ul class="pan-nav-file">
        <li class="pan-nav-file-all">
          <a @click="handleChange('Files')" :class="{'checked': active === 'Files'}" href="javascript:void(0);">
            <span class="text">
              <el-icon class="icon"><Folder /></el-icon>
              <span v-show="!isCollapsed">全部文件</span>
            </span>
          </a>
        </li>
        <ul class="sub-menu" v-show="!isCollapsed">
          <li class="pan-nav-file-pic">
            <a @click="handleChange('Imgs')" :class="{'checked': active === 'Imgs'}" href="javascript:void(0);">
              <span class="text">
                <el-icon class="icon"><Picture /></el-icon>
                <span>图片</span>
              </span>
            </a>
          </li>
          <li class="pan-nav-file-doc">
            <a @click="handleChange('Docs')" :class="{'checked': active === 'Docs'}" href="javascript:void(0);">
              <span class="text">
                <el-icon class="icon"><Document /></el-icon>
                <span>文档</span>
              </span>
            </a>
          </li>
          <li class="pan-nav-file-video">
            <a @click="handleChange('Videos')" :class="{'checked': active === 'Videos'}" href="javascript:void(0);">
              <span class="text">
                <el-icon class="icon"><VideoPlay /></el-icon>
                <span>视频</span>
              </span>
            </a>
          </li>
          <li class="pan-nav-file-music">
            <a @click="handleChange('Musics')" :class="{'checked': active === 'Musics'}" href="javascript:void(0);">
              <span class="text">
                <el-icon class="icon"><Headset /></el-icon>
                <span>音乐</span>
              </span>
            </a>
          </li>
        </ul>
      </ul>
      <ul class="pan-nav-share">
        <li class="pan-nav-share-li">
          <a @click="handleChange('Shares')" :class="{'checked': active === 'Shares'}" href="javascript:void(0);">
            <span class="text">
              <el-icon class="icon"><Share /></el-icon>
              <span v-show="!isCollapsed">我的分享</span>
            </span>
          </a>
        </li>
      </ul>
      <ul class="pan-nav-recycle">
        <li class="pan-nav-recycle-li">
          <a @click="handleChange('Recycle')" :class="{'checked': active === 'Recycle'}" href="javascript:void(0);">
            <span class="text">
              <el-icon class="icon"><Delete /></el-icon>
              <span v-show="!isCollapsed">回收站</span>
            </span>
          </a>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { storeToRefs } from 'pinia'
import { useNavbarStore } from '@/stores/navbar'
import { useRoute, useRouter } from 'vue-router'
import { onMounted, watch } from 'vue'
import {
  House,
  Folder,
  Picture,
  Document,
  VideoPlay,
  Headset,
  Share,
  Delete
} from '@element-plus/icons-vue'

const props = defineProps({
  isCollapsed: {
    type: Boolean,
    default: false
  }
})

const store = useNavbarStore()
const route = useRoute()
const router = useRouter()

const { active } = storeToRefs(store)
const { change } = store

const handleChange = (name) => {
  change(name)
  const current = router.currentRoute.value
  switch (name) {
    case 'Home':
      if (current.name !== 'Home') router.push({ name: 'Home' })
      break
    case 'Files':
      if (current.name !== 'Files') router.push({ name: 'Files' })
      break
    case 'Imgs':
      if (!(current.name === 'Files' && current.query.type === 'image')) {
        router.push({ name: 'Files', query: { type: 'image' } })
      }
      break
    case 'Docs':
      if (!(current.name === 'Files' && current.query.type === 'document')) {
        router.push({ name: 'Files', query: { type: 'document' } })
      }
      break
    case 'Videos':
      if (!(current.name === 'Files' && current.query.type === 'video')) {
        router.push({ name: 'Files', query: { type: 'video' } })
      }
      break
    case 'Musics':
      if (!(current.name === 'Files' && current.query.type === 'music')) {
        router.push({ name: 'Files', query: { type: 'music' } })
      }
      break
    case 'Shares':
      if (current.name !== 'Shares') router.push({ name: 'Shares' })
      break
    case 'Recycle':
      if (current.name !== 'Recycle') router.push({ name: 'Recycle' })
      break
  }
}

// 根据路由名称更新navbar状态
const updateNavbarByRoute = () => {
  let name = route.name
  change(name)
}

// 监听路由变化
watch(() => route.name, () => {
  updateNavbarByRoute()
}, { immediate: false })

onMounted(() => {
  updateNavbarByRoute()
})
</script>

<style scoped>
.checked {
  background: rgba(102, 126, 234, 0.1);
}

.checked span {
  color: #667eea !important;
}

.checked .icon {
  color: #667eea !important;
}

ul {
  list-style: none;
  padding-inline-start: 0;
  margin: 0;
}

li {
  display: list-item;
  text-align: -webkit-match-parent;
  list-style: none;
}

.pan-nav-content-wrapper {
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  width: 160px;
  height: fit-content;
  padding: 10px 0;
  flex-shrink: 0;
  transition: all 0.3s ease;
}

.pan-nav-content-wrapper.collapsed {
  width: 50px;
  padding: 10px 0;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .pan-nav-content-wrapper {
    width: 140px;
    padding: 6px 0;
  }
}

@media (max-width: 992px) {
  .pan-nav-content-wrapper {
    width: 120px;
    padding: 4px 0;
  }
}

.pan-nav-content ul a {
  height: 40px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  text-decoration: none;
  transition: all 0.3s ease;
}

.pan-nav-content ul a:hover {
  background: rgba(102, 126, 234, 0.05);
}

.pan-nav-content ul a:hover span {
  color: #667eea;
}

.pan-nav-content ul a:hover .icon {
  color: #667eea;
}

.pan-nav-content ul .text {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #333;
  font-size: 14px;
  font-weight: 500;
}

.pan-nav-content ul .icon {
  font-size: 18px;
  color: #666;
  transition: all 0.3s ease;
}

.pan-nav-home {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.pan-nav-share, .pan-nav-recycle {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
}

.sub-menu {
  margin-left: 20px !important;
  position: relative;
}

.sub-menu::before {
  content: '';
  position: absolute;
  left: 8px;
  top: 0;
  bottom: 0;
  width: 1px;
  background: rgba(102, 126, 234, 0.1);
}

.sub-menu li a {
  position: relative;
  padding-left: 28px !important;
}

.sub-menu li a::before {
  content: '';
  position: absolute;
  left: 8px;
  top: 50%;
  width: 12px;
  height: 1px;
  background: rgba(102, 126, 234, 0.1);
}

.sub-menu li a:hover::before {
  background: rgba(102, 126, 234, 0.3);
}

.sub-menu li a.checked::before {
  background: rgba(102, 126, 234, 0.3);
}

.pan-nav-file-all {
  margin-bottom: 4px;
}

.pan-nav-content ul .text {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #333;
  font-size: 14px;
  font-weight: 500;
}

.pan-nav-content ul.sub-menu .text {
  font-weight: 400;
  color: #666;
}

.pan-nav-content ul.sub-menu .icon {
  font-size: 16px;
}
</style>
