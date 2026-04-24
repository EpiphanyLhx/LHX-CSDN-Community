<template>
  <div class="search-page">
    <nav class="search-nav">
      <div class="nav-content">
        <span class="logo" @click="router.push('/home')">LF</span>
        <el-input
            v-model="searchKey"
            placeholder="搜索感兴趣的内容..."
            class="nav-search"
            @keyup.enter="handleSearch"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
      </div>
    </nav>

    <main class="search-container">
      <div class="search-info">
        找到关于 “<span class="keyword">{{ route.query.q }}</span>” 的结果 {{ results.length }} 条
      </div>

      <div v-loading="loading" class="results-list">
        <div v-for="item in results" :key="item.id" class="result-item" @click="goToDetail(item.id)">
          <h3 class="title" v-html="highlight(item.title)"></h3>
          <p class="summary">{{ item.summary }}</p>
          <div class="meta">
            <el-tag size="small" type="danger" effect="plain">{{ item.category }}</el-tag>
            <span class="time">{{ item.createTime }}</span>
          </div>
        </div>

        <el-empty v-if="!loading && results.length === 0" description="换个关键词试试看？" />
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const searchKey = ref(route.query.q || '')
const results = ref([])
const loading = ref(false)

const handleSearch = async () => {
  if (!searchKey.value.trim()) return
  loading.value = true
  try {
    const res = await axios.get(`/api/article/search?keyword=${searchKey.value}`)
    if (res.data.code === 200) {
      results.value = res.data.data
    }
    // 同步修改 URL，但不刷新页面
    router.replace({ query: { q: searchKey.value } })
  } catch (e) {
    console.error('搜索失败')
  } finally {
    loading.value = false
  }
}

// 简单的关键词高亮逻辑
const highlight = (text) => {
  if (!searchKey.value) return text
  const reg = new RegExp(searchKey.value, 'gi')
  return text.replace(reg, `<span style="color: #fc3d39;">${searchKey.value}</span>`)
}

const goToDetail = (id) => router.push(`/article/${id}`)

onMounted(handleSearch)
// 监听 URL 参数变化（当用户在搜索页再次搜索时触发）
watch(() => route.query.q, (newVal) => {
  searchKey.value = newVal
  handleSearch()
})
</script>

<style lang="scss" scoped>
.search-page { background: #fff; min-height: 100vh; }
.search-nav {
  height: 64px; border-bottom: 1px solid #f0f0f0; display: flex; align-items: center;
  position: sticky; top: 0; background: white; z-index: 100;
  .nav-content { max-width: 1000px; margin: 0 auto; width: 100%; display: flex; align-items: center; gap: 20px; padding: 0 20px; }
  .logo { font-size: 26px; font-weight: 900; color: #fc3d39; cursor: pointer; }
  .nav-search { width: 400px; :deep(.el-input__wrapper) { border-radius: 20px; background: #f4f5f7; box-shadow: none; } }
}
.search-container { max-width: 800px; margin: 0 auto; padding: 30px 20px; }
.search-info { margin-bottom: 25px; color: #666; font-size: 14px; .keyword { color: #fc3d39; font-weight: bold; } }
.result-item {
  padding: 20px 0; border-bottom: 1px solid #f0f0f0; cursor: pointer;
  &:hover .title { color: #fc3d39; }
  .title { margin: 0 0 10px; font-size: 18px; transition: 0.2s; }
  .summary { color: #666; font-size: 14px; line-height: 1.6; margin-bottom: 12px; }
  .meta { display: flex; align-items: center; gap: 15px; font-size: 12px; color: #999; }
}
</style>