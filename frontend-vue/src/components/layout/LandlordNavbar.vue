<template>
  <header class="landlord-navbar">
    <div class="nav-container">
      <router-link to="/landlord" class="logo">
        <el-icon><OfficeBuilding /></el-icon>
        <span>房东管理中心</span>
      </router-link>

      <nav class="nav-menu">
        <router-link to="/landlord/houses" class="nav-item">房源管理</router-link>
        <router-link to="/landlord/appointments" class="nav-item">预约处理</router-link>
        <router-link to="/landlord/contracts" class="nav-item">合同管理</router-link>
        <router-link to="/landlord/bills" class="nav-item">账单管理</router-link>
        <router-link to="/landlord/repairs" class="nav-item">报修处理</router-link>
      </nav>

      <div class="nav-right">
        <el-dropdown @command="handleCommand">
          <div class="user-info">
            <el-avatar :size="35" class="user-avatar">
              {{ (userStore.nickname || '用户').charAt(0) }}
            </el-avatar>
            <span class="username">{{ userStore.nickname || '用户' }}</span>
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
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/landlord/profile')
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

.landlord-navbar {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  box-shadow: 0 2px 15px rgba(17, 153, 142, 0.3);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.nav-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: $navbar-height;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 24px;
  font-weight: bold;
  color: white;
  text-decoration: none;
  
  .el-icon {
    font-size: 28px;
  }
}

.nav-menu {
  display: flex;
  gap: 30px;
  
  .nav-item {
    color: rgba(255, 255, 255, 0.9);
    text-decoration: none;
    font-size: 15px;
    transition: $transition-base;
    position: relative;
    
    &:hover {
      color: white;
    }
    
    &.router-link-active {
      color: white;
      font-weight: 500;
      
      &::after {
        content: '';
        position: absolute;
        bottom: -5px;
        left: 0;
        right: 0;
        height: 2px;
        background: white;
        border-radius: 2px;
      }
    }
  }
}

.nav-right {
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
  transition: $transition-base;
  
  &:hover {
    background: rgba(255, 255, 255, 0.1);
  }
  
  .user-avatar {
    background: rgba(255, 255, 255, 0.2);
  }
  
  .username {
    font-size: 14px;
    color: white;
    max-width: 100px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

@media (max-width: 768px) {
  .nav-menu {
    display: none;
  }
  
  .username {
    display: none;
  }
}
</style>