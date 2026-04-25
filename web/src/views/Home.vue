<template>
  <div class="home-container">
    <header class="navbar">
      <div class="nav-content">
        <div class="brand" @click="router.push('/home')">
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
          <el-button type="primary" class="publish-btn" @click="handlePublish">
            <el-icon><EditPen /></el-icon> 发布文章
          </el-button>

          <template v-if="isLoggedIn">
            <el-dropdown @command="handleCommand" trigger="click">
              <span class="avatar-wrapper">
                <el-avatar :size="38" src="/avatar/default.png" />
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
          </template>

          <el-button v-else type="info" plain @click="router.push('/login')">登录 / 注册</el-button>
        </div>
      </div>
    </header>

    <main class="main-content">
      <el-row :gutter="24">
        <el-col :span="18">
          <div class="article-feed">
            <el-card
                v-for="item in articles"
                :key="item.id"
                class="article-card"
                shadow="hover"
                @click="goDetail(item.id)"
            >
              <div class="article-content">
                <h3 class="title">{{ item.title }}</h3>
                <p class="summary">{{ item.summary }}</p>
                <div class="meta-info">
                  <div class="left-meta">
                    <span class="author"><el-icon><User /></el-icon> {{ item.authorName || '极客开发者' }}</span>
                    <span class="time"><el-icon><Clock /></el-icon> {{ formatDate(item.createTime) }}</span>
                    <span class="tags">
                      <el-tag size="small" type="primary" effect="light">{{ item.category || 'Java' }}</el-tag>
                    </span>
                  </div>
                  <div class="right-meta">
                    <span class="stat-item"><el-icon><View /></el-icon> {{ item.viewCount || 0 }}</span>
                    <span class="stat-item"><el-icon><ChatDotRound /></el-icon> {{ item.commentCount || 0 }}</span>
                  </div>
                </div>
              </div>
            </el-card>

            <el-empty v-if="articles.length === 0" description="暂无文章" />
          </div>
        </el-col>

        <el-col :span="6">
          <div class="sidebar">
            <el-card class="sidebar-card creator-card" shadow="never">
              <div class="greeting">下午好，创造者！</div>
              <p class="sub-greeting">今天想分享点什么？</p>
              <el-button type="primary" class="start-write-btn" @click="handlePublish">开始写作</el-button>
            </el-card>

            <el-card class="sidebar-card hot-card" shadow="never">
              <template #header>
                <div class="card-header">
                  <span>🔥 全站热度榜</span>
                </div>
              </template>
              <div class="hot-list">
                <div
                    v-for="(item, index) in hotArticles"
                    :key="item.id"
                    class="hot-item"
                    @click="goDetail(item.id)"
                >
                  <span :class="['rank-num', index < 3 ? 'top-rank' : '']">{{ index + 1 }}</span>
                  <span class="hot-title">{{ item.title }}</span>
                  <span class="hot-views">{{ item.viewCount || 0 }} 阅</span>
                </div>
              </div>
            </el-card>
          </div>
        </el-col>
      </el-row>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue' // 修复：导入核心 API
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { Search, EditPen, User, SwitchButton, Clock, View, ChatDotRound } from '@element-plus/icons-vue'

const router = useRouter()
const searchQuery = ref('')
const articles = ref([])
const hotArticles = ref([])

// 获取热榜数据的方法
const fetchHotArticles = async () => {
  try {
    const res = await axios.get('/api/article/hot')
    if (res.data.code === 200) {
      hotArticles.value = res.data.data
    }
  } catch (error) {
    console.error('获取热榜失败', error)
  }
}


// 计算属性：判断用户是否已登录
const isLoggedIn = computed(() => !!localStorage.getItem('token'))

const fetchArticles = async () => {
  try {
    const res = await axios.get('/api/article/list')
    if (res.data.code === 200) articles.value = res.data.data
  } catch (error) {
    ElMessage.error('获取文章列表失败')
  }
}

