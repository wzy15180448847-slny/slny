<template>
  <el-dialog
    :title="title"
    :visible.sync="visible"
    width="450px"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    @close="handleClose"
  >
    <div class="payment-confirm">
      <div class="payment-summary">
        <div class="summary-item main">
          <span class="label">支付金额</span>
          <span class="amount">¥{{ formatAmount(payment.amount) }}</span>
        </div>
        
        <div class="divider"></div>
        
        <div class="summary-details">
          <div class="detail-item" v-if="payment.orderNo">
            <span class="label">订单编号</span>
            <span class="value">{{ payment.orderNo }}</span>
          </div>
          <div class="detail-item" v-if="payment.title">
            <span class="label">支付项目</span>
            <span class="value">{{ payment.title }}</span>
          </div>
          <div class="detail-item" v-if="payment.target">
            <span class="label">收款方</span>
            <span class="value">{{ payment.target }}</span>
          </div>
          <div class="detail-item" v-if="payment.dueDate">
            <span class="label">截止日期</span>
            <span class="value">{{ payment.dueDate }}</span>
          </div>
        </div>
      </div>

      <div class="payment-method">
        <div class="method-title">支付方式</div>
        <div class="method-options">
          <div
            v-for="method in paymentMethods"
            :key="method.value"
            class="method-option"
            :class="{ active: selectedMethod === method.value }"
            @click="selectedMethod = method.value"
          >
            <div class="method-icon"><component :is="method.icon" /></div>
            <div class="method-info">
              <div class="method-name">{{ method.name }}</div>
              <div class="method-desc">{{ method.desc }}</div>
            </div>
            <div class="method-radio">
              <el-radio :value="method.value" v-model="selectedMethod" />
            </div>
          </div>
        </div>
      </div>

      <div class="wallet-info" v-if="selectedMethod === 'WALLET'">
        <div class="wallet-balance">
          <span class="label">钱包余额</span>
          <span class="balance" :class="{ insufficient: walletBalance < payment.amount }">
            ¥{{ formatAmount(walletBalance) }}
          </span>
        </div>
        <div v-if="walletBalance < payment.amount" class="balance-warning">
          <el-icon class="warning-icon"><Warning /></el-icon>
          <span>余额不足，请先充值</span>
        </div>
      </div>

      <div class="agreement">
        <el-checkbox v-model="agreed">
          我已阅读并同意
          <a href="#" class="link">《支付协议》</a>
        </el-checkbox>
      </div>
    </div>

    <template #footer>
      <div class="footer-actions">
        <el-button @click="handleClose">取消</el-button>
        <el-button
          type="primary"
          :loading="loading"
          :disabled="!canPay"
          @click="handlePay"
        >
          确认支付 ¥{{ formatAmount(payment.amount) }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch, markRaw } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: '确认支付'
  },
  payment: {
    type: Object,
    default: () => ({
      amount: 0,
      orderNo: '',
      title: '',
      target: '',
      dueDate: ''
    })
  },
  walletBalance: {
    type: Number,
    default: 0
  }
})

const emit = defineEmits(['pay', 'close', 'recharge'])

const selectedMethod = ref('WALLET')
const agreed = ref(false)
const loading = ref(false)

const paymentMethods = [
  { value: 'WALLET', name: '钱包支付', desc: '余额支付，免手续费', icon: markRaw(ElementPlusIconsVue.Wallet) },
  { value: 'WECHAT', name: '微信支付', desc: '推荐使用', icon: markRaw(ElementPlusIconsVue.Smartphone) },
  { value: 'ALIPAY', name: '支付宝', desc: '支持花呗', icon: markRaw(ElementPlusIconsVue.CreditCard) }
]

const canPay = computed(() => {
  if (!props.payment.amount || props.payment.amount <= 0) return false
  if (!agreed.value) return false
  if (selectedMethod.value === 'WALLET' && props.walletBalance < props.payment.amount) return false
  return true
})

const formatAmount = (amount) => {
  if (!amount) return '0.00'
  return Number(amount).toFixed(2)
}

