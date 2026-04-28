<template>
  <div class="detail-page">
    <h1>房源详情测试页</h1>
    <p>当前URL: {{ window.location.href }}</p>
    <p>房源ID: {{ currentHouseId }}</p>
    <p>请求次数: {{ requestCount }}</p>
    <p>上次请求时间: {{ lastRequestTime }}</p>

    <div v-if="house">
      <h2>{{ house.title }}</h2>
      <p>城市: {{ house.city }}</p>
      <p>区域: {{ house.district }}</p>
      <p>价格: {{ Math.floor(house.rentPrice) }} 元/月</p>
      <p>房源ID(数据中): {{ house.id }}</p>
    </div>
    <div v-else>
      <p>正在加载...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const currentHouseId = ref('')
const house = ref(null)
const requestCount = ref(0)
const lastRequestTime = ref('')

const loadHouse = async (id) => {
  requestCount.value++
  lastRequestTime.value = new Date().toLocaleTimeString()

  try {
    const response = await axios.get(`/api/houses/${id}`, {
      headers: {
        'Cache-Control': 'no-cache, no-store, must-revalidate',
        'Pragma': 'no-cache',
        'Expires': '0'
      }
    })

    if (response.data.code === 200) {
      house.value = response.data.data
    }
  } catch (error) {
    console.error('请求失败:', error)
  }
}

onMounted(() => {
  const path = window.location.pathname
  const match = path.match(/\/house\/(\d+)/)
  if (match) {
    currentHouseId.value = match[1]
    loadHouse(currentHouseId.value)
  }
})
</script>

<style scoped>
.detail-page {
  padding: 20px;
}
</style>
