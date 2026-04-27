<template>
  <div class="admin-dashboard">
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6" class="stat-col">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon revenue-icon">
              <el-icon :size="28"><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ stats.totalRevenue || 0 }}</div>
              <div class="stat-label">平台总营收</div>
              <div class="stat-change neutral">暂无数据</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6" class="stat-col">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon house-icon">
              <el-icon :size="28"><OfficeBuilding /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalHouses || 0 }}</div>
              <div class="stat-label">房源总数</div>
              <div class="stat-change positive">今日新增 {{ stats.todayNewHouseCount || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6" class="stat-col">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon user-icon">
              <el-icon :size="28"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalUsers || 0 }}</div>
              <div class="stat-label">用户总数</div>
              <div class="stat-change positive">今日新增 {{ stats.todayNewUserCount || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6" class="stat-col">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon order-icon">
              <el-icon :size="28"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalOrders || 0 }}</div>
              <div class="stat-label">订单总数</div>
              <div class="stat-change neutral">暂无数据</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="12" class="chart-col">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <h3>近7日用户增长趋势</h3>
            </div>
          </template>
          <div ref="userChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :lg="12" class="chart-col">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <h3>房源区域占比</h3>
            </div>
          </template>
          <div ref="regionChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="12" class="chart-col">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <h3>房源出租率</h3>
            </div>
          </template>
          <div ref="rentRateChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :lg="12" class="chart-col">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <h3>用户类型分布</h3>
            </div>
          </template>
          <div ref="userTypeChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="bottom-row">
      <el-col :xs="24" :lg="8" class="info-col">
        <el-card class="info-card">
          <template #header>
            <h3>待审核事项</h3>
          </template>
          <div class="pending-list">
            <div class="pending-item">
              <span class="pending-icon house"></span>
              <span class="pending-text">房源审核</span>
              <span class="pending-count">{{ pendingStats.houseAudit || 0 }}</span>
            </div>
            <div class="pending-item">
              <span class="pending-icon qualification"></span>
              <span class="pending-text">资质审核</span>
              <span class="pending-count">{{ pendingStats.qualificationAudit || 0 }}</span>
            </div>
            <div class="pending-item">
              <span class="pending-icon complaint"></span>
              <span class="pending-text">投诉处理</span>
              <span class="pending-count">{{ pendingStats.complaintHandle || 0 }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :lg="16" class="info-col">
        <el-card class="info-card">
          <template #header>
            <h3>最近登录日志</h3>
          </template>
          <el-table :data="recentLogs" :border="false" :show-header="false">
            <el-table-column prop="username" label="用户名" width="120"></el-table-column>
            <el-table-column prop="loginTime" label="登录时间" width="180"></el-table-column>
            <el-table-column prop="ipAddress" label="IP地址"></el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'

const userChartRef = ref(null)
const regionChartRef = ref(null)
const rentRateChartRef = ref(null)
const userTypeChartRef = ref(null)
import { TrendCharts, OfficeBuilding, User, Document } from '@element-plus/icons-vue'
import { getDashboardStats, getChartData, getRecentLogs } from '@/api/admin'
import * as echarts from 'echarts'

const stats = reactive({
  totalRevenue: '0',
  totalHouses: 0,
  totalUsers: 0,
  totalOrders: 0,
  todayNewHouseCount: 0,
  todayNewUserCount: 0
})

const pendingStats = reactive({
  houseAudit: 0,
  qualificationAudit: 0,
  complaintHandle: 0
})

const recentLogs = ref([])
let charts = []
let chartData = ref({})

onMounted(() => {
  nextTick(() => {
    loadDashboardData()
  })
})

onUnmounted(() => {
  charts.forEach(chart => {
    if (chart) {
      chart.dispose()
    }
  })
})

const loadDashboardData = async () => {
  try {
    const [statsRes, chartsRes, logsRes] = await Promise.all([
      getDashboardStats(),
      getChartData(),
      getRecentLogs()
    ])
    
    Object.assign(stats, statsRes)
    chartData.value = chartsRes
    recentLogs.value = logsRes || []
    
    if (statsRes) {
      pendingStats.houseAudit = statsRes.pendingHouseAuditCount || 0
      pendingStats.qualificationAudit = statsRes.pendingQualificationAuditCount || 0
      pendingStats.complaintHandle = statsRes.pendingComplaintCount || 0
    }
    
    initCharts()
  } catch (error) {
    console.error('加载仪表盘数据失败:', error)
  }
}

const initCharts = () => {
  initUserGrowthChart()
  initRegionChart()
  initRentRateChart()
  initUserTypeChart()
}

const initUserGrowthChart = () => {
  const dom = userChartRef.value
  if (!dom) return
  
  const chart = echarts.init(dom)
  const data = chartData.value.userGrowth || {
    dates: ['4/19', '4/20', '4/21', '4/22', '4/23', '4/24', '4/25'],
    values: [4, 0, 0, 0, 0, 0, 0]
  }
  
  chart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: data.dates },
    yAxis: { type: 'value' },
    series: [{
      name: '新增用户',
      type: 'line',
      data: data.values,
      smooth: true,
      areaStyle: { color: 'rgba(102, 126, 234, 0.1)' },
      lineStyle: { color: '#667eea', width: 3 }
    }]
  })
  
  charts.push(chart)
}

