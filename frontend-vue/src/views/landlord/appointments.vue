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
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const activeTab = ref('all')

const appointments = ref([
  { id: 1, houseName: '阳光小区3室2厅', address: '朝阳区阳光路88号', tenantName: '用户A', tenantPhone: '138****8888', date: '2024-01-16 14:00-16:00', status: 'PENDING', remark: '希望下午看房' },
  { id: 2, houseName: '幸福花园2室1厅', address: '海淀区幸福街12号', tenantName: '用户B', tenantPhone: '139****9999', date: '2024-01-17 10:00-11:00', status: 'PENDING', remark: '' },
  { id: 3, houseName: '锦绣家园1室1厅', address: '西城区锦绣路36号', tenantName: '用户C', tenantPhone: '137****7777', date: '2024-01-15 09:00-11:00', status: 'CONFIRMED', remark: '' },
  { id: 4, houseName: '阳光小区3室2厅', address: '朝阳区阳光路88号', tenantName: '用户D', tenantPhone: '136****6666', date: '2024-01-14 14:00-16:00', status: 'COMPLETED', remark: '' }
])

const selectedAppointment = ref(null)
const showDetailDialog = ref(false)

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

const viewDetail = (appointment) => {
  selectedAppointment.value = appointment
  showDetailDialog.value = true
}

const confirmAppointment = (appointment) => {
  appointment.status = 'CONFIRMED'
  ElMessage.success('预约已确认')
}

const rejectAppointment = async (appointment) => {
  try {
    await ElMessageBox.confirm('确定要拒绝这个预约吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    appointment.status = 'REJECTED'
    ElMessage.success('预约已拒绝')
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const completeAppointment = (appointment) => {
  appointment.status = 'COMPLETED'
  ElMessage.success('看房已完成')
}

const createContract = () => {
  ElMessage.success('合同已发起')
  showDetailDialog.value = false
}
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