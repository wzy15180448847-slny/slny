<template>
  <div class="search-page">
    <div class="container">
      <div class="search-header">
        <h1>房源搜索</h1>
        <p>共找到 <span class="highlight">{{ pagination.total }}</span> 套房源</p>
      </div>
      
      <div class="search-content">
        <aside class="search-sidebar">
          <el-card class="filter-card">
            <template #header>
              <div class="card-header">
                <span>筛选条件</span>
                <el-button text type="primary" @click="handleReset">重置</el-button>
              </div>
            </template>
            
            <el-form :model="searchForm" label-position="top">
              <el-form-item label="城市">
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
              
              <el-form-item label="区域">
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
              
              <el-form-item label="房型">
                <el-select v-model="searchForm.houseType" placeholder="选择房型" clearable>
                  <el-option label="一室" value="1室" />
                  <el-option label="二室" value="2室" />
                  <el-option label="三室" value="3室" />
                  <el-option label="四室及以上" value="4室" />
                </el-select>
              </el-form-item>
              
              <el-form-item label="租金范围">
                <div class="price-inputs">
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
              
              <el-form-item label="面积范围">
                <div class="price-inputs">
                  <el-input 
                    v-model.number="searchForm.minArea" 
                    placeholder="最小"
                    type="number"
                  />
                  <span class="separator">-</span>
                  <el-input 
                    v-model.number="searchForm.maxArea" 
                    placeholder="最大"
                    type="number"
                  />
                </div>
              </el-form-item>
              
              <el-form-item label="租赁方式">
                <el-radio-group v-model="searchForm.rentWay">
                  <el-radio :label="1">整租</el-radio>
                  <el-radio :label="2">合租</el-radio>
                  <el-radio :label="null">不限</el-radio>
                </el-radio-group>
              </el-form-item>
              
              <el-form-item>
                <el-button 
                  type="primary" 
                  :loading="loading"
                  @click="handleSearch"
                >
                  搜索
                </el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </aside>
        
        <main class="search-main">
          <div class="sort-bar">
            <span>排序：</span>
            <el-radio-group v-model="sortBy" size="small" @change="handleSort">
              <el-radio-button label="createTime">最新发布</el-radio-button>
              <el-radio-button label="rentPrice">价格最低</el-radio-button>
              <el-radio-button label="area">面积最大</el-radio-button>
              <el-radio-button label="viewCount">最多浏览</el-radio-button>
            </el-radio-group>
          </div>
          
          <div v-loading="loading" class="house-list">
            <house-card 
              v-for="house in houseList" 
              :key="house.id"
              :house="house"
              @favorite-change="handleFavoriteChange"
            />
          </div>
          
          <el-empty v-if="!loading && houseList.length === 0" description="暂无符合条件的房源" />
          
          <div v-if="houseList.length > 0" class="pagination">
            <el-pagination
              v-model:current-page="pagination.current"
              v-model:page-size="pagination.size"
              :page-sizes="[12, 24, 36, 48]"
              :total="pagination.total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handlePageChange"
            />
          </div>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import HouseCard from '@/components/house/HouseCard.vue'
import { searchHouses } from '@/api/house'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const sortBy = ref('createTime')
const houseList = ref([])
const pagination = reactive({
  current: 1,
  size: 12,
  total: 0
})

const searchForm = reactive({
  city: '',
  district: '',
  houseType: '',
  minPrice: null,
  maxPrice: null,
  minArea: null,
  maxArea: null,
  rentWay: null
})

const cities = ['北京', '上海', '广州', '深圳', '杭州', '成都', '武汉', '西安']

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

const fetchHouses = async () => {
  loading.value = true
  try {
    const { data } = await searchHouses({
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    })
    houseList.value = data.records || []
    pagination.total = data.total || 0
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleCityChange = () => {
  searchForm.district = ''
}

const handleSearch = () => {
  pagination.current = 1
  fetchHouses()
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
    rentWay: null
  })
  pagination.current = 1
  fetchHouses()
}

const handleSort = () => {
  pagination.current = 1
  fetchHouses()
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  fetchHouses()
}

const handlePageChange = (page) => {
  pagination.current = page
  fetchHouses()
  window.scrollTo({ top: 0, behavior: 'smooth' })
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

watch(() => route.query, (query) => {
  if (Object.keys(query).length > 0) {
    Object.assign(searchForm, query)
    fetchHouses()
  }
}, { immediate: true })

onMounted(() => {
  if (Object.keys(route.query).length === 0) {
    fetchHouses()
  }
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.search-page {
  min-height: calc(100vh - #{$navbar-height} - #{$footer-height});
  padding: 30px 0;
  background: $bg-color;
}

.search-header {
  margin-bottom: 30px;
  
  h1 {
    font-size: 32px;
    font-weight: 600;
    margin-bottom: 10px;
    color: $text-primary;
  }
  
  p {
    font-size: 16px;
    color: $text-secondary;
    
    .highlight {
      color: $primary-color;
      font-weight: 600;
      font-size: 18px;
    }
  }
}

.search-content {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 30px;
}

.search-sidebar {
  .filter-card {
    position: sticky;
    top: 90px;
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
  
  .price-inputs {
    display: flex;
    align-items: center;
    gap: 8px;
    
    .el-input {
      flex: 1;
    }
    
    .separator {
      color: $text-secondary;
    }
  }
  
  :deep(.el-select) {
    width: 100%;
  }
}

.search-main {
  .sort-bar {
    background: white;
    padding: 15px 20px;
    border-radius: $radius-base;
    margin-bottom: 20px;
    display: flex;
    align-items: center;
    gap: 15px;
    
    span {
      color: $text-secondary;
      font-size: 14px;
    }
  }
}

.house-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 25px;
  margin-bottom: 30px;
}

.pagination {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

@media (max-width: 992px) {
  .search-content {
    grid-template-columns: 1fr;
  }
  
  .search-sidebar {
    order: 1;
    
    .filter-card {
      position: static;
    }
  }
  
  .search-main {
    order: 2;
  }
}
</style>
