<template>
  <div class="user-center-layout">
    <header class="uc-nav">
      <div class="nav-content" @click="router.push('/home')">
        <span class="logo">LF</span>
        <span class="back-text"><el-icon><ArrowLeft /></el-icon> 返回社区大厅</span>
      </div>
    </header>

    <main class="uc-container">
      <el-row :gutter="24">
        <el-col :span="7">
          <el-card class="profile-card" shadow="never">
            <div class="avatar-box">
              <el-avatar :size="100" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
            </div>
            <h2 class="nickname">{{ userInfo.nickname || '未登录' }}</h2>
            <p class="bio">{{ userInfo.bio || '这个人很懒，还没写简介...' }}</p>

            <div class="user-tags">
              <el-tag v-for="tag in getTagList()" :key="tag" size="small" type="info" round effect="plain">
                {{ tag }}
              </el-tag>
            </div>

            <el-divider border-style="dashed" />

            <div class="info-list">
              <div class="info-item"><el-icon><Location /></el-icon> {{ userInfo.location || '未知' }}</div>
              <div class="info-item"><el-icon><Suitcase /></el-icon> {{ userInfo.profession || '开发者' }}</div>
              <div class="info-item"><el-icon><School /></el-icon> {{ userInfo.education || '未知学校' }}</div>
            </div>

            <el-button type="primary" class="edit-btn" plain @click="openEditDialog">
              <el-icon><Edit /></el-icon> 编辑个人资料
            </el-button>
          </el-card>

          <el-card class="stat-card mt-20" shadow="never">
            <div class="stat-grid">
              <div class="stat-item"><b>{{ myArticles.length }}</b><span>原创</span></div>
              <div class="stat-item"><b>1.2k</b><span>获赞</span></div>
              <div class="stat-item"><b>340</b><span>粉丝</span></div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="17">
          <el-card class="chart-card" shadow="never">
            <template #header><div class="card-header"><span>📊 创作技术足迹</span></div></template>
            <div id="main-chart" style="width: 100%; height: 280px;"></div>
          </el-card>

          <el-card class="content-card mt-20" shadow="never">
            <el-tabs v-model="activeTab">
              <el-tab-pane label="我的创作" name="posts">
                <div v-loading="loading" class="article-list">
                  <div v-for="post in myArticles" :key="post.id" class="post-item" @click="goToDetail(post.id)">
                    <div class="post-title">{{ post.title }}</div>
                    <div class="post-meta">
                      <span class="time">{{ formatDate(post.createTime) }}</span>
                      <span><el-icon><View /></el-icon> {{ post.viewCount || 0 }}</span>
                      <span><el-icon><Star /></el-icon> {{ post.likeCount || 0 }}</span>
                    </div>
                  </div>
                  <el-empty v-if="!loading && myArticles.length === 0" description="暂无创作内容" />
                </div>
              </el-tab-pane>
              <el-tab-pane label="我的收藏" name="collections">
                <div v-loading="loading" class="article-list">
                  <div v-for="post in myCollections" :key="post.id" class="post-item" @click="goToDetail(post.id)">
                    <div class="post-title">{{ post.title }}</div>
                    <div class="post-meta">
                      <span class="time">{{ formatDate(post.createTime) }}</span>
                      <span><el-icon><View /></el-icon> {{ post.viewCount || 0 }}</span>
                      <span><el-icon><Star /></el-icon> {{ post.likeCount || 0 }}</span>
                    </div>
                  </div>
                  <el-empty v-if="!loading && myCollections.length === 0" description="暂无收藏" />
                </div>
              </el-tab-pane>
            </el-tabs>
          </el-card>
        </el-col>
      </el-row>
    </main>

    <el-dialog v-model="editDialogVisible" title="完善个人名片" width="500px" append-to-body>
      <el-form :model="editForm" label-width="80px" label-position="left">
        <el-form-item label="昵称"><el-input v-model="editForm.nickname" /></el-form-item>
        <el-form-item label="个人简介"><el-input v-model="editForm.bio" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="所在地"><el-input v-model="editForm.location" /></el-form-item>
        <el-form-item label="职业"><el-input v-model="editForm.profession" /></el-form-item>
        <el-form-item label="教育背景"><el-input v-model="editForm.education" /></el-form-item>
        <el-form-item label="技术标签">
          <el-input v-model="editForm.tags" placeholder="多个标签用英文逗号隔开" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleUpdate">保存修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick} from 'vue'
import {useRouter} from 'vue-router'
import axios from 'axios'
import * as echarts from 'echarts'
import {ElMessage} from 'element-plus'
import {ArrowLeft, Location, Suitcase, School, View, Star, Edit} from '@element-plus/icons-vue'

const router = useRouter()
const activeTab = ref('posts')
const loading = ref(false)
const submitting = ref(false)
const editDialogVisible = ref(false)
const myArticles = ref([])
const myCollections = ref([])

// 1. 用户信息状态
const userInfo = ref({
  id: null,
  nickname: '',
  bio: '',
  location: '',
  profession: '',
  education: '',
  tags: ''
})

// 2. 编辑表单镜像数据
const editForm = reactive({
  id: null,
  nickname: '',
  bio: '',
  location: '',
  profession: '',
  education: '',
  tags: ''
})

