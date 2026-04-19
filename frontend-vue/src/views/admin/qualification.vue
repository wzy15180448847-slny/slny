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
        <div class="detail-images">
          <span class="detail-label">证件照片</span>
          <div class="images-grid">
            <img v-for="(img, index) in selectedQualification.images" :key="index" :src="img" class="id-image" />
          </div>
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
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const activeTab = ref('pending')

const STORAGE_KEY = 'qualification_data'

const defaultPending = [
  { id: 1, landlordName: '王房东', username: 'user5', phone: '138****0006', idCard: '110101********1234', houseCount: 3, createTime: '2024-01-15 10:30', images: [], status: 'PENDING' },
  { id: 2, landlordName: '刘房东', username: 'user6', phone: '138****0007', idCard: '110102********5678', houseCount: 2, createTime: '2024-01-15 09:00', images: [], status: 'PENDING' }
]

const defaultApproved = [
  { id: 3, landlordName: '张房东', username: 'user0', phone: '138****0002', houseCount: 4, auditTime: '2024-01-10 14:00' },
  { id: 4, landlordName: '李房东', username: 'user2', phone: '138****0004', houseCount: 2, auditTime: '2024-01-08 10:00' }
]

const defaultRejected = [
  { id: 5, landlordName: '陈房东', username: 'user7', phone: '138****0008', rejectReason: '身份证照片模糊，无法识别', auditTime: '2024-01-14 16:00' }
]

const loadData = () => {
  try {
    const stored = localStorage.getItem(STORAGE_KEY)
    if (stored) {
      const data = JSON.parse(stored)
      return {
        pending: data.pending || defaultPending,
        approved: data.approved || defaultApproved,
        rejected: data.rejected || defaultRejected
      }
    }
  } catch (e) {
    console.error('Failed to load data from localStorage:', e)
  }
  return {
    pending: [...defaultPending],
    approved: [...defaultApproved],
    rejected: [...defaultRejected]
  }
}

const saveData = () => {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify({
      pending: pendingQualifications.value,
      approved: approvedQualifications.value,
      rejected: rejectedQualifications.value
    }))
  } catch (e) {
    console.error('Failed to save data to localStorage:', e)
  }
}

const loadedData = loadData()
const pendingQualifications = ref(loadedData.pending)
const approvedQualifications = ref(loadedData.approved)
const rejectedQualifications = ref(loadedData.rejected)

const selectedQualification = ref(null)
const showDetailDialog = ref(false)
const showRejectDialog = ref(false)

const rejectForm = reactive({
  reason: ''
})

const viewDetail = (qualification) => {
  selectedQualification.value = qualification
  showDetailDialog.value = true
}

const approveQualification = (qualification) => {
  const index = pendingQualifications.value.findIndex(q => q.id === qualification.id)
  if (index > -1) {
    pendingQualifications.value.splice(index, 1)
    approvedQualifications.value.unshift({
      ...qualification,
      auditTime: new Date().toLocaleString()
    })
    saveData()
  }
  ElMessage.success('审核通过')
  showDetailDialog.value = false
}

const openRejectDialog = () => {
  showRejectDialog.value = true
}

const confirmReject = () => {
  if (!rejectForm.reason) {
    ElMessage.error('请输入拒绝原因')
    return
  }
  
  const index = pendingQualifications.value.findIndex(q => q.id === selectedQualification.value.id)
  if (index > -1) {
    pendingQualifications.value.splice(index, 1)
    rejectedQualifications.value.unshift({
      ...selectedQualification.value,
      rejectReason: rejectForm.reason,
      auditTime: new Date().toLocaleString()
    })
    saveData()
  }
  
  ElMessage.success('已拒绝')
  showRejectDialog.value = false
  showDetailDialog.value = false
  rejectForm.reason = ''
}

const rejectQualification = (qualification) => {
  selectedQualification.value = qualification
  showRejectDialog.value = true
}

const reApprove = (qualification) => {
  const index = rejectedQualifications.value.findIndex(q => q.id === qualification.id)
  if (index > -1) {
    rejectedQualifications.value.splice(index, 1)
    pendingQualifications.value.unshift({
      ...qualification,
      status: 'PENDING'
    })
    saveData()
  }
  ElMessage.success('已重新提交审核')
}
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
  
  .detail-images {
    padding: 12px 0;
    
    .images-grid {
      display: flex;
      gap: 10px;
      margin-top: 10px;
      
      .id-image {
        width: 150px;
        height: 90px;
        object-fit: cover;
        border-radius: 8px;
      }
    }
  }
}
</style>