<template>
  <div class="not-found-page">
    <div class="content">
      <div class="error-code">404</div>
      <h1>页面未找到</h1>
      <p>抱歉，您访问的页面不存在或已被删除</p>
      <div class="actions">
        <el-button type="primary" size="large" @click="goHome">
          <el-icon><HomeFilled /></el-icon>
          返回首页
        </el-button>
        <el-button size="large" @click="goBack">
          <el-icon><Back /></el-icon>
          返回上页
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const goBack = () => {
  router.go(-1)
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.not-found-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, $primary-color 0%, #764ba2 100%);
  padding: 20px;
}

.content {
  text-align: center;
  color: white;
}

.error-code {
  font-size: 150px;
  font-weight: 700;
  line-height: 1;
  margin-bottom: 20px;
  text-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

h1 {
  font-size: 36px;
  font-weight: 600;
  margin-bottom: 15px;
}

p {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 40px;
}

.actions {
  display: flex;
  gap: 20px;
  justify-content: center;
  
  .el-button {
    min-width: 140px;
    height: 45px;
    font-size: 16px;
  }
}

@media (max-width: 576px) {
  .error-code {
    font-size: 100px;
  }
  
  h1 {
    font-size: 28px;
  }
  
  p {
    font-size: 16px;
  }
  
  .actions {
    flex-direction: column;
    
    .el-button {
      width: 100%;
    }
  }
}
</style>