const initRegionChart = () => {
  const dom = regionChartRef.value
  if (!dom) return
  
  const chart = echarts.init(dom)
  const data = chartData.value.regionDistribution || [
    { value: 35, name: '朝阳区' },
    { value: 25, name: '海淀区' },
    { value: 20, name: '西城区' },
    { value: 15, name: '东城区' },
    { value: 5, name: '其他' }
  ]
  
  chart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      name: '区域分布',
      type: 'pie',
      radius: '50%',
      data: data,
      itemStyle: {
        colors: ['#667eea', '#764ba2', '#f093fb', '#f5576c', '#4facfe']
      }
    }]
  })
  
  charts.push(chart)
}

const initRentRateChart = () => {
  const dom = rentRateChartRef.value
  if (!dom) return
  
  const chart = echarts.init(dom)
  const data = chartData.value.rentRate || {
    months: ['1月', '2月', '3月', '4月', '5月', '6月'],
    values: [65, 72, 68, 78, 82, 85]
  }
  
  chart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: data.months },
    yAxis: { type: 'value', max: 100 },
    series: [{
      name: '出租率',
      type: 'bar',
      data: data.values,
      itemStyle: { color: 'rgba(102, 126, 234, 0.8)' }
    }]
  })
  
  charts.push(chart)
}

const initUserTypeChart = () => {
  const dom = userTypeChartRef.value
  if (!dom) return
  
  const chart = echarts.init(dom)
  const data = chartData.value.userType || [
    { value: 65, name: '租客' },
    { value: 25, name: '房东' },
    { value: 10, name: '管理员' }
  ]
  
  chart.setOption({
    tooltip: { 
      trigger: 'item',
      formatter: '{b}: {d}%'
    },
    series: [{
      name: '用户类型',
      type: 'pie',
      radius: ['40%', '70%'],
      data: data,
      label: {
        formatter: '{b}\n{d}%'
      },
      itemStyle: {
        colors: ['#11998e', '#38ef7d', '#667eea']
      }
    }]
  })
  
  charts.push(chart)
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.admin-dashboard {
  padding: 0;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-col {
  .stat-card {
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
    border: none;
    overflow: hidden;
    
    .stat-content {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 16px;
    }
    
    .stat-icon {
      width: 56px;
      height: 56px;
      border-radius: 14px;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
      
      svg {
        color: white;
      }
      
      &.revenue-icon {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      }
      
      &.house-icon {
        background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
      }
      
      &.user-icon {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      }
      
      &.order-icon {
        background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      }
    }
    
    .stat-info {
      flex: 1;
      
      .stat-value {
        font-size: 28px;
        font-weight: 700;
        color: $text-primary;
        line-height: 1.2;
      }
      
      .stat-label {
        font-size: 13px;
        color: $text-secondary;
        margin-top: 4px;
      }
      
      .stat-change {
        display: inline-block;
        font-size: 12px;
        margin-top: 6px;
        padding: 2px 10px;
        border-radius: 10px;
        
        &.positive {
          color: #67c23a;
          background: rgba(103, 194, 58, 0.1);
        }
        
        &.negative {
          color: #f56c6c;
          background: rgba(245, 108, 108, 0.1);
        }
        
        &.neutral {
          color: #909399;
          background: rgba(144, 147, 153, 0.1);
        }
      }
    }
  }
}

.chart-row {
  margin-bottom: 20px;
}

.chart-col {
  .chart-card {
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
    border: none;
    
    .card-header {
      h3 {
        font-size: 16px;
        font-weight: 600;
        color: $text-primary;
        margin: 0;
      }
    }
    
    .chart-container {
      width: 100%;
      height: 260px;
    }
  }
}

.bottom-row {
  margin-bottom: 20px;
}

.info-col {
  .info-card {
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
    border: none;
    
    :deep(.el-card__header) {
      padding: 16px 20px;
      border-bottom: 1px solid #f0f0f0;
      
      h3 {
        font-size: 16px;
        font-weight: 600;
        color: $text-primary;
        margin: 0;
      }
    }
    
    :deep(.el-card__body) {
      padding: 16px 20px;
    }
  }
}

.pending-list {
  .pending-item {
    display: flex;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;
    
    &:last-child {
      border-bottom: none;
    }
    
    .pending-icon {
      width: 36px;
      height: 36px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 12px;
      
      &.house {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      }
      
      &.qualification {
        background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
      }
      
      &.complaint {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      }
    }
    
    .pending-text {
      flex: 1;
      font-size: 14px;
      color: $text-primary;
    }
    
    .pending-count {
      background: #f5f7fa;
      color: #666;
      font-size: 12px;
      padding: 4px 12px;
      border-radius: 20px;
      font-weight: 600;
    }
  }
}

:deep(.el-table) {
  .el-table__row {
    &:hover {
      background: #f5f7fa;
    }
  }
  
  .el-table__cell {
    padding: 12px 0;
    font-size: 14px;
    color: $text-primary;
    border-bottom: 1px solid #f0f0f0;
  }
}
</style>