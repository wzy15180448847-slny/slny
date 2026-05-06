<template>
  <div class="admin-contracts">
    <div class="page-header">
      <h1>合同管理</h1>
      <p class="header-desc">查看所有合同（只读）</p>
    </div>
    
    <el-tabs v-model="activeTab" type="card" class="custom-tabs">
      <el-tab-pane label="全部" name="all">
        <el-table :data="contracts" border class="contract-table">
          <el-table-column prop="houseName" label="房源名称" min-width="120" />
          <el-table-column prop="address" label="地址" min-width="180" />
          <el-table-column prop="contractNo" label="合同编号" min-width="150" />
          <el-table-column prop="landlordName" label="房东" min-width="100" />
          <el-table-column prop="tenantName" label="租客" min-width="100" />
          <el-table-column prop="startDate" label="开始日期" width="120" />
          <el-table-column prop="endDate" label="结束日期" width="120" />
          <el-table-column prop="rent" label="月租金" width="100" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="220">
            <template #default="scope">
              <el-button size="small" class="btn-preview" @click="previewContract(scope.row)">查看合同</el-button>
              <el-button size="small" class="btn-export" @click="exportContract(scope.row)">导出Word</el-button>
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
          <el-table-column prop="landlordName" label="房东" min-width="100" />
          <el-table-column prop="tenantName" label="租客" min-width="100" />
          <el-table-column prop="startDate" label="开始日期" width="120" />
          <el-table-column prop="rent" label="月租金" width="100" />
          <el-table-column label="操作" width="220">
            <template #default="scope">
              <el-button size="small" class="btn-preview" @click="previewContract(scope.row)">查看合同</el-button>
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
          <el-table-column prop="landlordName" label="房东" min-width="100" />
          <el-table-column prop="tenantName" label="租客" min-width="100" />
          <el-table-column prop="startDate" label="开始日期" width="120" />
          <el-table-column prop="rent" label="月租金" width="100" />
          <el-table-column label="操作" width="220">
            <template #default="scope">
              <el-button size="small" class="btn-preview" @click="previewContract(scope.row)">查看合同</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="contracts.filter(c => c.status === 'ACTIVE').length === 0" class="empty-state">
          <el-empty description="暂无生效中合同" />
        </div>
      </el-tab-pane>
    </el-tabs>
    
    <el-dialog title="合同详情" v-model="showPreviewDialog" width="500px">
      <div v-if="selectedContract" class="contract-preview">
        <div class="preview-header">
          <h2>房屋租赁合同</h2>
          <p>合同编号: {{ selectedContract.contractNo }}</p>
        </div>
        <div class="preview-body">
          <div class="preview-section">
            <h3>一、双方当事人</h3>
            <div class="section-content">
              <p><span class="detail-label">房东</span><span class="detail-value">{{ selectedContract.landlordName }}</span></p>
              <p><span class="detail-label">租客</span><span class="detail-value">{{ selectedContract.tenantName }}</span></p>
            </div>
          </div>
          <div class="preview-section">
            <h3>二、租赁房屋</h3>
            <div class="section-content">
              <p><span class="detail-label">房屋地址</span><span class="detail-value">{{ selectedContract.address }}</span></p>
              <p><span class="detail-label">房屋名称</span><span class="detail-value">{{ selectedContract.houseName }}</span></p>
            </div>
          </div>
          <div class="preview-section">
            <h3>三、租赁期限</h3>
            <div class="section-content">
              <p><span class="detail-label">租赁期限</span><span class="detail-value">自 {{ selectedContract.startDate }} 至 {{ selectedContract.endDate }}</span></p>
            </div>
          </div>
          <div class="preview-section">
            <h3>四、租金及支付方式</h3>
            <div class="section-content">
              <p><span class="detail-label">月租金</span><span class="detail-value">¥{{ selectedContract.rent }}</span></p>
            </div>
          </div>
          <div class="preview-section">
            <h3>五、合同状态</h3>
            <div class="section-content">
              <el-tag :type="getStatusType(selectedContract.status)">{{ getStatusText(selectedContract.status) }}</el-tag>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showPreviewDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdminContracts, exportContract as exportContractApi } from '@/api/admin'

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
    'TERMINATED': '已解约'
  }
  return texts[status] || status
}

const loadContracts = async () => {
  try {
    const data = await getAdminContracts()
    contracts.value = (data?.records || []).map(lease => ({
      id: lease.id,
      contractNo: lease.leaseNo || '',
      houseName: lease.house?.title || '未知房源',
      landlordName: lease.landlord?.nickname || lease.landlord?.username || '未知房东',
      tenantName: lease.tenant?.nickname || lease.tenant?.username || '未知租客',
      address: lease.house?.address || '',
      startDate: lease.startDate ? formatDate(lease.startDate) : '',
      endDate: lease.endDate ? formatDate(lease.endDate) : '',
      rent: lease.rentPrice,
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

const previewContract = (contract) => {
  selectedContract.value = contract
  showPreviewDialog.value = true
}

const exportContract = async (contract) => {
  try {
    const response = await exportContractApi(contract.id)
    
    const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' })
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
.admin-contracts {
  padding: 24px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.page-header .header-desc {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.custom-tabs :deep(.el-tabs__header) {
  margin-bottom: 24px;
  border-bottom: 2px solid #f3f4f6;
}

.contract-table {
  width: 100%;
  border-radius: 12px;
  overflow: hidden;
}

.contract-table :deep(.el-table__header-wrapper) {
  background: #f9fafb;
}

.btn-preview {
  margin-right: 8px;
  background: #f3f4f6;
  color: #4b5563;
  border: none;
}

.btn-preview:hover {
  background: #e5e5e7;
  color: #374151;
}

.btn-export {
  background: #6366f1;
  color: white;
  border: none;
}

.btn-export:hover {
  background: #4f46e5;
}

.empty-state {
  padding: 60px 0;
  text-align: center;
}

.contract-preview .preview-header {
  text-align: center;
  padding-bottom: 24px;
  border-bottom: 2px solid #667eea;
  margin-bottom: 24px;
}

.contract-preview .preview-header h2 {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 12px 0;
}

.contract-preview .preview-header p {
  color: #6b7280;
  font-size: 14px;
  margin: 0;
}

.contract-preview .preview-body .preview-section {
  margin-bottom: 24px;
}

.contract-preview .preview-body .preview-section h3 {
  font-size: 16px;
  font-weight: 600;
  color: #667eea;
  margin: 0 0 16px 0;
  padding-left: 12px;
  border-left: 4px solid #667eea;
}

.contract-preview .preview-body .preview-section .section-content {
  background: #f9fafb;
  padding: 16px 20px;
  border-radius: 8px;
}

.contract-preview .preview-body .preview-section .section-content p {
  display: flex;
  font-size: 14px;
  line-height: 1.8;
  color: #374151;
  margin: 8px 0;
}

.contract-preview .preview-body .preview-section .section-content .detail-label {
  min-width: 100px;
  color: #6b7280;
  font-weight: 500;
}

.contract-preview .preview-body .preview-section .section-content .detail-value {
  color: #1f2937;
}
</style>
