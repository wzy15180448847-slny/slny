<template>
  <div class="admin-house-audit">
    <div class="page-header">
      <h1>房源审核</h1>
    </div>
    
    <el-tabs v-model="activeTab" type="card">
      <el-tab-pane label="待审核" name="pending">
        <el-table :data="pendingHouses" border>
          <el-table-column prop="houseName" label="房源名称" />
          <el-table-column prop="address" label="地址" />
          <el-table-column prop="landlordName" label="房东" />
          <el-table-column prop="rent" label="租金">
            <template #default="scope">¥{{ scope.row.rent }}/月</template>
          </el-table-column>
          <el-table-column prop="createTime" label="提交时间" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="viewDetail(scope.row)">查看详情</el-button>
              <el-button size="small" type="success" @click="approveHouse(scope.row)">通过</el-button>
              <el-button size="small" type="danger" @click="rejectHouse(scope.row)">拒绝</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="已通过" name="approved">
        <el-table :data="approvedHouses" border>
          <el-table-column prop="houseName" label="房源名称" />
          <el-table-column prop="address" label="地址" />
          <el-table-column prop="landlordName" label="房东" />
          <el-table-column prop="rent" label="租金">
            <template #default="scope">¥{{ scope.row.rent }}/月</template>
          </el-table-column>
          <el-table-column prop="auditTime" label="审核时间" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="viewDetail(scope.row)">查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="已拒绝" name="rejected">
        <el-table :data="rejectedHouses" border>
          <el-table-column prop="houseName" label="房源名称" />
          <el-table-column prop="address" label="地址" />
          <el-table-column prop="landlordName" label="房东" />
          <el-table-column prop="rejectReason" label="拒绝原因" />
          <el-table-column prop="auditTime" label="审核时间" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="viewDetail(scope.row)">查看详情</el-button>
              <el-button size="small" type="primary" @click="reApprove(scope.row)">重新审核</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
    
    <el-dialog title="房源审核详情" v-model="showDetailDialog" width="600px">
      <div v-if="selectedHouse" class="detail-content">
        <div class="detail-images">
          <img v-for="(img, index) in selectedHouse.images" :key="index" :src="img" class="house-image" />
        </div>
        <div class="detail-info">
          <h3>{{ selectedHouse.houseName }}</h3>
          <p class="address">{{ selectedHouse.address }}</p>
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">房东</span>
              <span class="info-value">{{ selectedHouse.landlordName }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">租金</span>
              <span class="info-value">¥{{ selectedHouse.rent }}/月</span>
            </div>
            <div class="info-item">
              <span class="info-label">面积</span>
              <span class="info-value">{{ selectedHouse.area }}㎡</span>
            </div>
            <div class="info-item">
              <span class="info-label">户型</span>
              <span class="info-value">{{ selectedHouse.rooms }}</span>
            </div>
          </div>
          <div class="description">
            <h4>房源描述</h4>
            <p>{{ selectedHouse.description }}</p>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
        <el-button v-if="selectedHouse?.status === 'PENDING'" type="success" @click="approveHouse(selectedHouse)">通过审核</el-button>
        <el-button v-if="selectedHouse?.status === 'PENDING'" type="danger" @click="openRejectDialog">拒绝</el-button>
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

const pendingHouses = ref([
  { id: 1, houseName: '锦绣家园1室1厅', address: '西城区锦绣路36号', landlordName: '王房东', rent: 2000, area: 55, rooms: '1室1厅', createTime: '2024-01-15 10:30', images: ['https://picsum.photos/seed/audit1/800/600'], description: '单身公寓，适合年轻人居住。' },
  { id: 2, houseName: '绿城花园3室1厅', address: '南城区绿环路18号', landlordName: '刘房东', rent: 3200, area: 100, rooms: '3室1厅', createTime: '2024-01-15 09:00', images: ['https://picsum.photos/seed/audit2/800/600', 'https://picsum.photos/seed/audit2a/800/600'], description: '交通便利，周边配套齐全。' }
])

const approvedHouses = ref([
  { id: 3, houseName: '阳光小区3室2厅', address: '朝阳区阳光路88号', landlordName: '张房东', rent: 3500, area: 120, rooms: '3室2厅', auditTime: '2024-01-10 14:00', images: ['https://picsum.photos/seed/audit3/800/600', 'https://picsum.photos/seed/audit3a/800/600'], description: '阳光充足，视野开阔。' },
  { id: 4, houseName: '幸福花园2室1厅', address: '海淀区幸福街12号', landlordName: '李房东', rent: 2800, area: 85, rooms: '2室1厅', auditTime: '2024-01-08 10:00', images: ['https://picsum.photos/seed/audit4/800/600'], description: '温馨舒适，拎包入住。' }
])

const rejectedHouses = ref([
  { id: 5, houseName: '老旧小区1室1厅', address: '北城区旧路街5号', landlordName: '陈房东', rent: 1500, area: 40, rooms: '1室1厅', rejectReason: '房源照片不清晰，信息不完整', auditTime: '2024-01-14 16:00', images: ['https://picsum.photos/seed/audit5/800/600'], description: '老旧小区，设施陈旧。' }
])

const selectedHouse = ref(null)
const showDetailDialog = ref(false)
const showRejectDialog = ref(false)

const rejectForm = reactive({
  reason: ''
})

const viewDetail = (house) => {
  selectedHouse.value = house
  showDetailDialog.value = true
}

const approveHouse = (house) => {
  const index = pendingHouses.value.findIndex(h => h.id === house.id)
  if (index > -1) {
    pendingHouses.value.splice(index, 1)
    approvedHouses.value.unshift({
      ...house,
      status: 'ACTIVE',
      auditTime: new Date().toLocaleString()
    })
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
  
  const index = pendingHouses.value.findIndex(h => h.id === selectedHouse.value.id)
  if (index > -1) {
    pendingHouses.value.splice(index, 1)
    rejectedHouses.value.unshift({
      ...selectedHouse.value,
      status: 'REJECTED',
      rejectReason: rejectForm.reason,
      auditTime: new Date().toLocaleString()
    })
  }
  
  ElMessage.success('已拒绝')
  showRejectDialog.value = false
  showDetailDialog.value = false
  rejectForm.reason = ''
}

const rejectHouse = (house) => {
  selectedHouse.value = house
  showRejectDialog.value = true
}

const reApprove = (house) => {
  const index = rejectedHouses.value.findIndex(h => h.id === house.id)
  if (index > -1) {
    rejectedHouses.value.splice(index, 1)
    pendingHouses.value.unshift({
      ...house,
      status: 'PENDING'
    })
  }
  ElMessage.success('已重新提交审核')
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.admin-house-audit {
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
  .detail-images {
    display: flex;
    gap: 10px;
    margin-bottom: 20px;
    overflow-x: auto;
    
    .house-image {
      width: 150px;
      height: 100px;
      object-fit: cover;
      border-radius: 8px;
      flex-shrink: 0;
    }
  }
  
  .detail-info {
    .address {
      color: $text-secondary;
      font-size: 14px;
      margin-bottom: 15px;
    }
    
    .info-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 10px;
      margin-bottom: 15px;
      
      .info-item {
        display: flex;
        justify-content: space-between;
        
        .info-label {
          color: $text-secondary;
          font-size: 14px;
        }
        
        .info-value {
          color: $text-primary;
          font-weight: 500;
        }
      }
    }
    
    .description {
      h4 {
        font-size: 14px;
        color: $text-primary;
        margin-bottom: 10px;
      }
      
      p {
        font-size: 14px;
        color: $text-secondary;
        line-height: 1.6;
      }
    }
  }
}
</style>