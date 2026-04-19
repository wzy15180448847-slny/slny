<template>
  <div class="contract-sign">
    <div class="contract-header">
      <h2>电子合同签署</h2>
      <p class="contract-no">合同编号: {{ contract.contractNo }}</p>
    </div>

    <div class="contract-content">
      <div class="contract-section">
        <h3 class="section-title">合同信息</h3>
        <div class="info-grid">
          <div class="info-item">
            <span class="label">合同名称</span>
            <span class="value">{{ contract.title }}</span>
          </div>
          <div class="info-item">
            <span class="label">签订日期</span>
            <span class="value">{{ contract.signDate }}</span>
          </div>
          <div class="info-item">
            <span class="label">合同期限</span>
            <span class="value">{{ contract.startDate }} 至 {{ contract.endDate }}</span>
          </div>
          <div class="info-item">
            <span class="label">月租金</span>
            <span class="value">¥{{ contract.monthlyRent }}</span>
          </div>
          <div class="info-item">
            <span class="label">押金</span>
            <span class="value">¥{{ contract.deposit }}</span>
          </div>
          <div class="info-item">
            <span class="label">支付方式</span>
            <span class="value">{{ getPaymentWayText(contract.paymentWay) }}</span>
          </div>
        </div>
      </div>

      <div class="contract-section">
        <h3 class="section-title">房源信息</h3>
        <div class="info-grid">
          <div class="info-item">
            <span class="label">房源地址</span>
            <span class="value">{{ contract.houseAddress }}</span>
          </div>
          <div class="info-item">
            <span class="label">户型</span>
            <span class="value">{{ contract.houseType }}</span>
          </div>
          <div class="info-item">
            <span class="label">面积</span>
            <span class="value">{{ contract.houseArea }} ㎡</span>
          </div>
        </div>
      </div>

      <div class="contract-section">
        <h3 class="section-title">双方信息</h3>
        <div class="party-info">
          <div class="party">
            <h4>甲方（房东）</h4>
            <div class="party-detail">
              <span class="label">姓名：</span>
              <span class="value">{{ contract.landlordName }}</span>
            </div>
            <div class="party-detail">
              <span class="label">身份证号：</span>
              <span class="value">{{ maskIdCard(contract.landlordIdCard) }}</span>
            </div>
            <div class="party-detail">
              <span class="label">联系方式：</span>
              <span class="value">{{ maskPhone(contract.landlordPhone) }}</span>
            </div>
          </div>
          <div class="party-divider"></div>
          <div class="party">
            <h4>乙方（租客）</h4>
            <div class="party-detail">
              <span class="label">姓名：</span>
              <span class="value">{{ contract.tenantName }}</span>
            </div>
            <div class="party-detail">
              <span class="label">身份证号：</span>
              <span class="value">{{ maskIdCard(contract.tenantIdCard) }}</span>
            </div>
            <div class="party-detail">
              <span class="label">联系方式：</span>
              <span class="value">{{ maskPhone(contract.tenantPhone) }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="contract-section terms-section">
        <h3 class="section-title">合同条款摘要</h3>
        <div class="terms-list">
          <div class="term-item" v-for="(term, index) in contract.terms" :key="index">
            <span class="term-num">{{ index + 1 }}.</span>
            <span class="term-content">{{ term }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="sign-section">
      <div class="sign-area">
        <div class="sign-label">电子签名</div>
        <div 
          class="sign-pad"
          ref="signPad"
          @mousedown="startSign"
          @mousemove="drawSign"
          @mouseup="endSign"
          @mouseleave="endSign"
          @touchstart="startSign"
          @touchmove="drawSign"
          @touchend="endSign"
        >
          <canvas ref="signCanvas" class="sign-canvas"></canvas>
          <div v-if="!hasSignature" class="sign-hint">请在此处签名</div>
        </div>
        <div class="sign-actions">
          <el-button @click="clearSign">清除签名</el-button>
        </div>
      </div>

      <div class="agreement-checkbox">
        <el-checkbox v-model="agreeTerms">
          我已仔细阅读并同意以上合同条款
        </el-checkbox>
      </div>

      <div class="submit-actions">
        <el-button @click="cancelSign">取消</el-button>
        <el-button 
          type="primary" 
          :loading="signing"
          :disabled="!hasSignature || !agreeTerms"
          @click="submitSign"
        >
          确认签署
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  contract: {
    type: Object,
    default: () => ({
      contractNo: 'HT202401150001',
      title: '房屋租赁合同',
      signDate: '2024-01-15',
      startDate: '2024-02-01',
      endDate: '2025-01-31',
      monthlyRent: 3500,
      deposit: 7000,
      paymentWay: 'QUARTERLY',
      houseAddress: '北京市朝阳区望京街道阳光小区3号楼201室',
      houseType: '3室2厅',
      houseArea: 120,
      landlordName: '张建国',
      landlordIdCard: '110101197505151234',
      landlordPhone: '13912345678',
      tenantName: '李明',
      tenantIdCard: '110101199008205678',
      tenantPhone: '13887654321',
      terms: [
        '租赁期限为12个月，自2024年2月1日起至2025年1月31日止。',
        '租金按月支付，每月5日前支付当月租金。',
        '押金为两个月租金，租赁期满无违约全额退还。',
        '乙方不得擅自转租房屋，如需转租需经甲方书面同意。',
        '租赁期间水电费由乙方承担，物业费由甲方承担。'
      ]
    })
  }
})

