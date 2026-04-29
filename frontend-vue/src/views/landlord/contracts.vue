<template>
  <div class="landlord-contracts">
    <div class="page-header">
      <h1>合同管理</h1>
      <p class="header-desc">管理您的租赁合同</p>
    </div>
    
    <el-card class="contract-card">
      <el-tabs v-model="activeTab" type="card" class="custom-tabs">
        <el-tab-pane label="全部" name="all">
          <el-table :data="contracts" border class="contract-table">
            <el-table-column prop="houseName" label="房源名称" min-width="120" />
            <el-table-column prop="address" label="地址" min-width="180" />
            <el-table-column prop="contractNo" label="合同编号" min-width="150" />
            <el-table-column prop="tenantName" label="租客" min-width="100" />
            <el-table-column prop="startDate" label="开始日期" width="120" />
            <el-table-column prop="endDate" label="结束日期" width="120" />
            <el-table-column prop="rent" label="月租金" width="100" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)" class="status-tag">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="220">
              <template #default="scope">
                <el-button size="small" class="btn-preview" @click="previewContract(scope.row)">预览合同</el-button>
                <el-button 
                  v-if="scope.row.status === 'PENDING'" 
                  size="small" 
                  type="primary" 
                  class="btn-send"
                  @click="sendContract(scope.row)"
                >
                  发送合同
                </el-button>
                <el-button 
                  v-if="scope.row.status === 'ACTIVE'" 
                  size="small" 
                  type="warning" 
                  class="btn-bill"
                  @click="generateBill(scope.row)"
                >
                  生成账单
                </el-button>
                <el-button 
                  v-if="scope.row.status === 'ACTIVE'" 
                  size="small" 
                  class="btn-export"
                  @click="exportContract(scope.row)"
                >
                  导出Word
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="contracts.length === 0" class="empty-state">
            <el-empty description="暂无合同记录" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="待签署" name="pending">
          <el-table :data="contracts.filter(c => c.status === 'PENDING')" border class="contract-table">
            <el-table-column prop="houseName" label="房源名称" min-width="120" />
            <el-table-column prop="address" label="地址" min-width="180" />
            <el-table-column prop="contractNo" label="合同编号" min-width="150" />
            <el-table-column prop="tenantName" label="租客" min-width="100" />
            <el-table-column prop="startDate" label="开始日期" width="120" />
            <el-table-column prop="endDate" label="结束日期" width="120" />
            <el-table-column prop="rent" label="月租金" width="100" />
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <el-button size="small" class="btn-preview" @click="previewContract(scope.row)">预览合同</el-button>
                <el-button size="small" type="primary" class="btn-send" @click="sendContract(scope.row)">发送合同</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="contracts.filter(c => c.status === 'PENDING').length === 0" class="empty-state">
            <el-empty description="暂无待签署合同" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="生效中" name="active">
          <el-table :data="contracts.filter(c => c.status === 'ACTIVE')" border class="contract-table">
            <el-table-column prop="houseName" label="房源名称" min-width="120" />
            <el-table-column prop="address" label="地址" min-width="180" />
            <el-table-column prop="contractNo" label="合同编号" min-width="150" />
            <el-table-column prop="tenantName" label="租客" min-width="100" />
            <el-table-column prop="startDate" label="开始日期" width="120" />
            <el-table-column prop="endDate" label="结束日期" width="120" />
            <el-table-column prop="rent" label="月租金" width="100" />
            <el-table-column label="操作" width="220">
              <template #default="scope">
                <el-button size="small" class="btn-preview" @click="previewContract(scope.row)">预览合同</el-button>
                <el-button size="small" type="warning" class="btn-bill" @click="generateBill(scope.row)">生成账单</el-button>
                <el-button size="small" class="btn-export" @click="exportContract(scope.row)">导出Word</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="contracts.filter(c => c.status === 'ACTIVE').length === 0" class="empty-state">
            <el-empty description="暂无生效中合同" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
    
    <el-dialog title="合同预览" v-model="showPreviewDialog" width="800px" class="contract-dialog">
      <div v-if="selectedContract" class="contract-preview">
        <div class="preview-header">
          <h2>房屋租赁合同</h2>
          <p>合同编号: {{ selectedContract.contractNo }}</p>
        </div>
        <div class="preview-body">
          <div class="preview-section">
            <h3>一、双方当事人</h3>
            <div class="section-content">
              <p><span class="label">甲方（出租方）:</span> {{ userStore.nickname }}</p>
              <p><span class="label">乙方（承租方）:</span> {{ selectedContract.tenantName }}</p>
            </div>
          </div>
          <div class="preview-section">
            <h3>二、租赁房屋</h3>
            <div class="section-content">
              <p><span class="label">房屋地址:</span> {{ selectedContract.address }}</p>
              <p><span class="label">房屋名称:</span> {{ selectedContract.houseName }}</p>
            </div>
          </div>
          <div class="preview-section">
            <h3>三、租赁期限</h3>
            <div class="section-content">
              <p><span class="label">租赁期限:</span> 自 {{ selectedContract.startDate }} 至 {{ selectedContract.endDate }}</p>
            </div>
          </div>
          <div class="preview-section">
            <h3>四、租金及支付方式</h3>
            <div class="section-content">
              <p><span class="label">月租金:</span> ¥{{ selectedContract.rent.replace('¥', '') }}</p>
              <p><span class="label">押金:</span> ¥{{ selectedContract.deposit }}</p>
            </div>
          </div>
          <div class="preview-section">
            <h3>五、双方权利义务</h3>
            <div class="section-content">
              <p>1. 甲方保证房屋设施完好，乙方应爱护房屋及设施。</p>
              <p>2. 乙方应按时支付租金，逾期支付需承担违约责任。</p>
              <p>3. 租赁期间双方不得擅自解除合同。</p>
            </div>
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
import { ElMessage, ElEmpty } from 'element-plus'
import { getLandlordContracts, sendContract as sendContractApi, exportContract as apiExportContract, generateBill as apiGenerateBill } from '@/api/landlord'

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
    const data = await getLandlordContracts()
    contracts.value = (data?.records || []).map(lease => ({
      id: lease.id,
      contractNo: lease.leaseNo || '',
      houseName: lease.house?.title || '未知房源',
      tenantName: lease.tenant?.nickname || lease.tenant?.username || '未知租客',
      address: lease.house?.address || '',
      startDate: lease.startDate ? formatDate(lease.startDate) : '',
      endDate: lease.endDate ? formatDate(lease.endDate) : '',
      rent: lease.rentPrice ? '¥' + lease.rentPrice.toString() : '¥0',
      deposit: lease.deposit ? lease.deposit.toString() : '0',
      status: mapStatus(lease.status)
    }))
  } catch (error) {
    console.error('加载合同列表失败:', error)
    ElMessage.error('加载合同列表失败')
  }
}

