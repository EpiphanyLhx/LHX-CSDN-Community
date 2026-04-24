<template>
  <div class="publish-container">
    <header class="publish-header">
      <div class="left" @click="router.push('/home')">
        <el-icon><ArrowLeft /></el-icon> 返回
      </div>
      <div class="center">
        <el-input v-model="articleData.title" placeholder="输入文章标题..." class="title-input" />
      </div>
      <div class="right">
        <el-select v-model="articleData.category" placeholder="选择分类" style="width: 120px; margin-right: 15px;">
          <el-option label="Java" value="Java" />
          <el-option label="前端" value="前端" />
          <el-option label="数据库" value="数据库" />
          <el-option label="随笔" value="随笔" />
        </el-select>
        <el-button type="danger" round @click="handlePublish" :loading="publishing">发布文章</el-button>
      </div>
    </header>

    <main class="editor-main">
      <MdEditor
          v-model="articleData.content"
          :theme="theme"
          placeholder="在这里开始你的技术创作，支持 Markdown 语法..."
          style="height: calc(100vh - 70px);"
      />
    </main>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'

// 引入 md-editor-v3 及其样式
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'

const router = useRouter()
const theme = ref('light') // 可选 'light' 或 'dark'
const publishing = ref(false)

const articleData = reactive({
  title: '',
  category: 'Java',
  content: ''
})

const handlePublish = async () => {
  if (!articleData.title.trim()) return ElMessage.warning('起个响亮的标题吧！')
  if (!articleData.content.trim()) return ElMessage.warning('内容不能为空哦！')

  publishing.value = true
  try {
    const res = await axios.post('/api/article/publish', articleData)
    if (res.data.code === 200) {
      ElMessage.success('文章发布成功！')
      setTimeout(() => {
        router.push('/home')
      }, 1000)
    } else {
      ElMessage.error(res.data.message || '发布失败')
    }
  } catch (error) {
    ElMessage.error('服务器异常，发布失败')
  } finally {
    publishing.value = false
  }
}
</script>

<style lang="scss" scoped>
.publish-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.publish-header {
  height: 70px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  border-bottom: 1px solid #eee;

  .left {
    cursor: pointer;
    font-size: 15px;
    color: #666;
    display: flex;
    align-items: center;
    gap: 5px;
    &:hover { color: #fc3d39; }
  }

  .center {
    flex: 1;
    margin: 0 40px;
    .title-input {
      :deep(.el-input__wrapper) {
        box-shadow: none;
        font-size: 24px;
        font-weight: bold;
        background: transparent;
        border-bottom: 1px dashed transparent;
        transition: all 0.3s;
        &:hover, &.is-focus { border-bottom-color: #ddd; }
      }
    }
  }

  .right {
    display: flex;
    align-items: center;
  }
}

.editor-main { flex: 1; }
</style>