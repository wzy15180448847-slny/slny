<template>
  <div class="landlord-bills">
    <div class="page-header">
      <h1>账单管理</h1>
    </div>
    
    <div class="summary-cards">
      <div class="summary-card">
        <div class="summary-icon income-icon">
          <el-icon><TrendCharts /></el-icon>
        </div>
        <div class="summary-info">
          <div class="summary-value">¥{{ summary.totalIncome }}</div>
          <div class="summary-label">本月总收入</div>
        </div>
      </div>
      <div class="summary-card">
        <div class="summary-icon pending-icon">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="summary-info">
          <div class="summary-value">¥{{ summary.pendingAmount }}</div>
          <div class="summary-label">待收金额</div>
        </div>
      </div>
      <div class="summary-card">
        <div class="summary-icon overdue-icon">
          <el-icon><Warning /></el-icon>
        </div>
        <div class="summary-info">
          <div class="summary-value">{{ summary.overdueCount }}</div>
          <div class="summary-label">逾期账单</div>
        </div>
      </div>
    </div>
    
    <el-tabs v-model="activeTab" type="card">
      <el-tab-pane label="全部" name="all">
        <el-table :data="bills" border>
          <el-table-column prop="billNo" label="账单编号" />
          <el-table-column prop="houseName" label="房源" />
          <el-table-column prop="tenantName" label="租客" />
          <el-table-column prop="type" label="账单类型">
            <template #default="scope">
              <el-tag :type="getTypeTag(scope.row.type)">{{ getTypeText(scope.row.type) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="金额">
            <template #default="scope">
              <span :class="scope.row.status === 1 ? 'unpaid-amount' : ''">¥{{ scope.row.amount }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="dueDate" label="创建日期" />
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="viewBill(scope.row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="bills.length === 0" class="empty-state">
          <el-empty description="暂无账单记录" />
        </div>
      </el-tab-pane>
      <el-tab-pane label="待收款" name="unpaid">
        <el-table :data="bills.filter(b => b.status === 1)" border>
          <el-table-column prop="billNo" label="账单编号" />
          <el-table-column prop="houseName" label="房源" />
          <el-table-column prop="tenantName" label="租客" />
          <el-table-column prop="amount" label="金额">
            <template #default="scope">
              <span class="unpaid-amount">¥{{ scope.row.amount }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="dueDate" label="创建日期" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="viewBill(scope.row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="bills.filter(b => b.status === 1).length === 0" class="empty-state">
          <el-empty description="暂无待收款账单" />
        </div>
      </el-tab-pane>
      <el-tab-pane label="已收款" name="paid">
        <el-table :data="bills.filter(b => b.status === 2)" border>
          <el-table-column prop="billNo" label="账单编号" />
          <el-table-column prop="houseName" label="房源" />
          <el-table-column prop="tenantName" label="租客" />
          <el-table-column prop="amount" label="金额">
            <template #default="scope">
              <span>¥{{ scope.row.amount }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="dueDate" label="创建日期" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="viewBill(scope.row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="bills.filter(b => b.status === 2).length === 0" class="empty-state">
          <el-empty description="暂无已收款账单" />
        </div>
      </el-tab-pane>
    </el-tabs>
    
    <el-dialog title="账单详情" v-model="showDetailDialog" width="400px">
      <div v-if="selectedBill" class="detail-content">
        <div class="detail-row">
          <span class="detail-label">账单编号</span>
          <span class="detail-value">{{ selectedBill.billNo }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">房源</span>
          <span class="detail-value">{{ selectedBill.houseName }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">租客</span>
          <span class="detail-value">{{ selectedBill.tenantName }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">账单类型</span>
          <el-tag :type="getTypeTag(selectedBill.type)">{{ getTypeText(selectedBill.type) }}</el-tag>
        </div>
        <div class="detail-row total-row">
          <span class="detail-label">金额</span>
          <span class="total-amount">¥{{ selectedBill.amount }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">创建日期</span>
          <span class="detail-value">{{ selectedBill.dueDate }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">状态</span>
          <el-tag :type="getStatusType(selectedBill.status)">{{ getStatusText(selectedBill.status) }}</el-tag>
        </div>
      </div>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElEmpty } from 'element-plus'
import { getLandlordBills } from '@/api/landlord'

const activeTab = ref('all')

const summary = reactive({
  totalIncome: 0,
  pendingAmount: 0,
  overdueCount: 0
})

const bills = ref([])

const selectedBill = ref(null)
const showDetailDialog = ref(false)

const getTypeTag = (type) => {
  const types = {
    1: 'primary',
    2: 'info',
    3: 'warning'
  }
  return types[type] || 'default'
}

const getTypeText = (type) => {
  const texts = {
    1: '租金',
    2: '押金',
    3: '违约金'
  }
  return texts[type] || type
}

const getStatusType = (status) => {
  const types = {
    1: 'warning',
    2: 'success',
    3: 'danger'
  }
  return types[status] || 'default'
}

const getStatusText = (status) => {
  const texts = {
    1: '待支付',
    2: '已支付',
    3: '已取消'
  }
  return texts[status] || status
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

const loadBills = async () => {
  try {
    const response = await getLandlordBills()
    const data = response?.records || response || []
    bills.value = data.map(bill => ({
      id: bill.id,
      billNo: 'B' + bill.id,
      houseName: '房源' + bill.houseId,
      tenantName: '租户' + bill.tenantId,
      type: bill.billType,
      amount: bill.amount?.toString() || '0',
      dueDate: bill.createTime ? formatDate(bill.createTime) : '',
      status: bill.status,
      daysOverdue: null
    }))
    updateSummary()
  } catch (error) {
    console.error('加载账单失败:', error)
    ElMessage.error('加载账单失败')
  }
}

const updateSummary = () => {
  const paidBills = bills.value.filter(b => b.status === 2)
  const unpaidBills = bills.value.filter(b => b.status === 1)
  
  summary.totalIncome = paidBills.reduce((sum, b) => sum + parseFloat(b.amount), 0).toFixed(2)
  summary.pendingAmount = unpaidBills.reduce((sum, b) => sum + parseFloat(b.amount), 0).toFixed(2)
  summary.overdueCount = 0
}

const viewBill = (bill) => {
  selectedBill.value = bill
  showDetailDialog.value = true
}

onMounted(() => {
  loadBills()
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.landlord-bills {
  padding: 30px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 20px;
  
  h1 {
    font-size: 24px;
    color: $text-primary;
  }
}

.summary-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.summary-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  
  .summary-icon {
    width: 45px;
    height: 45px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    
    .el-icon {
      font-size: 22px;
      color: white;
    }
    
    &.income-icon {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }
    
    &.pending-icon {
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    }
    
    &.overdue-icon {
      background: linear-gradient(135deg, #faad14 0%, #ffc53d 100%);
    }
  }
  
  .summary-info {
    .summary-value {
      font-size: 24px;
      font-weight: bold;
      color: $text-primary;
    }
    
    .summary-label {
      font-size: 13px;
      color: $text-secondary;
      margin-top: 3px;
    }
  }
}

.unpaid-amount {
  color: #faad14;
  font-weight: bold;
}

.overdue-amount {
  color: #f56c6c;
  font-weight: bold;
}

.empty-state {
  padding: 60px 0;
  text-align: center;
}

.detail-content {
  .detail-row {
    display: flex;
    justify-content: space-between;
    padding: 12px 0;
    border-bottom: 1px solid $border-color-base;
    
    &.total-row {
      border-bottom: 2px solid $primary-color;
      
      .total-amount {
        font-size: 20px;
        font-weight: bold;
        color: $primary-color;
      }
    }
    
    &:last-child {
      border-bottom: none;
    }
    
    .detail-label {
      color: $text-secondary;
      font-size: 14px;
    }
    
    .detail-value {
      color: $text-primary;
      font-size: 14px;
      font-weight: 500;
    }
  }
}
</style>