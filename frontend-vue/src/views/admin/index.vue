<template>
  <div class="admin-page">
    <div class="admin-header">
      <h1>管理员后台</h1>
      <p>欢迎进入房屋租赁平台管理系统</p>
    </div>
    
    <div class="admin-grid">
      <div class="stat-card">
        <div class="stat-icon users-icon">
          <el-icon><User /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalUsers }}</div>
          <div class="stat-label">总用户数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon house-icon">
          <el-icon><House /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalHouses }}</div>
          <div class="stat-label">房源总数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon appointment-icon">
          <el-icon><Calendar /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalAppointments }}</div>
          <div class="stat-label">预约总数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon order-icon">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalOrders }}</div>
          <div class="stat-label">订单总数</div>
        </div>
      </div>
    </div>
    
    <div class="admin-section">
      <h2>快捷操作</h2>
      <div class="action-grid">
        <el-button type="primary" size="large" @click="navigateTo('users')">
          <el-icon><User /></el-icon>
          用户管理
        </el-button>
        <el-button type="primary" size="large" @click="navigateTo('houses')">
          <el-icon><House /></el-icon>
          房源管理
        </el-button>
        <el-button type="primary" size="large" @click="navigateTo('appointments')">
          <el-icon><Calendar /></el-icon>
          预约管理
        </el-button>
        <el-button type="primary" size="large" @click="navigateTo('orders')">
          <el-icon><Document /></el-icon>
          订单管理
        </el-button>
      </div>
    </div>
    
    <div class="admin-section">
      <h2>最近活动</h2>
      <el-table :data="recentActivities" border>
        <el-table-column prop="time" label="时间" width="180" />
        <el-table-column prop="type" label="类型" width="120" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="operator" label="操作者" width="120" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const stats = reactive({
  totalUsers: 100,
  totalHouses: 50,
  totalAppointments: 200,
  totalOrders: 80
})

const recentActivities = ref([
  { time: '2024-01-15 14:30', type: '用户注册', description: '新用户 user100 注册成功', operator: '系统' },
  { time: '2024-01-15 14:25', type: '房源发布', description: '房东 user0 发布了新房源', operator: 'user0' },
  { time: '2024-01-15 14:20', type: '预约看房', description: '租客 user1 预约了房源看房', operator: 'user1' },
  { time: '2024-01-15 14:15', type: '订单创建', description: '订单 Order20240115001 创建成功', operator: 'user1' },
  { time: '2024-01-15 14:10', type: '用户登录', description: '管理员 admin 登录系统', operator: 'admin' }
])

const navigateTo = (path) => {
  router.push(`/admin/${path}`)
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.admin-page {
  padding: 30px;
  max-width: 1200px;
  margin: 0 auto;
}

.admin-header {
  margin-bottom: 30px;
  
  h1 {
    font-size: 28px;
    color: $text-primary;
    margin-bottom: 10px;
  }
  
  p {
    color: $text-secondary;
    font-size: 14px;
  }
}

.admin-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 25px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  
  .stat-icon {
    width: 50px;
    height: 50px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    
    .el-icon {
      font-size: 24px;
      color: white;
    }
    
    &.users-icon {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }
    
    &.house-icon {
      background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
    }
    
    &.appointment-icon {
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    }
    
    &.order-icon {
      background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    }
  }
  
  .stat-info {
    .stat-value {
      font-size: 28px;
      font-weight: bold;
      color: $text-primary;
    }
    
    .stat-label {
      font-size: 14px;
      color: $text-secondary;
      margin-top: 5px;
    }
  }
}

.admin-section {
  background: white;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  
  h2 {
    font-size: 18px;
    color: $text-primary;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 1px solid $border-color-base;
  }
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
  
  .el-button {
    height: 50px;
    font-size: 15px;
    
    .el-icon {
      margin-right: 8px;
    }
  }
}

.el-table {
  font-size: 14px;
  
  :deep(.el-table__header) {
    background: $bg-color;
  }
}
</style>