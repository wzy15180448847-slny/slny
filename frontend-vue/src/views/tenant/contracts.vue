<template>
  <div class="tenant-contracts">
    <div class="page-header">
      <h1>我的合同</h1>
    </div>
    
    <el-tabs v-model="activeTab" type="card">
      <el-tab-pane label="全部" name="all">
        <div class="contract-list">
          <div v-for="contract in contracts" :key="contract.id" class="contract-card">
            <div class="contract-header">
              <div class="contract-title">{{ contract.houseName }}</div>
              <el-tag :type="getStatusType(contract.status)">{{ getStatusText(contract.status) }}</el-tag>
            </div>
            <div class="contract-info">
              <div class="info-row">
                <span>合同编号:</span>
                <span class="info-value">{{ contract.contractNo }}</span>
              </div>
              <div class="info-row">
                <span>租赁期限:</span>
                <span class="info-value">{{ contract.startDate }} 至 {{ contract.endDate }}</span>
              </div>
              <div class="info-row">
                <span>租金:</span>
                <span class="info-value">¥{{ contract.rent }}/月</span>
              </div>
              <div class="info-row">
                <span>押金:</span>
                <span class="info-value">¥{{ contract.deposit }}</span>
              </div>
            </div>
            <div class="contract-actions">
              <el-button size="small" @click="previewContract(contract)">预览合同</el-button>
              <el-button v-if="contract.status === 'PENDING'" size="small" type="primary" @click="signContract(contract)">签署合同</el-button>
            </div>
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="待签署" name="pending">
        <div class="contract-list">
          <div v-for="contract in contracts.filter(c => c.status === 'PENDING')" :key="contract.id" class="contract-card">
            <div class="contract-header">
              <div class="contract-title">{{ contract.houseName }}</div>
              <el-tag type="warning">待签署</el-tag>
            </div>
            <div class="contract-info">
              <div class="info-row">
                <span>合同编号:</span>
                <span class="info-value">{{ contract.contractNo }}</span>
              </div>
              <div class="info-row">
                <span>租赁期限:</span>
                <span class="info-value">{{ contract.startDate }} 至 {{ contract.endDate }}</span>
              </div>
            </div>
            <div class="contract-actions">
              <el-button size="small" @click="previewContract(contract)">预览合同</el-button>
              <el-button size="small" type="primary" @click="signContract(contract)">签署合同</el-button>
            </div>
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="生效中" name="active">
        <div class="contract-list">
          <div v-for="contract in contracts.filter(c => c.status === 'ACTIVE')" :key="contract.id" class="contract-card">
            <div class="contract-header">
              <div class="contract-title">{{ contract.houseName }}</div>
              <el-tag type="success">生效中</el-tag>
            </div>
            <div class="contract-info">
              <div class="info-row">
                <span>合同编号:</span>
                <span class="info-value">{{ contract.contractNo }}</span>
              </div>
              <div class="info-row">
                <span>到期提醒:</span>
                <span class="info-value">{{ contract.daysRemaining }}天后到期</span>
              </div>
            </div>
            <div class="contract-actions">
              <el-button size="small" @click="previewContract(contract)">预览合同</el-button>
              <el-button size="small" type="warning" @click="terminateContract(contract)">申请解约</el-button>
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
    
    <el-dialog title="合同预览" v-model="showPreviewDialog" width="800px">
      <div v-if="selectedContract" class="contract-preview">
        <div class="preview-header">
          <h2>房屋租赁合同</h2>
          <p>合同编号: {{ selectedContract.contractNo }}</p>
        </div>
        <div class="preview-body">
          <div class="preview-section">
            <h3>一、双方当事人</h3>
            <p>甲方（出租方）: {{ selectedContract.landlordName }}</p>
            <p>乙方（承租方）: {{ userStore.nickname }}</p>
          </div>
          <div class="preview-section">
            <h3>二、租赁房屋</h3>
            <p>房屋地址: {{ selectedContract.address }}</p>
            <p>房屋名称: {{ selectedContract.houseName }}</p>
          </div>
          <div class="preview-section">
            <h3>三、租赁期限</h3>
            <p>自 {{ selectedContract.startDate }} 至 {{ selectedContract.endDate }}</p>
          </div>
          <div class="preview-section">
            <h3>四、租金及支付方式</h3>
            <p>月租金: ¥{{ selectedContract.rent }}</p>
            <p>押金: ¥{{ selectedContract.deposit }}</p>
          </div>
          <div class="preview-section">
            <h3>五、双方权利义务</h3>
            <p>1. 甲方保证房屋设施完好，乙方应爱护房屋及设施。</p>
            <p>2. 乙方应按时支付租金，逾期支付需承担违约责任。</p>
            <p>3. 租赁期间双方不得擅自解除合同。</p>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showPreviewDialog = false">关闭</el-button>
        <el-button v-if="selectedContract?.status === 'PENDING'" type="primary" @click="signContract(selectedContract)">电子签署</el-button>
      </template>
    </el-dialog>
    
    <el-dialog title="电子签名" v-model="showSignDialog" width="400px">
      <div class="sign-content">
        <p>请确认签署以下合同:</p>
        <p class="contract-name">{{ selectedContract?.houseName }}</p>
        <p>合同编号: {{ selectedContract?.contractNo }}</p>
        <div class="sign-area">
          <canvas ref="signCanvas" class="sign-canvas" @mousedown="startSign" @mousemove="drawing" @mouseup="endSign"></canvas>
        </div>
        <p>请在上方区域签名</p>
      </div>
      <template #footer>
        <el-button @click="showSignDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmSign">确认签署</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyContracts, signContract as signContractApi, terminateContract as apiTerminateContract } from '@/api/contracts'

