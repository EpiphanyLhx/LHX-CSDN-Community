<template>
  <div class="detail-page">
    <el-backtop />

    <div class="top-nav" @click="router.push('/home')">
      <el-icon><ArrowLeft /></el-icon> 返回首页
    </div>

    <div v-if="loading" class="loading-box">
      <el-skeleton :rows="10" animated />
    </div>

    <el-empty v-else-if="!article || !article.id" description="文章找不到了，可能已被删除" />

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
              <el-button
                  :type="liked ? 'danger' : 'default'"
                  icon="CaretTop"
                  @click="doLike"
                  size="large"
                  class="like-btn"
              >
                点赞 {{ article.likeCount || 0 }}
              </el-button>
              <el-button icon="Star" size="large" @click="msg('已加入收藏')">收藏</el-button>
            </div>
          </el-card>

          <el-card shadow="never" class="comment-card">
            <h3>评论区 ({{ comments.length }})</h3>
            <div class="input-area">
              <el-input
                  v-model="myComment"
                  type="textarea"
                  :rows="3"
                  placeholder="尊重原创，从友善评论开始..."
              />
              <el-button type="danger" class="submit-btn" @click="submitComment">发表评论</el-button>
            </div>

            <div class="comment-list">
              <div v-for="c in comments" :key="c.id" class="comment-item">
                <el-avatar :size="36" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
                <div class="c-info">
                  <div class="c-user">{{ c.nickname || 'LF 匿名用户' }}</div>
                  <div class="c-text">{{ c.content }}</div>
                  <div class="c-time">{{ formatDate(c.createTime) }}</div>
                </div>
              </div>
              <el-empty v-if="comments.length === 0" description="暂无评论，快来抢沙发吧！" :image-size="60" />
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card class="author-side" shadow="never">
            <el-avatar :size="64" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
            <h4 class="author-name">LF_Admin</h4>
            <p class="author-desc">LF 社区联合创始人 | 资深后端开发</p>
            <el-button type="danger" plain class="follow-btn">+ 关注作者</el-button>

            <el-divider />

            <div class="author-stats">
              <div class="stat-item"><b>12</b><span>原创</span></div>
              <div class="stat-item"><b>880</b><span>粉丝</span></div>
              <div class="stat-item"><b>1.2w</b><span>获赞</span></div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { ArrowLeft, CaretTop, Star } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const article = ref({})
const comments = ref([])
const myComment = ref('')
const liked = ref(false)
const loading = ref(true)

// 安全的数据加载逻辑
const loadContent = async () => {
  const id = route.params.id
  if (!id) return

  loading.value = true
  try {
    // 捕获文章详情和评论数据
    const [artRes, comRes] = await Promise.all([
      axios.get(`/api/article/${id}`),
      axios.get(`/api/article/${id}/comments`)
    ])

    if (artRes.data.code === 200) {
      article.value = artRes.data.data
    } else {
      ElMessage.error(artRes.data.message || '文章获取失败')
    }

    if (comRes.data.code === 200) {
      comments.value = comRes.data.data
    }

  } catch (error) {
    console.error('获取详情报错:', error)
    ElMessage.error('服务器连接失败，请检查后端状态')
  } finally {
    loading.value = false
  }
}

// 提交评论
const submitComment = async () => {
  if (!myComment.value.trim()) {
    return ElMessage.warning('评论内容不能为空')
  }

  try {
    const res = await axios.post('/api/article/comment', {
      article_id: article.value.id,
      user_id: 1, // 当前先默认用户 1
      content: myComment.value
    })

    if (res.data.code === 200) {
      ElMessage.success('评论成功')
      myComment.value = ''
      loadContent() // 刷新评论列表
    }
  } catch (error) {
    ElMessage.error('评论提交失败')
  }
}

// 点赞模拟
const doLike = () => {
  if (!liked.value) {
    article.value.likeCount = (article.value.likeCount || 0) + 1
    liked.value = true
    ElMessage.success('感谢点赞！')
    // 实际项目中这里还会调用后端 addLike 接口
  } else {
    ElMessage.info('您已经点过赞啦')
  }
}

const msg = (text) => ElMessage.success(text)

// 简单时间格式化
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`
}

onMounted(() => {
  loadContent()
})
</script>

<style lang="scss" scoped>
.detail-page { padding: 20px 0 50px; background: #f4f6f8; min-height: 100vh; }
.top-nav { max-width: 1200px; margin: 0 auto 20px; cursor: pointer; color: #64748b; font-size: 14px; display: flex; align-items: center; gap: 5px; transition: color 0.3s; &:hover { color: #fc3d39; } }
.loading-box { max-width: 1200px; margin: 0 auto; background: white; padding: 40px; border-radius: 12px; }
.main-content { max-width: 1200px; margin: 0 auto; }

.article-card { border-radius: 12px; padding: 10px 20px; border: none; box-shadow: 0 4px 15px rgba(0,0,0,0.02); }
.title { font-size: 32px; font-weight: 800; color: #1e293b; margin-bottom: 20px; line-height: 1.4; }
.meta { display: flex; align-items: center; gap: 24px; color: #94a3b8; font-size: 14px; margin-bottom: 40px; background: #f8fafc; padding: 12px 20px; border-radius: 8px; }
.content-body { font-size: 16px; color: #334155; line-height: 2; min-height: 300px; margin-bottom: 40px; }
.interact-bar { display: flex; justify-content: center; gap: 20px; border-top: 1px solid #f1f5f9; padding-top: 30px; margin-top: 40px; .like-btn { width: 140px; font-weight: bold; } }

.comment-card { margin-top: 24px; border-radius: 12px; border: none; padding: 10px 20px; box-shadow: 0 4px 15px rgba(0,0,0,0.02); h3 { font-size: 20px; color: #1e293b; margin-bottom: 20px; font-weight: 600; } }
.input-area { text-align: right; margin-bottom: 30px; background: #f8fafc; padding: 20px; border-radius: 8px; .submit-btn { margin-top: 15px; padding: 0 30px; } }
.comment-item { display: flex; gap: 16px; padding: 20px 0; border-bottom: 1px solid #f1f5f9; &:last-child { border-bottom: none; }
  .c-info { flex: 1; }
  .c-user { font-size: 15px; font-weight: 600; color: #334155; margin-bottom: 8px; }
  .c-text { font-size: 14px; color: #475569; line-height: 1.6; margin-bottom: 8px; }
  .c-time { font-size: 12px; color: #94a3b8; }
}

.author-side { text-align: center; border-radius: 12px; border: none; box-shadow: 0 4px 15px rgba(0,0,0,0.02); padding: 20px 0;
  .author-name { font-size: 18px; margin: 15px 0 8px; color: #1e293b; }
  .author-desc { font-size: 13px; color: #64748b; margin-bottom: 20px; }
  .follow-btn { width: 80%; border-radius: 20px; }
  .author-stats { display: flex; justify-content: space-around; margin-top: 20px;
    .stat-item { b { display: block; font-size: 18px; color: #1e293b; margin-bottom: 5px; } span { font-size: 13px; color: #94a3b8; } }
  }
}
</style>