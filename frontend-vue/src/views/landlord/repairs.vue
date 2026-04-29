<template>
  <div class="landlord-repairs">
    <div class="page-header">
      <h1>报修处理</h1>
    </div>
    
    <el-tabs v-model="activeTab" type="card">
      <el-tab-pane label="全部" name="all">
        <el-table :data="repairList" border>
          <el-table-column prop="houseName" label="房源" />
          <el-table-column prop="repairType" label="报修类型">
            <template #default="scope">
              <el-tag :type="getTypeTag(scope.row.repairType)">{{ getTypeText(scope.row.repairType) }}</el-tag>
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
        <el-table :data="repairList.filter(r => r.status === 'PENDING')" border>
          <el-table-column prop="houseName" label="房源" />
          <el-table-column prop="repairType" label="报修类型" />
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
        <el-table :data="repairList.filter(r => r.status === 'REPAIRING')" border>
          <el-table-column prop="houseName" label="房源" />
          <el-table-column prop="repairType" label="报修类型" />
          <el-table-column prop="tenantName" label="租客" />
          <el-table-column prop="description" label="问题描述" />
          <el-table-column prop="updateTime" label="接单时间" />
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
          <el-tag :type="getTypeTag(selectedRepair.repairType)">{{ getTypeText(selectedRepair.repairType) }}</el-tag>
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
        <div class="detail-row">
          <span class="detail-label">状态</span>
          <el-tag :type="getStatusType(selectedRepair.status)">{{ getStatusText(selectedRepair.status) }}</el-tag>
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
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getLandlordRepairs, acceptRepair as acceptRepairApi, completeRepair as completeRepairApi } from '@/api/landlord'

const activeTab = ref('all')
const repairList = ref([])
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

const loadRepairs = async () => {
  try {
    const response = await getLandlordRepairs()
    if (response.code === 200) {
      const data = response.data
      repairList.value = data.records.map(item => ({
        ...item,
        status: mapStatus(item.status),
        repairType: item.repairType || 'OTHER',
        createTime: formatDate(item.createTime),
        updateTime: formatDate(item.updateTime),
        houseName: item.houseName || '未知房源',
        tenantName: item.tenantName || '未知租客',
        tenantPhone: item.tenantPhone || '未知'
      }))
    }
  } catch (error) {
    console.error('加载报修列表失败:', error)
    ElMessage.error('加载报修列表失败')
  }
}

const mapStatus = (status) => {
  const statusMap = {
    0: 'PENDING',
    1: 'REPAIRING',
    2: 'COMPLETED',
    3: 'CANCELLED'
  }
  return statusMap[status] || 'PENDING'
}

const formatDate = (date) => {
  if (!date) return ''
  return date.replace('T', ' ').substring(0, 16)
}

const viewDetail = (repair) => {
  selectedRepair.value = repair
  showDetailDialog.value = true
}

const handleAcceptRepair = async (repair) => {
  try {
    const response = await acceptRepairApi(repair.id)
    if (response.code === 200) {
      repair.status = 'REPAIRING'
      ElMessage.success('已接单处理')
      showDetailDialog.value = false
    } else {
      ElMessage.error('接单失败')
    }
  } catch (error) {
    console.error('接单失败:', error)
    ElMessage.error('接单失败')
  }
}

const handleCompleteRepair = async (repair) => {
  try {
    const response = await completeRepairApi(repair.id)
    if (response.code === 200) {
      repair.status = 'COMPLETED'
      ElMessage.success('维修已完成')
      showDetailDialog.value = false
    } else {
      ElMessage.error('完成维修失败')
    }
  } catch (error) {
    console.error('完成维修失败:', error)
    ElMessage.error('完成维修失败')
  }
}

const acceptRepair = (repair) => {
  if (showDetailDialog.value) {
    handleAcceptRepair(repair)
  } else {
    handleAcceptRepair(repair)
  }
}

const completeRepair = (repair) => {
  if (showDetailDialog.value) {
    handleCompleteRepair(repair)
  } else {
    handleCompleteRepair(repair)
  }
}

onMounted(() => {
  loadRepairs()
})
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
}
</style>