onMounted(() => {
  fetchArticles()
  fetchHotArticles()
})

const goDetail = (id) => router.push(`/article/${id}`)

// 拦截逻辑：发布前检查登录状态
const handlePublish = () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录后再进行发布')
    router.push('/login')
    return
  }
  router.push('/publish')
}

const handleCommand = (command) => {
  if (command === 'profile') router.push('/user')
  else if (command === 'logout') {
    localStorage.clear()
    ElMessage.success('已安全退出')
    router.push('/login')
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`
}
</script>

<style scoped>
/* 保持原有样式 */
.home-container { background-color: #f2f3f5; min-height: 100vh; padding-bottom: 40px; }
.navbar { background-color: #ffffff; height: 64px; display: flex; align-items: center; box-shadow: 0 1px 4px rgba(0,21,41,0.08); position: sticky; top: 0; z-index: 100; }
.nav-content { width: 100%; max-width: 1200px; margin: 0 auto; padding: 0 24px; display: flex; justify-content: space-between; align-items: center; }
.brand { display: flex; align-items: center; gap: 8px; cursor: pointer; }
.logo-icon { font-size: 28px; }
.logo-text { font-size: 22px; font-weight: 800; color: #1e80ff; }
.search-box { flex: 1; max-width: 400px; margin: 0 40px; }
.user-actions { display: flex; align-items: center; gap: 20px; }
.publish-btn { border-radius: 20px; padding: 8px 20px; }
.avatar-wrapper { cursor: pointer; display: flex; align-items: center; }
.main-content { max-width: 1200px; margin: 24px auto 0; padding: 0 24px; }
.article-card { border-radius: 8px; border: none; margin-bottom: 16px; cursor: pointer; transition: all 0.3s ease; }
.article-card:hover { transform: translateY(-2px); box-shadow: 0 8px 24px rgba(0,0,0,0.08) !important; }
.title { margin: 0 0 10px 0; font-size: 18px; color: #1d2129; font-weight: bold; }
.summary { font-size: 14px; color: #86909c; line-height: 1.6; margin-bottom: 16px; display: -webkit-box; -webkit-box-orient: vertical; -webkit-line-clamp: 2; overflow: hidden; }
.meta-info { display: flex; justify-content: space-between; align-items: center; border-top: 1px solid #f2f3f5; padding-top: 12px; }
.left-meta { display: flex; align-items: center; gap: 16px; font-size: 13px; color: #4e5969; }
.right-meta { display: flex; gap: 16px; font-size: 13px; color: #86909c; }
.sidebar-card { border-radius: 8px; border: none; margin-bottom: 16px; }
.creator-card { text-align: center; background: linear-gradient(135deg, #e8f3ff 0%, #ffffff 100%); }
.start-write-btn { width: 100%; border-radius: 6px; }
/* web/src/views/Home.vue */
/* 在 <style scoped> 末尾追加以下样式 */
.hot-card {margin-top: 16px;}
.card-header span {font-weight: bold;font-size: 16px;color: #1d2129;}
.hot-item {display: flex;align-items: center;padding: 12px 0;cursor: pointer;border-bottom: 1px dashed #f2f3f5;transition: background-color 0.2s;}
.hot-item:last-child {border-bottom: none;}
.hot-item:hover .hot-title {color: #1e80ff; /* 悬浮时标题变蓝 */}
.rank-num {width: 24px;font-weight: 900;color: #c9cdd4;font-style: italic;font-size: 15px;}
.top-rank {color: #f53f3f; /* 前三名红色高亮 */font-size: 17px;}
.hot-title {flex: 1;margin-left: 8px;margin-right: 12px;white-space: nowrap;overflow: hidden;text-overflow: ellipsis; /* 文字过长显示省略号 */font-size: 14px;color: #4e5969;}
.hot-views {font-size: 12px;color: #86909c;white-space: nowrap;}
</style>