<template>
  <div class="test-chart-page">
    <h1>图表测试</h1>
    <div id="testChart" style="width: 600px; height: 300px; border: 1px solid #ccc;"></div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import * as echarts from 'echarts'

onMounted(() => {
  setTimeout(() => {
    const chartDom = document.getElementById('testChart')
    if (chartDom) {
      const chart = echarts.init(chartDom)
      chart.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: ['4/19', '4/20', '4/21', '4/22', '4/23', '4/24', '4/25'] },
        yAxis: { type: 'value' },
        series: [{
          name: '新增用户',
          type: 'line',
          data: [4, 0, 0, 0, 0, 0, 0],
          smooth: true,
          areaStyle: { color: 'rgba(102, 126, 234, 0.1)' },
          lineStyle: { color: '#667eea', width: 3 }
        }]
      })
      console.log('Chart initialized successfully')
    } else {
      console.error('Chart container not found')
    }
  }, 500)
})
</script>

<style scoped>
.test-chart-page {
  padding: 20px;
}
</style>