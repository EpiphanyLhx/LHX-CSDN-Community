<template>
  <div class="home-container">
    <header class="navbar">
      <div class="nav-content">
        <div class="brand">
          <span class="logo-icon">♈️</span>
          <span class="logo-text">LFJ CSDN社区</span>
        </div>

        <div class="search-box">
          <el-input
              v-model="searchQuery"
              placeholder="搜索文章、问答或技术标签..."
              :prefix-icon="Search"
              clearable
              class="custom-search"
          />
        </div>

        <div class="user-actions">
          <el-button type="primary" class="publish-btn" @click="router.push('/publish')">
            <el-icon><EditPen /></el-icon> 发布文章
          </el-button>

          <el-dropdown @command="handleCommand" trigger="click">
            <span class="avatar-wrapper">
              <el-avatar :size="38" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon> 个人中心
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided style="color: #f56c6c;">
                  <el-icon><SwitchButton /></el-icon> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <main class="main-content">
      <el-row :gutter="24">
        <el-col :span="18">
          <div class="article-feed">
            <el-card v-for="i in 5" :key="i" class="article-card" shadow="hover">
              <div class="article-content">
                <h3 class="title" @click="goDetail(i)">Vue3 + SpringBoot3 打造高性能 LFJ 社区系统</h3>
                <p class="summary">本文详细记录了从零开始搭建 LFJ CSDN 社区的全过程，涵盖了 JWT 鉴权、Vue Router 动态路由、Axios 拦截器以及 MySQL 数据库设计等核心技术栈，干货满满...</p>
                <div class="meta-info">
                  <div class="left-meta">
                    <span class="author"><el-icon><User /></el-icon> 极客开发者</span>
                    <span class="time"><el-icon><Clock /></el-icon> 2小时前</span>
                    <span class="tags">
                      <el-tag size="small" type="primary" effect="light">Vue.js</el-tag>
                      <el-tag size="small" type="success" effect="light">Spring Boot</el-tag>
                    </span>
                  </div>
                  <div class="right-meta">
                    <span class="stat-item"><el-icon><View /></el-icon> 1.2k</span>
                    <span class="stat-item"><el-icon><ChatDotRound /></el-icon> 38</span>
                  </div>
                </div>
              </div>
            </el-card>
          </div>
        </el-col>

        <el-col :span="6">
          <div class="sidebar">
            <el-card class="sidebar-card creator-card" shadow="never">
              <div class="greeting">下午好，创造者！</div>
              <p class="sub-greeting">今天想分享点什么硬核技术？</p>
              <el-button type="primary" class="start-write-btn" @click="router.push('/publish')">开始写作</el-button>
            </el-card>

            <el-card class="sidebar-card tags-card" shadow="never">
              <template #header>
                <div class="card-header">
                  <span class="header-title">🔥 热门技术标签</span>
                </div>
              </template>
              <div class="tag-cloud">
                <el-tag class="cloud-tag" type="info">Java 17</el-tag>
                <el-tag class="cloud-tag" type="info">Redis 缓存</el-tag>
                <el-tag class="cloud-tag" type="info">微服务架构</el-tag>
                <el-tag class="cloud-tag" type="info">Element Plus</el-tag>
                <el-tag class="cloud-tag" type="info">MySQL 优化</el-tag>
              </div>
            </el-card>
          </div>
        </el-col>
      </el-row>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Search, EditPen, User, SwitchButton,
  Clock, View, ChatDotRound
} from '@element-plus/icons-vue'

const router = useRouter()
const searchQuery = ref('')

// 点击文章标题跳转详情页 (暂时预留)
const goDetail = (id) => {
  ElMessage.success('准备跳转到文章详情页：' + id)
  router.push(`/article/${id}`)
}

// 处理右上角下拉菜单点击
const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/user')
  } else if (command === 'logout') {
    localStorage.clear()
    ElMessage.success('已安全退出 LFJ CSDN社区')
    router.push('/login')
  }
}
</script>

