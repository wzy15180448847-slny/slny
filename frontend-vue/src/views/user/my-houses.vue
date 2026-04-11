<template>
  <div class="my-houses-page">
    <div class="my-houses-container">
      <div class="page-header">
        <h2>我的房源</h2>
        <p>管理您发布的房源信息</p>
        <el-button type="primary" @click="gotoPublish">
          <el-icon><Plus /></el-icon>
          发布新房源
        </el-button>
      </div>
      
      <div class="filter-bar">
        <el-select v-model="filterStatus" placeholder="筛选状态" size="large" class="filter-select">
          <el-option label="全部" value="ALL" />
          <el-option label="待审核" value="PENDING" />
          <el-option label="已发布" value="PUBLISHED" />
          <el-option label="已下架" value="UNPUBLISHED" />
        </el-select>
        
        <el-input 
          v-model="searchKeyword" 
          placeholder="搜索房源标题" 
          size="large"
          class="search-input"
          prefix-icon="Search"
          @keyup.enter="handleSearch"
        />
        
        <el-button type="primary" size="large" @click="handleSearch">搜索</el-button>
      </div>
      
      <div class="houses-list">
        <div v-if="loading" class="loading">
          <el-skeleton :rows="3" animated />
        </div>
        
        <div v-else-if="houses.length === 0" class="empty">
          <el-empty description="暂无房源" />
          <el-button type="primary" @click="gotoPublish">发布房源</el-button>
        </div>
        
        <div v-else class="houses-grid">
          <div v-for="house in houses" :key="house.id" class="house-card">
            <div class="house-image">
              <img :src="house.images[0] || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=modern%20apartment%20interior&image_size=square_hd'" :alt="house.title" />
              <div class="house-status" :class="house.status.toLowerCase()">
                {{ getStatusText(house.status) }}
              </div>
            </div>
            
            <div class="house-info">
              <h3 class="house-title">{{ house.title }}</h3>
              <div class="house-details">
                <span class="price">¥{{ house.price }}/月</span>
                <span class="rent-type">{{ getRentTypeText(house.rentType) }}</span>
                <span class="house-type">{{ getHouseTypeText(house.houseType) }}</span>
                <span class="area">{{ house.area }}㎡</span>
              </div>
              <div class="house-location">
                <el-icon><Position /></el-icon>
                <span>{{ house.city }} {{ house.district }} {{ house.community }}</span>
              </div>
              <div class="house-actions">
                <el-button type="primary" size="small" @click="viewHouse(house.id)">查看</el-button>
                <el-button size="small" @click="editHouse(house.id)">编辑</el-button>
                <el-button 
                  :type="house.status === 'PUBLISHED' ? 'warning' : 'success'" 
                  size="small"
                  @click="toggleStatus(house)"
                >
                  {{ house.status === 'PUBLISHED' ? '下架' : '上架' }}
                </el-button>
                <el-button type="danger" size="small" @click="deleteHouse(house.id)">删除</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div v-if="!loading && houses.length > 0" class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useHouseStore } from '@/store/house'

const router = useRouter()
const houseStore = useHouseStore()

const houses = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const filterStatus = ref('ALL')
const searchKeyword = ref('')

const getStatusText = (status) => {
  const statusMap = {
    'PENDING': '待审核',
    'PUBLISHED': '已发布',
    'UNPUBLISHED': '已下架'
  }
  return statusMap[status] || status
}

const getRentTypeText = (rentType) => {
  const rentTypeMap = {
    'ENTIRE': '整租',
    'SHARED': '合租',
    'SINGLE_ROOM': '单间'
  }
  return rentTypeMap[rentType] || rentType
}

const getHouseTypeText = (houseType) => {
  const houseTypeMap = {
    'ONE_BEDROOM': '一室一厅',
    'TWO_BEDROOM': '两室一厅',
    'THREE_BEDROOM': '三室一厅',
    'FOUR_PLUS_BEDROOM': '四室及以上'
  }
  return houseTypeMap[houseType] || houseType
}

