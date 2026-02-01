<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <div class="logo">
          <el-icon class="logo-icon"><Folder /></el-icon>
        </div>
        <h2>卡码网盘</h2>
        <p>安全、便捷的云端存储解决方案</p>
      </div>
      
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="email">
          <el-input
            v-model="loginForm.email"
            placeholder="请输入邮箱"
            prefix-icon="User"
            size="large"
            class="custom-input"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
            class="custom-input"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            <el-icon v-if="!loading"><Right /></el-icon>
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>
        
        <div class="login-footer">
          <el-link type="primary" @click="$router.push('/register')" class="register-link">
            <el-icon><UserFilled /></el-icon>
            还没有账号？立即注册
          </el-link>
        </div>
      </el-form>
    </div>
    
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { useFileStore } from '@/stores/file'

const router = useRouter()
const userStore = useUserStore()
const fileStore = useFileStore()

const loginFormRef = ref()
const loading = ref(false)

const loginForm = reactive({
  email: '',
  password: ''
})

const loginRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    await loginFormRef.value.validate()
    loading.value = true
    const result = await userStore.loginAction(loginForm)
    if (result.success) {
      ElMessage.success('登录成功')
      fileStore.initRoot()
      // 等待一下确保token完全设置后再跳转
      await new Promise(resolve => setTimeout(resolve, 200))
      router.push('/')
    } else {
      ElMessage.error(result.message || '登录失败')
    }
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--primary-gradient);
  position: relative;
  overflow: hidden;
}

.login-box {
  width: 420px;
  padding: 48px;
  background: var(--bg-secondary);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-heavy);
  position: relative;
  z-index: 10;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: var(--spacing-xl);
}

.logo {
  margin-bottom: var(--spacing-md);
}

.logo-icon {
  font-size: 48px;
  color: var(--primary-color);
  background: var(--primary-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.login-header h2 {
  color: var(--text-primary);
  margin-bottom: 8px;
  font-size: 28px;
  font-weight: 600;
}

.login-header p {
  color: var(--text-secondary);
  font-size: 14px;
  line-height: 1.5;
}

.login-form {
  margin-top: 24px;
}

.custom-input :deep(.el-input__wrapper) {
  border-radius: var(--border-radius);
  box-shadow: 0 0 0 1px var(--border-color);
  transition: all 0.3s ease;
}

.custom-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--primary-color);
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px var(--primary-color);
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: var(--border-radius);
  background: var(--primary-gradient);
  border: none;
  transition: all 0.3s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-medium);
  background: var(--primary-hover);
}

.login-btn:active {
  transform: translateY(0);
}

.login-footer {
  text-align: center;
  margin-top: 24px;
}

.register-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.register-link:hover {
  transform: translateY(-1px);
}

/* 背景装饰 */
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.circle-1 {
  width: 200px;
  height: 200px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.circle-2 {
  width: 150px;
  height: 150px;
  top: 60%;
  right: 15%;
  animation-delay: 2s;
}

.circle-3 {
  width: 100px;
  height: 100px;
  bottom: 20%;
  left: 20%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-box {
    width: 90%;
    padding: 32px;
  }
  
  .login-header h2 {
    font-size: 24px;
  }
}
</style> 