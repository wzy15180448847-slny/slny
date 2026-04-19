<template>
  <div class="tenant-wallet">
    <div class="wallet-header">
      <h1>我的钱包</h1>
    </div>
    
    <div class="balance-section">
      <div class="balance-card">
        <div class="balance-label">可用余额</div>
        <div class="balance-amount">¥{{ wallet.balance }}</div>
        <div class="balance-actions">
          <el-button type="primary" @click="showRechargeDialog = true">充值</el-button>
          <el-button @click="showWithdrawDialog = true">提现</el-button>
        </div>
      </div>
    </div>
    
    <div class="wallet-content">
      <div class="tab-section">
        <el-tabs v-model="activeTab" type="card">
          <el-tab-pane label="交易记录" name="transactions">
            <el-table :data="transactions" border>
              <el-table-column prop="type" label="类型">
                <template #default="scope">
                  <el-tag :type="getTypeTag(scope.row.type)">{{ getTypeText(scope.row.type) }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="amount" label="金额">
                <template #default="scope">
                  <span :class="scope.row.type === 'RECHARGE' ? 'income' : 'expense'">
                    {{ scope.row.type === 'RECHARGE' ? '+' : '-' }}¥{{ scope.row.amount }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="description" label="描述" />
              <el-table-column prop="createTime" label="时间" />
            </el-table>
          </el-tab-pane>
          <el-tab-pane label="充值记录" name="recharges">
            <el-table :data="transactions.filter(t => t.type === 'RECHARGE')" border>
              <el-table-column prop="amount" label="充值金额">
                <template #default="scope">
                  <span class="income">+¥{{ scope.row.amount }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="description" label="描述" />
              <el-table-column prop="createTime" label="时间" />
            </el-table>
          </el-tab-pane>
          <el-tab-pane label="消费记录" name="consumptions">
            <el-table :data="transactions.filter(t => t.type === 'PAYMENT')" border>
              <el-table-column prop="amount" label="消费金额">
                <template #default="scope">
                  <span class="expense">-¥{{ scope.row.amount }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="description" label="描述" />
              <el-table-column prop="createTime" label="时间" />
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
    
    <el-dialog title="充值" v-model="showRechargeDialog" width="400px">
      <el-form :model="rechargeForm" label-width="80px">
        <el-form-item label="充值金额">
          <div class="amount-options">
            <el-button 
              v-for="amount in [100, 500, 1000, 2000]" 
              :key="amount"
              :type="rechargeForm.amount === amount ? 'primary' : 'default'"
              @click="rechargeForm.amount = amount"
            >
              ¥{{ amount }}
            </el-button>
          </div>
          <el-input v-model="rechargeForm.amount" placeholder="自定义金额" style="margin-top: 15px" />
        </el-form-item>
        <el-form-item label="支付方式">
          <el-radio-group v-model="rechargeForm.paymentMethod">
            <el-radio label="微信支付" />
            <el-radio label="支付宝" />
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRechargeDialog = false">取消</el-button>
        <el-button type="primary" @click="submitRecharge">确认充值</el-button>
      </template>
    </el-dialog>
    
    <el-dialog title="提现" v-model="showWithdrawDialog" width="400px">
      <el-form :model="withdrawForm" label-width="80px">
        <el-form-item label="提现金额">
          <el-input v-model="withdrawForm.amount" placeholder="请输入提现金额" />
          <p class="tips">可用余额: ¥{{ wallet.balance }}</p>
        </el-form-item>
        <el-form-item label="收款账户">
          <el-select v-model="withdrawForm.account" placeholder="请选择收款账户">
            <el-option label="微信" value="WECHAT" />
            <el-option label="支付宝" value="ALIPAY" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showWithdrawDialog = false">取消</el-button>
        <el-button type="primary" @click="submitWithdraw">确认提现</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getWallet, getTransactions, recharge } from '@/api/wallet'

const activeTab = ref('transactions')

const wallet = reactive({
  balance: 0
})

const transactions = ref([])

const showRechargeDialog = ref(false)
const showWithdrawDialog = ref(false)

const rechargeForm = reactive({
  amount: 500,
  paymentMethod: 'WECHAT'
})

const withdrawForm = reactive({
  amount: '',
  account: ''
})

const getTypeTag = (type) => {
  return type === 'RECHARGE' || type === '1' ? 'success' : 'danger'
}

const getTypeText = (type) => {
  if (type === 'RECHARGE' || type === '1') return '充值'
  if (type === 'PAYMENT' || type === '2') return '消费'
  if (type === 'TRANSFER' || type === '4') return '转账'
  return type
}

const loadWalletData = async () => {
  try {
    const walletRes = await getWallet()
    wallet.balance = walletRes.data.balance || 0
    
    const transactionsRes = await getTransactions()
    transactions.value = transactionsRes.data || []
  } catch (error) {
    console.error('加载钱包数据失败:', error)
    ElMessage.error('加载钱包数据失败')
  }
}

const submitRecharge = async () => {
  if (!rechargeForm.amount || rechargeForm.amount <= 0) {
    ElMessage.error('请输入有效的充值金额')
    return
  }
  
  try {
    await recharge({ amount: rechargeForm.amount })
    ElMessage.success('充值成功')
    await loadWalletData()
    showRechargeDialog.value = false
    rechargeForm.amount = 500
  } catch (error) {
    ElMessage.error('充值失败: ' + (error.response?.data?.message || error.message))
  }
}

onMounted(() => {
  loadWalletData()
})

const submitWithdraw = async () => {
  if (!withdrawForm.amount || withdrawForm.amount <= 0) {
    ElMessage.error('请输入有效的提现金额')
    return
  }
  
  if (withdrawForm.amount > wallet.balance) {
    ElMessage.error('提现金额超过可用余额')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定提现 ¥${withdrawForm.amount} 吗？`, '提现确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    wallet.balance -= withdrawForm.amount
    transactions.value.unshift({
      id: Date.now(),
      type: 'PAYMENT',
      amount: withdrawForm.amount,
      description: '提现',
      createTime: new Date().toLocaleString()
    })
    
    ElMessage.success('提现申请已提交，将在1-3个工作日内到账')
    showWithdrawDialog.value = false
    withdrawForm.amount = ''
    withdrawForm.account = ''
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.tenant-wallet {
  padding: 30px;
  max-width: 800px;
  margin: 0 auto;
}

.wallet-header {
  margin-bottom: 20px;
  
  h1 {
    font-size: 24px;
    color: $text-primary;
  }
}

.balance-section {
  margin-bottom: 20px;
}

.balance-card {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  border-radius: 12px;
  padding: 40px;
  text-align: center;
  color: white;
  
  .balance-label {
    font-size: 14px;
    opacity: 0.8;
    margin-bottom: 10px;
  }
  
  .balance-amount {
    font-size: 48px;
    font-weight: bold;
    margin-bottom: 20px;
  }
  
  .balance-actions {
    display: flex;
    gap: 15px;
    justify-content: center;
    
    .el-button {
      padding: 10px 30px;
      border-radius: 20px;
      
      &:first-child {
        background: white;
        color: #11998e;
        border: none;
      }
      
      &:last-child {
        background: transparent;
        border: 1px solid white;
        color: white;
      }
    }
  }
}

.wallet-content {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.amount-options {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  
  .el-button {
    padding: 8px 20px;
    border-radius: 20px;
  }
}

.tips {
  color: $text-secondary;
  font-size: 13px;
  margin-top: 10px;
}

.income {
  color: #67c23a;
  font-weight: bold;
}

.expense {
  color: #f56c6c;
  font-weight: bold;
}
</style>