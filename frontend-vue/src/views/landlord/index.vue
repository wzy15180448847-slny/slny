<template>
  <div class="landlord-home">
    <div class="page-header">
      <h1>房东管理中心</h1>
      <p>欢迎回来，{{ userStore.nickname }}</p>
    </div>
    
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon house-icon">
          <el-icon><OfficeBuilding /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.houses }}</div>
          <div class="stat-label">我的房源</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon rent-icon">
          <el-icon><Wallet /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">¥{{ stats.revenue }}</div>
          <div class="stat-label">本月收益</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon appointment-icon">
          <el-icon><Calendar /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.appointments }}</div>
          <div class="stat-label">待处理预约</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon repair-icon">
          <el-icon><Tools /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.repairs }}</div>
          <div class="stat-label">待处理报修</div>
        </div>
      </div>
    </div>
    
    <div class="quick-actions">
      <h2>快捷操作</h2>
      <div class="action-grid">
        <el-button type="primary" size="large" @click="goTo('/landlord/houses/add')">
          <el-icon><Plus /></el-icon>
          <span>发布房源</span>
        </el-button>
        <el-button type="primary" size="large" @click="goTo('/landlord/appointments')">
          <el-icon><Calendar /></el-icon>
          <span>处理预约</span>
        </el-button>
        <el-button type="primary" size="large" @click="goTo('/landlord/contracts')">
          <el-icon><Document /></el-icon>
          <span>发起合同</span>
        </el-button>
        <el-button type="primary" size="large" @click="goTo('/landlord/repairs')">
          <el-icon><Tools /></el-icon>
          <span>处理报修</span>
        </el-button>
      </div>
    </div>
    
    <div class="recent-section">
      <div class="section-left">
        <h2>房源状态</h2>
        <div class="status-chart">
          <div class="status-item">
            <div class="status-bar" style="width: 60%">
              <span>展示中 ({{ stats.activeHouses }})</span>
            </div>
          </div>
          <div class="status-item">
            <div class="status-bar pending" style="width: 20%">
              <span>审核中 ({{ stats.pendingHouses }})</span>
            </div>
          </div>
          <div class="status-item">
            <div class="status-bar rented" style="width: 20%">
              <span>已出租 ({{ stats.rentedHouses }})</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="section-right">
        <h2>最近预约</h2>
        <el-table :data="recentAppointments" border>
          <el-table-column prop="houseName" label="房源" />
          <el-table-column prop="tenantName" label="租客" />
          <el-table-column prop="date" label="预约时间" />
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
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
  houses: 8,
  revenue: 28500,
  appointments: 3,
  repairs: 1,
  activeHouses: 5,
  pendingHouses: 1,
  rentedHouses: 2
})

const recentAppointments = ref([
  { id: 1, houseName: '阳光小区3室2厅', tenantName: '用户A', date: '2024-01-16 14:00', status: 'PENDING' },
  { id: 2, houseName: '幸福花园2室1厅', tenantName: '用户B', date: '2024-01-17 10:00', status: 'PENDING' },
  { id: 3, houseName: '锦绣家园1室1厅', tenantName: '用户C', date: '2024-01-15 09:00', status: 'CONFIRMED' }
])

const goTo = (path) => {
  router.push(path)
}

const getStatusType = (status) => {
  const types = {
    'PENDING': 'warning',
    'CONFIRMED': 'success',
    'COMPLETED': 'info',
    'CANCELLED': 'danger'
  }
  return types[status] || 'default'
}

const getStatusText = (status) => {
  const texts = {
    'PENDING': '待确认',
    'CONFIRMED': '已安排',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return texts[status] || status
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.landlord-home {
  padding: 30px;
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
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

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
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
    
    &.house-icon {
      background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
    }
    
    &.rent-icon {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }
    
    &.appointment-icon {
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    }
    
    &.repair-icon {
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

.quick-actions {
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

.recent-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  
  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
}

.section-left, .section-right {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  
  h2 {
    font-size: 18px;
    color: $text-primary;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 1px solid $border-color-base;
  }
}

.status-chart {
  .status-item {
    margin-bottom: 15px;
    
    .status-bar {
      display: flex;
      align-items: center;
      height: 30px;
      background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
      border-radius: 6px;
      padding-left: 15px;
      color: white;
      font-size: 14px;
      font-weight: 500;
      
      &.pending {
        background: linear-gradient(135deg, #faad14 0%, #ffc53d 100%);
      }
      
      &.rented {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      }
    }
  }
}

.el-table {
  font-size: 14px;
}
</style>