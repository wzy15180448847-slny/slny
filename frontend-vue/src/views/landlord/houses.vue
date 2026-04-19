<template>
  <div class="landlord-houses">
    <div class="page-header">
      <h1>房源管理</h1>
      <el-button type="primary" @click="goToAdd">
        <el-icon><Plus /></el-icon>
        发布房源
      </el-button>
    </div>
    
    <el-tabs v-model="activeTab" type="card">
      <el-tab-pane label="全部" name="all">
        <el-table :data="houses" border>
          <el-table-column prop="houseName" label="房源名称" />
          <el-table-column prop="address" label="地址" />
          <el-table-column prop="rent" label="租金">
            <template #default="scope">¥{{ scope.row.rent }}/月</template>
          </el-table-column>
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="viewCount" label="浏览量" />
          <el-table-column prop="appointmentCount" label="预约数" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="viewHouse(scope.row)">查看</el-button>
              <el-button size="small" @click="editHouse(scope.row)">编辑</el-button>
              <el-button 
                v-if="scope.row.status === 'ACTIVE'" 
                size="small" 
                type="warning" 
                @click="toggleHouse(scope.row)"
              >
                下架
              </el-button>
              <el-button 
                v-else-if="scope.row.status === 'INACTIVE'" 
                size="small" 
                type="success" 
                @click="toggleHouse(scope.row)"
              >
                上架
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="展示中" name="active">
        <el-table :data="houses.filter(h => h.status === 'ACTIVE')" border>
          <el-table-column prop="houseName" label="房源名称" />
          <el-table-column prop="address" label="地址" />
          <el-table-column prop="rent" label="租金">
            <template #default="scope">¥{{ scope.row.rent }}/月</template>
          </el-table-column>
          <el-table-column prop="viewCount" label="浏览量" />
          <el-table-column prop="appointmentCount" label="预约数" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="viewHouse(scope.row)">查看</el-button>
              <el-button size="small" @click="editHouse(scope.row)">编辑</el-button>
              <el-button size="small" type="warning" @click="toggleHouse(scope.row)">下架</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="审核中" name="pending">
        <el-table :data="houses.filter(h => h.status === 'PENDING')" border>
          <el-table-column prop="houseName" label="房源名称" />
          <el-table-column prop="address" label="地址" />
          <el-table-column prop="rent" label="租金">
            <template #default="scope">¥{{ scope.row.rent }}/月</template>
          </el-table-column>
          <el-table-column prop="createTime" label="提交时间" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="viewHouse(scope.row)">查看</el-button>
              <el-button size="small" @click="editHouse(scope.row)">编辑</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="已下架" name="inactive">
        <el-table :data="houses.filter(h => h.status === 'INACTIVE')" border>
          <el-table-column prop="houseName" label="房源名称" />
          <el-table-column prop="address" label="地址" />
          <el-table-column prop="rent" label="租金">
            <template #default="scope">¥{{ scope.row.rent }}/月</template>
          </el-table-column>
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="viewHouse(scope.row)">查看</el-button>
              <el-button size="small" @click="editHouse(scope.row)">编辑</el-button>
              <el-button size="small" type="success" @click="toggleHouse(scope.row)">上架</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
    
    <el-dialog title="房源详情" v-model="showDetailDialog" width="600px">
      <div v-if="selectedHouse" class="detail-content">
        <div class="detail-images">
          <img v-for="(img, index) in selectedHouse.images" :key="index" :src="img" class="house-image" />
        </div>
        <div class="detail-info">
          <h3>{{ selectedHouse.houseName }}</h3>
          <p class="address">{{ selectedHouse.address }}</p>
          <div class="info-grid">
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
            <div class="info-item">
              <span class="info-label">朝向</span>
              <span class="info-value">{{ selectedHouse.direction }}</span>
            </div>
          </div>
          <div class="tags">
            <span v-for="tag in selectedHouse.tags" :key="tag" class="tag">{{ tag }}</span>
          </div>
          <div class="description">
            <h4>房源描述</h4>
            <p>{{ selectedHouse.description }}</p>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const activeTab = ref('all')

const houses = ref([
  { id: 1, houseName: '阳光小区3室2厅', address: '朝阳区阳光路88号', rent: 3500, area: 120, rooms: '3室2厅', direction: '南北通透', status: 'ACTIVE', viewCount: 120, appointmentCount: 5, createTime: '2024-01-10', images: ['https://picsum.photos/seed/landlord1/800/600', 'https://picsum.photos/seed/landlord1a/800/600', 'https://picsum.photos/seed/landlord1b/800/600'], tags: ['精装修', '近地铁'], description: '小区环境优美，交通便利，周边配套齐全。' },
  { id: 2, houseName: '幸福花园2室1厅', address: '海淀区幸福街12号', rent: 2800, area: 85, rooms: '2室1厅', direction: '朝南', status: 'ACTIVE', viewCount: 85, appointmentCount: 3, createTime: '2024-01-08', images: ['https://picsum.photos/seed/landlord2/800/600', 'https://picsum.photos/seed/landlord2a/800/600'], tags: ['拎包入住'], description: '温馨两居，采光良好。' },
  { id: 3, houseName: '锦绣家园1室1厅', address: '西城区锦绣路36号', rent: 2000, area: 55, rooms: '1室1厅', direction: '朝北', status: 'PENDING', viewCount: 0, appointmentCount: 0, createTime: '2024-01-15', images: ['https://picsum.photos/seed/landlord3/800/600'], tags: ['单身公寓'], description: '单身公寓，适合年轻人居住。' },
  { id: 4, houseName: '星河湾4室2厅', address: '东城区星河大道1号', rent: 5500, area: 180, rooms: '4室2厅', direction: '南北通透', status: 'INACTIVE', viewCount: 200, appointmentCount: 8, createTime: '2024-01-05', images: ['https://picsum.photos/seed/landlord4/800/600', 'https://picsum.photos/seed/landlord4a/800/600', 'https://picsum.photos/seed/landlord4b/800/600', 'https://picsum.photos/seed/landlord4c/800/600'], tags: ['豪华装修', '带车位'], description: '豪华大四居，带地下车位。' }
])

const selectedHouse = ref(null)
const showDetailDialog = ref(false)

const getStatusType = (status) => {
  const types = {
    'ACTIVE': 'success',
    'PENDING': 'warning',
    'INACTIVE': 'info',
    'RENTED': 'primary'
  }
  return types[status] || 'default'
}

const getStatusText = (status) => {
  const texts = {
    'ACTIVE': '展示中',
    'PENDING': '审核中',
    'INACTIVE': '已下架',
    'RENTED': '已出租'
  }
  return texts[status] || status
}

const goToAdd = () => {
  router.push('/landlord/houses/add')
}

const viewHouse = (house) => {
  selectedHouse.value = house
  showDetailDialog.value = true
}

const editHouse = (house) => {
  router.push(`/landlord/houses/edit/${house.id}`)
}

const toggleHouse = (house) => {
  if (house.status === 'ACTIVE') {
    house.status = 'INACTIVE'
    ElMessage.success('房源已下架')
  } else if (house.status === 'INACTIVE') {
    house.status = 'ACTIVE'
    ElMessage.success('房源已上架')
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.landlord-houses {
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
    
    .tags {
      display: flex;
      flex-wrap: wrap;
      gap: 5px;
      margin-bottom: 15px;
      
      .tag {
        background: $bg-color;
        padding: 3px 8px;
        border-radius: 4px;
        font-size: 12px;
        color: $text-secondary;
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