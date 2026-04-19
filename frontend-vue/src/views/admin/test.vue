<template>
  <div class="test-page">
    <h1>管理员测试页面</h1>
    <p>登录成功！欢迎来到管理员后台</p>
    <p>用户类型: {{ userStore.userType }}</p>
    <p>用户名: {{ userStore.username }}</p>
  </div>
</template>

<script setup>
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
console.log('Test page - userStore:', userStore)
console.log('Test page - userType:', userStore.userType)
</script>

<style scoped>
.test-page {
  padding: 20px;
}
</style>