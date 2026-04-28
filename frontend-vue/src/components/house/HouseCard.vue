<template>
  <div class="house-card" @click="handleClick">
    <div class="house-image">
      <img 
        v-if="houseImages && houseImages.length > 0" 
        :src="houseImages[0]" 
        :alt="props.house.title"
      />
      <div v-else class="image-placeholder">
        <el-icon><Picture /></el-icon>
      </div>
      
      <div class="house-tags">
        <span v-if="props.house.status === 0" class="tag tag-success">已上架</span>
        <span v-else-if="props.house.status === 1" class="tag tag-warning">已出租</span>
        <span v-else-if="props.house.status === 2" class="tag tag-danger">已下架</span>
        <span v-else class="tag">待发布</span>
      </div>
      
      <div class="house-price">
        <span class="price">{{ Math.floor(props.house.rentPrice) }}</span>
        <span class="unit">元/月</span>
      </div>
    </div>
    
    <div class="house-info">
      <div class="house-id-badge">ID: {{ props.house.id }}</div>
      <h3 class="house-title text-ellipsis-2">{{ props.house.title }}</h3>
      
      <div class="house-meta">
        <span class="meta-item">
          <el-icon><Location /></el-icon>
          {{ props.house.city }} {{ props.house.district }}
        </span>
      </div>
      
      <div class="house-detail">
        <span class="detail-item">{{ props.house.houseType }}</span>
        <span class="divider">|</span>
        <span class="detail-item">{{ props.house.area }}㎡</span>
        <span class="divider">|</span>
        <span class="detail-item">{{ orientationText }}</span>
      </div>
      
      <div class="house-footer">
        <div class="house-stats">
          <span class="stat-item">
            <el-icon><View /></el-icon>
            {{ props.house.viewCount || 0 }}
          </span>
          <span class="stat-item">
            <el-icon><Star /></el-icon>
            {{ props.house.favoriteCount || 0 }}
          </span>
        </div>
        
        <el-button 
          v-if="showFavorite"
          :type="isFavorited ? 'danger' : 'default'"
          size="small"
          @click.stop="handleFavorite"
        >
          <el-icon><Star /></el-icon>
          {{ isFavorited ? '已收藏' : '收藏' }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { addFavorite, removeFavorite } from '@/api/house'
import { ElMessage } from 'element-plus'

const props = defineProps({
  house: {
    type: Object,
    required: true
  },
  showFavorite: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['favorite-change'])
const router = useRouter()
const userStore = useUserStore()

const isFavorited = computed(() => {
  return props.house?.isFavorited || false
})

const houseImages = computed(() => {
  const images = props.house?.images
  if (!images) return []
  try {
    return typeof images === 'string' ? JSON.parse(images) : images
  } catch {
    return []
  }
})

const orientationText = computed(() => {
  const map = { 1: '东', 2: '南', 3: '西', 4: '北', 5: '东南', 6: '西南', 7: '东北', 8: '西北' }
  return map[props.house?.orientation] || '未知'
})

const handleClick = () => {
  console.log('=== HouseCard点击事件 ===')
  console.log('props.house.id:', props.house.id)
  console.log('props.house.title:', props.house.title)
  console.log('props.house.city:', props.house.city)
  console.log('跳转到:', `/house/${props.house.id}`)
  router.push(`/house/${props.house.id}`)
}

const handleFavorite = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  try {
    if (isFavorited.value) {
      await removeFavorite(props.house.id)
      ElMessage.success('取消收藏成功')
    } else {
      await addFavorite(props.house.id)
      ElMessage.success('收藏成功')
    }
    emit('favorite-change', {
      id: props.house.id,
      isFavorited: !isFavorited.value
    })
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  console.log('=== HouseCard mounted ===')
  console.log('props.house:', props.house)
  console.log('props.house.id:', props.house?.id)
  console.log('props.house.title:', props.house?.title)
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.house-card {
  background: white;
  border-radius: $radius-large;
  overflow: hidden;
  box-shadow: $shadow-light;
  transition: $transition-base;
  cursor: pointer;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: $shadow-dark;
  }
}

.house-image {
  width: 100%;
  height: 200px;
  background: $bg-color;
  position: relative;
  overflow: hidden;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;
  }
  
  .house-card:hover & img {
    transform: scale(1.05);
  }
  
  .image-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: $text-placeholder;
    
    .el-icon {
      font-size: 60px;
    }
  }
}

.house-tags {
  position: absolute;
  top: 10px;
  left: 10px;
  display: flex;
  gap: 5px;
}

.house-price {
  position: absolute;
  bottom: 10px;
  right: 10px;
  background: rgba(245, 108, 108, 0.9);
  color: white;
  padding: 5px 12px;
  border-radius: 15px;
  display: flex;
  align-items: baseline;
  gap: 2px;
  
  .price {
    font-size: 20px;
    font-weight: bold;
  }
  
  .unit {
    font-size: 12px;
  }
}

.house-info {
  padding: 15px;
}

.house-id-badge {
  font-size: 12px;
  color: $text-secondary;
  background: $bg-color;
  padding: 2px 8px;
  border-radius: 4px;
  margin-bottom: 8px;
  display: inline-block;
}

.house-title {
  font-size: 16px;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 10px;
  line-height: 1.4;
  height: 44px;
}

.house-meta {
  margin-bottom: 8px;
  
  .meta-item {
    display: flex;
    align-items: center;
    gap: 5px;
    font-size: 13px;
    color: $text-secondary;
    
    .el-icon {
      font-size: 14px;
    }
  }
}

.house-detail {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  font-size: 13px;
  color: $text-regular;
  
  .divider {
    color: $border-color-base;
  }
}

.house-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid $border-color-lighter;
}

.house-stats {
  display: flex;
  gap: 15px;
  
  .stat-item {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 12px;
    color: $text-secondary;
    
    .el-icon {
      font-size: 14px;
    }
  }
}
</style>
