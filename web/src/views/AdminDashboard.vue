<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <div class="brand">LF Console</div>
      <el-menu 
        :default-active="activeMenu" 
        background-color="#2c3e50" 
        text-color="#fff" 
        active-text-color="#409EFF"
        @select="handleMenuSelect"
      >
        <el-menu-item index="users"><el-icon><User /></el-icon>用户管理</el-menu-item>
        <el-menu-item index="articles"><el-icon><Document /></el-icon>文章管理</el-menu-item>
        <el-menu-item index="comments"><el-icon><ChatDotRound /></el-icon>评论审核</el-menu-item>
        <el-menu-item index="categories"><el-icon><Folder /></el-icon>分类管理</el-menu-item>
        <el-menu-item index="tags"><el-icon><PriceTag /></el-icon>标签管理</el-menu-item>
        <el-menu-item index="logs"><el-icon><Warning /></el-icon>系统日志</el-menu-item>
      </el-menu>
    </aside>

    <main class="main-content">
      <header class="top-header">
        <h2>{{ pageTitle }}</h2>
        <div class="user-info">
          <el-avatar :size="32">Ad</el-avatar>
          <span style="margin-left: 10px;">admin (超级管理员)</span>
          <el-button type="danger" link style="margin-left: 15px;" @click="router.push('/home')">退出后台</el-button>
        </div>
      </header>

      <!-- 用户管理 -->
      <div v-if="activeMenu === 'users'" class="table-container">
        <el-table :data="users" border stripe style="width: 100%" v-loading="loading">
          <el-table-column prop="id" label="UID" width="80" align="center" />
          <el-table-column prop="username" label="登录账号" />
          <el-table-column prop="nickname" label="用户昵称" />
          <el-table-column label="当前角色" width="150" align="center">
            <template #default="scope">
              <el-tag :type="getRoleTag(scope.row.role)">{{ scope.row.role }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="250" align="center">
            <template #default="scope">
              <el-button size="small" type="primary" @click="openAssignDialog(scope.row)">分配角色</el-button>
              <el-button size="small" type="danger" @click="handleDelete(scope.row.id)" :disabled="scope.row.username === 'admin'">删除用户</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 文章管理 -->
      <div v-if="activeMenu === 'articles'" class="table-container">
        <div class="toolbar">
          <el-input v-model="articleFilters.keyword" placeholder="搜索标题或摘要" style="width: 200px;" @keyup.enter="fetchArticles" clearable />
          <el-select v-model="articleFilters.status" placeholder="状态" style="width: 120px; margin-left: 10px;" clearable>
            <el-option label="草稿" value="DRAFT" />
            <el-option label="已发布" value="PUBLISHED" />
            <el-option label="已删除" value="DELETED" />
          </el-select>
          <el-select v-model="articleFilters.category" placeholder="分类" style="width: 120px; margin-left: 10px;" clearable>
            <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
          </el-select>
          <el-button type="primary" style="margin-left: 10px;" @click="fetchArticles">搜索</el-button>
          <el-button @click="resetArticleFilters">重置</el-button>
          <el-button type="success" style="margin-left: 10px;" @click="openArticleDialog(null)">新建文章</el-button>
          <el-button type="danger" :disabled="selectedArticleIds.length === 0" @click="batchDeleteArticles">批量删除</el-button>
        </div>

        <el-table 
          :data="articles" 
          border 
          stripe 
          style="width: 100%" 
          v-loading="articleLoading"
          @selection-change="handleArticleSelectionChange"
        >
          <el-table-column type="selection" width="50" />
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="title" label="标题" min-width="200" />
          <el-table-column prop="authorName" label="作者" width="120" />
          <el-table-column prop="category" label="分类" width="120" />
          <el-table-column label="状态" width="100" align="center">
            <template #default="scope">
              <el-tag :type="getArticleStatusTag(scope.row.status)">{{ scope.row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="viewCount" label="浏览" width="80" align="center" />
          <el-table-column prop="likeCount" label="点赞" width="80" align="center" />
          <el-table-column prop="commentCount" label="评论" width="80" align="center" />
          <el-table-column prop="createTime" label="创建时间" width="160" />
          <el-table-column label="操作" width="250" align="center" fixed="right">
            <template #default="scope">
              <el-button size="small" type="primary" @click="openArticleDialog(scope.row)">编辑</el-button>
              <el-button size="small" :type="scope.row.status === 'PUBLISHED' ? 'warning' : 'success'" 
                @click="toggleArticleStatus(scope.row)">
                {{ scope.row.status === 'PUBLISHED' ? '下架' : '发布' }}
              </el-button>
              <el-button size="small" type="danger" @click="deleteArticle(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination">
          <el-pagination
            v-model:current-page="articlePage.current"
            v-model:page-size="articlePage.size"
            :page-sizes="[10, 20, 50, 100]"
            :total="articlePage.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="fetchArticles"
            @current-change="fetchArticles"
          />
        </div>
      </div>

      <!-- 评论审核 -->
      <div v-if="activeMenu === 'comments'" class="table-container">
        <div class="toolbar">
          <el-input v-model="commentFilters.keyword" placeholder="搜索评论内容" style="width: 200px;" @keyup.enter="fetchComments" clearable />
          <el-select v-model="commentFilters.status" placeholder="审核状态" style="width: 120px; margin-left: 10px;" clearable>
            <el-option label="待审核" value="PENDING" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
          </el-select>
          <el-input v-model="commentFilters.articleId" placeholder="文章ID" style="width: 120px; margin-left: 10px;" clearable />
          <el-button type="primary" style="margin-left: 10px;" @click="fetchComments">搜索</el-button>
          <el-button @click="resetCommentFilters">重置</el-button>
          <el-button type="success" :disabled="selectedCommentIds.length === 0" @click="batchAuditComments('APPROVED')">批量通过</el-button>
          <el-button type="danger" :disabled="selectedCommentIds.length === 0" @click="batchAuditComments('REJECTED')">批量拒绝</el-button>
        </div>

        <el-table 
          :data="comments" 
          border 
          stripe 
          style="width: 100%" 
          v-loading="commentLoading"
          @selection-change="handleCommentSelectionChange"
        >
          <el-table-column type="selection" width="50" />
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="articleId" label="文章ID" width="100" align="center" />
          <el-table-column prop="nickname" label="评论用户" width="120" />
          <el-table-column prop="content" label="评论内容" min-width="200" />
          <el-table-column label="状态" width="100" align="center">
            <template #default="scope">
              <el-tag :type="getCommentStatusTag(scope.row.status)">{{ scope.row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="auditReason" label="审核原因" width="150" />
          <el-table-column prop="createTime" label="评论时间" width="160" />
          <el-table-column label="操作" width="200" align="center" fixed="right">
            <template #default="scope">
              <el-button size="small" type="success" @click="auditComment(scope.row.id, 'APPROVED')" v-if="scope.row.status !== 'APPROVED'">通过</el-button>
              <el-button size="small" type="danger" @click="auditComment(scope.row.id, 'REJECTED')" v-if="scope.row.status !== 'REJECTED'">拒绝</el-button>
              <el-button size="small" type="warning" @click="showAuditDialog(scope.row)">审核</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination">
          <el-pagination
            v-model:current-page="commentPage.current"
            v-model:page-size="commentPage.size"
            :page-sizes="[10, 20, 50, 100]"
            :total="commentPage.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="fetchComments"
            @current-change="fetchComments"
          />
        </div>
      </div>

      <!-- 分类管理 -->
      <div v-if="activeMenu === 'categories'" class="table-container">
        <div class="toolbar">
          <el-button type="primary" @click="openCategoryDialog(null)">新建分类</el-button>
        </div>
        <el-table :data="categories" border stripe style="width: 100%" v-loading="categoryLoading">
          <el-table-column prop="value" label="分类名称" />
          <el-table-column label="操作" width="200" align="center">
            <template #default="scope">
              <el-button size="small" type="primary" @click="openCategoryDialog(scope.row)">编辑</el-button>
              <el-button size="small" type="danger" @click="deleteCategory(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 标签管理 -->
      <div v-if="activeMenu === 'tags'" class="table-container">
        <div class="toolbar">
          <el-input v-model="tagFilters.keyword" placeholder="搜索标签名称" style="width: 200px;" @keyup.enter="fetchTags" clearable />
          <el-button type="primary" style="margin-left: 10px;" @click="fetchTags">搜索</el-button>
          <el-button @click="resetTagFilters">重置</el-button>
          <el-button type="success" @click="openTagDialog(null)">新建标签</el-button>
        </div>
        <el-table :data="tags" border stripe style="width: 100%" v-loading="tagLoading">
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="name" label="标签名称" />
          <el-table-column prop="description" label="描述" />
          <el-table-column prop="createTime" label="创建时间" width="160" />
          <el-table-column label="操作" width="200" align="center">
            <template #default="scope">
              <el-button size="small" type="primary" @click="openTagDialog(scope.row)">编辑</el-button>
              <el-button size="small" type="danger" @click="deleteTag(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 系统日志 -->
      <div v-if="activeMenu === 'logs'" class="table-container">
        <div class="toolbar">
          <el-button type="danger" @click="cleanLogs">清理日志</el-button>
        </div>
        <el-alert title="日志功能待实现" type="info" :closable="false" />
      </div>
    </main>

    <!-- 用户角色分配弹窗 -->
    <el-dialog v-model="dialogVisible" title="分配权限角色" width="400px">
      <div style="margin-bottom: 20px;">正在为用户 <b>{{ currentUser.nickname }}</b> 调整身份：</div>
      <el-select v-model="selectedRole" placeholder="请选择新角色" style="width: 100%">
        <el-option label="超级管理员 (全部权限)" value="SUPER_ADMIN" />
        <el-option label="内容管理员 (删文章/删评论)" value="CONTENT_ADMIN" />
        <el-option label="用户管理员 (删用户/清日志)" value="USER_ADMIN" />
        <el-option label="普通用户" value="USER" />
      </el-select>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmAssign">确认分配</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 文章编辑弹窗 -->
    <el-dialog v-model="articleDialogVisible" :title="articleDialogTitle" width="80%" top="5vh">
      <el-form :model="articleForm" label-width="80px" :rules="articleRules" ref="articleFormRef">
        <el-form-item label="标题" prop="title">
          <el-input v-model="articleForm.title" placeholder="请输入文章标题" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="articleForm.category" placeholder="请选择分类" style="width: 200px;">
            <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="articleForm.status" placeholder="请选择状态" style="width: 200px;">
            <el-option label="草稿" value="DRAFT" />
            <el-option label="已发布" value="PUBLISHED" />
            <el-option label="已删除" value="DELETED" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签" prop="tags">
          <el-select v-model="articleForm.tags" multiple placeholder="请选择标签" style="width: 100%;">
            <el-option v-for="tag in allTags" :key="tag.id" :label="tag.name" :value="tag.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="摘要" prop="summary">
          <el-input v-model="articleForm.summary" type="textarea" :rows="3" placeholder="请输入文章摘要（可选）" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <div style="border: 1px solid #dcdfe6; border-radius: 4px;">
            <textarea id="editor" style="height: 400px;"></textarea>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="articleDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveArticle">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 评论审核弹窗 -->
    <el-dialog v-model="auditDialogVisible" title="评论审核" width="500px">
      <el-form :model="auditForm" label-width="80px">
        <el-form-item label="评论内容">
          <div style="padding: 10px; background: #f5f7fa; border-radius: 4px;">{{ auditForm.content }}</div>
        </el-form-item>
        <el-form-item label="审核结果" prop="status">
          <el-radio-group v-model="auditForm.status">
            <el-radio label="APPROVED">通过</el-radio>
            <el-radio label="REJECTED">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核原因" prop="auditReason">
          <el-input v-model="auditForm.auditReason" type="textarea" :rows="3" placeholder="请输入审核原因（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="auditDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmAudit">确认审核</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 分类编辑弹窗 -->
    <el-dialog v-model="categoryDialogVisible" :title="categoryDialogTitle" width="400px">
      <el-form :model="categoryForm" label-width="80px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="categoryDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveCategory">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 标签编辑弹窗 -->
    <el-dialog v-model="tagDialogVisible" :title="tagDialogTitle" width="400px">
      <el-form :model="tagForm" label-width="80px">
        <el-form-item label="标签名称" prop="name">
          <el-input v-model="tagForm.name" placeholder="请输入标签名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="tagForm.description" type="textarea" :rows="3" placeholder="请输入标签描述（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="tagDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveTag">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import { User, Document, ChatDotRound, Warning, Folder, PriceTag } from '@element-plus/icons-vue'

const router = useRouter()
const activeMenu = ref('users')
const pageTitle = computed(() => {
  const map = {
    users: '用户管理',
    articles: '文章管理',
    comments: '评论审核',
    categories: '分类管理',
    tags: '标签管理',
    logs: '系统日志'
  }
  return map[activeMenu.value] || '管理员控制台'
})

// 用户管理相关
const users = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const currentUser = ref({})
const selectedRole = ref('')

// 文章管理相关
const articles = ref([])
const articleLoading = ref(false)
const articleFilters = ref({
  status: '',
  category: '',
  keyword: ''
})
const articlePage = ref({ current: 1, size: 20, total: 0 })
const selectedArticleIds = ref([])
const categories = ref([])
const allTags = ref([])

// 评论管理相关
const comments = ref([])
const commentLoading = ref(false)
const commentFilters = ref({
  status: '',
  articleId: '',
  keyword: ''
})
const commentPage = ref({ current: 1, size: 20, total: 0 })
const selectedCommentIds = ref([])

// 分类管理相关
const categoryLoading = ref(false)

// 标签管理相关
const tags = ref([])
const tagLoading = ref(false)
const tagFilters = ref({ keyword: '' })

// 弹窗相关
const articleDialogVisible = ref(false)
const articleDialogTitle = ref('')
const articleForm = ref({})
const articleFormRef = ref()
const articleRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const auditDialogVisible = ref(false)
const auditForm = ref({})
const categoryDialogVisible = ref(false)
const categoryDialogTitle = ref('')
const categoryForm = ref({})
const tagDialogVisible = ref(false)
const tagDialogTitle = ref('')
const tagForm = ref({})

// 菜单选择
const handleMenuSelect = (index) => {
  activeMenu.value = index
  switch (index) {
    case 'users':
      fetchUsers()
      break
    case 'articles':
      fetchArticles()
      fetchCategories()
      fetchAllTags()
      break
    case 'comments':
      fetchComments()
      break
    case 'categories':
      fetchCategories()
      break
    case 'tags':
      fetchTags()
      break
    case 'logs':
      // 日志页面
      break
  }
}

// ========== 用户管理函数 ==========
const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/admin/users')
    if (res.data.code === 200) users.value = res.data.data
  } catch (e) {
    ElMessage.error('权限不足或网络错误')
  } finally {
    loading.value = false
  }
}

const getRoleTag = (role) => {
  if (role === 'SUPER_ADMIN') return 'danger'
  if (role === 'CONTENT_ADMIN') return 'warning'
  if (role === 'USER_ADMIN') return 'success'
  return 'info'
}

const openAssignDialog = (row) => {
  if (row.username === 'admin') return ElMessage.warning('禁止修改超级管理员权限！')
  currentUser.value = row
  selectedRole.value = row.role
  dialogVisible.value = true
}

const confirmAssign = async () => {
  try {
    await axios.post('/api/admin/assignRole', {
      userId: currentUser.value.id,
      roleCode: selectedRole.value
    })
    ElMessage.success('角色更新成功')
    dialogVisible.value = false
    fetchUsers()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确认要永久删除该用户吗？', '高危操作', { type: 'warning' }).then(async () => {
    try {
      await axios.delete(`/api/admin/user/${id}`)
      ElMessage.success('用户已删除')
      fetchUsers()
    } catch (e) {
      ElMessage.error('你没有删除用户的权限！(测试 RBAC 拦截)')
    }
  }).catch(() => {})
}

// ========== 文章管理函数 ==========
const fetchArticles = async () => {
  articleLoading.value = true
  try {
    const params = {
      status: articleFilters.value.status,
      category: articleFilters.value.category,
      keyword: articleFilters.value.keyword
    }
    const res = await axios.get('/api/admin/articles', { params })
    if (res.data.code === 200) {
      articles.value = res.data.data
      articlePage.value.total = res.data.data.length // 暂时没有分页，使用总数
    }
  } catch (e) {
    ElMessage.error('加载文章列表失败')
  } finally {
    articleLoading.value = false
  }
}

const fetchCategories = async () => {
  try {
    const res = await axios.get('/api/admin/articles/categories')
    if (res.data.code === 200) {
      categories.value = res.data.data
    }
  } catch (e) {
    console.error('加载分类失败', e)
  }
}

const fetchAllTags = async () => {
  try {
    const res = await axios.get('/api/admin/articles/tags')
    if (res.data.code === 200) {
      allTags.value = res.data.data
    }
  } catch (e) {
    console.error('加载标签失败', e)
  }
}

const resetArticleFilters = () => {
  articleFilters.value = { status: '', category: '', keyword: '' }
  fetchArticles()
}

const getArticleStatusTag = (status) => {
  const map = { DRAFT: 'info', PUBLISHED: 'success', DELETED: 'danger' }
  return map[status] || 'info'
}

const handleArticleSelectionChange = (selection) => {
  selectedArticleIds.value = selection.map(item => item.id)
}

const openArticleDialog = (article) => {
  if (article) {
    articleDialogTitle.value = '编辑文章'
    articleForm.value = { ...article, tags: [] } // 需要加载文章标签
  } else {
    articleDialogTitle.value = '新建文章'
    articleForm.value = { status: 'DRAFT', tags: [] }
  }
  articleDialogVisible.value = true
  nextTick(() => {
    // 初始化富文本编辑器
    if (window.tinymce) {
      window.tinymce.init({
        selector: '#editor',
        height: 400,
        menubar: false,
        plugins: 'link image code lists',
        toolbar: 'undo redo | formatselect | bold italic | alignleft aligncenter alignright | bullist numlist | link image | code'
      })
    }
  })
}

const saveArticle = async () => {
  if (!articleFormRef.value) return
  await articleFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (articleForm.value.id) {
        await axios.put(`/api/admin/articles/${articleForm.value.id}`, articleForm.value)
        ElMessage.success('文章更新成功')
      } else {
        await axios.post('/api/article/publish', articleForm.value)
        ElMessage.success('文章创建成功')
      }
      articleDialogVisible.value = false
      fetchArticles()
    } catch (e) {
      ElMessage.error('保存失败')
    }
  })
}

