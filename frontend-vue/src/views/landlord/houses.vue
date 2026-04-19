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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMyHouses, updateHouseStatus } from '@/api/landlord'

const router = useRouter()
const activeTab = ref('all')

const houses = ref([])
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

const loadHouses = async () => {
  try {
    const { data } = await getMyHouses()
    houses.value = data || []
  } catch (error) {
    console.error('加载房源列表失败:', error)
    ElMessage.error('加载房源列表失败')
  }
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

const toggleHouse = async (house) => {
  try {
    const newStatus = house.status === 'ACTIVE' ? 2 : 0
    await updateHouseStatus(house.id, newStatus)
    
    if (house.status === 'ACTIVE') {
      house.status = 'INACTIVE'
      ElMessage.success('房源已下架')
    } else if (house.status === 'INACTIVE') {
      house.status = 'ACTIVE'
      ElMessage.success('房源已上架')
    }
    
    await loadHouses()
  } catch (error) {
    console.error('修改房源状态失败:', error)
    ElMessage.error('修改房源状态失败')
  }
}

onMounted(() => {
  loadHouses()
})
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