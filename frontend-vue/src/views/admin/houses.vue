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
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllHouses, deleteHouse as deleteHouseApi } from '@/api/house'

const searchForm = reactive({
  keyword: '',
  status: ''
})

const houses = ref([])

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

const loadHouses = async () => {
  try {
    const { data } = await getAllHouses()
    houses.value = data.map(house => ({
      id: house.id,
      houseName: house.houseName,
      address: house.address,
      landlordName: house.landlordName || '未知',
      rent: house.rent,
      area: house.area,
      rooms: house.houseType,
      status: house.houseStatus === 0 ? 'ACTIVE' : house.houseStatus === 1 ? 'RENTED' : house.houseStatus === 2 ? 'INACTIVE' : 'PENDING',
      viewCount: house.viewCount || 0,
      createTime: formatDate(house.createTime),
      images: house.images ? JSON.parse(house.images) : [],
      tags: house.tags ? JSON.parse(house.tags) : []
    }))
  } catch (error) {
    console.error('加载房源列表失败:', error)
    ElMessage.error('加载房源列表失败')
  }
}

const formatDate = (dateTime) => {
  if (!dateTime) return ''
  return dateTime.substring(0, 10)
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
    await deleteHouseApi(house.id)
    const index = houses.value.findIndex(h => h.id === house.id)
    if (index > -1) {
      houses.value.splice(index, 1)
    }
    ElMessage.success('房源已删除')
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error('删除房源失败')
    }
  }
}

onMounted(() => {
  loadHouses()
})
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