const toggleArticleStatus = async (article) => {
  const newStatus = article.status === 'PUBLISHED' ? 'DRAFT' : 'PUBLISHED'
  try {
    await axios.put(`/api/admin/articles/${article.id}/status`, { status: newStatus })
    ElMessage.success('状态更新成功')
    fetchArticles()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const deleteArticle = (id) => {
  ElMessageBox.confirm('确认要删除这篇文章吗？', '提示', { type: 'warning' }).then(async () => {
    try {
      await axios.delete(`/api/admin/articles/${id}`)
      ElMessage.success('文章删除成功')
      fetchArticles()
    } catch (e) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const batchDeleteArticles = () => {
  ElMessageBox.confirm(`确认要删除选中的 ${selectedArticleIds.value.length} 篇文章吗？`, '批量删除', { type: 'warning' }).then(async () => {
    try {
      // 批量删除接口待实现，这里循环调用
      for (const id of selectedArticleIds.value) {
        await axios.delete(`/api/admin/articles/${id}`)
      }
      ElMessage.success('批量删除成功')
      selectedArticleIds.value = []
      fetchArticles()
    } catch (e) {
      ElMessage.error('批量删除失败')
    }
  }).catch(() => {})
}

// ========== 评论管理函数 ==========
const fetchComments = async () => {
  commentLoading.value = true
  try {
    const params = {
      status: commentFilters.value.status,
      articleId: commentFilters.value.articleId,
      keyword: commentFilters.value.keyword
    }
    const res = await axios.get('/api/admin/articles/comments', { params })
    if (res.data.code === 200) {
      comments.value = res.data.data
      commentPage.value.total = res.data.data.length
    }
  } catch (e) {
    ElMessage.error('加载评论列表失败')
  } finally {
    commentLoading.value = false
  }
}

const resetCommentFilters = () => {
  commentFilters.value = { status: '', articleId: '', keyword: '' }
  fetchComments()
}

const getCommentStatusTag = (status) => {
  const map = { PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger' }
  return map[status] || 'info'
}

const handleCommentSelectionChange = (selection) => {
  selectedCommentIds.value = selection.map(item => item.id)
}

const showAuditDialog = (comment) => {
  auditForm.value = { ...comment, status: 'APPROVED', auditReason: '' }
  auditDialogVisible.value = true
}

const confirmAudit = async () => {
  try {
    await axios.put(`/api/admin/articles/comments/${auditForm.value.id}/audit`, {
      status: auditForm.value.status,
      auditReason: auditForm.value.auditReason
    })
    ElMessage.success('审核完成')
    auditDialogVisible.value = false
    fetchComments()
  } catch (e) {
    ElMessage.error('审核失败')
  }
}

const auditComment = async (id, status) => {
  try {
    await axios.put(`/api/admin/articles/comments/${id}/audit`, { status, auditReason: '' })
    ElMessage.success('操作成功')
    fetchComments()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const batchAuditComments = (status) => {
  if (selectedCommentIds.value.length === 0) return
  ElMessageBox.prompt('请输入审核原因（可选）', `批量${status === 'APPROVED' ? '通过' : '拒绝'}`, {
    inputPlaceholder: '审核原因',
    type: 'warning'
  }).then(async ({ value }) => {
    try {
      await axios.put('/api/admin/articles/comments/batch-audit', {
        ids: selectedCommentIds.value,
        status,
        auditReason: value
      })
      ElMessage.success('批量审核完成')
      selectedCommentIds.value = []
      fetchComments()
    } catch (e) {
      ElMessage.error('批量审核失败')
    }
  }).catch(() => {})
}

// ========== 分类管理函数 ==========
const openCategoryDialog = (category) => {
  if (category) {
    categoryDialogTitle.value = '编辑分类'
    categoryForm.value = { ...category }
  } else {
    categoryDialogTitle.value = '新建分类'
    categoryForm.value = { name: '' }
  }
  categoryDialogVisible.value = true
}

const saveCategory = async () => {
  // 分类保存逻辑待实现（需要后端API）
  ElMessage.warning('分类管理功能待实现')
  categoryDialogVisible.value = false
}

const deleteCategory = (category) => {
  ElMessageBox.confirm(`确认要删除分类 "${category.value}" 吗？`, '提示', { type: 'warning' }).then(() => {
    ElMessage.warning('分类删除功能待实现')
  }).catch(() => {})
}

// ========== 标签管理函数 ==========
const fetchTags = async () => {
  tagLoading.value = true
  try {
    const res = await axios.get('/api/admin/articles/tags')
    if (res.data.code === 200) {
      tags.value = res.data.data
    }
  } catch (e) {
    ElMessage.error('加载标签列表失败')
  } finally {
    tagLoading.value = false
  }
}

const resetTagFilters = () => {
  tagFilters.value = { keyword: '' }
  fetchTags()
}

const openTagDialog = (tag) => {
  if (tag) {
    tagDialogTitle.value = '编辑标签'
    tagForm.value = { ...tag }
  } else {
    tagDialogTitle.value = '新建标签'
    tagForm.value = { name: '', description: '' }
  }
  tagDialogVisible.value = true
}

const saveTag = async () => {
  try {
    if (tagForm.value.id) {
      await axios.put(`/api/admin/articles/tags/${tagForm.value.id}`, tagForm.value)
      ElMessage.success('标签更新成功')
    } else {
      await axios.post('/api/admin/articles/tags', tagForm.value)
      ElMessage.success('标签创建成功')
    }
    tagDialogVisible.value = false
    fetchTags()
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const deleteTag = (id) => {
  ElMessageBox.confirm('确认要删除这个标签吗？', '提示', { type: 'warning' }).then(async () => {
    try {
      await axios.delete(`/api/admin/articles/tags/${id}`)
      ElMessage.success('标签删除成功')
      fetchTags()
    } catch (e) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// ========== 系统日志函数 ==========
const cleanLogs = () => {
  ElMessageBox.confirm('确认要清理系统日志吗？', '清理日志', { type: 'warning' }).then(async () => {
    try {
      await axios.post('/api/admin/logs/clean')
      ElMessage.success('日志清理完成')
    } catch (e) {
      ElMessage.error('清理失败')
    }
  }).catch(() => {})
}

// 初始化
onMounted(() => {
  fetchUsers()
})
</script>

<style lang="scss" scoped>
.admin-layout { display: flex; height: 100vh; background: #f0f2f5; }
.sidebar { width: 220px; background: #2c3e50; color: white; display: flex; flex-direction: column;
  .brand { height: 60px; display: flex; align-items: center; justify-content: center; font-size: 20px; font-weight: bold; background: #1a252f; letter-spacing: 1px; }
  .el-menu { border-right: none; flex: 1; }
}
.main-content { flex: 1; display: flex; flex-direction: column; overflow: hidden; }
.top-header { height: 60px; background: white; padding: 0 20px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
.table-container { padding: 20px; flex: 1; overflow-y: auto; display: flex; flex-direction: column; }
.toolbar { margin-bottom: 15px; display: flex; flex-wrap: wrap; gap: 10px; align-items: center; }
.pagination { margin-top: 20px; display: flex; justify-content: center; }
</style>