const mapStatus = (status) => {
  const statusMap = {
    0: 'PENDING',
    1: 'PENDING',
    2: 'ACTIVE',
    3: 'EXPIRED',
    4: 'TERMINATED',
    5: 'TERMINATED'
  }
  return statusMap[status] || 'PENDING'
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

const sendContract = async (contract) => {
  try {
    await sendContractApi(contract.id)
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
  max-width: 1400px;
  margin: 0 auto;
  background: #f5f7fa;
  min-height: calc(100vh - 120px);
}

.page-header {
  margin-bottom: 24px;
  
  h1 {
    font-size: 28px;
    font-weight: 600;
    color: #1f2937;
    margin: 0 0 8px 0;
  }
  
  .header-desc {
    font-size: 14px;
    color: #6b7280;
    margin: 0;
  }
}

.contract-card {
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  border: none;
  
  :deep(.el-card__body) {
    padding: 24px;
  }
}

.custom-tabs {
  :deep(.el-tabs__header) {
    margin-bottom: 24px;
    border-bottom: 2px solid #f3f4f6;
    
    .el-tabs__nav-wrap {
      padding: 0;
    }
    
    .el-tabs__item {
      font-size: 15px;
      font-weight: 500;
      color: #6b7280;
      padding: 12px 32px;
      margin-right: 8px;
      border-radius: 8px 8px 0 0;
      transition: all 0.3s ease;
      
      &:hover {
        color: $primary-color;
        background: rgba($primary-color, 0.05);
      }
      
      &.is-active {
        color: $primary-color;
        background: #fff;
        border-bottom: 3px solid $primary-color;
      }
    }
  }
}

.contract-table {
  width: 100%;
  border-radius: 12px;
  overflow: hidden;
  
  :deep(.el-table__header-wrapper) {
    background: #f9fafb;
    
    th {
      background: #f9fafb;
      color: #4b5563;
      font-weight: 600;
      font-size: 14px;
      padding: 16px 12px;
      border-bottom: 2px solid #e5e7eb;
      
      &.el-table__cell {
        padding-left: 20px;
      }
    }
  }
  
  :deep(.el-table__body-wrapper) {
    tr {
      transition: background-color 0.2s ease;
      
      &:hover {
        background: rgba($primary-color, 0.03);
      }
      
      &.el-table__row--striped {
        background: #fafafa;
        
        &:hover {
          background: rgba($primary-color, 0.03);
        }
      }
    }
    
    td {
      padding: 16px 12px;
      font-size: 14px;
      color: #374151;
      border-bottom: 1px solid #f3f4f6;
      
      &.el-table__cell {
        padding-left: 20px;
      }
    }
  }
}

.status-tag {
  border-radius: 20px;
  padding: 4px 12px;
  font-size: 12px;
  font-weight: 500;
}

.btn-preview {
  margin-right: 8px;
  background: #f3f4f6;
  color: #4b5563;
  border: none;
  
  &:hover {
    background: #e5e7eb;
    color: #374151;
  }
}

.btn-send {
  margin-right: 8px;
  background: $primary-color;
  border: none;
  
  &:hover {
    background: darken($primary-color, 10%);
  }
}

.btn-bill {
  margin-right: 8px;
  background: #f59e0b;
  border: none;
  
  &:hover {
    background: #d97706;
  }
}

.btn-export {
  background: #6366f1;
  border: none;
  
  &:hover {
    background: #4f46e5;
  }
}

.empty-state {
  padding: 60px 0;
  text-align: center;
}

.contract-dialog {
  :deep(.el-dialog__header) {
    background: linear-gradient(135deg, $primary-color 0%, darken($primary-color, 10%) 100%);
    border-radius: 12px 12px 0 0;
    
    .el-dialog__title {
      color: #fff;
      font-size: 18px;
      font-weight: 600;
    }
    
    .el-dialog__close {
      color: rgba(255, 255, 255, 0.8);
      
      &:hover {
        color: #fff;
      }
    }
  }
  
  :deep(.el-dialog__body) {
    padding: 30px;
  }
}

.contract-preview {
  .preview-header {
    text-align: center;
    padding-bottom: 24px;
    border-bottom: 2px solid $primary-color;
    margin-bottom: 24px;
    
    h2 {
      font-size: 24px;
      font-weight: 700;
      color: #1f2937;
      margin: 0 0 12px 0;
    }
    
    p {
      color: #6b7280;
      font-size: 14px;
      margin: 0;
    }
  }
  
  .preview-body {
    .preview-section {
      margin-bottom: 24px;
      
      h3 {
        font-size: 16px;
        font-weight: 600;
        color: $primary-color;
        margin: 0 0 16px 0;
        padding-left: 12px;
        border-left: 4px solid $primary-color;
      }
      
      .section-content {
        background: #f9fafb;
        padding: 16px 20px;
        border-radius: 8px;
        
        .label {
          display: inline-block;
          min-width: 100px;
          color: #6b7280;
          font-weight: 500;
        }
        
        p {
          font-size: 14px;
          line-height: 1.8;
          color: #374151;
          margin: 8px 0;
        }
      }
    }
  }
}
</style>