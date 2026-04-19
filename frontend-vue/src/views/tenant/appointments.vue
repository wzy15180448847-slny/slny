<template>
  <div class="tenant-appointments">
    <div class="page-header">
      <h1>我的预约</h1>
      <el-button type="primary" @click="createAppointment">
        <el-icon><Plus /></el-icon>
        预约看房
      </el-button>
    </div>
    
    <el-tabs v-model="activeTab" type="card">
      <el-tab-pane label="全部" name="all">
        <el-table :data="filteredAppointments" border>
          <el-table-column prop="houseName" label="房源名称" />
          <el-table-column prop="address" label="地址" />
          <el-table-column prop="date" label="预约时间" />
          <el-table-column prop="landlordName" label="房东" />
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="viewDetail(scope.row)">详情</el-button>
              <el-button 
                v-if="scope.row.status === 'PENDING'" 
                size="small" 
                type="danger" 
                @click="cancelAppointment(scope.row)"
              >
                取消
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="待确认" name="pending">
        <el-table :data="appointments.filter(a => a.status === 'PENDING')" border>
          <el-table-column prop="houseName" label="房源名称" />
          <el-table-column prop="address" label="地址" />
          <el-table-column prop="date" label="预约时间" />
          <el-table-column prop="landlordName" label="房东" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="viewDetail(scope.row)">详情</el-button>
              <el-button size="small" type="danger" @click="cancelAppointment(scope.row)">取消</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="已安排" name="confirmed">
        <el-table :data="appointments.filter(a => a.status === 'CONFIRMED')" border>
          <el-table-column prop="houseName" label="房源名称" />
          <el-table-column prop="address" label="地址" />
          <el-table-column prop="date" label="预约时间" />
          <el-table-column prop="landlordName" label="房东" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="viewDetail(scope.row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="已完成" name="completed">
        <el-table :data="appointments.filter(a => a.status === 'COMPLETED')" border>
          <el-table-column prop="houseName" label="房源名称" />
          <el-table-column prop="address" label="地址" />
          <el-table-column prop="date" label="预约时间" />
          <el-table-column prop="landlordName" label="房东" />
          <el-table-column label="操作">
            <template #default="scope">
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
          <span class="detail-label">预约时间</span>
          <span class="detail-value">{{ selectedAppointment.date }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">房东</span>
          <span class="detail-value">{{ selectedAppointment.landlordName }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">联系电话</span>
          <span class="detail-value">{{ selectedAppointment.landlordPhone }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">状态</span>
          <el-tag :type="getStatusType(selectedAppointment.status)">
            {{ getStatusText(selectedAppointment.status) }}
          </el-tag>
        </div>
      </div>
    </el-dialog>
    
    <el-dialog title="预约看房" v-model="showCreateDialog" width="500px">
      <el-form :model="createForm" label-width="100px">
        <el-form-item label="选择房源">
          <el-select v-model="createForm.houseId" placeholder="请选择房源">
            <el-option v-for="house in availableHouses" :key="house.id" :label="house.name" :value="house.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="预约日期">
          <el-date-picker v-model="createForm.date" type="date" placeholder="选择日期" />
        </el-form-item>
        <el-form-item label="预约时间">
          <el-select v-model="createForm.time" placeholder="选择时间段">
            <el-option label="09:00-11:00" value="09:00-11:00" />
            <el-option label="14:00-16:00" value="14:00-16:00" />
            <el-option label="16:00-18:00" value="16:00-18:00" />
            <el-option label="19:00-21:00" value="19:00-21:00" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="createForm.remark" :rows="3" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="submitAppointment">提交预约</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('all')

const appointments = ref([
  { id: 1, houseName: '阳光小区3室2厅', address: '朝阳区阳光路88号', date: '2024-01-16 14:00-16:00', landlordName: '张房东', landlordPhone: '138****8888', status: 'PENDING' },
  { id: 2, houseName: '幸福花园2室1厅', address: '海淀区幸福街12号', date: '2024-01-17 10:00-11:00', landlordName: '李房东', landlordPhone: '139****9999', status: 'CONFIRMED' },
  { id: 3, houseName: '锦绣家园1室1厅', address: '西城区锦绣路36号', date: '2024-01-15 09:00-11:00', landlordName: '王房东', landlordPhone: '137****7777', status: 'COMPLETED' },
  { id: 4, houseName: '星河湾4室2厅', address: '东城区星河大道1号', date: '2024-01-18 14:00-16:00', landlordName: '赵房东', landlordPhone: '136****6666', status: 'PENDING' }
])

const availableHouses = ref([
  { id: 1, name: '阳光小区3室2厅' },
  { id: 2, name: '幸福花园2室1厅' },
  { id: 3, name: '锦绣家园1室1厅' },
  { id: 4, name: '星河湾4室2厅' },
  { id: 5, name: '绿城花园3室1厅' }
])

const selectedAppointment = ref(null)
const showDetailDialog = ref(false)
const showCreateDialog = ref(false)

const createForm = reactive({
  houseId: '',
  date: '',
  time: '',
  remark: ''
})

const filteredAppointments = ref(appointments.value)

const getStatusType = (status) => {
  const types = {
    'PENDING': 'warning',
    'CONFIRMED': 'success',
    'COMPLETED': 'info',
    'CANCELLED': 'danger'
  }
  return types[status] || 'default'
}

const getStatusText = (status) => {
  const texts = {
    'PENDING': '待确认',
    'CONFIRMED': '已安排',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return texts[status] || status
}

const viewDetail = (appointment) => {
  selectedAppointment.value = appointment
  showDetailDialog.value = true
}

const cancelAppointment = async (appointment) => {
  try {
    await ElMessageBox.confirm('确定要取消这个预约吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    appointment.status = 'CANCELLED'
    ElMessage.success('预约已取消')
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const createAppointment = () => {
  showCreateDialog.value = true
}

const submitAppointment = () => {
  if (!createForm.houseId || !createForm.date || !createForm.time) {
    ElMessage.error('请填写完整信息')
    return
  }
  const house = availableHouses.value.find(h => h.id === createForm.houseId)
  appointments.value.unshift({
    id: Date.now(),
    houseName: house.name,
    address: '待完善',
    date: `${createForm.date} ${createForm.time}`,
    landlordName: '待完善',
    landlordPhone: '待完善',
    status: 'PENDING'
  })
  ElMessage.success('预约提交成功')
  showCreateDialog.value = false
  createForm.houseId = ''
  createForm.date = ''
  createForm.time = ''
  createForm.remark = ''
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.tenant-appointments {
  padding: 30px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  
  h1 {
    font-size: 24px;
    color: $text-primary;
  }
}

.detail-content {
  padding: 10px 0;
}

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
</style>