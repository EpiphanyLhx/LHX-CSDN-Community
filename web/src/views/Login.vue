<template>
  <div class="auth-container">
    <div class="auth-card">
      <div class="auth-brand">
        <div class="brand-content">
          <div class="logo-area">
            <img src="https://csdnimg.cn/release/cms_static/re-pwa/images/csdn-logo.png" alt="logo">
            <span class="divider"></span>
            <span class="platform-name">LF 社区</span>
          </div>
          <h1>探索技术的无限可能</h1>
          <p>加入 com.LFCSDN，与全球开发者共同成长，分享每一行代码的价值。</p>
          <div class="stats">
            <div class="stat-item"><b>10w+</b><span>博文</span></div>
            <div class="stat-item"><b>5w+</b><span>开源代码</span></div>
            <div class="stat-item"><b>24h</b><span>技术交流</span></div>
          </div>
        </div>
        <div class="glow-1"></div>
        <div class="glow-2"></div>
      </div>

      <div class="auth-form-box">
        <el-tabs v-model="activeTab" class="custom-tabs" stretch>
          <el-tab-pane label="密码登录" name="login">
            <el-form :model="loginData" class="form-fade-in">
              <el-form-item>
                <el-input v-model="loginData.username" placeholder="用户名" prefix-icon="User" size="large" />
              </el-form-item>
              <el-form-item>
                <el-input v-model="loginData.password" type="password" placeholder="密码" prefix-icon="Lock" show-password size="large" />
              </el-form-item>
              <div class="form-options">
                <el-checkbox v-model="rememberMe">记住我</el-checkbox>
                <el-link type="info" :underline="false">忘记密码？</el-link>
              </div>
              <el-button type="danger" class="submit-btn" @click="handleLogin" :loading="loading" size="large">立即登录</el-button>
            </el-form>
          </el-tab-pane>

          <el-tab-pane label="用户注册" name="register">
            <el-form :model="registerData" class="form-fade-in">
              <el-form-item>
                <el-input v-model="registerData.username" placeholder="设置用户名" prefix-icon="Plus" size="large" />
              </el-form-item>
              <el-form-item>
                <el-input v-model="registerData.nickname" placeholder="显示昵称" prefix-icon="Avatar" size="large" />
              </el-form-item>
              <el-form-item>
                <el-input v-model="registerData.password" type="password" placeholder="设置 6-16 位密码" prefix-icon="Lock" show-password size="large" />
              </el-form-item>
              <el-button type="primary" class="submit-btn register-btn" @click="handleRegister" :loading="loading" size="large">创建账号</el-button>
            </el-form>
          </el-tab-pane>
        </el-tabs>

        <div class="social-login">
          <p>其他登录方式</p>
          <div class="social-icons">
            <el-avatar :size="35" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
            <el-avatar :size="35" icon="ChatDotRound" />
            <el-avatar :size="35" icon="Platform" />
          </div>
        </div>
      </div>
    </div>

    <footer class="auth-footer">
      Copyright © 2026 com.LHXCSDN. All Rights Reserved.
    </footer>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const activeTab = ref('login')
const loading = ref(false)
const rememberMe = ref(false)
const router = useRouter()

const loginData = reactive({ username: '', password: '' })
const registerData = reactive({ username: '', nickname: '', password: '' })

// 注册请求
const handleRegister = async () => {
  if (!registerData.username || !registerData.password) {
    return ElMessage.warning('请填写完整信息')
  }
  loading.value = true
  try {
    const res = await axios.post('/api/user/register', registerData)
    if (res.data.code === 200) {
      ElMessage.success('注册成功！快去登录吧')
      activeTab.value = 'login'
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (error) {
    ElMessage.error('连接服务器失败，请检查后端')
  } finally {
    loading.value = false
  }

  try {
    const res = await axios.post('/api/user/login', loginForm)
    if (res.data.code === 200) {
      // ✨ 核心操作：把 Token 和昵称存入浏览器的本地存储
      localStorage.setItem('token', res.data.data.token)
      localStorage.setItem('nickname', res.data.data.nickname)

      ElMessage.success('欢迎回来，' + res.data.data.nickname)
      router.push('/home')
    }
  } catch (error) {
    ElMessage.error('登录失败，请检查账号密码')
  }
}

// 登录请求（模拟跳转）
const handleLogin = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    router.push('/home')
  }, 800)
}
</script>

