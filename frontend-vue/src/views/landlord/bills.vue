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
              <span :class="scope.row.status === 'UNPAID' ? 'unpaid-amount' : ''">¥{{ scope.row.amount }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="dueDate" label="到期日期" />
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template #default="scope">
              <el-button v-if="scope.row.status === 'UNPAID'" size="small" type="warning" @click="sendReminder(scope.row)">发送提醒</el-button>
              <el-button v-if="scope.row.status === 'OVERDUE'" size="small" type="danger" @click="sendCollection(scope.row)">催收通知</el-button>
              <el-button size="small" @click="viewBill(scope.row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="待收款" name="unpaid">
        <el-table :data="bills.filter(b => b.status === 'UNPAID')" border>
          <el-table-column prop="billNo" label="账单编号" />
          <el-table-column prop="houseName" label="房源" />
          <el-table-column prop="tenantName" label="租客" />
          <el-table-column prop="amount" label="金额">
            <template #default="scope">
              <span class="unpaid-amount">¥{{ scope.row.amount }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="dueDate" label="到期日期" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" type="warning" @click="sendReminder(scope.row)">发送提醒</el-button>
              <el-button size="small" @click="viewBill(scope.row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="逾期" name="overdue">
        <el-table :data="bills.filter(b => b.status === 'OVERDUE')" border>
          <el-table-column prop="billNo" label="账单编号" />
          <el-table-column prop="houseName" label="房源" />
          <el-table-column prop="tenantName" label="租客" />
          <el-table-column prop="amount" label="金额">
            <template #default="scope">
              <span class="overdue-amount">¥{{ scope.row.amount }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="daysOverdue" label="逾期天数">
            <template #default="scope">{{ scope.row.daysOverdue }}天</template>
          </el-table-column>
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" type="danger" @click="sendCollection(scope.row)">催收通知</el-button>
              <el-button size="small" @click="viewBill(scope.row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
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
          <span class="detail-label">到期日期</span>
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
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const activeTab = ref('all')

const summary = reactive({
  totalIncome: 28500,
  pendingAmount: 6300,
  overdueCount: 2
})

const bills = ref([
  { id: 1, billNo: 'ZD202401001', houseName: '阳光小区3室2厅', tenantName: '用户A', type: 'RENT', amount: 3500, dueDate: '2024-02-01', status: 'UNPAID', daysOverdue: null },
  { id: 2, billNo: 'ZD202401002', houseName: '幸福花园2室1厅', tenantName: '用户B', type: 'RENT', amount: 2800, dueDate: '2024-02-01', status: 'UNPAID', daysOverdue: null },
  { id: 3, billNo: 'ZD202401003', houseName: '锦绣家园1室1厅', tenantName: '用户C', type: 'RENT', amount: 2000, dueDate: '2024-01-15', status: 'OVERDUE', daysOverdue: 5 },
  { id: 4, billNo: 'ZD202401004', houseName: '星河湾4室2厅', tenantName: '用户D', type: 'RENT', amount: 5500, dueDate: '2024-01-10', status: 'OVERDUE', daysOverdue: 10 },
  { id: 5, billNo: 'ZD202401005', houseName: '阳光小区3室2厅', tenantName: '用户A', type: 'RENT', amount: 3500, dueDate: '2024-01-01', status: 'PAID', daysOverdue: null }
])

const selectedBill = ref(null)
const showDetailDialog = ref(false)

const getTypeTag = (type) => {
  const types = {
    'RENT': 'primary',
    'WATER': 'info',
    'ELECTRIC': 'warning',
    'GAS': 'success'
  }
  return types[type] || 'default'
}

const getTypeText = (type) => {
  const texts = {
    'RENT': '租金',
    'WATER': '水费',
    'ELECTRIC': '电费',
    'GAS': '燃气费'
  }
  return texts[type] || type
}

const getStatusType = (status) => {
  const types = {
    'UNPAID': 'warning',
    'PAID': 'success',
    'OVERDUE': 'danger'
  }
  return types[status] || 'default'
}

const getStatusText = (status) => {
  const texts = {
    'UNPAID': '待支付',
    'PAID': '已支付',
    'OVERDUE': '已逾期'
  }
  return texts[status] || status
}

const sendReminder = (bill) => {
  ElMessage.success('提醒通知已发送')
}

const sendCollection = (bill) => {
  ElMessage.success('催收通知已发送')
}

const viewBill = (bill) => {
  selectedBill.value = bill
  showDetailDialog.value = true
}
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