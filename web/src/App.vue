<script setup>
/**
 * LF 社区 2026 全新启航
 * 核心补丁：修复 Element Plus 在某些浏览器下 ResizeObserver 循环报错的问题
 */
const debounce = (fn, delay) => {
  let timer = null;
  return function () {
    let context = this;
    let args = arguments;
    clearTimeout(timer);
    timer = setTimeout(function () {
      fn.apply(context, args);
    }, delay);
  }
}

const _ResizeObserver = window.ResizeObserver;
window.ResizeObserver = class ResizeObserver extends _ResizeObserver {
  constructor(callback) {
    callback = debounce(callback, 16);
    super(callback);
  }
}
</script>

<template>
  <router-view />
</template>

<style>
/* 全局基础样式重置 */
body {
  margin: 0;
  padding: 0;
  background-color: #f5f7f9;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

#app {
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* 统一 Element Plus 的某些微调 */
:deep(.el-card) {
  border-radius: 8px;
}
</style>