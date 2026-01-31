<template>
  <div class="pan-user-info-content">
    <el-dropdown @command="handleCommand">
      <span class="user-info">
        <el-avatar 
          :size="36" 
          :src="userStore.userInfo.profilePhotoUrl || userStore.userInfo.avatar"
          :icon="!userStore.userInfo.profilePhotoUrl && !userStore.userInfo.avatar ? 'UserFilled' : undefined"
          class="user-avatar"
        />
        <span class="username">{{ userStore.userInfo.nickname || userStore.userInfo.username || '用户' }}</span>
        <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="profile">
            <el-icon><User /></el-icon>
            个人资料
          </el-dropdown-item>
          <el-dropdown-item command="avatar">
            <el-icon><Picture /></el-icon>
            更换头像
          </el-dropdown-item>
          <el-dropdown-item divided command="logout">
            <el-icon><SwitchButton /></el-icon>
            退出登录
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup>
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { ArrowDown, User, Picture, SwitchButton } from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()

const handleCommand = async (command) => {
  switch (command) {
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        userStore.logout()
        router.push('/login')
      } catch {
        // 用户取消
      }
      break
      
    case 'profile':
      ElMessage.info('个人资料功能开发中...')
      break
      
    case 'avatar':
      ElMessage.info('更换头像功能开发中...')
      break
  }
}
</script>

<style scoped>
.pan-user-info-content {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
  background: transparent;
  border: 1px solid transparent;
  gap: 8px;
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-color: rgba(255, 255, 255, 0.3);
}

.user-avatar {
  border: 2px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.user-info:hover .user-avatar {
  border-color: #fff;
  transform: scale(1.05);
}

.username {
  color: #fff;
  font-weight: 500;
  font-size: 14px;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  opacity: 0.9;
}

.dropdown-icon {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  transition: all 0.3s ease;
}

.user-info:hover .dropdown-icon {
  color: #fff;
  transform: rotate(180deg);
  opacity: 1;
}
</style>

