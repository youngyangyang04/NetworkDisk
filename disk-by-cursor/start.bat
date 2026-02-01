@echo off
chcp 65001 >nul

echo 🚀 启动网盘系统前端项目...

REM 检查 Node.js 是否安装
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 错误: 未找到 Node.js，请先安装 Node.js
    pause
    exit /b 1
)

REM 检查 npm 是否安装
npm --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 错误: 未找到 npm，请先安装 npm
    pause
    exit /b 1
)

echo 📦 安装依赖...
npm install

if %errorlevel% neq 0 (
    echo ❌ 依赖安装失败
    pause
    exit /b 1
)

echo ✅ 依赖安装完成

echo 🌐 启动开发服务器...
echo 📝 提示: 项目将在 http://localhost:3000 启动
echo 📝 提示: API 代理到 http://localhost:8082
echo 📝 提示: 按 Ctrl+C 停止服务器

npm run dev

pause 