const userStore = useUserStore()
const activeTab = ref('all')

const contracts = ref([])

const selectedContract = ref(null)
const showPreviewDialog = ref(false)
const showSignDialog = ref(false)
const signCanvas = ref(null)
const isDrawing = ref(false)

const getStatusType = (status) => {
  const types = {
    'PENDING': 'warning',
    'ACTIVE': 'success',
    'EXPIRED': 'info',
    'TERMINATED': 'danger'
  }
  return types[status] || 'default'
}

const getStatusText = (status) => {
  const texts = {
    'PENDING': '待签署',
    'ACTIVE': '生效中',
    'EXPIRED': '已到期',
    'TERMINATED': '已终止'
  }
  return texts[status] || status
}

const previewContract = (contract) => {
  selectedContract.value = contract
  showPreviewDialog.value = true
}

const signContract = (contract) => {
  selectedContract.value = contract
  showPreviewDialog.value = false
  showSignDialog.value = true
}

const startSign = (e) => {
  isDrawing.value = true
  const canvas = signCanvas.value
  const ctx = canvas.getContext('2d')
  ctx.beginPath()
  ctx.moveTo(e.offsetX, e.offsetY)
}

const drawing = (e) => {
  if (!isDrawing.value) return
  const canvas = signCanvas.value
  const ctx = canvas.getContext('2d')
  ctx.lineTo(e.offsetX, e.offsetY)
  ctx.stroke()
}

const endSign = () => {
  isDrawing.value = false
}

const loadContracts = async () => {
  try {
    const res = await getMyContracts()
    contracts.value = res.data || []
  } catch (error) {
    console.error('加载合同失败:', error)
    ElMessage.error('加载合同失败')
  }
}

const confirmSign = async () => {
  const canvas = signCanvas.value
  if (!canvas) {
    ElMessage.error('请先签名')
    return
  }
  
  const signatureBase64 = canvas.toDataURL('image/png')
  
  try {
    await signContractApi(selectedContract.value.id, {
      signature: signatureBase64
    })
    
    selectedContract.value.status = 'ACTIVE'
    ElMessage.success('合同签署成功')
    showSignDialog.value = false
    
    canvas.getContext('2d').clearRect(0, 0, canvas.width, canvas.height)
  } catch (error) {
    console.error('签署失败:', error)
    ElMessage.error('签署失败: ' + (error.response?.data?.message || error.message))
  }
}

const terminateContract = async (contract) => {
  try {
    await ElMessageBox.confirm('确定要申请解除合同吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await apiTerminateContract(contract.id, {
      reason: '租客申请解约'
    })
    
    ElMessage.success('解约申请已提交')
    await loadContracts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('解约失败:', error)
      ElMessage.error('解约失败: ' + (error.response?.data?.message || error.message))
    }
  }
}

onMounted(() => {
  loadContracts()
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.tenant-contracts {
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

.contract-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 20px;
}

.contract-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  
  .contract-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
    padding-bottom: 10px;
    border-bottom: 1px solid $border-color-base;
    
    .contract-title {
      font-size: 16px;
      font-weight: 600;
      color: $text-primary;
    }
  }
  
  .contract-info {
    margin-bottom: 15px;
    
    .info-row {
      display: flex;
      justify-content: space-between;
      padding: 8px 0;
      font-size: 14px;
      
      .info-value {
        color: $text-primary;
        font-weight: 500;
      }
    }
  }
  
  .contract-actions {
    display: flex;
    gap: 10px;
  }
}

.contract-preview {
  .preview-header {
    text-align: center;
    padding-bottom: 20px;
    border-bottom: 2px solid $primary-color;
    margin-bottom: 20px;
    
    h2 {
      font-size: 20px;
      color: $text-primary;
      margin-bottom: 5px;
    }
    
    p {
      color: $text-secondary;
      font-size: 14px;
    }
  }
  
  .preview-body {
    .preview-section {
      margin-bottom: 20px;
      
      h3 {
        font-size: 16px;
        color: $primary-color;
        margin-bottom: 10px;
      }
      
      p {
        font-size: 14px;
        line-height: 1.8;
        color: $text-primary;
      }
    }
  }
}

.sign-content {
  text-align: center;
  
  .contract-name {
    font-size: 18px;
    font-weight: 600;
    color: $primary-color;
    margin: 10px 0;
  }
  
  .sign-area {
    margin: 20px 0;
    
    .sign-canvas {
      width: 100%;
      height: 150px;
      border: 2px dashed $border-color-base;
      border-radius: 8px;
      cursor: crosshair;
    }
  }
}
</style>