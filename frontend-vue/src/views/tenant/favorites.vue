<template>
  <div class="tenant-favorites">
    <div class="page-header">
      <h1>我的收藏</h1>
    </div>
    
    <div v-if="favorites.length > 0" class="favorite-grid">
      <div v-for="favorite in favorites" :key="favorite.id" class="favorite-card">
        <div class="card-image">
          <img :src="favorite.image" alt="房源图片" />
          <div class="price-tag">¥{{ favorite.rent }}/月</div>
        </div>
        <div class="card-content">
          <h3 class="card-title">{{ favorite.houseName }}</h3>
          <p class="card-address">{{ favorite.address }}</p>
          <div class="card-tags">
            <span v-for="tag in favorite.tags" :key="tag" class="tag">{{ tag }}</span>
          </div>
          <div class="card-footer">
            <span class="card-area">{{ favorite.area }}㎡</span>
            <span class="card-rooms">{{ favorite.rooms }}</span>
          </div>
        </div>
        <div class="card-actions">
          <el-button size="small" @click="viewHouse(favorite)">查看详情</el-button>
          <el-button size="small" type="danger" @click="removeFavorite(favorite)">取消收藏</el-button>
        </div>
      </div>
    </div>
    
    <div v-else class="empty-state">
      <el-icon class="empty-icon"><Star /></el-icon>
      <p>暂无收藏的房源</p>
      <el-button type="primary" @click="goToSearch">去看看</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFavoriteHouses, removeFavorite as removeFavoriteApi } from '@/api/house'

const router = useRouter()

const favorites = ref([])

const loadFavorites = async () => {
  try {
    const { data } = await getFavoriteHouses()
    favorites.value = (data?.records || []).map(house => ({
      id: house.id,
      houseName: house.houseName,
      address: house.address,
      rent: house.rent,
      area: house.area,
      rooms: house.houseType,
      image: house.images ? (JSON.parse(house.images)[0] || '') : '',
      tags: house.tags ? JSON.parse(house.tags) : []
    }))
  } catch (error) {
    console.error('加载收藏列表失败:', error)
    ElMessage.error('加载收藏列表失败')
  }
}

const viewHouse = (favorite) => {
  router.push(`/house/${favorite.id}`)
}

const removeFavorite = async (favorite) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await removeFavoriteApi(favorite.id)
    const index = favorites.value.findIndex(f => f.id === favorite.id)
    if (index > -1) {
      favorites.value.splice(index, 1)
    }
    ElMessage.success('已取消收藏')
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error('取消收藏失败')
    }
  }
}

const goToSearch = () => {
  router.push('/search')
}

onMounted(() => {
  loadFavorites()
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.tenant-favorites {
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

.favorite-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.favorite-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  
  .card-image {
    position: relative;
    height: 200px;
    overflow: hidden;
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
    
    .price-tag {
      position: absolute;
      top: 10px;
      right: 10px;
      background: rgba(255, 255, 255, 0.9);
      padding: 5px 12px;
      border-radius: 20px;
      font-size: 14px;
      font-weight: bold;
      color: $primary-color;
    }
  }
  
  .card-content {
    padding: 15px;
    
    .card-title {
      font-size: 16px;
      font-weight: 600;
      color: $text-primary;
      margin-bottom: 5px;
    }
    
    .card-address {
      font-size: 13px;
      color: $text-secondary;
      margin-bottom: 10px;
    }
    
    .card-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 5px;
      margin-bottom: 10px;
      
      .tag {
        background: $bg-color;
        padding: 3px 8px;
        border-radius: 4px;
        font-size: 12px;
        color: $text-secondary;
      }
    }
    
    .card-footer {
      display: flex;
      gap: 15px;
      font-size: 13px;
      color: $text-secondary;
    }
  }
  
  .card-actions {
    padding: 10px 15px 15px;
    display: flex;
    gap: 10px;
  }
}

.empty-state {
  text-align: center;
  padding: 60px 0;
  
  .empty-icon {
    font-size: 64px;
    color: #ccc;
    margin-bottom: 20px;
  }
  
  p {
    color: $text-secondary;
    margin-bottom: 20px;
  }
}
</style>