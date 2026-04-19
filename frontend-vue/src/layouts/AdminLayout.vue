<template>
  <el-container class="admin-container">
    <el-aside :width="isCollapsed ? '64px' : '220px'" class="admin-sidebar">
      <div class="sidebar-header">
        <div class="logo" @click="toggleSidebar">
          <el-icon :size="24"><Setting /></el-icon>
          <span v-show="!isCollapsed" class="logo-text">管理后台</span>
        </div>
      </div>
      
      <el-menu 
        :default-active="activeMenu" 
        mode="vertical" 
        :collapse="isCollapsed"
        background-color="#1f2937" 
        text-color="#a0aec0" 
        active-text-color="#fff"
        class="sidebar-menu"
        router
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><PieChart /></el-icon>
          <template #title>数据大屏</template>
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
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/admin/credit">信用管理</el-menu-item>
          <el-menu-item index="/admin/logs">系统日志</el-menu-item>
          <el-menu-item index="/admin/complaints">投诉仲裁</el-menu-item>
          <el-menu-item index="/admin/profile">个人中心</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    
    <el-container class="admin-content-wrapper">
      <el-header class="admin-header">
        <div class="header-left">
          <el-button type="text" class="menu-toggle" @click="toggleSidebar">
            <el-icon :size="20"><Menu /></el-icon>
          </el-button>
          <h2 class="page-title">{{ pageTitle }}</h2>
        </div>
        
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="35" class="user-avatar">
                {{ userStore.nickname?.charAt(0) || 'A' }}
              </el-avatar>
              <span v-show="!isCollapsed" class="username">{{ userStore.nickname }}</span>
              <el-icon :size="16"><ArrowDown /></el-icon>
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
      </el-header>
      
      <el-main class="admin-main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import { 
  Setting, PieChart, OfficeBuilding, User, 
  Menu, ArrowDown, SwitchButton 
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const isCollapsed = ref(false)

const activeMenu = computed(() => {
  return route.path
})

const pageTitle = computed(() => {
  return route.meta.title || '管理后台'
})

const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value
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
@import '@/styles/variables.scss';

.admin-container {
  height: 100%;
  background: $bg-color;
}

.admin-sidebar {
  background: linear-gradient(180deg, #1f2937 0%, #111827 100%);
  color: #fff;
  overflow: hidden;
  transition: width 0.3s ease;
  border-right: 1px solid #374151;
  
  .sidebar-header {
    padding: 16px;
    border-bottom: 1px solid #374151;
    
    .logo {
      display: flex;
      align-items: center;
      gap: 10px;
      cursor: pointer;
      
      svg {
        color: $primary-color;
      }
      
      .logo-text {
        font-size: 18px;
        font-weight: 600;
        color: #fff;
      }
    }
  }
  
  .sidebar-menu {
    border-right: none;
    padding: 10px 0;
    
    :deep(.el-menu-item), 
    :deep(.el-sub-menu__title) {
      margin: 4px 8px;
      border-radius: 8px;
      transition: all 0.2s ease;
      
      &:hover {
        background: rgba(102, 126, 234, 0.15);
      }
    }
    
    :deep(.el-menu-item.is-active) {
      background: linear-gradient(135deg, $primary-color 0%, #764ba2 100%);
    }
  }
}

.admin-content-wrapper {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.admin-header {
  background: #fff;
  border-bottom: 1px solid #e5e7eb;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 15px;
  }
  
  .menu-toggle {
    font-size: 20px;
    color: #4b5563;
    padding: 8px;
    
    &:hover {
      background: #f3f4f6;
    }
  }
  
  .page-title {
    font-size: 18px;
    font-weight: 600;
    color: #1f2937;
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
    padding: 6px 12px;
    border-radius: 20px;
    transition: all 0.2s ease;
    
    &:hover {
      background: #f3f4f6;
    }
    
    .user-avatar {
      background: linear-gradient(135deg, $primary-color 0%, #764ba2 100%);
    }
    
    .username {
      font-size: 14px;
      color: #374151;
    }
  }
}

.admin-main {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  height: calc(100vh - 60px);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>