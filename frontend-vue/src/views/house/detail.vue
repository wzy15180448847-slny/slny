<template>
  <div class="house-detail-page" v-loading="loading">
    <div v-if="house" class="container">
      <div class="detail-header">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ path: '/search', query: { city: house.city } }">
            {{ house.city }}
          </el-breadcrumb-item>
          <el-breadcrumb-item :to="{ path: '/search', query: { city: house.city, district: house.district } }">
            {{ house.district }}
          </el-breadcrumb-item>
          <el-breadcrumb-item>{{ house.title }}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      
      <div class="detail-content">
        <div class="gallery-section">
          <div class="main-image">
            <img 
              v-if="currentImage"
              :src="currentImage"
              :alt="house.title"
            />
            <div v-else class="image-placeholder">
              <el-icon><Picture /></el-icon>
            </div>
          </div>
          
          <div v-if="houseImages && houseImages.length > 1" class="thumbnail-list">
            <div 
              v-for="(image, index) in houseImages"
              :key="index"
              :class="['thumbnail', { active: currentImageIndex === index }]"
              @click="currentImageIndex = index"
            >
              <img :src="image" :alt="`图片${index + 1}`" />
            </div>
          </div>
        </div>
        
        <div class="info-section">
          <div class="house-title">
            <h1>{{ house.title }}</h1>
            <div class="title-tags">
              <el-tag v-if="house.status === 2" type="success">在租</el-tag>
              <el-tag v-else-if="house.status === 1" type="warning">审核中</el-tag>
              <el-tag v-if="house.rentWay === 1">整租</el-tag>
              <el-tag v-else type="info">合租</el-tag>
            </div>
          </div>
          
          <div class="price-box">
            <div class="price">
              <span class="amount">{{ house.rentPrice }}</span>
              <span class="unit">元/月</span>
            </div>
            <div class="deposit">押{{ house.depositMonth }}付{{ getPaymentText(house.paymentWay) }}</div>
          </div>
          
          <div class="basic-info">
            <div class="info-item">
              <span class="label">房型</span>
              <span class="value">{{ house.houseType }}</span>
            </div>
            <div class="info-item">
              <span class="label">面积</span>
              <span class="value">{{ house.area }}㎡</span>
            </div>
            <div class="info-item">
              <span class="label">朝向</span>
              <span class="value">{{ orientationText }}</span>
            </div>
            <div class="info-item">
              <span class="label">楼层</span>
              <span class="value">{{ house.floor }}/{{ house.totalFloor }}层</span>
            </div>
            <div class="info-item">
              <span class="label">装修</span>
              <span class="value">{{ getDecorationText(house.decoration) }}</span>
            </div>
            <div class="info-item">
              <span class="label">电梯</span>
              <span class="value">{{ house.hasElevator ? '有' : '无' }}</span>
            </div>
          </div>
          
          <div class="location">
            <el-icon><Location /></el-icon>
            <span>{{ house.province }} {{ house.city }} {{ house.district }} {{ house.street }} {{ house.address }}</span>
          </div>
          
          <div class="facilities" v-if="house.facilities">
            <h3>房屋配置</h3>
            <div class="facility-list">
              <el-tag 
                v-for="facility in parseFacilities(house.facilities)"
                :key="facility"
                class="facility-tag"
              >
                {{ facility }}
              </el-tag>
            </div>
          </div>
          
          <div class="actions">
            <el-button 
              type="primary"
              size="large"
              :icon="isFavorited ? 'StarFilled' : 'Star'"
              @click="handleFavorite"
            >
              {{ isFavorited ? '已收藏' : '收藏房源' }}
            </el-button>
            <el-button 
              type="success"
              size="large"
              icon="Phone"
              @click="handleAppointment"
            >
              预约看房
            </el-button>
          </div>
        </div>
      </div>
      
      <div class="detail-tabs">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="房源描述" name="description">
            <div class="description-content">
              <p>{{ house.description }}</p>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="房源信息" name="info">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="房源编号">{{ house.houseNo }}</el-descriptions-item>
              <el-descriptions-item label="发布时间">{{ formatDate(house.createTime) }}</el-descriptions-item>
              <el-descriptions-item label="浏览次数">{{ house.viewCount }}</el-descriptions-item>
              <el-descriptions-item label="收藏次数">{{ house.favoriteCount }}</el-descriptions-item>
            </el-descriptions>
          </el-tab-pane>
          
          <el-tab-pane label="联系方式" name="contact">
            <el-card>
              <div class="contact-info">
                <div class="contact-item">
                  <el-icon><User /></el-icon>
                  <span>联系人：{{ house.contactName }}</span>
                </div>
                <div class="contact-item">
                  <el-icon><Phone /></el-icon>
                  <span>联系电话：{{ house.contactPhone }}</span>
                </div>
              </div>
            </el-card>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getHouseDetail, addFavorite, removeFavorite } from '@/api/house'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const house = ref(null)
const currentImageIndex = ref(0)
const activeTab = ref('description')
const isFavorited = ref(false)

const houseImages = computed(() => {
  if (!house.value || !house.value.images) return []
  const images = house.value.images
  try {
    return typeof images === 'string' ? JSON.parse(images) : images
  } catch {
    return []
  }
})

