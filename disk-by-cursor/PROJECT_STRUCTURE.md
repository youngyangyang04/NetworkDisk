# 项目结构说明

```
disk-by-cursor/
├── src/                          # 源代码目录
│   ├── api/                      # API 接口
│   │   ├── file.js              # 文件相关 API
│   │   ├── share.js             # 分享相关 API
│   │   ├── user.js              # 用户相关 API
│   │   └── recycle.js           # 回收站相关 API
│   ├── components/               # 公共组件
│   │   └── UploadDialog.vue     # 上传对话框组件
│   ├── router/                   # 路由配置
│   │   └── index.js             # 路由定义
│   ├── stores/                   # 状态管理
│   │   └── user.js              # 用户状态管理
│   ├── utils/                    # 工具函数
│   │   ├── request.js           # HTTP 请求封装
│   │   └── upload.js            # 分片上传工具
│   ├── views/                    # 页面组件
│   │   ├── Login.vue            # 登录页面
│   │   ├── Register.vue         # 注册页面
│   │   ├── Layout.vue           # 主布局
│   │   ├── Home.vue             # 首页
│   │   ├── Files.vue            # 文件管理
│   │   ├── Shares.vue           # 分享管理
│   │   └── Recycle.vue          # 回收站
│   ├── App.vue                   # 根组件
│   └── main.js                   # 入口文件
├── public/                       # 静态资源
├── index.html                    # HTML 模板
├── package.json                  # 项目配置
├── vite.config.js               # Vite 配置
├── README.md                     # 项目说明
├── PROJECT_STRUCTURE.md          # 项目结构说明
├── start.sh                      # Linux/Mac 启动脚本
├── start.bat                     # Windows 启动脚本
└── .gitignore                    # Git 忽略文件
```

## 核心文件说明

### 1. 入口文件
- `main.js` - Vue 应用入口，注册全局组件和插件
- `App.vue` - 根组件，包含路由视图

### 2. 路由配置
- `router/index.js` - 定义应用路由，包含路由守卫

### 3. 状态管理
- `stores/user.js` - 用户状态管理，包含登录状态、用户信息等

### 4. API 接口
- `api/user.js` - 用户相关接口（登录、注册、获取用户信息等）
- `api/file.js` - 文件相关接口（上传、下载、列表、搜索等）
- `api/share.js` - 分享相关接口（创建分享、分享列表等）
- `api/recycle.js` - 回收站相关接口（还原、彻底删除等）

### 5. 工具函数
- `utils/request.js` - HTTP 请求封装，包含拦截器
- `utils/upload.js` - 分片上传工具类，支持断点续传

### 6. 页面组件
- `views/Login.vue` - 登录页面
- `views/Register.vue` - 注册页面
- `views/Layout.vue` - 主布局，包含导航菜单
- `views/Home.vue` - 首页，显示统计信息和最近文件
- `views/Files.vue` - 文件管理页面，支持文件操作
- `views/Shares.vue` - 分享管理页面
- `views/Recycle.vue` - 回收站页面

### 7. 公共组件
- `components/UploadDialog.vue` - 上传对话框组件，支持分片上传

## 技术栈

- **Vue 3** - 使用 Composition API
- **Vue Router 4** - 路由管理
- **Pinia** - 状态管理
- **Element Plus** - UI 组件库
- **Axios** - HTTP 客户端
- **Vite** - 构建工具

## 开发规范

1. **组件命名**: 使用 PascalCase
2. **文件命名**: 使用 kebab-case
3. **API 接口**: 按功能模块分类
4. **状态管理**: 使用 Pinia 进行集中管理
5. **路由配置**: 使用懒加载优化性能

## 启动方式

### 方式一：使用启动脚本
```bash
# Linux/Mac
./start.sh

# Windows
start.bat
```

### 方式二：手动启动
```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

## 构建部署

```bash
# 构建生产版本
npm run build

# 预览生产版本
npm run preview
``` 