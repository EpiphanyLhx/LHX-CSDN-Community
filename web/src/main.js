import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import axios from 'axios'

// 1. 引入 Element Plus 的所有图标
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App)

// 2. 将图标全局注册到 Vue 实例中 (解决图标不显示的核心代码)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

// 挂载路由和 UI 组件库
app.use(router).use(ElementPlus).mount('#app')

axios.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    if (token) {
        // 按照行业标准，在 Header 里加入 Bearer Token
        config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
}, error => {
    return Promise.reject(error)
})