<template>
  <div class="detail-page">
    <el-backtop />
    <div class="top-nav" @click="router.push('/home')">
      <el-icon><ArrowLeft /></el-icon> 返回首页
    </div>

    <div v-if="loading" class="loading-box">
      <el-skeleton :rows="10" animated />
    </div>

    <el-empty v-else-if="!article || !article.id" description="文章找不到了" />

    <div v-else class="main-content">
      <el-row :gutter="24">
        <el-col :span="18">
          <el-card shadow="never" class="article-card">
            <h1 class="title">{{ article.title }}</h1>
            <div class="meta">
              <el-tag size="small" type="danger" effect="plain">{{ article.category || 'Java' }}</el-tag>
              <span>发布时间：{{ formatDate(article.createTime) }}</span>
              <span>阅读量：{{ article.viewCount || 0 }}</span>
            </div>
            <div class="content-body" v-html="article.content"></div>

            <div class="interact-bar">
              <el-button icon="CaretTop" size="large" type="danger" plain @click="doLike">
                点赞 {{ article.likeCount || 0 }}
              </el-button>
              <el-button icon="Star" size="large" @click="handleCollect">收藏</el-button>
            </div>
          </el-card>

          <el-card shadow="never" class="comment-card">
            <h3>评论区 ({{ comments.length }})</h3>
            <div class="input-area">
              <el-input v-model="myComment" type="textarea" :rows="3" placeholder="写下你的评论..." />
              <el-button type="danger" class="submit-btn" @click="submitComment">发表评论</el-button>
            </div>
            <div class="comment-list">
              <div v-for="c in comments" :key="c.id" class="comment-item">
                <el-avatar :size="36" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
                <div class="c-info">
                  <div class="c-user">{{ c.nickname || '匿名用户' }}</div>
                  <div class="c-text">{{ c.content }}</div>
                  <div class="c-time">{{ formatDate(c.createTime) }}</div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import axios from 'axios'
import {ElMessage} from 'element-plus'
import {ArrowLeft, CaretTop, Star} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const article = ref({})
const comments = ref([])
const myComment = ref('')
const loading = ref(true)

// 核心功能：操作权限检查
const checkLogin = () => {
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请登录后再进行此操作')
    router.push('/login')
    return false
  }
  return true
}

const loadContent = async () => {
  const id = route.params.id
  if (!id) return
  loading.value = true
  try {
    const [artRes, comRes] = await Promise.all([
      axios.get(`/api/article/${id}`),
      axios.get(`/api/article/${id}/comments`)
    ])
    if (artRes.data.code === 200) article.value = artRes.data.data
    if (comRes.data.code === 200) comments.value = comRes.data.data
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 拦截点：提交评论
const submitComment = async () => {
  if (!checkLogin()) return
  if (!myComment.value.trim()) return ElMessage.warning('内容不能为空')
  try {
    const res = await axios.post('/api/article/comment', {
      article_id: article.value.id,
      content: myComment.value
    })
    if (res.data.code === 200) {
      ElMessage.success('评论成功')
      myComment.value = ''
      loadContent()
    }
  } catch (e) {
    ElMessage.error('提交失败')
  }
}

// 拦截点：点赞
const doLike = async () => {
  if (!checkLogin()) return
  try {
    const res = await axios.post(`/api/article/${article.value.id}/like`)
    if (res.data.code === 200) {
      article.value.likeCount++
      ElMessage.success('点赞成功')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

// 拦截点：收藏
const handleCollect = () => {
  if (!checkLogin()) return
  ElMessage.success('已加入收藏')
}

onMounted(() => loadContent())

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}
</script>

<style scoped>
/* 保持原有样式不变 */
.detail-page {
  padding: 20px 0 50px;
  background: #f4f6f8;
  min-height: 100vh;
}

.top-nav {
  max-width: 1200px;
  margin: 0 auto 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  color: #64748b;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
}

.article-card {
  border-radius: 12px;
  padding: 20px;
  border: none;
}

.title {
  font-size: 32px;
  font-weight: 800;
  margin-bottom: 20px;
}

.meta {
  display: flex;
  gap: 24px;
  color: #94a3b8;
  font-size: 14px;
  margin-bottom: 30px;
  background: #f8fafc;
  padding: 12px;
  border-radius: 8px;
}

.content-body {
  font-size: 16px;
  line-height: 2;
  margin-bottom: 40px;
}

.interact-bar {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding-top: 30px;
  border-top: 1px solid #f1f5f9;
}

.comment-card {
  margin-top: 24px;
  border-radius: 12px;
  border: none;
}

.input-area {
  text-align: right;
  margin-bottom: 30px;
}

.submit-btn {
  margin-top: 15px;
}
</style>