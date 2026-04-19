<template>
  <div class="admin-logs">
    <div class="page-header">
      <h1>系统日志</h1>
    </div>
    
    <div class="search-bar">
      <el-input v-model="searchForm.keyword" placeholder="搜索用户名" class="search-input" />
      <el-select v-model="searchForm.type" placeholder="日志类型">
        <el-option label="全部" value="" />
        <el-option label="登录成功" value="SUCCESS" />
        <el-option label="登录失败" value="FAILURE" />
        <el-option label="异常登录" value="ABNORMAL" />
      </el-select>
      <el-date-picker v-model="searchForm.date" type="date" placeholder="选择日期" />
      <el-button type="primary" @click="search">搜索</el-button>
    </div>
    
    <div class="stats-row">
      <div class="stat-item">
        <span class="stat-label">今日登录</span>
        <span class="stat-value success">{{ todayStats.total }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">成功</span>
        <span class="stat-value success">{{ todayStats.success }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">失败</span>
        <span class="stat-value danger">{{ todayStats.failure }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">异常</span>
        <span class="stat-value warning">{{ todayStats.abnormal }}</span>
      </div>
    </div>
    
    <el-table :data="filteredLogs" border>
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="ip" label="IP地址" />
      <el-table-column prop="location" label="登录地点" />
      <el-table-column prop="device" label="设备" />
      <el-table-column prop="time" label="登录时间" />
      <el-table-column prop="type" label="登录状态">
        <template #default="scope">
          <el-tag :type="getTypeTag(scope.row.type)">{{ getTypeText(scope.row.type) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="reason" label="失败原因" />
    </el-table>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'

const searchForm = reactive({
  keyword: '',
  type: '',
  date: ''
})

const todayStats = reactive({
  total: 120,
  success: 115,
  failure: 3,
  abnormal: 2
})

const logs = ref([
  { id: 1, username: 'admin', ip: '192.168.1.100', location: '北京', device: 'Windows Chrome', time: '2024-01-15 14:30:25', type: 'SUCCESS', reason: '' },
  { id: 2, username: 'user0', ip: '192.168.1.101', location: '北京', device: 'Mac Safari', time: '2024-01-15 14:25:10', type: 'SUCCESS', reason: '' },
  { id: 3, username: 'test', ip: '10.0.0.1', location: '未知', device: 'Linux Firefox', time: '2024-01-15 14:20:33', type: 'FAILURE', reason: '密码错误' },
  { id: 4, username: 'admin', ip: '192.168.1.200', location: '上海', device: 'Windows Edge', time: '2024-01-15 14:15:00', type: 'ABNORMAL', reason: '异地登录' },
  { id: 5, username: 'user1', ip: '192.168.1.102', location: '北京', device: 'Android Chrome', time: '2024-01-15 14:10:45', type: 'SUCCESS', reason: '' },
  { id: 6, username: 'hacker', ip: '123.123.123.123', location: '海外', device: 'Unknown', time: '2024-01-15 14:05:20', type: 'FAILURE', reason: '账号不存在' },
  { id: 7, username: 'user2', ip: '192.168.1.103', location: '北京', device: 'iOS Safari', time: '2024-01-15 14:00:15', type: 'SUCCESS', reason: '' },
  { id: 8, username: 'admin', ip: '192.168.1.100', location: '北京', device: 'Windows Chrome', time: '2024-01-15 13:55:30', type: 'SUCCESS', reason: '' }
])

const filteredLogs = computed(() => {
  return logs.value.filter(log => {
    const matchKeyword = !searchForm.keyword || log.username.includes(searchForm.keyword)
    const matchType = !searchForm.type || log.type === searchForm.type
    return matchKeyword && matchType
  })
})

const getTypeTag = (type) => {
  const types = {
    'SUCCESS': 'success',
    'FAILURE': 'danger',
    'ABNORMAL': 'warning'
  }
  return types[type] || 'default'
}

const getTypeText = (type) => {
  const texts = {
    'SUCCESS': '成功',
    'FAILURE': '失败',
    'ABNORMAL': '异常'
  }
  return texts[type] || type
}

const search = () => {}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.admin-logs {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
  
  h1 {
    font-size: 22px;
    color: $text-primary;
    margin: 0;
  }
}

.search-bar {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  flex-wrap: wrap;
  
  .search-input {
    width: 200px;
  }
}

.stats-row {
  display: flex;
  gap: 30px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  
  .stat-label {
    font-size: 13px;
    color: $text-secondary;
    margin-bottom: 5px;
  }
  
  .stat-value {
    font-size: 24px;
    font-weight: bold;
    
    &.success {
      color: #67c23a;
    }
    
    &.danger {
      color: #f56c6c;
    }
    
    &.warning {
      color: #e6a23c;
    }
  }
}
</style>