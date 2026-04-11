<template>
  <div class="search-box">
    <div class="search-tabs" v-if="showTabs">
      <span 
        v-for="tab in tabs" 
        :key="tab.value"
        :class="['search-tab', { active: activeTab === tab.value }]"
        @click="activeTab = tab.value"
      >
        {{ tab.label }}
      </span>
    </div>
    
    <el-form :model="searchForm" class="search-form" @submit.prevent="handleSearch">
      <div class="search-row">
        <el-form-item class="search-input-group">
          <label>城市</label>
          <el-select 
            v-model="searchForm.city" 
            placeholder="选择城市"
            clearable
            @change="handleCityChange"
          >
            <el-option 
              v-for="city in cities" 
              :key="city" 
              :label="city" 
              :value="city"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item class="search-input-group">
          <label>区域</label>
          <el-select 
            v-model="searchForm.district" 
            placeholder="选择区域"
            clearable
            :disabled="!searchForm.city"
          >
            <el-option 
              v-for="district in districts" 
              :key="district" 
              :label="district" 
              :value="district"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item class="search-input-group">
          <label>房型</label>
          <el-select v-model="searchForm.houseType" placeholder="选择房型" clearable>
            <el-option label="一室" value="1室" />
            <el-option label="二室" value="2室" />
            <el-option label="三室" value="3室" />
            <el-option label="四室及以上" value="4室" />
          </el-select>
        </el-form-item>
        
        <el-form-item class="search-input-group">
          <label>租金范围</label>
          <div class="price-range">
            <el-input 
              v-model.number="searchForm.minPrice" 
              placeholder="最低"
              type="number"
            />
            <span class="separator">-</span>
            <el-input 
              v-model.number="searchForm.maxPrice" 
              placeholder="最高"
              type="number"
            />
          </div>
        </el-form-item>
      </div>
      
      <div class="search-actions">
        <el-button 
          type="primary" 
          size="large" 
          @click="handleSearch"
          :loading="loading"
        >
          <el-icon><Search /></el-icon>
          搜索房源
        </el-button>
        
        <el-button size="large" @click="handleReset">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
      </div>
    </el-form>
    
    <div class="hot-cities" v-if="showHotCities">
      <span class="label">热门城市：</span>
      <div class="city-tags">
        <span 
          v-for="city in hotCities" 
          :key="city"
          class="city-tag"
          @click="selectCity(city)"
        >
          {{ city }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'

const props = defineProps({
  showTabs: {
    type: Boolean,
    default: true
  },
  showHotCities: {
    type: Boolean,
    default: true
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['search', 'reset'])

const activeTab = ref('rent')
const tabs = [
  { label: '整租', value: 'rent' },
  { label: '合租', value: 'share' }
]

const searchForm = reactive({
  city: '',
  district: '',
  houseType: '',
  minPrice: null,
  maxPrice: null,
  minArea: null,
  maxArea: null,
  rentWay: 1
})

const cities = ['北京', '上海', '广州', '深圳', '杭州', '成都', '武汉', '西安']
const hotCities = ['北京', '上海', '广州', '深圳', '杭州']

const districtMap = {
  '北京': ['朝阳区', '海淀区', '丰台区', '东城区', '西城区', '通州区', '昌平区'],
  '上海': ['浦东新区', '徐汇区', '长宁区', '普陀区', '静安区', '虹口区', '杨浦区'],
  '广州': ['天河区', '越秀区', '海珠区', '荔湾区', '白云区', '番禺区', '黄埔区'],
  '深圳': ['南山区', '福田区', '罗湖区', '宝安区', '龙岗区', '龙华区', '光明区'],
  '杭州': ['西湖区', '上城区', '下城区', '江干区', '拱墅区', '滨江区', '余杭区'],
  '成都': ['锦江区', '青羊区', '金牛区', '武侯区', '成华区', '高新区', '天府新区'],
  '武汉': ['江岸区', '江汉区', '硚口区', '汉阳区', '武昌区', '青山区', '洪山区'],
  '西安': ['新城区', '碑林区', '莲湖区', '雁塔区', '未央区', '灞桥区', '长安区']
}

const districts = computed(() => {
  return districtMap[searchForm.city] || []
})

const handleCityChange = () => {
  searchForm.district = ''
}

const selectCity = (city) => {
  searchForm.city = city
  handleSearch()
}

const handleSearch = () => {
  searchForm.rentWay = activeTab.value === 'rent' ? 1 : 2
  emit('search', { ...searchForm })
}

const handleReset = () => {
  Object.assign(searchForm, {
    city: '',
    district: '',
    houseType: '',
    minPrice: null,
    maxPrice: null,
    minArea: null,
    maxArea: null,
    rentWay: 1
  })
  activeTab.value = 'rent'
  emit('reset')
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.search-box {
  background: white;
  border-radius: $radius-large;
  padding: 25px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.search-tabs {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.search-tab {
  padding: 8px 20px;
  border-radius: 20px;
  background: $bg-color;
  color: $text-secondary;
  cursor: pointer;
  transition: $transition-base;
  
  &:hover {
    background: $primary-light;
    color: $primary-color;
  }
  
  &.active {
    background: $primary-color;
    color: white;
  }
}

.search-form {
  .search-row {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 15px;
    margin-bottom: 20px;
  }
  
  .search-input-group {
    margin-bottom: 0;
    
    :deep(.el-form-item__content) {
      flex-direction: column;
      align-items: stretch;
    }
    
    label {
      display: block;
      font-size: 12px;
      color: $text-secondary;
      margin-bottom: 5px;
    }
    
    :deep(.el-select) {
      width: 100%;
    }
  }
}

.price-range {
  display: flex;
  align-items: center;
  gap: 8px;
  
  :deep(.el-input) {
    flex: 1;
  }
  
  .separator {
    color: $text-secondary;
  }
}

.search-actions {
  display: flex;
  justify-content: center;
  gap: 15px;
  
  .el-button {
    min-width: 120px;
  }
}

.hot-cities {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid $border-color-lighter;
  display: flex;
  align-items: center;
  gap: 10px;
  
  .label {
    font-size: 13px;
    color: $text-secondary;
  }
}

.city-tags {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.city-tag {
  padding: 6px 15px;
  background: $bg-color;
  border-radius: 15px;
  font-size: 13px;
  color: $text-regular;
  cursor: pointer;
  transition: $transition-base;
  
  &:hover {
    background: $primary-color;
    color: white;
  }
}

@media (max-width: 992px) {
  .search-form .search-row {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 576px) {
  .search-form .search-row {
    grid-template-columns: 1fr;
  }
}
</style>
