<template>
  <div class="tenant-home">
    <div class="page-header">
      <h1>租客中心</h1>
      <p>欢迎回来，{{ userStore.nickname }}</p>
    </div>
    
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon contract-icon">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.contracts }}</div>
          <div class="stat-label">进行中合同</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon bill-icon">
          <el-icon><Wallet /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.pendingBills }}</div>
          <div class="stat-label">待支付账单</div>
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
      
      <div class="stat-card">
        <div class="stat-icon favorite-icon">
          <el-icon><Star /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.favorites }}</div>
          <div class="stat-label">收藏房源</div>
        </div>
      </div>
    </div>
    
    <div class="quick-actions">
      <h2>快捷操作</h2>
      <div class="action-grid">
        <el-button type="primary" size="large" @click="goTo('/tenant/appointments')">
          <el-icon><Calendar /></el-icon>
          <span>预约看房</span>
        </el-button>
        <el-button type="primary" size="large" @click="goTo('/tenant/contracts')">
          <el-icon><Document /></el-icon>
          <span>查看合同</span>
        </el-button>
        <el-button type="primary" size="large" @click="goTo('/tenant/bills')">
          <el-icon><Wallet /></el-icon>
          <span>支付账单</span>
        </el-button>
        <el-button type="primary" size="large" @click="goTo('/tenant/repairs')">
          <el-icon><Tools /></el-icon>
          <span>报修申请</span>
        </el-button>
      </div>
    </div>
    
    <div class="recent-section">
      <div class="section-left">
        <h2>最近预约</h2>
        <el-table :data="recentAppointments" border>
          <el-table-column prop="houseName" label="房源名称" />
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
      
      <div class="section-right">
        <h2>我的合同</h2>
        <el-table :data="recentContracts" border>
          <el-table-column prop="houseName" label="房源" />
          <el-table-column prop="startDate" label="开始日期" />
          <el-table-column prop="endDate" label="结束日期" />
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag :type="getContractStatusType(scope.row.status)">
                {{ getContractStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { getMyAppointments } from '@/api/appointment'
import { getMyContracts } from '@/api/contract'

const router = useRouter()
const userStore = useUserStore()

const stats = reactive({
  contracts: 0,
  pendingBills: 0,
  repairs: 0,
  favorites: 0
})

const recentAppointments = ref([])
const recentContracts = ref([])

const loadData = async () => {
  try {
    const [appointmentsRes, contractsRes] = await Promise.all([
      getMyAppointments(),
      getMyContracts()
    ])
    
    recentAppointments.value = (appointmentsRes.data || []).map(app => ({
      id: app.id,
      houseName: app.houseName || '未知房源',
      date: formatDateTime(app.appointmentTime),
      status: app.status === 0 ? 'PENDING' : app.status === 1 ? 'CONFIRMED' : app.status === 2 ? 'COMPLETED' : 'CANCELLED'
    }))
    
    recentContracts.value = (contractsRes.data || []).map(contract => ({
      id: contract.id,
      houseName: contract.houseName || '未知房源',
      startDate: formatDate(contract.startDate),
      endDate: formatDate(contract.endDate),
      status: contract.status === 0 ? 'PENDING' : contract.status === 1 ? 'ACTIVE' : contract.status === 2 ? 'EXPIRED' : 'TERMINATED'
    }))
    
    stats.contracts = recentContracts.value.length
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return dateTime.replace('T', ' ').substring(0, 16)
}

const formatDate = (dateTime) => {
  if (!dateTime) return ''
  return dateTime.substring(0, 10)
}

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

const getContractStatusType = (status) => {
  const types = {
    'ACTIVE': 'success',
    'PENDING': 'warning',
    'EXPIRED': 'info',
    'TERMINATED': 'danger'
  }
  return types[status] || 'default'
}

const getContractStatusText = (status) => {
  const texts = {
    'ACTIVE': '生效中',
    'PENDING': '待签署',
    'EXPIRED': '已到期',
    'TERMINATED': '已终止'
  }
  return texts[status] || status
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.tenant-home {
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
    
    &.contract-icon {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }
    
    &.bill-icon {
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    }
    
    &.repair-icon {
      background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    }
    
    &.favorite-icon {
      background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
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

.el-table {
  font-size: 14px;
}
</style>