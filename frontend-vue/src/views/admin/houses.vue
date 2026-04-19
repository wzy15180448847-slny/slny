<template>
  <div class="admin-houses">
    <div class="page-header">
      <h1>房源列表</h1>
    </div>
    
    <div class="search-bar">
      <el-input v-model="searchForm.keyword" placeholder="搜索房源名称或地址" class="search-input" />
      <el-select v-model="searchForm.status" placeholder="选择状态">
        <el-option label="全部" value="" />
        <el-option label="展示中" value="ACTIVE" />
        <el-option label="审核中" value="PENDING" />
        <el-option label="已下架" value="INACTIVE" />
        <el-option label="已出租" value="RENTED" />
      </el-select>
      <el-button type="primary" @click="search">搜索</el-button>
    </div>
    
    <el-table :data="filteredHouses" border>
      <el-table-column prop="houseName" label="房源名称" />
      <el-table-column prop="address" label="地址" />
      <el-table-column prop="landlordName" label="房东" />
      <el-table-column prop="rent" label="租金">
        <template #default="scope">¥{{ scope.row.rent }}/月</template>
      </el-table-column>
      <el-table-column prop="area" label="面积">
        <template #default="scope">{{ scope.row.area }}㎡</template>
      </el-table-column>
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="viewCount" label="浏览量" />
      <el-table-column prop="createTime" label="创建时间" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button size="small" @click="viewDetail(scope.row)">查看</el-button>
          <el-button size="small" type="warning" @click="editHouse(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteHouse(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
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
          <div class="tags">
            <span v-for="tag in selectedHouse.tags" :key="tag" class="tag">{{ tag }}</span>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchForm = reactive({
  keyword: '',
  status: ''
})

const houses = ref([
  { id: 1, houseName: '阳光小区3室2厅', address: '朝阳区阳光路88号', landlordName: '张房东', rent: 3500, area: 120, rooms: '3室2厅', status: 'ACTIVE', viewCount: 120, createTime: '2024-01-10', images: ['https://picsum.photos/seed/house1/800/600', 'https://picsum.photos/seed/house1a/800/600'], tags: ['精装修', '近地铁'] },
  { id: 2, houseName: '幸福花园2室1厅', address: '海淀区幸福街12号', landlordName: '李房东', rent: 2800, area: 85, rooms: '2室1厅', status: 'ACTIVE', viewCount: 85, createTime: '2024-01-08', images: ['https://picsum.photos/seed/house2/800/600'], tags: ['拎包入住'] },
  { id: 3, houseName: '锦绣家园1室1厅', address: '西城区锦绣路36号', landlordName: '王房东', rent: 2000, area: 55, rooms: '1室1厅', status: 'PENDING', viewCount: 0, createTime: '2024-01-15', images: ['https://picsum.photos/seed/house3/800/600'], tags: ['单身公寓'] },
  { id: 4, houseName: '星河湾4室2厅', address: '东城区星河大道1号', landlordName: '赵房东', rent: 5500, area: 180, rooms: '4室2厅', status: 'RENTED', viewCount: 200, createTime: '2024-01-05', images: ['https://picsum.photos/seed/house4/800/600', 'https://picsum.photos/seed/house4a/800/600'], tags: ['豪华装修', '带车位'] }
])

const router = useRouter()
const selectedHouse = ref(null)
const showDetailDialog = ref(false)

const filteredHouses = computed(() => {
  return houses.value.filter(house => {
    const matchKeyword = !searchForm.keyword || 
      house.houseName.includes(searchForm.keyword) || 
      house.address.includes(searchForm.keyword)
    const matchStatus = !searchForm.status || house.status === searchForm.status
    return matchKeyword && matchStatus
  })
})

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

const search = () => {}

const viewDetail = (house) => {
  selectedHouse.value = house
  showDetailDialog.value = true
}

const editHouse = (house) => {
  router.push(`/admin/houses/edit/${house.id}`)
}

const deleteHouse = async (house) => {
  try {
    await ElMessageBox.confirm('确定要删除这个房源吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const index = houses.value.findIndex(h => h.id === house.id)
    if (index > -1) {
      houses.value.splice(index, 1)
    }
    ElMessage.success('房源已删除')
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.admin-houses {
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

.search-bar {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  flex-wrap: wrap;
  
  .search-input {
    width: 300px;
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
      
      .tag {
        background: $bg-color;
        padding: 3px 8px;
        border-radius: 4px;
        font-size: 12px;
        color: $text-secondary;
      }
    }
  }
}
</style>