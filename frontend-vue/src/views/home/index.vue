<template>
  <div class="home-page">
    <section class="hero">
      <div class="hero-content">
        <h1>找房子，更轻松</h1>
        <p>海量真实房源，智能匹配推荐，让租房变得简单</p>
      </div>
      <search-box 
        :loading="loading"
        @search="handleSearch"
        @reset="handleReset"
      />
    </section>
    
    <section class="section">
      <div class="section-header">
        <h2 class="section-title">最新房源</h2>
        <router-link to="/search" class="section-more">
          查看更多
          <el-icon><ArrowRight /></el-icon>
        </router-link>
      </div>
      
      <div v-loading="loading" class="house-grid">
        <house-card 
          v-for="house in houseList" 
          :key="house.id"
          :house="house"
          @favorite-change="handleFavoriteChange"
        />
      </div>
      
      <el-empty v-if="!loading && houseList.length === 0" description="暂无房源数据" />
    </section>
    
    <section class="features-section">
      <div class="container">
        <h2 class="section-title text-center mb-30">为什么选择我们</h2>
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <h3>真实房源</h3>
            <p>所有房源实地验证，确保信息真实可靠</p>
          </div>
          
          <div class="feature-card">
            <div class="feature-icon">
              <el-icon><Lock /></el-icon>
            </div>
            <h3>安全保障</h3>
            <p>资金托管，交易安全有保障</p>
          </div>
          
          <div class="feature-card">
            <div class="feature-icon">
              <el-icon><Timer /></el-icon>
            </div>
            <h3>快速响应</h3>
            <p>专业客服团队，7x24小时在线服务</p>
          </div>
          
          <div class="feature-card">
            <div class="feature-icon">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <h3>智能推荐</h3>
            <p>AI智能匹配，精准推荐理想房源</p>
          </div>
        </div>
      </div>
    </section>
    
    <section class="section">
      <div class="section-header">
        <h2 class="section-title">热门区域</h2>
      </div>
      
      <div class="area-grid">
        <div 
          v-for="area in hotAreas" 
          :key="area.name"
          class="area-card"
          @click="handleAreaClick(area)"
        >
          <div class="area-image">
            <img :src="area.image" :alt="area.name" />
          </div>
          <div class="area-info">
            <h3>{{ area.name }}</h3>
            <p>{{ area.count }}套在租房源</p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import SearchBox from '@/components/house/SearchBox.vue'
import HouseCard from '@/components/house/HouseCard.vue'
import { getHouseList } from '@/api/house'

const router = useRouter()
const loading = ref(false)
const houseList = ref([])

const hotAreas = [
  { name: '朝阳区', city: '北京', count: 1234, image: 'https://picsum.photos/400/300?random=1' },
  { name: '海淀区', city: '北京', count: 987, image: 'https://picsum.photos/400/300?random=2' },
  { name: '浦东新区', city: '上海', count: 1567, image: 'https://picsum.photos/400/300?random=3' },
  { name: '南山区', city: '深圳', count: 876, image: 'https://picsum.photos/400/300?random=4' }
]

const fetchHouseList = async () => {
  loading.value = true
  try {
    const data = await getHouseList({ current: 1, size: 8 })
    houseList.value = data.records || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = (searchParams) => {
  router.push({
    path: '/search',
    query: searchParams
  })
}

const handleReset = () => {
  console.log('reset')
}

const handleFavoriteChange = ({ id, isFavorited }) => {
  const house = houseList.value.find(h => h.id === id)
  if (house) {
    house.isFavorited = isFavorited
    house.favoriteCount = isFavorited 
      ? house.favoriteCount + 1 
      : house.favoriteCount - 1
  }
}

const handleAreaClick = (area) => {
  router.push({
    path: '/search',
    query: { city: area.city, district: area.name }
  })
}

onMounted(() => {
  fetchHouseList()
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.home-page {
  min-height: 100vh;
}

.hero {
  background: linear-gradient(135deg, $primary-color 0%, #764ba2 100%);
  padding: 80px 20px 100px;
  text-align: center;
  color: white;
  
  .hero-content {
    max-width: 800px;
    margin: 0 auto 40px;
    
    h1 {
      font-size: 42px;
      margin-bottom: 15px;
      font-weight: 700;
    }
    
    p {
      font-size: 18px;
      opacity: 0.9;
    }
  }
  
  .search-box {
    max-width: 900px;
    margin: 0 auto;
  }
}

.section {
  max-width: 1200px;
  margin: 50px auto;
  padding: 0 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.section-title {
  font-size: 28px;
  font-weight: bold;
  color: $text-primary;
}

.section-more {
  display: flex;
  align-items: center;
  gap: 5px;
  color: $primary-color;
  text-decoration: none;
  font-size: 14px;
  transition: $transition-base;
  
  &:hover {
    color: $primary-hover;
  }
}

.house-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 25px;
}

.features-section {
  background: white;
  padding: 60px 0;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
}

.feature-card {
  text-align: center;
  padding: 30px 20px;
  border-radius: $radius-base;
  transition: $transition-base;
  
  &:hover {
    background: $bg-color;
    transform: translateY(-5px);
  }
  
  .feature-icon {
    width: 70px;
    height: 70px;
    margin: 0 auto 20px;
    background: linear-gradient(135deg, $primary-color 0%, #764ba2 100%);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    
    .el-icon {
      font-size: 32px;
      color: white;
    }
  }
  
  h3 {
    font-size: 18px;
    font-weight: 600;
    margin-bottom: 10px;
    color: $text-primary;
  }
  
  p {
    font-size: 14px;
    color: $text-secondary;
    line-height: 1.6;
  }
}

.area-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 25px;
}

.area-card {
  position: relative;
  border-radius: $radius-large;
  overflow: hidden;
  cursor: pointer;
  transition: $transition-base;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: $shadow-dark;
    
    .area-image img {
      transform: scale(1.1);
    }
  }
  
  .area-image {
    width: 100%;
    height: 200px;
    overflow: hidden;
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.3s ease;
    }
  }
  
  .area-info {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    padding: 20px;
    background: linear-gradient(to top, rgba(0, 0, 0, 0.7), transparent);
    color: white;
    
    h3 {
      font-size: 20px;
      font-weight: 600;
      margin-bottom: 5px;
    }
    
    p {
      font-size: 14px;
      opacity: 0.9;
    }
  }
}

@media (max-width: 768px) {
  .hero {
    padding: 60px 20px 80px;
    
    .hero-content h1 {
      font-size: 32px;
    }
  }
  
  .section-title {
    font-size: 24px;
  }
}
</style>
