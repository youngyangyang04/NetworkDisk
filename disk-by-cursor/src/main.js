import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import './styles/theme.css'
import { useUserStore } from '@/stores/user'

const app = createApp(App)
const pinia = createPinia()
app.use(pinia)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 初始化用户信息，保证 token 和登录状态同步
const userStore = useUserStore()
await userStore.initUserInfo()

app.use(router)
app.use(ElementPlus)

app.mount('#app') 