const handleClose = () => {
  emit('close')
}

const handlePay = async () => {
  if (!canPay.value) return

  try {
    await ElMessageBox.confirm(
      `确认支付 ¥${formatAmount(props.payment.amount)} 吗？`,
      '支付确认',
      {
        confirmButtonText: '确定支付',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'btn-danger'
      }
    )

    loading.value = true

    await new Promise(resolve => setTimeout(resolve, 2000))

    const result = {
      success: true,
      method: selectedMethod.value,
      amount: props.payment.amount,
      orderNo: props.payment.orderNo,
      payTime: new Date().toLocaleString()
    }

    ElMessage.success('支付成功')
    emit('pay', result)
    loading.value = false
  } catch (error) {
    if (error !== 'cancel') {
      console.error('支付失败:', error)
      ElMessage.error('支付失败，请稍后重试')
    }
    loading.value = false
  }
}

watch(() => props.visible, (val) => {
  if (val) {
    selectedMethod.value = 'WALLET'
    agreed.value = false
    loading.value = false
  }
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.payment-confirm {
  padding: 10px 0;
}

.payment-summary {
  background: #f8fafc;
  border-radius: 10px;
  padding: 20px;
  margin-bottom: 20px;
}

.summary-item {
  &.main {
    display: flex;
    justify-content: space-between;
    align-items: baseline;
    margin-bottom: 15px;
    
    .label {
      font-size: 14px;
      color: $text-secondary;
    }
    
    .amount {
      font-size: 32px;
      font-weight: bold;
      color: #e74c3c;
    }
  }
}

.divider {
  height: 1px;
  background: $border-color-light;
  margin: 15px 0;
}

.summary-details {
  .detail-item {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
    font-size: 14px;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .label {
      color: $text-secondary;
    }
    
    .value {
      color: $text-primary;
      font-weight: 500;
    }
  }
}

.payment-method {
  margin-bottom: 20px;
  
  .method-title {
    font-size: 14px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: 12px;
  }
}

.method-options {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.method-option {
  display: flex;
  align-items: center;
  padding: 15px;
  border: 2px solid $border-color-light;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &.active {
    border-color: $primary-color;
    background: rgba($primary-color, 0.05);
  }
  
  &:hover {
    border-color: $primary-color-light;
  }
  
  .method-icon {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f1f5f9;
    border-radius: 50%;
    margin-right: 15px;
    
    .el-icon {
      font-size: 20px;
      color: $primary-color;
    }
  }
  
  .method-info {
    flex: 1;
    
    .method-name {
      font-size: 15px;
      font-weight: 500;
      color: $text-primary;
      margin-bottom: 3px;
    }
    
    .method-desc {
      font-size: 12px;
      color: $text-secondary;
    }
  }
  
  .method-radio {
    .el-radio {
      --el-radio-size: 20px;
    }
  }
}

.wallet-info {
  background: #fffbeb;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 20px;
  
  .wallet-balance {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
    
    .label {
      font-size: 14px;
      color: $text-secondary;
    }
    
    .balance {
      font-size: 20px;
      font-weight: bold;
      color: #22c55e;
      
      &.insufficient {
        color: #e74c3c;
      }
    }
  }
  
  .balance-warning {
    display: flex;
    align-items: center;
    color: #e74c3c;
    font-size: 13px;
    
    .warning-icon {
      margin-right: 5px;
    }
  }
}

.agreement {
  .el-checkbox {
    font-size: 13px;
    color: $text-secondary;
    
    .link {
      color: $primary-color;
      
      &:hover {
        text-decoration: underline;
      }
    }
  }
}

.footer-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  
  .el-button {
    padding: 10px 24px;
    border-radius: 8px;
    
    &.btn-danger {
      background: #e74c3c;
      border-color: #e74c3c;
      
      &:hover {
        background: darken(#e74c3c, 10%);
        border-color: darken(#e74c3c, 10%);
      }
    }
  }
}
</style>