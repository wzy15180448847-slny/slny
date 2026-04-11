<template>
  <div class="house-card" @click="handleClick">
    <div class="house-image">
      <img 
        v-if="house.images && house.images.length > 0" 
        :src="house.images[0]" 
        :alt="house.title"
      />
      <div v-else class="image-placeholder">
        <el-icon><Picture /></el-icon>
      </div>
      
      <div class="house-tags">
        <span v-if="house.status === 2" class="tag tag-success">已上架</span>
        <span v-else-if="house.status === 1" class="tag tag-warning">审核中</span>
        <span v-else class="tag">待发布</span>
      </div>
      
      <div class="house-price">
        <span class="price">{{ house.rentPrice }}</span>
        <span class="unit">元/月</span>
      </div>
    </div>
    
    <div class="house-info">
      <h3 class="house-title text-ellipsis-2">{{ house.title }}</h3>
      
      <div class="house-meta">
        <span class="meta-item">
          <el-icon><Location /></el-icon>
          {{ house.city }} {{ house.district }}
        </span>
      </div>
      
      <div class="house-detail">
        <span class="detail-item">{{ house.houseType }}</span>
        <span class="divider">|</span>
        <span class="detail-item">{{ house.area }}㎡</span>
        <span class="divider">|</span>
        <span class="detail-item">{{ house.orientation }}</span>
      </div>
      
      <div class="house-footer">
        <div class="house-stats">
          <span class="stat-item">
            <el-icon><View /></el-icon>
            {{ house.viewCount || 0 }}
          </span>
          <span class="stat-item">
            <el-icon><Star /></el-icon>
            {{ house.favoriteCount || 0 }}
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
import { computed } from 'vue'
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
  return props.house.isFavorited || false
})

const handleClick = () => {
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
