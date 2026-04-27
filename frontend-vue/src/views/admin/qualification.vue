<template>
  <div class="admin-qualification">
    <div class="page-header">
      <h1>资质审核</h1>
    </div>
    
    <el-tabs v-model="activeTab" type="card">
      <el-tab-pane label="待审核" name="pending">
        <el-table :data="pendingQualifications" border>
          <el-table-column prop="landlordName" label="房东姓名" />
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="phone" label="手机号" />
          <el-table-column prop="idCard" label="身份证号" />
          <el-table-column prop="houseCount" label="房源数量" />
          <el-table-column prop="createTime" label="提交时间" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="viewDetail(scope.row)">查看详情</el-button>
              <el-button size="small" type="success" @click="approveQualification(scope.row)">通过</el-button>
              <el-button size="small" type="danger" @click="rejectQualification(scope.row)">拒绝</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="已通过" name="approved">
        <el-table :data="approvedQualifications" border>
          <el-table-column prop="landlordName" label="房东姓名" />
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="phone" label="手机号" />
          <el-table-column prop="houseCount" label="房源数量" />
          <el-table-column prop="auditTime" label="审核时间" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="已拒绝" name="rejected">
        <el-table :data="rejectedQualifications" border>
          <el-table-column prop="landlordName" label="房东姓名" />
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="phone" label="手机号" />
          <el-table-column prop="rejectReason" label="拒绝原因" />
          <el-table-column prop="auditTime" label="审核时间" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" type="primary" @click="reApprove(scope.row)">重新审核</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
    
    <el-dialog title="资质审核详情" v-model="showDetailDialog" width="500px">
      <div v-if="selectedQualification" class="detail-content">
        <div class="detail-row">
          <span class="detail-label">房东姓名</span>
          <span class="detail-value">{{ selectedQualification.landlordName }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">用户名</span>
          <span class="detail-value">{{ selectedQualification.username }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">手机号</span>
          <span class="detail-value">{{ selectedQualification.phone }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">身份证号</span>
          <span class="detail-value">{{ selectedQualification.idCard }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">房源数量</span>
          <span class="detail-value">{{ selectedQualification.houseCount }} 套</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
        <el-button v-if="selectedQualification?.status === 'PENDING'" type="success" @click="approveQualification(selectedQualification)">通过审核</el-button>
        <el-button v-if="selectedQualification?.status === 'PENDING'" type="danger" @click="openRejectDialog">拒绝</el-button>
      </template>
    </el-dialog>
    
    <el-dialog title="拒绝审核" v-model="showRejectDialog" width="400px">
      <el-form :model="rejectForm" label-width="80px">
        <el-form-item label="拒绝原因">
          <el-input type="textarea" v-model="rejectForm.reason" :rows="3" placeholder="请输入拒绝原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRejectDialog = false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确认拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const activeTab = ref('pending')

const pendingQualifications = ref([])
const approvedQualifications = ref([])
const rejectedQualifications = ref([])

const selectedQualification = ref(null)
const showDetailDialog = ref(false)
const showRejectDialog = ref(false)

const rejectForm = reactive({
  reason: ''
})

const loadPendingList = async () => {
  try {
    const response = await request.get('/agent-qualification/pending', {
      params: { page: 1, size: 100 }
    })
    if (response && response.records) {
      pendingQualifications.value = response.records.map(item => ({
        id: item.id,
        landlordName: item.realName || '未知',
        username: '',
        phone: item.phone ? item.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2') : '未知',
        idCard: item.idCard ? item.idCard.replace(/(\d{4})\d{8}(\d{4})/, '$1********$2') : '未知',
        houseCount: item.houseCount || 0,
        createTime: item.createTime,
        status: 'PENDING'
      }))
    }
  } catch (error) {
    console.error('加载待审核列表失败:', error)
    ElMessage.error('加载待审核列表失败')
  }
}

const loadApprovedList = async () => {
  try {
    const response = await request.get('/agent-qualification/approved', {
      params: { page: 1, size: 100 }
    })
    if (response && response.records) {
      approvedQualifications.value = response.records.map(item => ({
        id: item.id,
        landlordName: item.realName || '未知',
        username: '',
        phone: item.phone ? item.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2') : '未知',
        houseCount: item.houseCount || 0,
        auditTime: item.auditTime || item.updateTime
      }))
    }
  } catch (error) {
    console.error('加载已通过列表失败:', error)
    ElMessage.error('加载已通过列表失败')
  }
}

const loadRejectedList = async () => {
  try {
    const response = await request.get('/agent-qualification/rejected', {
      params: { page: 1, size: 100 }
    })
    if (response && response.records) {
      rejectedQualifications.value = response.records.map(item => ({
        id: item.id,
        landlordName: item.realName || '未知',
        username: '',
        phone: item.phone ? item.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2') : '未知',
        rejectReason: item.rejectReason || '未说明原因',
        auditTime: item.auditTime || item.updateTime
      }))
    }
  } catch (error) {
    console.error('加载已拒绝列表失败:', error)
    ElMessage.error('加载已拒绝列表失败')
  }
}

const viewDetail = (qualification) => {
  selectedQualification.value = qualification
  showDetailDialog.value = true
}

const approveQualification = async (qualification) => {
  try {
    await request.post(`/agent-qualification/audit/${qualification.id}`, null, {
      params: { auditStatus: 1, auditRemark: '审核通过' }
    })
    
    const index = pendingQualifications.value.findIndex(q => q.id === qualification.id)
    if (index > -1) {
      pendingQualifications.value.splice(index, 1)
      approvedQualifications.value.unshift({
        ...qualification,
        auditTime: new Date().toLocaleString()
      })
    }
    ElMessage.success('审核通过')
    showDetailDialog.value = false
  } catch (error) {
    console.error('审核失败:', error)
    ElMessage.error('审核失败')
  }
}

const openRejectDialog = () => {
  showRejectDialog.value = true
}

const confirmReject = async () => {
  if (!rejectForm.reason) {
    ElMessage.error('请输入拒绝原因')
    return
  }
  
  try {
    await request.post(`/api/agent-qualification/audit/${selectedQualification.value.id}`, null, {
      params: { auditStatus: 2, auditRemark: rejectForm.reason }
    })
    
    const index = pendingQualifications.value.findIndex(q => q.id === selectedQualification.value.id)
    if (index > -1) {
      pendingQualifications.value.splice(index, 1)
      rejectedQualifications.value.unshift({
        ...selectedQualification.value,
        rejectReason: rejectForm.reason,
        auditTime: new Date().toLocaleString()
      })
    }
    
    ElMessage.success('已拒绝')
    showRejectDialog.value = false
    showDetailDialog.value = false
    rejectForm.reason = ''
  } catch (error) {
    console.error('拒绝失败:', error)
    ElMessage.error('拒绝失败')
  }
}

const rejectQualification = (qualification) => {
  selectedQualification.value = qualification
  showRejectDialog.value = true
}

const reApprove = async (qualification) => {
  try {
    await request.post(`/agent-qualification/audit/${qualification.id}`, null, {
      params: { auditStatus: 0, auditRemark: '重新审核' }
    })
    
    const index = rejectedQualifications.value.findIndex(q => q.id === qualification.id)
    if (index > -1) {
      rejectedQualifications.value.splice(index, 1)
      pendingQualifications.value.unshift({
        ...qualification,
        status: 'PENDING'
      })
    }
    ElMessage.success('已重新提交审核')
  } catch (error) {
    console.error('重新审核失败:', error)
    ElMessage.error('重新审核失败')
  }
}

onMounted(() => {
  loadPendingList()
  loadApprovedList()
  loadRejectedList()
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.admin-qualification {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
  
  h1 {
    font-size: 22px;
    color: $text-primary;
    margin: 0;
  }
}

.detail-content {
  .detail-row {
    display: flex;
    justify-content: space-between;
    padding: 12px 0;
    border-bottom: 1px solid $border-color-base;
    
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