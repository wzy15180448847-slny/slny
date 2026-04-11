<template>
  <div class="appointments-page">
    <div class="appointments-container">
      <div class="page-header">
        <h2>我的预约</h2>
        <p>管理您的看房预约</p>
      </div>
      
      <div class="filter-bar">
        <el-select v-model="filterStatus" placeholder="筛选状态" size="large" class="filter-select">
          <el-option label="全部" value="ALL" />
          <el-option label="待确认" value="PENDING" />
          <el-option label="已确认" value="CONFIRMED" />
          <el-option label="已完成" value="COMPLETED" />
          <el-option label="已取消" value="CANCELED" />
        </el-select>
        
        <el-input 
          v-model="searchKeyword" 
          placeholder="搜索预约的房源" 
          size="large"
          class="search-input"
          prefix-icon="Search"
          @keyup.enter="handleSearch"
        />
        
        <el-button type="primary" size="large" @click="handleSearch">搜索</el-button>
      </div>
      
      <div class="appointments-list">
        <div v-if="loading" class="loading">
          <el-skeleton :rows="3" animated />
        </div>
        
        <div v-else-if="appointments.length === 0" class="empty">
          <el-empty description="暂无预约记录" />
          <el-button type="primary" @click="gotoHome">去首页找房</el-button>
        </div>
        
        <div v-else class="appointments-table">
          <el-table :data="appointments" style="width: 100%">
            <el-table-column prop="house.title" label="房源信息" min-width="200">
              <template #default="scope">
                <div class="house-info">
                  <div class="house-image">
                    <img :src="scope.row.house.images[0] || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=modern%20apartment%20interior&image_size=square_hd'" :alt="scope.row.house.title" />
                  </div>
                  <div class="house-details">
                    <h4 class="house-title">{{ scope.row.house.title }}</h4>
                    <p class="house-address">{{ scope.row.house.city }} {{ scope.row.house.district }} {{ scope.row.house.community }}</p>
                    <p class="house-price">¥{{ scope.row.house.price }}/月</p>
                  </div>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column prop="appointmentTime" label="预约时间" width="180">
              <template #default="scope">
                {{ formatDate(scope.row.appointmentTime) }}
              </template>
            </el-table-column>
            
            <el-table-column prop="status" label="状态" width="120">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            
            <el-table-column prop="createdAt" label="创建时间" width="180">
              <template #default="scope">
                {{ formatDate(scope.row.createdAt) }}
              </template>
            </el-table-column>
            
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <div class="action-buttons">
                  <el-button type="primary" size="small" @click="viewHouse(scope.row.house.id)">查看房源</el-button>
                  <el-button 
                    v-if="scope.row.status === 'PENDING'" 
                    type="danger" 
                    size="small" 
                    @click="cancelAppointment(scope.row.id)"
                  >
                    取消预约
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
      
      <div v-if="!loading && appointments.length > 0" class="pagination">
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
import dayjs from 'dayjs'
import { useHouseStore } from '@/store/house'

const router = useRouter()
const houseStore = useHouseStore()

const appointments = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const filterStatus = ref('ALL')
const searchKeyword = ref('')

const getStatusText = (status) => {
  const statusMap = {
    'PENDING': '待确认',
    'CONFIRMED': '已确认',
    'COMPLETED': '已完成',
    'CANCELED': '已取消'
  }
  return statusMap[status] || status
}

const getStatusType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'CONFIRMED': 'primary',
    'COMPLETED': 'success',
    'CANCELED': 'info'
  }
  return typeMap[status] || 'default'
}

const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const fetchAppointments = async () => {
  loading.value = true
  try {
    const response = await houseStore.getMyAppointments({
      page: currentPage.value,
      size: pageSize.value,
      status: filterStatus.value === 'ALL' ? '' : filterStatus.value,
      keyword: searchKeyword.value
    })
    appointments.value = response.appointments
    total.value = response.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchAppointments()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchAppointments()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchAppointments()
}

const gotoHome = () => {
  router.push('/')
}

const viewHouse = (id) => {
  router.push(`/house/${id}`)
}

const cancelAppointment = async (id) => {
  try {
    await ElMessageBox.confirm('确定要取消这个预约吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await houseStore.cancelAppointment(id)
    ElMessage.success('预约取消成功')
    fetchAppointments()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  fetchAppointments()
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.appointments-page {
  padding: 40px 20px;
  min-height: 80vh;
}

.appointments-container {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
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

.appointments-list {
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
  
  .appointments-table {
    .house-info {
      display: flex;
      gap: 15px;
      
      .house-image {
        width: 80px;
        height: 80px;
        border-radius: 6px;
        overflow: hidden;
        
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }
      
      .house-details {
        flex: 1;
        
        .house-title {
          font-size: 16px;
          font-weight: 600;
          color: $text-primary;
          margin-bottom: 5px;
          line-height: 1.4;
        }
        
        .house-address {
          font-size: 14px;
          color: $text-secondary;
          margin-bottom: 5px;
        }
        
        .house-price {
          font-size: 16px;
          font-weight: 600;
          color: $primary-color;
        }
      }
    }
    
    .action-buttons {
      display: flex;
      gap: 8px;
    }
  }
}

.pagination {
  display: flex;
  justify-content: center;
}
</style>