const emit = defineEmits(['sign-success', 'cancel'])

const signCanvas = ref(null)
const signPad = ref(null)
const hasSignature = ref(false)
const agreeTerms = ref(false)
const signing = ref(false)
const isDrawing = ref(false)
const ctx = ref(null)

const getPaymentWayText = (way) => {
  const map = {
    MONTHLY: '押一付一',
    QUARTERLY: '押一付三',
    HALF_YEAR: '押一付六',
    YEARLY: '押一付年'
  }
  return map[way] || way
}

const maskIdCard = (idCard) => {
  if (!idCard) return ''
  return idCard.replace(/(\d{4})\d{10}(\d{4})/, '$1**********$2')
}

const maskPhone = (phone) => {
  if (!phone) return ''
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

const initCanvas = () => {
  if (!signCanvas.value || !signPad.value) return
  
  const rect = signPad.value.getBoundingClientRect()
  signCanvas.value.width = rect.width
  signCanvas.value.height = rect.height
  
  ctx.value = signCanvas.value.getContext('2d')
  ctx.value.strokeStyle = '#333'
  ctx.value.lineWidth = 2
  ctx.value.lineCap = 'round'
  ctx.value.lineJoin = 'round'
}

const startSign = (e) => {
  isDrawing.value = true
  const rect = signCanvas.value.getBoundingClientRect()
  const clientX = e.touches ? e.touches[0].clientX : e.clientX
  const clientY = e.touches ? e.touches[0].clientY : e.clientY
  ctx.value.beginPath()
  ctx.value.moveTo(clientX - rect.left, clientY - rect.top)
}

const drawSign = (e) => {
  if (!isDrawing.value) return
  
  const rect = signCanvas.value.getBoundingClientRect()
  const clientX = e.touches ? e.touches[0].clientX : e.clientX
  const clientY = e.touches ? e.touches[0].clientY : e.clientY
  
  ctx.value.lineTo(clientX - rect.left, clientY - rect.top)
  ctx.value.stroke()
  hasSignature.value = true
}

const endSign = () => {
  isDrawing.value = false
}

const clearSign = () => {
  if (!ctx.value || !signCanvas.value) return
  ctx.value.clearRect(0, 0, signCanvas.value.width, signCanvas.value.height)
  hasSignature.value = false
}

const cancelSign = () => {
  emit('cancel')
}

const submitSign = async () => {
  if (!hasSignature.value) {
    ElMessage.error('请先进行电子签名')
    return
  }
  
  if (!agreeTerms.value) {
    ElMessage.error('请同意合同条款')
    return
  }
  
  signing.value = true
  
  await new Promise(resolve => setTimeout(resolve, 2000))
  
  ElMessage.success('合同签署成功')
  emit('sign-success', {
    contractNo: props.contract.contractNo,
    signTime: new Date().toLocaleString()
  })
  
  signing.value = false
}

onMounted(() => {
  nextTick(() => {
    initCanvas()
  })
  
  window.addEventListener('resize', initCanvas)
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.contract-sign {
  max-width: 800px;
  margin: 0 auto;
  padding: 30px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.08);
}

.contract-header {
  text-align: center;
  padding-bottom: 20px;
  border-bottom: 1px solid $border-color-light;
  margin-bottom: 25px;
  
  h2 {
    font-size: 22px;
    color: $text-primary;
    margin-bottom: 8px;
  }
  
  .contract-no {
    color: $text-secondary;
    font-size: 14px;
  }
}

.contract-content {
  margin-bottom: 30px;
}

.contract-section {
  margin-bottom: 25px;
  
  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: 15px;
    padding-bottom: 10px;
    border-bottom: 1px solid $border-color-light;
  }
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.info-item {
  display: flex;
  flex-direction: column;
  
  .label {
    font-size: 13px;
    color: $text-secondary;
    margin-bottom: 5px;
  }
  
  .value {
    font-size: 14px;
    color: $text-primary;
    font-weight: 500;
  }
}

.party-info {
  display: flex;
  gap: 30px;
  
  .party {
    flex: 1;
    
    h4 {
      font-size: 15px;
      font-weight: 600;
      color: $text-primary;
      margin-bottom: 12px;
      padding-bottom: 8px;
      border-bottom: 1px dashed $border-color-light;
    }
    
    .party-detail {
      display: flex;
      margin-bottom: 8px;
      font-size: 14px;
      
      .label {
        color: $text-secondary;
        min-width: 80px;
      }
      
      .value {
        color: $text-primary;
      }
    }
  }
  
  .party-divider {
    width: 1px;
    background: $border-color-light;
    margin: 20px 0;
  }
}

.terms-section {
  background: #f9fafb;
  padding: 20px;
  border-radius: 8px;
  
  .terms-list {
    .term-item {
      display: flex;
      margin-bottom: 10px;
      line-height: 1.6;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      .term-num {
        color: $primary-color;
        font-weight: 600;
        margin-right: 8px;
        flex-shrink: 0;
      }
      
      .term-content {
        color: $text-primary;
        font-size: 14px;
      }
    }
  }
}

.sign-section {
  padding-top: 25px;
  border-top: 1px solid $border-color-light;
}

.sign-area {
  margin-bottom: 20px;
  
  .sign-label {
    font-size: 14px;
    color: $text-secondary;
    margin-bottom: 10px;
    display: block;
  }
  
  .sign-pad {
    position: relative;
    border: 2px dashed $border-color-base;
    border-radius: 8px;
    height: 150px;
    background: #fafafa;
    
    .sign-canvas {
      width: 100%;
      height: 100%;
      cursor: crosshair;
    }
    
    .sign-hint {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      color: $text-placeholder;
      font-size: 14px;
    }
  }
  
  .sign-actions {
    margin-top: 10px;
    text-align: right;
  }
}

.agreement-checkbox {
  margin-bottom: 25px;
  
  .el-checkbox {
    font-size: 13px;
    color: $text-primary;
  }
}

.submit-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  
  .el-button {
    padding: 10px 30px;
    border-radius: 8px;
  }
}

@media (max-width: 768px) {
  .contract-sign {
    padding: 20px;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .party-info {
    flex-direction: column;
    
    .party-divider {
      display: none;
    }
    
    .party {
      padding-bottom: 20px;
      border-bottom: 1px dashed $border-color-light;
      
      &:last-child {
        border-bottom: none;
        padding-bottom: 0;
      }
    }
  }
}
</style>