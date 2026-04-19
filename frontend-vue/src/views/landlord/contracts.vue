<template>
  <div class="landlord-contracts">
    <div class="page-header">
      <h1>合同管理</h1>
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
                <span>租客:</span>
                <span class="info-value">{{ contract.tenantName }}</span>
              </div>
              <div class="info-row">
                <span>租赁期限:</span>
                <span class="info-value">{{ contract.startDate }} 至 {{ contract.endDate }}</span>
              </div>
              <div class="info-row">
                <span>租金:</span>
                <span class="info-value">¥{{ contract.rent }}/月</span>
              </div>
            </div>
            <div class="contract-actions">
              <el-button size="small" @click="previewContract(contract)">预览合同</el-button>
              <el-button v-if="contract.status === 'PENDING'" size="small" type="primary" @click="sendContract(contract)">发送合同</el-button>
              <el-button v-if="contract.status === 'ACTIVE'" size="small" type="warning" @click="generateBill(contract)">生成账单</el-button>
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
                <span>租客:</span>
                <span class="info-value">{{ contract.tenantName }}</span>
              </div>
              <div class="info-row">
                <span>租赁期限:</span>
                <span class="info-value">{{ contract.startDate }} 至 {{ contract.endDate }}</span>
              </div>
            </div>
            <div class="contract-actions">
              <el-button size="small" @click="previewContract(contract)">预览合同</el-button>
              <el-button size="small" type="primary" @click="sendContract(contract)">发送合同</el-button>
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
                <span>租客:</span>
                <span class="info-value">{{ contract.tenantName }}</span>
              </div>
              <div class="info-row">
                <span>到期提醒:</span>
                <span class="info-value">{{ contract.daysRemaining }}天后到期</span>
              </div>
            </div>
            <div class="contract-actions">
              <el-button size="small" @click="previewContract(contract)">预览合同</el-button>
              <el-button size="small" type="warning" @click="generateBill(contract)">生成账单</el-button>
              <el-button size="small" @click="exportContract(contract)">导出Word</el-button>
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
            <p>甲方（出租方）: {{ userStore.nickname }}</p>
            <p>乙方（承租方）: {{ selectedContract.tenantName }}</p>
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
        </div>
      </div>
      <template #footer>
        <el-button @click="showPreviewDialog = false">关闭</el-button>
        <el-button v-if="selectedContract?.status === 'PENDING'" type="primary" @click="sendContract(selectedContract)">发送给租客</el-button>
        <el-button v-if="selectedContract?.status === 'ACTIVE'" @click="exportContract(selectedContract)">导出Word</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { getLandlordContracts, sendContract, exportContract as apiExportContract, generateBill as apiGenerateBill } from '@/api/landlord'

const userStore = useUserStore()
const activeTab = ref('all')

const contracts = ref([])

const selectedContract = ref(null)
const showPreviewDialog = ref(false)

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

const loadContracts = async () => {
  try {
    const { data } = await getLandlordContracts()
    contracts.value = data || []
  } catch (error) {
    console.error('加载合同列表失败:', error)
    ElMessage.error('加载合同列表失败')
  }
}

const sendContract = async (contract) => {
  try {
    await sendContract(contract.id)
    contract.status = 'PENDING'
    ElMessage.success('合同已发送给租客')
    showPreviewDialog.value = false
    await loadContracts()
  } catch (error) {
    console.error('发送合同失败:', error)
    ElMessage.error('发送合同失败')
  }
}

const generateBill = async (contract) => {
  try {
    await apiGenerateBill(contract.id)
    ElMessage.success('账单已生成')
    await loadContracts()
  } catch (error) {
    console.error('生成账单失败:', error)
    ElMessage.error('生成账单失败')
  }
}

const exportContract = async (contract) => {
  try {
    const response = await apiExportContract(contract.id)
    
    const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' })
    const downloadUrl = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = downloadUrl
    a.download = `${contract.contractNo}.docx`
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(downloadUrl)
    
    ElMessage.success('合同导出成功')
  } catch (error) {
    console.error('导出合同失败:', error)
    ElMessage.error('导出合同失败')
  }
}

onMounted(() => {
  loadContracts()
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.landlord-contracts {
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
</style>