<style scoped>
/* 整体页面背景 */
.home-container { background-color: #f2f3f5; min-height: 100vh; padding-bottom: 40px; }

/* --- 1. 顶部导航栏 --- */
.navbar {
  background-color: #ffffff;
  height: 64px;
  display: flex;
  align-items: center;
  box-shadow: 0 1px 4px rgba(0,21,41,0.08);
  position: sticky;
  top: 0;
  z-index: 100;
}
.nav-content {
  width: 100%; max-width: 1200px; margin: 0 auto; padding: 0 24px;
  display: flex; justify-content: space-between; align-items: center;
}
.brand { display: flex; align-items: center; gap: 8px; cursor: pointer; }
.logo-icon { font-size: 28px; }
.logo-text { font-size: 22px; font-weight: 800; color: #1e80ff; font-family: 'PingFang SC', sans-serif;}

/* 搜索框美化 */
.search-box { flex: 1; max-width: 400px; margin: 0 40px; }
:deep(.custom-search .el-input__wrapper) {
  border-radius: 20px;
  background-color: #f2f3f5;
  box-shadow: none;
  transition: all 0.3s;
}
:deep(.custom-search .el-input__wrapper.is-focus) {
  background-color: #ffffff;
  box-shadow: 0 0 0 1px #1e80ff;
}

/* 右侧用户区 */
.user-actions { display: flex; align-items: center; gap: 20px; }
.publish-btn { border-radius: 20px; padding: 8px 20px; }
.avatar-wrapper { cursor: pointer; display: flex; align-items: center; transition: transform 0.2s; }
.avatar-wrapper:hover { transform: scale(1.05); }

/* --- 2. 主体内容区 --- */
.main-content { max-width: 1200px; margin: 24px auto 0; padding: 0 24px; }

/* 左侧文章卡片 */
.article-card {
  border-radius: 8px; border: none; margin-bottom: 16px; cursor: pointer;
  transition: all 0.3s ease;
}
.article-card:hover { transform: translateY(-2px); box-shadow: 0 8px 24px rgba(0,0,0,0.08) !important; }
.article-content { padding: 4px; }
.article-content .title { margin: 0 0 10px 0; font-size: 18px; color: #1d2129; transition: color 0.3s; }
.article-card:hover .title { color: #1e80ff; } /* 悬浮时标题变蓝 */
.article-content .summary { font-size: 14px; color: #86909c; line-height: 1.6; margin-bottom: 16px;
  /* 超过两行显示省略号 */
  display: -webkit-box; -webkit-box-orient: vertical; -webkit-line-clamp: 2; overflow: hidden;
}

/* 底部元信息 (作者、时间、标签) */
.meta-info { display: flex; justify-content: space-between; align-items: center; border-top: 1px solid #f2f3f5; padding-top: 12px; }
.left-meta { display: flex; align-items: center; gap: 16px; font-size: 13px; color: #4e5969; }
.left-meta span { display: flex; align-items: center; gap: 4px; }
.right-meta { display: flex; gap: 16px; font-size: 13px; color: #86909c; }

/* --- 右侧边栏 --- */
.sidebar-card { border-radius: 8px; border: none; margin-bottom: 16px; background: #ffffff; }
.creator-card { text-align: center; padding: 10px 0; background: linear-gradient(135deg, #e8f3ff 0%, #ffffff 100%); }
.greeting { font-size: 18px; font-weight: bold; color: #1d2129; margin-bottom: 8px; }
.sub-greeting { font-size: 13px; color: #86909c; margin-bottom: 20px; }
.start-write-btn { width: 100%; border-radius: 6px; }

.header-title { font-weight: bold; color: #1d2129; }
.tag-cloud { display: flex; flex-wrap: wrap; gap: 10px; }
.cloud-tag { border-radius: 4px; cursor: pointer; transition: all 0.2s; }
.cloud-tag:hover { background-color: #1e80ff; color: white; border-color: #1e80ff; }
</style>