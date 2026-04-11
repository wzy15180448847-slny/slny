<template>
  <div class="favorites-page">
    <div class="favorites-container">
      <div class="page-header">
        <h2>我的收藏</h2>
        <p>管理您收藏的房源</p>
      </div>
      
      <div class="filter-bar">
        <el-input 
          v-model="searchKeyword" 
          placeholder="搜索收藏的房源" 
          size="large"
          class="search-input"
          prefix-icon="Search"
          @keyup.enter="handleSearch"
        />
        
        <el-button type="primary" size="large" @click="handleSearch">搜索</el-button>
      </div>
      
      <div class="favorites-list">
        <div v-if="loading" class="loading">
          <el-skeleton :rows="3" animated />
        </div>
        
        <div v-else-if="favorites.length === 0" class="empty">
          <el-empty description="暂无收藏房源" />
          <el-button type="primary" @click="gotoHome">去首页找房</el-button>
        </div>
        
        <div v-else class="houses-grid">
          <div v-for="item in favorites" :key="item.id" class="house-card">
            <div class="house-image">
              <img :src="item.house.images[0] || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=modern%20apartment%20interior&image_size=square_hd'" :alt="item.house.title" />
              <div class="favorite-icon" @click="removeFavorite(item.id)">
                <el-icon><StarFilled /></el-icon>
              </div>
            </div>
            
            <div class="house-info">
              <h3 class="house-title">{{ item.house.title }}</h3>
              <div class="house-details">
                <span class="price">¥{{ item.house.price }}/月</span>
                <span class="rent-type">{{ getRentTypeText(item.house.rentType) }}</span>
                <span class="house-type">{{ getHouseTypeText(item.house.houseType) }}</span>
                <span class="area">{{ item.house.area }}㎡</span>
              </div>
              <div class="house-location">
                <el-icon><Position /></el-icon>
                <span>{{ item.house.city }} {{ item.house.district }} {{ item.house.community }}</span>
              </div>
              <div class="house-actions">
                <el-button type="primary" size="small" @click="viewHouse(item.house.id)">查看详情</el-button>
                <el-button size="small" @click="removeFavorite(item.id)">取消收藏</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div v-if="!loading && favorites.length > 0" class="pagination">
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

const favorites = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')

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

const fetchFavorites = async () => {
  loading.value = true
  try {
    const response = await houseStore.getMyFavorites({
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value
    })
    favorites.value = response.favorites
    total.value = response.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchFavorites()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchFavorites()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchFavorites()
}

const gotoHome = () => {
  router.push('/')
}

const viewHouse = (id) => {
  router.push(`/house/${id}`)
}

const removeFavorite = async (id) => {
  try {
    await houseStore.removeFavorite(id)
    ElMessage.success('取消收藏成功')
    fetchFavorites()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchFavorites()
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.favorites-page {
  padding: 40px 20px;
  min-height: 80vh;
}

.favorites-container {
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
  
  .search-input {
    flex: 1;
    min-width: 300px;
  }
  
  @media (max-width: 768px) {
    flex-direction: column;
    
    .search-input {
      width: 100%;
      min-width: auto;
    }
  }
}

.favorites-list {
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
        
        .favorite-icon {
          position: absolute;
          top: 10px;
          right: 10px;
          width: 36px;
          height: 36px;
          background: rgba(255, 255, 255, 0.9);
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          cursor: pointer;
          transition: all 0.3s ease;
          
          .el-icon {
            color: $primary-color;
            font-size: 20px;
          }
          
          &:hover {
            background: rgba(255, 255, 255, 1);
            transform: scale(1.1);
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