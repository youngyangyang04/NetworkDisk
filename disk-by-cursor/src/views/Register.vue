<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <div class="logo">
          <el-icon class="logo-icon"><Folder /></el-icon>
        </div>
        <h2>用户注册</h2>
        <p>创建您的卡码网盘账号，开启云端存储之旅</p>
      </div>
      
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        @submit.prevent="handleRegister"
      >
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
            class="custom-input"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
            class="custom-input"
          />
        </el-form-item>
        
        <el-form-item prop="question">
          <el-select
            v-model="registerForm.question"
            placeholder="请选择密保问题"
            size="large"
            style="width: 100%"
            class="custom-select"
          >
            <el-option label="您的出生地是？" value="您的出生地是？" />
            <el-option label="您最喜欢的颜色是？" value="您最喜欢的颜色是？" />
            <el-option label="您的宠物名字是？" value="您的宠物名字是？" />
            <el-option label="您的母校是？" value="您的母校是？" />
            <el-option label="您最喜欢的电影是？" value="您最喜欢的电影是？" />
          </el-select>
        </el-form-item>
        
        <el-form-item prop="answer">
          <el-input
            v-model="registerForm.answer"
            placeholder="请输入密保答案"
            prefix-icon="Key"
            size="large"
            class="custom-input"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="register-btn"
            :loading="loading"
            @click="handleRegister"
          >
            <el-icon v-if="!loading"><UserFilled /></el-icon>
            {{ loading ? '注册中...' : '注册' }}
          </el-button>
        </el-form-item>
        
        <div class="register-footer">
          <el-link type="primary" @click="$router.push('/login')" class="login-link">
            <el-icon><Right /></el-icon>
            已有账号？立即登录
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

const router = useRouter()
const userStore = useUserStore()

const registerFormRef = ref()
const loading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  question: '',
  answer: ''
})

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, message: '用户名长度不能少于3位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  question: [
    { required: true, message: '请选择密保问题', trigger: 'change' }
  ],
  answer: [
    { required: true, message: '请输入密保答案', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  try {
    await registerFormRef.value.validate()
    loading.value = true
    
    const result = await userStore.registerAction(registerForm)
    
    if (result.success) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } else {
      ElMessage.error(result.message || '注册失败')
    }
  } catch (error) {
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--primary-gradient);
  position: relative;
  overflow: hidden;
}

.register-box {
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

.register-header {
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

.register-header h2 {
  color: var(--text-primary);
  margin-bottom: 8px;
  font-size: 28px;
  font-weight: 600;
}

.register-header p {
  color: var(--text-secondary);
  font-size: 14px;
  line-height: 1.5;
}

.register-form {
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

.custom-select :deep(.el-input__wrapper) {
  border-radius: var(--border-radius);
  box-shadow: 0 0 0 1px var(--border-color);
  transition: all 0.3s ease;
}

.custom-select :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--primary-color);
}

.custom-select :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px var(--primary-color);
}

.register-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: var(--border-radius);
  background: var(--primary-gradient);
  border: none;
  transition: all 0.3s ease;
}

.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-medium);
  background: var(--primary-hover);
}

.register-btn:active {
  transform: translateY(0);
}

.register-footer {
  text-align: center;
  margin-top: 24px;
}

.login-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.login-link:hover {
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
  .register-box {
    width: 90%;
    padding: 32px;
  }
  
  .register-header h2 {
    font-size: 24px;
  }
}
</style> 