<style lang="scss" scoped>
.auth-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: #f0f2f5;
  background: radial-gradient(circle at 50% 50%, #fdfdfd 0%, #e5e7eb 100%);
  overflow: hidden;

  .auth-card {
    display: flex;
    width: 900px;
    height: 550px;
    background: white;
    border-radius: 20px;
    box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.15);
    overflow: hidden;
    position: relative;
    z-index: 10;
  }

  /* 左侧品牌区 */
  .auth-brand {
    flex: 1.2;
    background: #2c3e50;
    position: relative;
    padding: 60px;
    display: flex;
    align-items: center;
    color: white;
    overflow: hidden;

    .brand-content {
      position: relative;
      z-index: 5;
      .logo-area {
        display: flex;
        align-items: center;
        margin-bottom: 40px;
        img { height: 35px; }
        .divider { width: 1px; height: 20px; background: rgba(255,255,255,0.3); margin: 0 15px; }
        .platform-name { font-weight: bold; font-size: 18px; letter-spacing: 1px; }
      }
      h1 { font-size: 32px; margin-bottom: 20px; font-weight: 800; line-height: 1.3; }
      p { font-size: 16px; opacity: 0.8; line-height: 1.6; margin-bottom: 40px; }
      .stats {
        display: flex;
        gap: 30px;
        .stat-item {
          b { display: block; font-size: 20px; }
          span { font-size: 12px; opacity: 0.6; }
        }
      }
    }

    .glow-1 { position: absolute; top: -10%; left: -10%; width: 300px; height: 300px; background: radial-gradient(circle, rgba(252,61,57,0.4) 0%, transparent 70%); }
    .glow-2 { position: absolute; bottom: -5%; right: -5%; width: 250px; height: 250px; background: radial-gradient(circle, rgba(44,126,248,0.3) 0%, transparent 70%); }
  }

  /* 右侧表单区 */
  .auth-form-box {
    flex: 1;
    padding: 50px 60px;
    display: flex;
    flex-direction: column;
    justify-content: center;

    .custom-tabs {
      :deep(.el-tabs__item) { font-size: 16px; height: 50px; }
      :deep(.el-tabs__active-bar) { background-color: #fc3d39; }
      :deep(.el-tabs__item.is-active) { color: #fc3d39; font-weight: bold; }
    }

    .form-fade-in { animation: fadeIn 0.4s ease-out; }

    .form-options {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      font-size: 14px;
    }

    .submit-btn {
      width: 100%;
      height: 48px;
      font-size: 16px;
      border-radius: 8px;
      font-weight: bold;
      transition: all 0.3s;
      &.register-btn { background: #409eff; border-color: #409eff; }
      &:hover { transform: translateY(-2px); box-shadow: 0 5px 15px rgba(252,61,57,0.3); }
    }

    .social-login {
      margin-top: 40px;
      text-align: center;
      p { font-size: 12px; color: #999; margin-bottom: 15px; position: relative;
        &::before, &::after { content: ''; position: absolute; top: 50%; width: 25%; height: 1px; background: #eee; }
        &::before { left: 0; } &::after { right: 0; }
      }
      .social-icons {
        display: flex;
        justify-content: center;
        gap: 20px;
        .el-avatar { cursor: pointer; transition: 0.3s; &:hover { transform: scale(1.1); } }
      }
    }
  }

  .auth-footer { margin-top: 30px; font-size: 13px; color: #9ca3af; }
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>