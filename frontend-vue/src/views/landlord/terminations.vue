<template>
  <div class="landlord-terminations">
    <div class="page-header">
      <h1>解约申请处理</h1>
    </div>
    
    <el-tabs v-model="activeTab" type="card">
      <el-tab-pane label="全部" name="all">
        <el-table :data="terminationList" border>
          <el-table-column prop="houseName" label="房源" />
          <el-table-column prop="applicantName" label="申请人" />
          <el-table-column prop="terminationReason" label="解约原因" />
          <el-table-column prop="applyTime" label="申请时间" />
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="viewDetail(scope.row)">详情</el-button>
              <el-button v-if="scope.row.status === 'PENDING'" size="small" type="primary" @click="showProcessDialogFn(scope.row)">处理申请</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="待处理" name="pending">
        <el-table :data="terminationList.filter(t => t.status === 'PENDING')" border>
          <el-table-column prop="houseName" label="房源" />
          <el-table-column prop="applicantName" label="申请人" />
          <el-table-column prop="terminationReason" label="解约原因" />
          <el-table-column prop="applyTime" label="申请时间" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="viewDetail(scope.row)">详情</el-button>
              <el-button size="small" type="primary" @click="showProcessDialogFn(scope.row)">处理申请</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="已同意" name="approved">
        <el-table :data="terminationList.filter(t => t.status === 'APPROVED')" border>
          <el-table-column prop="houseName" label="房源" />
          <el-table-column prop="applicantName" label="申请人" />
          <el-table-column prop="processingOpinion" label="处理意见" />
          <el-table-column prop="penaltyAmount" label="违约金（元）" />
          <el-table-column prop="processingTime" label="处理时间" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="viewDetail(scope.row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="已拒绝" name="rejected">
        <el-table :data="terminationList.filter(t => t.status === 'REJECTED')" border>
          <el-table-column prop="houseName" label="房源" />
          <el-table-column prop="applicantName" label="申请人" />
          <el-table-column prop="processingOpinion" label="处理意见" />
          <el-table-column prop="processingTime" label="处理时间" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="viewDetail(scope.row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
    
    <el-dialog title="解约申请详情" v-model="showDetailDialog" width="500px">
      <div v-if="selectedTermination" class="detail-content">
        <div class="detail-row">
          <span class="detail-label">房源名称</span>
          <span class="detail-value">{{ selectedTermination.houseName }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">申请人</span>
          <span class="detail-value">{{ selectedTermination.applicantName }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">申请人类型</span>
          <span class="detail-value">{{ selectedTermination.applicantType === 'TENANT' ? '租客' : '房东' }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">申请时间</span>
          <span class="detail-value">{{ selectedTermination.applyTime }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">解约原因</span>
          <span class="detail-value">{{ selectedTermination.terminationReason }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">状态</span>
          <el-tag :type="getStatusType(selectedTermination.status)">{{ getStatusText(selectedTermination.status) }}</el-tag>
        </div>
        <div v-if="selectedTermination.status !== 'PENDING'" class="detail-row">
          <span class="detail-label">处理人</span>
          <span class="detail-value">{{ selectedTermination.processorName }}</span>
        </div>
        <div v-if="selectedTermination.status !== 'PENDING'" class="detail-row">
          <span class="detail-label">处理时间</span>
          <span class="detail-value">{{ selectedTermination.processingTime }}</span>
        </div>
        <div v-if="selectedTermination.status !== 'PENDING'" class="detail-row">
          <span class="detail-label">处理意见</span>
          <span class="detail-value">{{ selectedTermination.processingOpinion }}</span>
        </div>
        <div v-if="selectedTermination.status === 'APPROVED' && selectedTermination.penaltyAmount" class="detail-row">
          <span class="detail-label">违约金（元）</span>
          <span class="detail-value">{{ selectedTermination.penaltyAmount }}</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
      </template>
    </el-dialog>
    
    <el-dialog title="处理解约申请" v-model="showProcessDialog" width="500px">
      <el-form v-if="selectedTermination" :model="processForm" label-width="100px">
        <el-form-item label="处理结果">
          <el-radio-group v-model="processForm.status">
            <el-radio :label="1">同意解约</el-radio>
            <el-radio :label="2">拒绝申请</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="processForm.status === 1" label="违约金（元）">
          <el-input-number v-model="processForm.penaltyAmount" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="处理意见">
          <el-input v-model="processForm.processingOpinion" type="textarea" :rows="3" placeholder="请输入处理意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showProcessDialog = false">取消</el-button>
        <el-button type="primary" @click="submitProcess">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getLandlordTerminations, processTermination } from '@/api/landlord'

const activeTab = ref('all')
const terminationList = ref([])
const selectedTermination = ref(null)
const showDetailDialog = ref(false)
const showProcessDialog = ref(false)

const processForm = ref({
  status: 1,
  processingOpinion: '',
  penaltyAmount: 0
})

const getStatusType = (status) => {
  const types = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger',
    'COMPLETED': 'info'
  }
  return types[status] || 'default'
}

const getStatusText = (status) => {
  const texts = {
    'PENDING': '待处理',
    'APPROVED': '已同意',
    'REJECTED': '已拒绝',
    'COMPLETED': '已完成'
  }
  return texts[status] || status
}

const loadTerminations = async () => {
  try {
    const data = await getLandlordTerminations()
    terminationList.value = (data?.records || []).map(item => ({
      id: item.id,
      applicationNo: item.applicationNo,
      houseName: item.lease?.house?.title || '未知房源',
      applicantName: item.applicant?.nickname || item.applicant?.username || '未知用户',
      applicantType: item.applicantType,
      terminationReason: item.terminationReason,
      applyTime: item.applyTime,
      status: mapStatus(item.status),
      processorName: item.processor?.nickname || item.processor?.username || '',
      processingTime: item.processingTime,
      processingOpinion: item.processingOpinion,
      penaltyAmount: item.penaltyAmount,
      leaseId: item.leaseId
    }))
  } catch (error) {
    console.error('加载解约申请失败:', error)
    ElMessage.error('加载解约申请失败')
  }
}

const mapStatus = (status) => {
  const statusMap = {
    0: 'PENDING',
    1: 'APPROVED',
    2: 'REJECTED',
    3: 'COMPLETED'
  }
  return statusMap[status] || 'PENDING'
}

const viewDetail = (item) => {
  selectedTermination.value = item
  showDetailDialog.value = true
}

const showProcessDialogFn = (item) => {
  selectedTermination.value = item
  processForm.value = {
    status: 1,
    processingOpinion: '',
    penaltyAmount: 0
  }
  showProcessDialog.value = true
}

const submitProcess = async () => {
  try {
    await processTermination(selectedTermination.value.id, processForm.value.status, processForm.value.processingOpinion, processForm.value.penaltyAmount)
    ElMessage.success('处理成功')
    showProcessDialog.value = false
    await loadTerminations()
  } catch (error) {
    console.error('处理失败:', error)
    ElMessage.error('处理失败: ' + (error.response?.data?.message || error.message))
  }
}

onMounted(() => {
  loadTerminations()
})
</script>

<style lang="scss" scoped>
.landlord-terminations {
  padding: 24px;
}

.page-header {
  margin-bottom: 24px;
  
  h1 {
    font-size: 28px;
    font-weight: 600;
    color: #1f2937;
  }
}

.detail-content {
  .detail-row {
    display: flex;
    padding: 8px 0;
    border-bottom: 1px solid #f3f4f6;
    
    &:last-child {
      border-bottom: none;
    }
  }
  
  .detail-label {
    min-width: 100px;
    color: #6b7280;
    font-weight: 500;
  }
  
  .detail-value {
    color: #1f2937;
  }
}
</style>
