<template>
  <div class="tenant-repairs">
    <div class="page-header">
      <h1>报修管理</h1>
      <el-button type="primary" @click="createRepair">
        <el-icon><Plus /></el-icon>
        提交报修
      </el-button>
    </div>
    
    <el-tabs v-model="activeTab" type="card">
      <el-tab-pane label="全部" name="all">
        <el-table :data="repairs" border>
          <el-table-column prop="houseName" label="房源" />
          <el-table-column prop="type" label="报修类型">
            <template #default="scope">
              <el-tag :type="getTypeTag(scope.row.type)">{{ getTypeText(scope.row.type) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="问题描述" />
          <el-table-column prop="createTime" label="提交时间" />
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="viewDetail(scope.row)">详情</el-button>
              <el-button v-if="scope.row.status === 'PENDING'" size="small" type="danger" @click="cancelRepair(scope.row)">取消</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="待处理" name="pending">
        <el-table :data="repairs.filter(r => r.status === 'PENDING')" border>
          <el-table-column prop="houseName" label="房源" />
          <el-table-column prop="type" label="报修类型" />
          <el-table-column prop="description" label="问题描述" />
          <el-table-column prop="createTime" label="提交时间" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="viewDetail(scope.row)">详情</el-button>
              <el-button size="small" type="danger" @click="cancelRepair(scope.row)">取消</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="维修中" name="repairing">
        <el-table :data="repairs.filter(r => r.status === 'REPAIRING')" border>
          <el-table-column prop="houseName" label="房源" />
          <el-table-column prop="type" label="报修类型" />
          <el-table-column prop="description" label="问题描述" />
          <el-table-column prop="handler" label="维修人员" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="viewDetail(scope.row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="已完成" name="completed">
        <el-table :data="repairs.filter(r => r.status === 'COMPLETED')" border>
          <el-table-column prop="houseName" label="房源" />
          <el-table-column prop="type" label="报修类型" />
          <el-table-column prop="description" label="问题描述" />
          <el-table-column prop="completeTime" label="完成时间" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="viewDetail(scope.row)">详情</el-button>
              <el-button size="small" type="primary" @click="evaluateRepair(scope.row)">评价</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
    
    <el-dialog title="报修详情" v-model="showDetailDialog" width="500px">
      <div v-if="selectedRepair" class="detail-content">
        <div class="detail-row">
          <span class="detail-label">房源名称</span>
          <span class="detail-value">{{ selectedRepair.houseName }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">报修类型</span>
          <el-tag :type="getTypeTag(selectedRepair.type)">{{ getTypeText(selectedRepair.type) }}</el-tag>
        </div>
        <div class="detail-row">
          <span class="detail-label">问题描述</span>
          <span class="detail-value">{{ selectedRepair.description }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">提交时间</span>
          <span class="detail-value">{{ selectedRepair.createTime }}</span>
        </div>
        <div class="detail-row" v-if="selectedRepair.handler">
          <span class="detail-label">维修人员</span>
          <span class="detail-value">{{ selectedRepair.handler }}</span>
        </div>
        <div class="detail-row" v-if="selectedRepair.completeTime">
          <span class="detail-label">完成时间</span>
          <span class="detail-value">{{ selectedRepair.completeTime }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">状态</span>
          <el-tag :type="getStatusType(selectedRepair.status)">{{ getStatusText(selectedRepair.status) }}</el-tag>
        </div>
        <div class="detail-images" v-if="selectedRepair.images && selectedRepair.images.length > 0">
          <span class="detail-label">现场照片</span>
          <div class="images-grid">
            <img v-for="(img, index) in selectedRepair.images" :key="index" :src="img" class="repair-image" />
          </div>
        </div>
      </div>
    </el-dialog>
    
    <el-dialog title="提交报修" v-model="showCreateDialog" width="500px">
      <el-form :model="createForm" label-width="100px">
        <el-form-item label="选择房源">
          <el-select v-model="createForm.houseId" placeholder="请选择房源">
            <el-option v-for="house in houses" :key="house.id" :label="house.name" :value="house.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="报修类型">
          <el-select v-model="createForm.type" placeholder="请选择类型">
            <el-option label="水管漏水" value="WATER_LEAK" />
            <el-option label="电器故障" value="ELECTRIC" />
            <el-option label="墙面损坏" value="WALL" />
            <el-option label="门窗问题" value="DOOR_WINDOW" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="问题描述">
          <el-input type="textarea" v-model="createForm.description" :rows="4" placeholder="请详细描述问题" />
        </el-form-item>
        <el-form-item label="上传图片">
          <el-upload
            action="#"
            :auto-upload="false"
            :file-list="createForm.images"
            list-type="picture-card"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="submitRepair">提交报修</el-button>
      </template>
    </el-dialog>
    
    <el-dialog title="评价维修服务" v-model="showEvaluateDialog" width="400px">
      <div class="evaluate-content">
        <p>请对本次维修服务进行评价</p>
        <el-rate v-model="evaluateForm.score" :max="5" show-text />
        <el-input type="textarea" v-model="evaluateForm.comment" :rows="3" placeholder="请输入评价内容" />
      </div>
      <template #footer>
        <el-button @click="showEvaluateDialog = false">取消</el-button>
        <el-button type="primary" @click="submitEvaluate">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('all')

const repairs = ref([
  { id: 1, houseName: '阳光小区3室2厅', type: 'WATER_LEAK', description: '卫生间水管漏水严重，需要维修', createTime: '2024-01-15 10:30', status: 'PENDING', handler: null, completeTime: null, images: [] },
  { id: 2, houseName: '阳光小区3室2厅', type: 'ELECTRIC', description: '客厅空调无法启动', createTime: '2024-01-14 15:00', status: 'REPAIRING', handler: '维修师傅张', completeTime: null, images: [] },
  { id: 3, houseName: '阳光小区3室2厅', type: 'WALL', description: '卧室墙面有裂缝', createTime: '2024-01-10 09:00', status: 'COMPLETED', handler: '维修师傅李', completeTime: '2024-01-12 16:00', images: [] }
])

const houses = ref([
  { id: 1, name: '阳光小区3室2厅' },
  { id: 2, name: '幸福花园2室1厅' }
])

const selectedRepair = ref(null)
const showDetailDialog = ref(false)
const showCreateDialog = ref(false)
const showEvaluateDialog = ref(false)

const createForm = reactive({
  houseId: '',
  type: '',
  description: '',
  images: []
})

const evaluateForm = reactive({
  score: 5,
  comment: ''
})

const getTypeTag = (type) => {
  const types = {
    'WATER_LEAK': 'danger',
    'ELECTRIC': 'warning',
    'WALL': 'info',
    'DOOR_WINDOW': 'primary',
    'OTHER': 'default'
  }
  return types[type] || 'default'
}

const getTypeText = (type) => {
  const texts = {
    'WATER_LEAK': '水管漏水',
    'ELECTRIC': '电器故障',
    'WALL': '墙面损坏',
    'DOOR_WINDOW': '门窗问题',
    'OTHER': '其他'
  }
  return texts[type] || type
}

const getStatusType = (status) => {
  const types = {
    'PENDING': 'warning',
    'REPAIRING': 'primary',
    'COMPLETED': 'success',
    'CANCELLED': 'danger'
  }
  return types[status] || 'default'
}

const getStatusText = (status) => {
  const texts = {
    'PENDING': '待处理',
    'REPAIRING': '维修中',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return texts[status] || status
}

const viewDetail = (repair) => {
  selectedRepair.value = repair
  showDetailDialog.value = true
}

const cancelRepair = async (repair) => {
  try {
    await ElMessageBox.confirm('确定要取消这个报修吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    repair.status = 'CANCELLED'
    ElMessage.success('报修已取消')
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const createRepair = () => {
  showCreateDialog.value = true
}

const submitRepair = () => {
  if (!createForm.houseId || !createForm.type || !createForm.description) {
    ElMessage.error('请填写完整信息')
    return
  }
  const house = houses.value.find(h => h.id === createForm.houseId)
  repairs.value.unshift({
    id: Date.now(),
    houseName: house.name,
    type: createForm.type,
    description: createForm.description,
    createTime: new Date().toLocaleString(),
    status: 'PENDING',
    handler: null,
    completeTime: null,
    images: []
  })
  ElMessage.success('报修提交成功')
  showCreateDialog.value = false
  createForm.houseId = ''
  createForm.type = ''
  createForm.description = ''
  createForm.images = []
}

const evaluateRepair = (repair) => {
  selectedRepair.value = repair
  showEvaluateDialog.value = true
}

const submitEvaluate = () => {
  ElMessage.success('评价提交成功')
  showEvaluateDialog.value = false
  evaluateForm.score = 5
  evaluateForm.comment = ''
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.tenant-repairs {
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
  
  .detail-images {
    padding: 12px 0;
    
    .images-grid {
      display: flex;
      gap: 10px;
      margin-top: 10px;
      
      .repair-image {
        width: 100px;
        height: 100px;
        object-fit: cover;
        border-radius: 8px;
      }
    }
  }
}

.evaluate-content {
  p {
    margin-bottom: 20px;
  }
  
  .el-rate {
    margin-bottom: 20px;
  }
}
</style>