const fetchMyHouses = async () => {
  loading.value = true
  try {
    const response = await houseStore.getMyHouses({
      page: currentPage.value,
      size: pageSize.value,
      status: filterStatus.value === 'ALL' ? '' : filterStatus.value,
      keyword: searchKeyword.value
    })
    houses.value = response.houses
    total.value = response.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchMyHouses()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchMyHouses()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchMyHouses()
}

const gotoPublish = () => {
  router.push('/publish')
}

const viewHouse = (id) => {
  router.push(`/house/${id}`)
}

const editHouse = (id) => {
  router.push(`/publish?id=${id}`)
}

const toggleStatus = async (house) => {
  try {
    await houseStore.updateHouseStatus(house.id, house.status === 'PUBLISHED' ? 'UNPUBLISHED' : 'PUBLISHED')
    ElMessage.success(`${house.status === 'PUBLISHED' ? '下架' : '上架'}成功`)
    fetchMyHouses()
  } catch (error) {
    console.error(error)
  }
}

const deleteHouse = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个房源吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await houseStore.deleteHouse(id)
    ElMessage.success('删除成功')
    fetchMyHouses()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  fetchMyHouses()
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.my-houses-page {
  padding: 40px 20px;
  min-height: 80vh;
}

.my-houses-container {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  
  h2 {
    font-size: 32px;
    color: $text-primary;
    margin-bottom: 5px;
    font-weight: 600;
  }
  
  p {
    color: $text-secondary;
    font-size: 16px;
  }
}

.filter-bar {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
  flex-wrap: wrap;
  
  .filter-select {
    min-width: 150px;
  }
  
  .search-input {
    flex: 1;
    min-width: 300px;
  }
  
  @media (max-width: 768px) {
    flex-direction: column;
    
    .filter-select,
    .search-input {
      width: 100%;
      min-width: auto;
    }
  }
}

.houses-list {
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 30px;
  margin-bottom: 30px;
  
  .loading {
    padding: 40px 0;
  }
  
  .empty {
    text-align: center;
    padding: 60px 0;
    
    .el-button {
      margin-top: 20px;
    }
  }
  
  .houses-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 20px;
    
    @media (max-width: 768px) {
      grid-template-columns: 1fr;
    }
    
    .house-card {
      border: 1px solid $border-color-base;
      border-radius: 8px;
      overflow: hidden;
      transition: all 0.3s ease;
      
      &:hover {
        box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
        transform: translateY(-2px);
      }
      
      .house-image {
        position: relative;
        height: 200px;
        overflow: hidden;
        
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          transition: transform 0.3s ease;
          
          &:hover {
            transform: scale(1.05);
          }
        }
        
        .house-status {
          position: absolute;
          top: 10px;
          right: 10px;
          padding: 5px 12px;
          border-radius: 20px;
          font-size: 12px;
          font-weight: 500;
          
          &.pending {
            background: rgba(255, 193, 7, 0.9);
            color: white;
          }
          
          &.published {
            background: rgba(40, 167, 69, 0.9);
            color: white;
          }
          
          &.unpublished {
            background: rgba(108, 117, 125, 0.9);
            color: white;
          }
        }
      }
      
      .house-info {
        padding: 20px;
        
        .house-title {
          font-size: 18px;
          font-weight: 600;
          color: $text-primary;
          margin-bottom: 12px;
          line-height: 1.4;
        }
        
        .house-details {
          display: flex;
          flex-wrap: wrap;
          gap: 10px;
          margin-bottom: 12px;
          
          .price {
            font-size: 18px;
            font-weight: 600;
            color: $primary-color;
          }
          
          .rent-type,
          .house-type,
          .area {
            font-size: 14px;
            color: $text-secondary;
            background: $bg-color;
            padding: 2px 8px;
            border-radius: 4px;
          }
        }
        
        .house-location {
          display: flex;
          align-items: center;
          gap: 5px;
          font-size: 14px;
          color: $text-secondary;
          margin-bottom: 20px;
        }
        
        .house-actions {
          display: flex;
          gap: 8px;
          
          .el-button {
            flex: 1;
          }
          
          @media (max-width: 375px) {
            flex-wrap: wrap;
            
            .el-button {
              flex: 1 1 48%;
            }
          }
        }
      }
    }
  }
}

.pagination {
  display: flex;
  justify-content: center;
}
</style>