const currentImage = computed(() => {
  if (houseImages.value && houseImages.value.length > 0) {
    return houseImages.value[currentImageIndex.value]
  }
  return null
})

const orientationText = computed(() => {
  if (!house.value) return '未知'
  const map = { 1: '东', 2: '南', 3: '西', 4: '北', 5: '东南', 6: '西南', 7: '东北', 8: '西北' }
  return map[house.value.orientation] || '未知'
})

const fetchHouseDetail = async () => {
  loading.value = true
  try {
    const { data } = await getHouseDetail(route.params.id)
    house.value = data
    isFavorited.value = data.isFavorited || false
  } catch (error) {
    console.error(error)
    ElMessage.error('获取房源详情失败')
    router.push('/')
  } finally {
    loading.value = false
  }
}

const handleFavorite = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  try {
    if (isFavorited.value) {
      await removeFavorite(house.value.id)
      ElMessage.success('取消收藏成功')
      isFavorited.value = false
    } else {
      await addFavorite(house.value.id)
      ElMessage.success('收藏成功')
      isFavorited.value = true
    }
  } catch (error) {
    console.error(error)
  }
}

const handleAppointment = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  ElMessage.info('预约功能开发中')
}

const getPaymentText = (way) => {
  const map = { 1: '一', 2: '二', 3: '三', 4: '四' }
  return map[way] || '一'
}

const getDecorationText = (level) => {
  const map = { 1: '毛坯', 2: '简装', 3: '精装', 4: '豪装' }
  return map[level] || '未知'
}

const parseFacilities = (facilities) => {
  if (!facilities) return []
  try {
    return JSON.parse(facilities)
  } catch {
    return facilities.split(',')
  }
}

const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

onMounted(() => {
  fetchHouseDetail()
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.detail-page {
  min-height: calc(100vh - #{$navbar-height} - #{$footer-height});
  padding: 30px 0;
  background: $bg-color;
}

.detail-header {
  margin-bottom: 20px;
}

.detail-content {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 30px;
  margin-bottom: 30px;
}

.gallery-section {
  .main-image {
    width: 100%;
    height: 450px;
    background: white;
    border-radius: $radius-large;
    overflow: hidden;
    margin-bottom: 15px;
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
    
    .image-placeholder {
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: $text-placeholder;
      
      .el-icon {
        font-size: 80px;
      }
    }
  }
  
  .thumbnail-list {
    display: flex;
    gap: 10px;
    overflow-x: auto;
    
    .thumbnail {
      width: 80px;
      height: 60px;
      border-radius: $radius-small;
      overflow: hidden;
      cursor: pointer;
      border: 2px solid transparent;
      transition: $transition-base;
      
      &.active {
        border-color: $primary-color;
      }
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }
  }
}

.info-section {
  background: white;
  border-radius: $radius-large;
  padding: 25px;
  height: fit-content;
  position: sticky;
  top: 90px;
}

.house-title {
  margin-bottom: 20px;
  
  h1 {
    font-size: 24px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: 10px;
    line-height: 1.4;
  }
  
  .title-tags {
    display: flex;
    gap: 8px;
  }
}

.price-box {
  background: linear-gradient(135deg, $primary-color 0%, #764ba2 100%);
  color: white;
  padding: 20px;
  border-radius: $radius-base;
  margin-bottom: 20px;
  
  .price {
    margin-bottom: 10px;
    
    .amount {
      font-size: 36px;
      font-weight: 700;
    }
    
    .unit {
      font-size: 16px;
      margin-left: 5px;
    }
  }
  
  .deposit {
    font-size: 14px;
    opacity: 0.9;
  }
}

.basic-info {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
  margin-bottom: 20px;
  
  .info-item {
    text-align: center;
    padding: 10px;
    background: $bg-color;
    border-radius: $radius-small;
    
    .label {
      display: block;
      font-size: 12px;
      color: $text-secondary;
      margin-bottom: 5px;
    }
    
    .value {
      display: block;
      font-size: 16px;
      font-weight: 600;
      color: $text-primary;
    }
  }
}

.location {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 15px;
  background: $bg-color;
  border-radius: $radius-small;
  margin-bottom: 20px;
  
  .el-icon {
    color: $primary-color;
    font-size: 18px;
    margin-top: 2px;
  }
  
  span {
    font-size: 14px;
    color: $text-regular;
    line-height: 1.6;
  }
}

.facilities {
  margin-bottom: 20px;
  
  h3 {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 15px;
    color: $text-primary;
  }
  
  .facility-list {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    
    .facility-tag {
      background: $primary-light;
      color: $primary-color;
      border: none;
    }
  }
}

.actions {
  display: flex;
  gap: 15px;
  
  .el-button {
    flex: 1;
    height: 45px;
    font-size: 16px;
  }
}

.detail-tabs {
  background: white;
  border-radius: $radius-large;
  padding: 25px;
  
  .description-content {
    line-height: 1.8;
    color: $text-regular;
  }
  
  .contact-info {
    .contact-item {
      display: flex;
      align-items: center;
      gap: 10px;
      margin-bottom: 15px;
      font-size: 15px;
      
      .el-icon {
        font-size: 18px;
        color: $primary-color;
      }
    }
  }
}

@media (max-width: 992px) {
  .detail-content {
    grid-template-columns: 1fr;
  }
  
  .info-section {
    position: static;
  }
}
</style>
