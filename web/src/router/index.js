import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  // 核心修复点：添加带动态参数 :id 的文章详情页路由
  {
    path: '/article/:id',
    name: 'ArticleDetail',
    component: () => import('../views/ArticleDetail.vue')
  },
  { path: '/publish', name: 'Publish', component: () => import('../views/Publish.vue') },

  { path: '/user', name: 'UserCenter', component: () => import('../views/UserCenter.vue') },

  { path: '/admin', name: 'AdminDashboard', component: () => import('../views/AdminDashboard.vue') },

  { path: '/search', name: 'Search', component: () => import('../views/Search.vue') },
]

const router = createRouter({
  // 使用 hash 模式
  history: createWebHashHistory(),
  routes
})

export default router