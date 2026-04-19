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
              <div class="stat-value">¥{{ stats.totalRevenue }}</div>
              <div class="stat-label">平台总营收</div>
              <div class="stat-change positive">+12.5%</div>
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
              <div class="stat-value">{{ stats.totalHouses }}</div>
              <div class="stat-label">房源总数</div>
              <div class="stat-change positive">+8.3%</div>
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
              <div class="stat-value">{{ stats.totalUsers }}</div>
              <div class="stat-label">用户总数</div>
              <div class="stat-change positive">+15.2%</div>
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
              <div class="stat-value">{{ stats.totalOrders }}</div>
              <div class="stat-label">订单总数</div>
              <div class="stat-change negative">-2.1%</div>
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
          <div ref="userChart" class="chart-container"></div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :lg="12" class="chart-col">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <h3>房源区域占比</h3>
            </div>
          </template>
          <div ref="regionChart" class="chart-container"></div>
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
          <div ref="rentRateChart" class="chart-container"></div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :lg="12" class="chart-col">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <h3>用户类型分布</h3>
            </div>
          </template>
          <div ref="userTypeChart" class="chart-container"></div>
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
              <span class="pending-count">12</span>
              <span class="pending-text">房源审核</span>
            </div>
            <div class="pending-item">
              <span class="pending-count">5</span>
              <span class="pending-text">资质审核</span>
            </div>
            <div class="pending-item">
              <span class="pending-count">8</span>
              <span class="pending-text">投诉处理</span>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :lg="16" class="info-col">
        <el-card class="info-card">
          <template #header>
            <h3>最近登录日志</h3>
          </template>
          <el-table :data="recentLogs" border size="small">
            <el-table-column prop="username" label="用户名" />
            <el-table-column prop="ip" label="IP地址" />
            <el-table-column prop="time" label="登录时间" />
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="scope.row.status === 'SUCCESS' ? 'success' : 'danger'">
                  {{ scope.row.status === 'SUCCESS' ? '成功' : '失败' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { TrendCharts, OfficeBuilding, User, Document } from '@element-plus/icons-vue'

const stats = reactive({
  totalRevenue: '2,850,000',
  totalHouses: 520,
  totalUsers: 3856,
  totalOrders: 1240
})

const recentLogs = ref([
  { id: 1, username: 'admin', ip: '192.168.1.100', time: '2024-01-15 14:30', status: 'SUCCESS' },
  { id: 2, username: 'user0', ip: '192.168.1.101', time: '2024-01-15 14:25', status: 'SUCCESS' },
  { id: 3, username: 'user1', ip: '192.168.1.102', time: '2024-01-15 14:20', status: 'SUCCESS' },
  { id: 4, username: 'test', ip: '192.168.1.103', time: '2024-01-15 14:15', status: 'FAILURE' }
])

const userChart = ref(null)
const regionChart = ref(null)
const rentRateChart = ref(null)
const userTypeChart = ref(null)

let charts = []

onMounted(() => {
  loadCharts()
})

onUnmounted(() => {
  charts.forEach(chart => {
    if (chart) {
      chart.dispose()
    }
  })
})

const loadCharts = () => {
  if (typeof window !== 'undefined' && window.echarts) {
    const echarts = window.echarts
    
    if (userChart.value) {
      const uc = echarts.init(userChart.value)
      uc.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: ['1/9', '1/10', '1/11', '1/12', '1/13', '1/14', '1/15'] },
        yAxis: { type: 'value' },
        series: [{
          name: '新增用户',
          type: 'line',
          data: [120, 200, 150, 250, 180, 300, 280],
          smooth: true,
          areaStyle: { color: 'rgba(102, 126, 234, 0.1)' },
          lineStyle: { color: '#667eea', width: 3 }
        }]
      })
      charts.push(uc)
    }
    
    if (regionChart.value) {
      const rc = echarts.init(regionChart.value)
      rc.setOption({
        tooltip: { trigger: 'item' },
        series: [{
          name: '区域分布',
          type: 'pie',
          radius: '50%',
          data: [
            { value: 35, name: '朝阳区' },
            { value: 25, name: '海淀区' },
            { value: 20, name: '西城区' },
            { value: 15, name: '东城区' },
            { value: 5, name: '其他' }
          ],
          itemStyle: {
            colors: ['#667eea', '#764ba2', '#f093fb', '#f5576c', '#4facfe']
          }
        }]
      })
      charts.push(rc)
    }
    
    if (rentRateChart.value) {
      const rrc = echarts.init(rentRateChart.value)
      rrc.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: ['1月', '2月', '3月', '4月', '5月', '6月'] },
        yAxis: { type: 'value', max: 100 },
        series: [{
          name: '出租率',
          type: 'bar',
          data: [65, 72, 68, 78, 82, 85],
          itemStyle: { color: 'rgba(102, 126, 234, 0.8)' }
        }]
      })
      charts.push(rrc)
    }
    
    if (userTypeChart.value) {
      const utc = echarts.init(userTypeChart.value)
      utc.setOption({
        tooltip: { trigger: 'item' },
        series: [{
          name: '用户类型',
          type: 'pie',
          radius: ['40%', '70%'],
          data: [
            { value: 65, name: '租客' },
            { value: 25, name: '房东' },
            { value: 10, name: '管理员' }
          ],
          itemStyle: {
            colors: ['#11998e', '#38ef7d', '#667eea']
          }
        }]
      })
      charts.push(utc)
    }
  }
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
    padding: 14px 0;
    border-bottom: 1px solid #f5f5f5;
    
    &:last-child {
      border-bottom: none;
    }
    
    .pending-count {
      width: 36px;
      height: 36px;
      background: linear-gradient(135deg, $primary-color 0%, #764ba2 100%);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 14px;
      font-weight: 600;
      color: #fff;
      margin-right: 14px;
    }
    
    .pending-text {
      font-size: 14px;
      color: $text-primary;
    }
  }
}

:deep(.el-table) {
  font-size: 13px;
  
  .el-table__row {
    &:hover {
      background: #f9fafb;
    }
  }
}

@media (min-width: 1366px) {
  .admin-dashboard {
    max-width: 1920px;
    margin: 0 auto;
  }
}
</style>