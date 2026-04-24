<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <div class="brand">LF Console</div>
      <el-menu default-active="1" background-color="#2c3e50" text-color="#fff" active-text-color="#409EFF">
        <el-menu-item index="1"><el-icon><User /></el-icon>用户管理</el-menu-item>
        <el-menu-item index="2"><el-icon><Document /></el-icon>文章管理</el-menu-item>
        <el-menu-item index="3"><el-icon><ChatDotRound /></el-icon>评论审核</el-menu-item>
        <el-menu-item index="4"><el-icon><Warning /></el-icon>系统日志</el-menu-item>
      </el-menu>
    </aside>

    <main class="main-content">
      <header class="top-header">
        <h2>用户及权限分配中心</h2>
        <div class="user-info">
          <el-avatar :size="32">Ad</el-avatar>
          <span style="margin-left: 10px;">admin (超级管理员)</span>
          <el-button type="danger" link style="margin-left: 15px;" @click="router.push('/home')">退出后台</el-button>
        </div>
      </header>

      <div class="table-container">
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
    </main>

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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import { User, Document, ChatDotRound, Warning } from '@element-plus/icons-vue'

const router = useRouter()
const users = ref([])
const loading = ref(false)

// 弹窗相关
const dialogVisible = ref(false)
const currentUser = ref({})
const selectedRole = ref('')

// 获取用户列表
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

// 角色标签颜色
const getRoleTag = (role) => {
  if (role === 'SUPER_ADMIN') return 'danger'
  if (role === 'CONTENT_ADMIN') return 'warning'
  if (role === 'USER_ADMIN') return 'success'
  return 'info'
}

// 打开分配弹窗
const openAssignDialog = (row) => {
  if (row.username === 'admin') return ElMessage.warning('禁止修改超级管理员权限！')
  currentUser.value = row
  selectedRole.value = row.role
  dialogVisible.value = true
}

// 确认分配角色请求
const confirmAssign = async () => {
  try {
    await axios.post('/api/admin/assignRole', {
      userId: currentUser.value.id,
      roleCode: selectedRole.value
    })
    ElMessage.success('角色更新成功')
    dialogVisible.value = false
    fetchUsers() // 刷新列表
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

// 删除用户请求
const handleDelete = (id) => {
  ElMessageBox.confirm('确认要永久删除该用户吗？', '高危操作', { type: 'warning' }).then(async () => {
    try {
      await axios.delete(`/api/admin/user/${id}`)
      ElMessage.success('用户已删除')
    } catch (e) {
      ElMessage.error('你没有删除用户的权限！(测试 RBAC 拦截)')
    }
  }).catch(() => {})
}

onMounted(fetchUsers)
</script>

<style lang="scss" scoped>
.admin-layout { display: flex; height: 100vh; background: #f0f2f5; }
.sidebar { width: 220px; background: #2c3e50; color: white; display: flex; flex-direction: column;
  .brand { height: 60px; display: flex; align-items: center; justify-content: center; font-size: 20px; font-weight: bold; background: #1a252f; letter-spacing: 1px; }
  .el-menu { border-right: none; flex: 1; }
}
.main-content { flex: 1; display: flex; flex-direction: column; overflow: hidden; }
.top-header { height: 60px; background: white; padding: 0 20px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
.table-container { padding: 20px; flex: 1; overflow-y: auto; }
</style>