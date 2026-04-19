<template>
  <header class="admin-header">
    <div class="header-content">
      <div class="header-left">
        <el-button type="text" class="menu-toggle" @click="toggleSidebar">
          <el-icon><Menu /></el-icon>
        </el-button>
        <h2 class="page-title">{{ pageTitle }}</h2>
      </div>
      
      <div class="header-right">
        <el-dropdown @command="handleCommand">
          <div class="user-info">
            <el-avatar :size="35" class="user-avatar">
              {{ (userStore.nickname || '管理员').charAt(0) }}
            </el-avatar>
            <span class="username">{{ userStore.nickname || '管理员' }}</span>
            <el-icon><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <el-icon><User /></el-icon>
                个人中心
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const pageTitle = computed(() => {
  return route.meta.title || '管理后台'
})

const toggleSidebar = () => {
  const sidebar = document.querySelector('.admin-sidebar')
  if (sidebar) {
    sidebar.classList.toggle('collapsed')
  }
}

const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/admin/profile')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await userStore.logout()
        ElMessage.success('退出成功')
        router.push('/')
      } catch (error) {
        if (error !== 'cancel') {
          console.error(error)
        }
      }
      break
  }
}
</script>

<style lang="scss" scoped>
.admin-header {
  background: white;
  border-bottom: 1px solid #e2e8f0;
  padding: 0 20px;
  height: 60px;
  display: flex;
  align-items: center;
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.menu-toggle {
  font-size: 20px;
  color: #4a5568;
  
  &:hover {
    background: #f7fafc;
  }
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #2d3748;
  margin: 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 20px;
  transition: all 0.3s ease;
  
  &:hover {
    background: #f7fafc;
  }
  
  .user-avatar {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  }
  
  .username {
    font-size: 14px;
    color: #2d3748;
    max-width: 100px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>