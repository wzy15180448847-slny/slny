<template>
  <header class="navbar">
    <div class="nav-container">
      <router-link to="/" class="logo">
        <el-icon><House /></el-icon>
        <span>房屋租赁平台</span>
      </router-link>

      <nav class="nav-menu">
        <router-link to="/" class="nav-item">首页</router-link>
        <router-link to="/search" class="nav-item">找房</router-link>
        <router-link 
          v-if="userStore.isLoggedIn && canPublish" 
          to="/publish" 
          class="nav-item"
        >
          发布房源
        </router-link>
      </nav>

      <div class="nav-right">
        <template v-if="userStore.isLoggedIn">
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="35" class="user-avatar">
                {{ userStore.nickname.charAt(0) }}
              </el-avatar>
              <span class="username">{{ userStore.nickname }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item command="my-houses" v-if="canPublish">
                  <el-icon><House /></el-icon>
                  我的房源
                </el-dropdown-item>
                <el-dropdown-item command="favorites">
                  <el-icon><Star /></el-icon>
                  我的收藏
                </el-dropdown-item>
                <el-dropdown-item command="appointments">
                  <el-icon><Calendar /></el-icon>
                  我的预约
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <router-link to="/login" class="btn btn-outline">登录</router-link>
          <router-link to="/register" class="btn btn-primary">注册</router-link>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const canPublish = computed(() => {
  return ['LANDLORD', 'AGENT'].includes(userStore.userType)
})

const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'my-houses':
      router.push('/my-houses')
      break
    case 'favorites':
      router.push('/my-favorites')
      break
    case 'appointments':
      router.push('/my-appointments')
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

.navbar {
  background: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.nav-container {
  max-width: 1200px;
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
  color: $primary-color;
  text-decoration: none;
  
  .el-icon {
    font-size: 28px;
  }
}

.nav-menu {
  display: flex;
  gap: 30px;
  
  .nav-item {
    color: $text-primary;
    text-decoration: none;
    font-size: 15px;
    transition: $transition-base;
    position: relative;
    
    &:hover {
      color: $primary-color;
    }
    
    &.router-link-active {
      color: $primary-color;
      font-weight: 500;
      
      &::after {
        content: '';
        position: absolute;
        bottom: -5px;
        left: 0;
        right: 0;
        height: 2px;
        background: $primary-color;
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

.btn {
  padding: 10px 25px;
  border-radius: 20px;
  border: none;
  cursor: pointer;
  font-size: 14px;
  text-decoration: none;
  transition: $transition-base;
  
  &.btn-outline {
    background: transparent;
    border: 1px solid $primary-color;
    color: $primary-color;
    
    &:hover {
      background: $primary-color;
      color: white;
    }
  }
  
  &.btn-primary {
    background: linear-gradient(135deg, $primary-color 0%, #764ba2 100%);
    color: white;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 5px 15px rgba(102, 126, 234, 0.3);
    }
  }
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
    background: $bg-color;
  }
  
  .user-avatar {
    background: linear-gradient(135deg, $primary-color 0%, #764ba2 100%);
  }
  
  .username {
    font-size: 14px;
    color: $text-primary;
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
