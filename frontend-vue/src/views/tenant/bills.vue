<template>
  <div class="tenant-bills">
    <div class="page-header">
      <h1>账单支付</h1>
      <el-button type="primary" @click="goToWallet">
        <el-icon><Wallet /></el-icon>
        充值钱包
      </el-button>
    </div>
    
    <div class="wallet-info">
      <div class="wallet-card">
        <div class="wallet-label">可用余额</div>
        <div class="wallet-balance">¥{{ walletBalance }}</div>
      </div>
    </div>
    
    <el-tabs v-model="activeTab" type="card">
      <el-tab-pane label="全部" name="all">
        <el-table :data="bills" border>
          <el-table-column prop="billNo" label="账单编号" />
          <el-table-column prop="houseName" label="房源" />
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
              <el-button v-if="scope.row.status === 'UNPAID'" size="small" type="primary" @click="payBill(scope.row)">立即支付</el-button>
              <el-button v-else size="small" @click="viewBill(scope.row)">查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="待支付" name="unpaid">
        <el-table :data="bills.filter(b => b.status === 'UNPAID')" border>
          <el-table-column prop="billNo" label="账单编号" />
          <el-table-column prop="houseName" label="房源" />
          <el-table-column prop="type" label="账单类型" />
          <el-table-column prop="amount" label="金额">
            <template #default="scope">
              <span class="unpaid-amount">¥{{ scope.row.amount }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="dueDate" label="到期日期" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" type="primary" @click="payBill(scope.row)">立即支付</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="已支付" name="paid">
        <el-table :data="bills.filter(b => b.status === 'PAID')" border>
          <el-table-column prop="billNo" label="账单编号" />
          <el-table-column prop="houseName" label="房源" />
          <el-table-column prop="type" label="账单类型" />
          <el-table-column prop="amount" label="金额" />
          <el-table-column prop="payDate" label="支付日期" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="viewBill(scope.row)">查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
    
    <el-dialog title="支付确认" v-model="showPayDialog" width="400px">
      <div v-if="selectedBill" class="pay-content">
        <div class="pay-info">
          <div class="info-row">
            <span>账单编号:</span>
            <span>{{ selectedBill.billNo }}</span>
          </div>
          <div class="info-row">
            <span>房源:</span>
            <span>{{ selectedBill.houseName }}</span>
          </div>
          <div class="info-row">
            <span>账单类型:</span>
            <span>{{ getTypeText(selectedBill.type) }}</span>
          </div>
          <div class="info-row total-row">
            <span>支付金额:</span>
            <span class="total-amount">¥{{ selectedBill.amount }}</span>
          </div>
          <div class="info-row">
            <span>可用余额:</span>
            <span>¥{{ walletBalance }}</span>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showPayDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmPay">确认支付</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPaymentRecords, payRent, getWallet } from '@/api/wallet'

const router = useRouter()
const activeTab = ref('all')
const walletBalance = ref(0)

const bills = ref([])

const selectedBill = ref(null)
const showPayDialog = ref(false)

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
    'UNPAID': 'danger',
    'PAID': 'success',
    'OVERDUE': 'warning'
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

const goToWallet = () => {
  router.push('/tenant/wallet')
}

const payBill = (bill) => {
  selectedBill.value = bill
  showPayDialog.value = true
}

const viewBill = (bill) => {
  selectedBill.value = bill
  showPayDialog.value = true
}

const loadBills = async () => {
  try {
    const res = await getPaymentRecords()
    bills.value = res.data || []
  } catch (error) {
    console.error('加载账单失败:', error)
    ElMessage.error('加载账单失败')
  }
}

const loadWalletBalance = async () => {
  try {
    const res = await getWallet()
    walletBalance.value = res.data.balance || 0
  } catch (error) {
    console.error('加载余额失败:', error)
  }
}

const confirmPay = async () => {
  if (walletBalance.value < selectedBill.value.amount) {
    ElMessage.error('余额不足，请先充值')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定支付 ¥${selectedBill.value.amount} 吗？`, '支付确认', {
      confirmButtonText: '确定支付',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await payRent({ orderNo: selectedBill.value.paymentNo || selectedBill.value.billNo })
    
    ElMessage.success('支付成功')
    showPayDialog.value = false
    
    await loadBills()
    await loadWalletBalance()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('支付失败:', error)
      ElMessage.error('支付失败: ' + (error.response?.data?.message || error.message))
    }
  }
}

onMounted(() => {
  loadBills()
  loadWalletBalance()
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.tenant-bills {
  padding: 30px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  
  h1 {
    font-size: 24px;
    color: $text-primary;
  }
}

.wallet-info {
  margin-bottom: 20px;
}

.wallet-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 30px;
  text-align: center;
  color: white;
  
  .wallet-label {
    font-size: 14px;
    opacity: 0.8;
    margin-bottom: 10px;
  }
  
  .wallet-balance {
    font-size: 36px;
    font-weight: bold;
  }
}

.unpaid-amount {
  color: #f56c6c;
  font-weight: bold;
}

.pay-content {
  .pay-info {
    padding: 20px 0;
    
    .info-row {
      display: flex;
      justify-content: space-between;
      padding: 12px 0;
      border-bottom: 1px solid $border-color-base;
      
      &.total-row {
        border-bottom: 2px solid $primary-color;
        
        .total-amount {
          font-size: 24px;
          font-weight: bold;
          color: $primary-color;
        }
      }
    }
  }
}
</style>