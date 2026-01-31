#!/bin/bash

echo "🚀 启动网盘系统前端项目..."

# 检查 Node.js 是否安装
if ! command -v node &> /dev/null; then
    echo "❌ 错误: 未找到 Node.js，请先安装 Node.js"
    exit 1
fi

# 检查 npm 是否安装
if ! command -v npm &> /dev/null; then
    echo "❌ 错误: 未找到 npm，请先安装 npm"
    exit 1
fi

echo "📦 安装依赖..."
npm install

if [ $? -ne 0 ]; then
    echo "❌ 依赖安装失败"
    exit 1
fi

echo "✅ 依赖安装完成"

echo "🌐 启动开发服务器..."
echo "📝 提示: 项目将在 http://localhost:3000 启动"
echo "📝 提示: API 代理到 http://localhost:8082"
echo "📝 提示: 按 Ctrl+C 停止服务器"

npm run dev 