// --- 核心方法：获取当前登录用户信息 ---
const fetchUserInfo = async () => {
  try {
    const res = await axios.get('/api/user/me')
    if (res.data.code === 200) {
      userInfo.value = res.data.data
      // 同步到本地存储，确保首页同步
      localStorage.setItem('nickname', res.data.data.nickname)
    }
  } catch (e) {
    ElMessage.error('会话已过期，请重新登录')
  }
}

// --- 核心方法：获取我的文章列表 ---
const fetchMyArticles = async () => {
  if (!userInfo.value.id) return
  loading.value = true
  try {
    const res = await axios.get(`/api/article/user/${userInfo.value.id}`)
    if (res.data.code === 200) {
      myArticles.value = res.data.data
    }
  } finally {
    loading.value = false
  }
}

// --- 资料更新提交 ---
const handleUpdate = async () => {
  submitting.value = true
  try {
    const res = await axios.post('/api/user/update', editForm)
    if (res.data.code === 200) {
      ElMessage.success('个人资料已更新！')
      editDialogVisible.value = false
      await fetchUserInfo() // 重新拉取最新数据
    }
  } finally {
    submitting.value = false
  }
}

// --- 打开编辑弹窗并回显数据 ---
const openEditDialog = () => {
  Object.assign(editForm, userInfo.value)
  editDialogVisible.value = true
}

// --- 修复报错的核心：时间格式化函数 ---
const formatDate = (dateStr) => {
  if (!dateStr) return '刚刚'
  const d = new Date(dateStr)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

// --- 处理标签字符串转数组 ---
const getTagList = () => {
  return userInfo.value.tags ? userInfo.value.tags.split(',').map(t => t.trim()) : ['技术控']
}

// --- 初始化 ECharts ---
const initChart = () => {
  const chartDom = document.getElementById('main-chart')
  if (!chartDom) return
  const myChart = echarts.init(chartDom)
  const option = {
    tooltip: {trigger: 'item'},
    legend: {bottom: '0', left: 'center', textStyle: {fontSize: 11}},
    color: ['#fc3d39', '#ff9f43', '#00d2d3', '#54a0ff'],
    series: [{
      name: '领域分布',
      type: 'pie',
      radius: ['45%', '75%'],
      avoidLabelOverlap: false,
      itemStyle: {borderRadius: 8, borderColor: '#fff', borderWidth: 2},
      label: {show: false},
      data: [
        {value: 35, name: 'Java/Spring'},
        {value: 25, name: '前端/Vue'},
        {value: 20, name: '环境科学'},
        {value: 20, name: '电商实战'}
      ]
    }]
  }
  myChart.setOption(option)
}

const goToDetail = (id) => router.push(`/article/${id}`)

// --- 挂载逻辑 ---
onMounted(async () => {
  await fetchUserInfo()
  fetchMyArticles()
  nextTick(() => {
    initChart()
  })
})
</script>

<style lang="scss" scoped>
.user-center-layout {
  background: #f4f7f9;
  min-height: 100vh;
  padding-bottom: 50px;
}

.uc-nav {
  background: white;
  height: 60px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;

  .nav-content {
    max-width: 1200px;
    margin: 0 auto;
    width: 100%;
    padding: 0 20px;
    display: flex;
    align-items: center;
    gap: 10px;
    cursor: pointer;
  }

  .logo {
    font-size: 24px;
    font-weight: 900;
    color: #fc3d39;
  }

  .back-text {
    font-size: 14px;
    color: #64748b;

    &:hover {
      color: #fc3d39;
    }
  }
}

.uc-container {
  max-width: 1200px;
  margin: 24px auto;
  padding: 0 20px;
}

.profile-card {
  text-align: center;
  border-radius: 12px;
  border: none;

  .avatar-box {
    margin-bottom: 15px;
  }

  .nickname {
    font-size: 22px;
    color: #1e293b;
    margin-bottom: 8px;
  }

  .bio {
    font-size: 13px;
    color: #64748b;
    line-height: 1.6;
    margin-bottom: 20px;
    padding: 0 10px;
  }

  .user-tags {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 8px;
    margin-bottom: 20px;
  }

  .info-list {
    text-align: left;
    margin-bottom: 25px;

    .info-item {
      font-size: 13px;
      color: #475569;
      margin-bottom: 12px;
      display: flex;
      align-items: center;
      gap: 10px;
    }
  }

  .edit-btn {
    width: 100%;
  }
}

.stat-card {
  border-radius: 12px;
  border: none;

  .stat-grid {
    display: flex;
    justify-content: space-around;

    .stat-item {
      text-align: center;

      b {
        display: block;
        font-size: 18px;
        color: #1e293b;
      }

      span {
        font-size: 12px;
        color: #94a3b8;
      }
    }
  }
}

.chart-card {
  border-radius: 12px;
  border: none;
}

.content-card {
  border-radius: 12px;
  border: none;

  .post-item {
    padding: 18px 0;
    border-bottom: 1px solid #f1f5f9;
    cursor: pointer;

    &:hover .post-title {
      color: #fc3d39;
    }

    .post-title {
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 8px;
      transition: 0.2s;
    }

    .post-meta {
      display: flex;
      gap: 20px;
      font-size: 12px;
      color: #94a3b8;
    }
  }
}

.mt-20 {
  margin-top: 20px;
}
</style>