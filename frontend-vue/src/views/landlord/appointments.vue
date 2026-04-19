<template>
  <div class="landlord-appointments">
    <div class="page-header">
      <h1>预约处理</h1>
    </div>
    
    <el-tabs v-model="activeTab" type="card">
      <el-tab-pane label="全部" name="all">
        <el-table :data="appointments" border>
          <el-table-column prop="houseName" label="房源名称" />
          <el-table-column prop="tenantName" label="租客" />
          <el-table-column prop="tenantPhone" label="联系电话" />
          <el-table-column prop="date" label="预约时间" />
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template #default="scope">
              <el-button v-if="scope.row.status === 'PENDING'" size="small" type="success" @click="confirmAppointment(scope.row)">确认预约</el-button>
              <el-button v-if="scope.row.status === 'PENDING'" size="small" type="danger" @click="rejectAppointment(scope.row)">拒绝</el-button>
              <el-button v-if="scope.row.status === 'CONFIRMED'" size="small" @click="completeAppointment(scope.row)">完成看房</el-button>
              <el-button size="small" @click="viewDetail(scope.row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="待确认" name="pending">
        <el-table :data="appointments.filter(a => a.status === 'PENDING')" border>
          <el-table-column prop="houseName" label="房源名称" />
          <el-table-column prop="tenantName" label="租客" />
          <el-table-column prop="tenantPhone" label="联系电话" />
          <el-table-column prop="date" label="预约时间" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" type="success" @click="confirmAppointment(scope.row)">确认预约</el-button>
              <el-button size="small" type="danger" @click="rejectAppointment(scope.row)">拒绝</el-button>
              <el-button size="small" @click="viewDetail(scope.row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="已安排" name="confirmed">
        <el-table :data="appointments.filter(a => a.status === 'CONFIRMED')" border>
          <el-table-column prop="houseName" label="房源名称" />
          <el-table-column prop="tenantName" label="租客" />
          <el-table-column prop="date" label="预约时间" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="completeAppointment(scope.row)">完成看房</el-button>
              <el-button size="small" @click="viewDetail(scope.row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
    
    <el-dialog title="预约详情" v-model="showDetailDialog" width="500px">
      <div v-if="selectedAppointment" class="detail-content">
        <div class="detail-row">
          <span class="detail-label">房源名称</span>
          <span class="detail-value">{{ selectedAppointment.houseName }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">地址</span>
          <span class="detail-value">{{ selectedAppointment.address }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">租客姓名</span>
          <span class="detail-value">{{ selectedAppointment.tenantName }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">联系电话</span>
          <span class="detail-value">{{ selectedAppointment.tenantPhone }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">预约时间</span>
          <span class="detail-value">{{ selectedAppointment.date }}</span>
        </div>
        <div class="detail-row" v-if="selectedAppointment.remark">
          <span class="detail-label">备注</span>
          <span class="detail-value">{{ selectedAppointment.remark }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">状态</span>
          <el-tag :type="getStatusType(selectedAppointment.status)">
            {{ getStatusText(selectedAppointment.status) }}
          </el-tag>
        </div>
      </div>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
        <el-button 
          v-if="selectedAppointment?.status === 'CONFIRMED'" 
          type="primary" 
          @click="createContract"
        >
          发起合同
        </el-button>
      </template>
    </el-dialog>
    
    <el-dialog title="拒绝预约" v-model="showRejectDialog" width="400px">
      <el-form :model="rejectForm" label-width="100px">
        <el-form-item label="拒绝原因">
          <el-input type="textarea" v-model="rejectForm.reason" :rows="3" placeholder="请输入拒绝原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRejectDialog = false">取消</el-button>
        <el-button type="danger" @click="submitReject">确认拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getLandlordAppointments, 
  confirmAppointment, 
  rejectAppointment, 
  completeAppointment,
  createContract 
} from '@/api/landlord'

const router = useRouter()
const activeTab = ref('all')

const appointments = ref([])
const selectedAppointment = ref(null)
const showDetailDialog = ref(false)
const showRejectDialog = ref(false)

const rejectForm = reactive({
  reason: ''
})

const getStatusType = (status) => {
  const types = {
    'PENDING': 'warning',
    'CONFIRMED': 'success',
    'COMPLETED': 'info',
    'CANCELLED': 'danger',
    'REJECTED': 'danger'
  }
  return types[status] || 'default'
}

const getStatusText = (status) => {
  const texts = {
    'PENDING': '待确认',
    'CONFIRMED': '已安排',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消',
    'REJECTED': '已拒绝'
  }
  return texts[status] || status
}

const loadAppointments = async () => {
  try {
    const { data } = await getLandlordAppointments()
    appointments.value = data || []
  } catch (error) {
    console.error('加载预约列表失败:', error)
    ElMessage.error('加载预约列表失败')
  }
}

const viewDetail = (appointment) => {
  selectedAppointment.value = appointment
  showDetailDialog.value = true
}

const confirmAppointment = async (appointment) => {
  try {
    await confirmAppointment(appointment.id)
    appointment.status = 'CONFIRMED'
    ElMessage.success('预约已确认')
    await loadAppointments()
  } catch (error) {
    console.error('确认预约失败:', error)
    ElMessage.error('确认预约失败')
  }
}

const rejectAppointment = (appointment) => {
  selectedAppointment.value = appointment
  rejectForm.reason = ''
  showRejectDialog.value = true
}

const submitReject = async () => {
  if (!rejectForm.reason) {
    ElMessage.error('请输入拒绝原因')
    return
  }
  
  try {
    await rejectAppointment(selectedAppointment.value.id, { reason: rejectForm.reason })
    selectedAppointment.value.status = 'REJECTED'
    ElMessage.success('预约已拒绝')
    showRejectDialog.value = false
    await loadAppointments()
  } catch (error) {
    console.error('拒绝预约失败:', error)
    ElMessage.error('拒绝预约失败')
  }
}

const completeAppointment = async (appointment) => {
  try {
    await completeAppointment(appointment.id)
    appointment.status = 'COMPLETED'
    ElMessage.success('看房已完成')
    await loadAppointments()
  } catch (error) {
    console.error('完成看房失败:', error)
    ElMessage.error('完成看房失败')
  }
}

const createContract = async () => {
  try {
    await createContract({
      houseId: selectedAppointment.value.houseId,
      tenantId: selectedAppointment.value.tenantId
    })
    ElMessage.success('合同已发起')
    showDetailDialog.value = false
    await loadAppointments()
  } catch (error) {
    console.error('发起合同失败:', error)
    ElMessage.error('发起合同失败')
  }
}

onMounted(() => {
  loadAppointments()
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.landlord-appointments {
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

.detail-content {
  padding: 10px 0;
  
  .detail-row {
    display: flex;
    justify-content: space-between;
    padding: 12px 0;
    border-bottom: 1px solid $border-color-base;
    
    &:last-child {
      border-bottom: none;
    }
    
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