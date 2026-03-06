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

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

const userStore = useUserStore()

async function bootstrap() {
  await userStore.initUserInfo()
  app.use(router)
  app.use(ElementPlus)
  app.mount('#app')
}

bootstrap()
