<template>
  <div class="landlord-repairs">
    <div class="page-header">
      <h1>报修处理</h1>
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
          <el-table-column prop="tenantName" label="租客" />
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
              <el-button v-if="scope.row.status === 'PENDING'" size="small" type="primary" @click="acceptRepair(scope.row)">接单处理</el-button>
              <el-button v-if="scope.row.status === 'REPAIRING'" size="small" type="success" @click="completeRepair(scope.row)">完成维修</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="待处理" name="pending">
        <el-table :data="repairs.filter(r => r.status === 'PENDING')" border>
          <el-table-column prop="houseName" label="房源" />
          <el-table-column prop="type" label="报修类型" />
          <el-table-column prop="tenantName" label="租客" />
          <el-table-column prop="description" label="问题描述" />
          <el-table-column prop="createTime" label="提交时间" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="viewDetail(scope.row)">详情</el-button>
              <el-button size="small" type="primary" @click="acceptRepair(scope.row)">接单处理</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="维修中" name="repairing">
        <el-table :data="repairs.filter(r => r.status === 'REPAIRING')" border>
          <el-table-column prop="houseName" label="房源" />
          <el-table-column prop="type" label="报修类型" />
          <el-table-column prop="tenantName" label="租客" />
          <el-table-column prop="handler" label="维修人员" />
          <el-table-column prop="repairTime" label="接单时间" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="viewDetail(scope.row)">详情</el-button>
              <el-button size="small" type="success" @click="completeRepair(scope.row)">完成维修</el-button>
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
          <span class="detail-label">租客</span>
          <span class="detail-value">{{ selectedRepair.tenantName }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">联系电话</span>
          <span class="detail-value">{{ selectedRepair.tenantPhone }}</span>
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
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
        <el-button v-if="selectedRepair?.status === 'PENDING'" type="primary" @click="acceptRepair(selectedRepair)">接单处理</el-button>
        <el-button v-if="selectedRepair?.status === 'REPAIRING'" type="success" @click="completeRepair(selectedRepair)">完成维修</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const activeTab = ref('all')

const repairs = ref([
  { id: 1, houseName: '阳光小区3室2厅', type: 'WATER_LEAK', tenantName: '用户A', tenantPhone: '138****8888', description: '卫生间水管漏水严重，需要维修', createTime: '2024-01-15 10:30', status: 'PENDING', handler: null, repairTime: null, images: [] },
  { id: 2, houseName: '幸福花园2室1厅', type: 'ELECTRIC', tenantName: '用户B', tenantPhone: '139****9999', description: '客厅空调无法启动', createTime: '2024-01-14 15:00', status: 'REPAIRING', handler: '维修师傅张', repairTime: '2024-01-14 16:00', images: [] },
  { id: 3, houseName: '锦绣家园1室1厅', type: 'WALL', tenantName: '用户C', tenantPhone: '137****7777', description: '卧室墙面有裂缝', createTime: '2024-01-10 09:00', status: 'COMPLETED', handler: '维修师傅李', repairTime: '2024-01-10 10:00', images: [] }
])

const selectedRepair = ref(null)
const showDetailDialog = ref(false)

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

const acceptRepair = (repair) => {
  repair.status = 'REPAIRING'
  repair.handler = '维修师傅张'
  repair.repairTime = new Date().toLocaleString()
  ElMessage.success('已接单处理')
  showDetailDialog.value = false
}

const completeRepair = (repair) => {
  repair.status = 'COMPLETED'
  ElMessage.success('维修已完成')
  showDetailDialog.value = false
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.landlord-repairs {
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
</style>