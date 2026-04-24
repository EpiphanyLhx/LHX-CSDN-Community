<template>
  <div class="home-container">
    <header class="navbar">
      <div class="nav-content">
        <div class="nav-left">
          <span class="logo">LF</span>
          <span class="tagline">Tech Community</span>
        </div>

        <div class="nav-center">
          <el-input
              v-model="searchText"
              placeholder="搜索 Java, Spring Boot, Vue..."
              class="search-bar"
              @keyup.enter="onSearch"
          >
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
          <el-button type="primary" class="search-btn" @click="onSearch">搜索</el-button>
        </div>

        <div class="nav-right">
          <el-button type="danger" round icon="EditPen" class="publish-btn" @click="router.push('/publish')">
            创作
          </el-button>

          <el-dropdown trigger="click">
            <div class="user-profile">
              <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
              <span class="username">{{ userNickname }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item icon="User" @click="router.push('/user')">个人中心</el-dropdown-item>
                <el-dropdown-item
                    v-if="userNickname === '超级管理员'"
                    icon="Setting"
                    @click="router.push('/admin')"
                >
                  后台管理
                </el-dropdown-item>
                <el-dropdown-item divided icon="SwitchButton" @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <main class="main-layout">
      <el-row :gutter="20">
        <el-col :span="18">
          <div class="article-list-wrapper">
            <div class="list-header">
              <span class="active">最新博文</span>
              <span>热门排行</span>
            </div>

            <div v-loading="loading" class="article-list">
              <div v-for="item in articles" :key="item.id" class="article-item" @click="goToDetail(item.id)">
                <div class="article-main">
                  <h3 class="title">{{ item.title }}</h3>
                  <p class="summary">{{ item.summary }}</p>
                  <div class="meta">
                    <el-tag size="small" type="info" effect="plain">{{ item.category }}</el-tag>
                    <span class="author">作者ID: {{ item.authorId }}</span>
                    <span class="stat"><el-icon><View /></el-icon> {{ item.viewCount || 0 }}</span>
                    <span class="stat"><el-icon><Star /></el-icon> {{ item.likeCount || 0 }}</span>
                  </div>
                </div>
              </div>
              <el-empty v-if="articles.length === 0" description="暂无内容，快去发布第一篇吧！" />
            </div>
          </div>
        </el-col>

        <el-col :span="6">
          <el-card class="sidebar-card" shadow="never">
            <template #header><div class="card-title">🔥 热门搜索排行</div></template>
            <div class="hot-list">
              <div v-for="(hot, index) in hotTags" :key="index" class="hot-item">
                <span :class="['rank', index < 3 ? 'top' : '']">{{ index + 1 }}</span>
                <span class="name">{{ hot }}</span>
              </div>
            </div>
          </el-card>

          <el-card class="sidebar-card mt-20" shadow="never">
            <div class="ad-box">
              <h4>LF 社区 2026 全新启航</h4>
              <p>让技术交流更有温度</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, EditPen, User, Setting, SwitchButton, View, Star } from '@element-plus/icons-vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const searchText = ref('')
const loading = ref(false)
const articles = ref([])

// 从本地存储获取用户信息，默认为“游客”
const userNickname = ref(localStorage.getItem('nickname') || '开发达人')

const hotTags = ['Java 21', 'Spring Boot 3', 'Redis 缓存', 'Vue 3 实战', 'MySQL 优化']

// --- 获取文章列表 ---
const fetchArticles = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/article/list')
    if (res.data.code === 200) {
      articles.value = res.data.data
    }
  } catch (error) {
    console.error('获取文章失败')
  } finally {
    loading.value = false
  }
}

// --- 搜索功能 ---
const onSearch = () => {
  if (!searchText.value.trim()) return
  router.push({ path: '/search', query: { q: searchText.value } })
}

// --- 退出登录 ---
const handleLogout = () => {
  localStorage.clear()
  ElMessage.success('已安全退出')
  router.push('/login')
}

const goToDetail = (id) => router.push(`/article/${id}`)

onMounted(fetchArticles)
</script>

<style lang="scss" scoped>
.home-container { background-color: #f4f5f7; min-height: 100vh; }

/* 导航栏样式 */
.navbar {
  height: 60px; background: #fff; box-shadow: 0 1px 4px rgba(0,0,0,0.05);
  position: sticky; top: 0; z-index: 1000;
  .nav-content {
    max-width: 1200px; margin: 0 auto; height: 100%;
    display: flex; align-items: center; justify-content: space-between; padding: 0 20px;
  }
  .logo { font-size: 26px; font-weight: 900; color: #fc3d39; margin-right: 8px; }
  .tagline { font-size: 14px; color: #999; font-weight: 500; }

  .nav-center {
    display: flex; gap: 10px;
    .search-bar { width: 350px; :deep(.el-input__wrapper) { border-radius: 20px; background: #f4f5f7; box-shadow: none; } }
    .search-btn { border-radius: 20px; }
  }

  .nav-right {
    display: flex; align-items: center; gap: 20px;
    .user-profile {
      display: flex; align-items: center; gap: 8px; cursor: pointer;
      .username { font-size: 14px; color: #333; }
    }
  }
}

/* 主体布局 */
.main-layout { max-width: 1200px; margin: 20px auto; padding: 0 20px; }

/* 文章列表 */
.article-list-wrapper {
  background: #fff; border-radius: 8px; overflow: hidden;
  .list-header {
    padding: 15px 20px; border-bottom: 1px solid #f0f0f0; display: flex; gap: 30px;
    span { font-size: 15px; color: #666; cursor: pointer; &.active { color: #fc3d39; font-weight: bold; } }
  }
}

.article-item {
  padding: 20px; border-bottom: 1px solid #f0f0f0; cursor: pointer; transition: 0.3s;
  &:hover { background: #fafbfc; }
  .title { margin: 0 0 10px; font-size: 18px; color: #222; }
  .summary { font-size: 14px; color: #666; line-height: 1.6; margin-bottom: 15px; }
  .meta {
    display: flex; align-items: center; gap: 20px; color: #999; font-size: 13px;
    .stat { display: flex; align-items: center; gap: 4px; }
  }
}

/* 侧边栏 */
.sidebar-card {
  border: none; border-radius: 8px;
  .card-title { font-weight: bold; color: #333; }
}

.hot-list {
  .hot-item {
    display: flex; align-items: center; gap: 12px; padding: 10px 0;
    .rank { width: 18px; height: 18px; line-height: 18px; text-align: center; font-size: 12px; color: #999; border-radius: 2px; }
    .rank.top { background: #ff9f43; color: #fff; }
    .name { font-size: 14px; color: #444; cursor: pointer; &:hover { color: #fc3d39; } }
  }
}

.ad-box {
  text-align: center; padding: 10px;
  h4 { color: #fc3d39; margin-bottom: 8px; }
  p { font-size: 12px; color: #999; }
}

.mt-20 { margin-top: 20px; }
</style>