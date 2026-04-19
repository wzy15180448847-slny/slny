<template>
  <aside class="admin-sidebar">
    <div class="sidebar-header">
      <div class="logo">
        <el-icon><Setting /></el-icon>
        <span>管理后台</span>
      </div>
    </div>
    
    <nav class="sidebar-menu">
      <el-menu :default-active="activeMenu" mode="vertical" background-color="#2d3748" text-color="#a0aec0" active-text-color="#fff">
        <el-menu-item index="/admin/dashboard">
          <el-icon><PieChart /></el-icon>
          <span>数据大屏</span>
        </el-menu-item>
        <el-sub-menu index="house">
          <template #title>
            <el-icon><OfficeBuilding /></el-icon>
            <span>房源管理</span>
          </template>
          <el-menu-item index="/admin/houses">房源列表</el-menu-item>
          <el-menu-item index="/admin/house-audit">房源审核</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="user">
          <template #title>
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </template>
          <el-menu-item index="/admin/users">用户列表</el-menu-item>
          <el-menu-item index="/admin/qualification">资质审核</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="system">
          <template #title>
            <el-icon><Check /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/admin/credit">信用管理</el-menu-item>
          <el-menu-item index="/admin/logs">系统日志</el-menu-item>
          <el-menu-item index="/admin/complaints">投诉仲裁</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </nav>
  </aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const activeMenu = computed(() => {
  return route.path
})

router.afterEach(() => {
  const menuItems = document.querySelectorAll('.el-menu-item')
  menuItems.forEach(item => {
    item.classList.remove('is-active')
    if (item.getAttribute('index') === route.path) {
      item.classList.add('is-active')
    }
  })
})
</script>

<style lang="scss" scoped>
.admin-sidebar {
  width: 220px;
  background: #2d3748;
  height: 100vh;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 999;
  overflow-y: auto;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid #4a5568;
  
  .logo {
    display: flex;
    align-items: center;
    gap: 10px;
    color: white;
    font-size: 18px;
    font-weight: bold;
    
    .el-icon {
      font-size: 24px;
    }
  }
}

.sidebar-menu {
  padding: 10px 0;
}

:deep(.el-menu) {
  border-right: none;
  
  .el-menu-item, .el-sub-menu {
    margin: 5px 10px;
    border-radius: 8px;
    
    &:hover {
      background: rgba(255, 255, 255, 0.1);
    }
  }
  
  .el-menu-item.is-active {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  }
}
</style>