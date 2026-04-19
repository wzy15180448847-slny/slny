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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getRecentLogs, searchLogs } from '@/api/admin'

const searchForm = reactive({
  keyword: '',
  type: '',
  date: ''
})

const todayStats = reactive({
  total: 0,
  success: 0,
  failure: 0,
  abnormal: 0
})

const logs = ref([])

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

const loadLogs = async () => {
  try {
    const { data } = await getRecentLogs()
    logs.value = data.map(log => ({
      id: log.id,
      username: log.username,
      ip: log.loginIp || '未知',
      location: log.loginLocation || '未知',
      device: log.loginDevice || '未知',
      time: formatDateTime(log.createTime),
      type: log.loginResult === 1 ? 'SUCCESS' : 'FAILURE',
      reason: log.failReason || ''
    }))
    
    updateStats()
  } catch (error) {
    console.error('加载日志失败:', error)
    ElMessage.error('加载日志失败')
  }
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return dateTime.replace('T', ' ').substring(0, 19)
}

const formatDate = (date) => {
  if (!date) return ''
  if (typeof date === 'string') return date
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const search = async () => {
  try {
    const params = {
      keyword: searchForm.keyword,
      type: searchForm.type,
      date: searchForm.date ? formatDate(searchForm.date) : ''
    }
    const { data } = await searchLogs(params)
    logs.value = data.map(log => ({
      id: log.id,
      username: log.username,
      ip: log.loginIp || '未知',
      location: log.loginLocation || '未知',
      device: log.loginDevice || '未知',
      time: formatDateTime(log.createTime),
      type: log.loginResult === 1 ? 'SUCCESS' : (log.loginResult === 2 ? 'ABNORMAL' : 'FAILURE'),
      reason: log.failReason || ''
    }))
    
    updateStats(searchForm.date ? formatDate(searchForm.date) : null)
  } catch (error) {
    console.error('搜索日志失败:', error)
    ElMessage.error('搜索日志失败')
  }
}

const updateStats = (targetDate = null) => {
  const dateToFilter = targetDate || new Date().toISOString().split('T')[0]
  const filteredLogs = logs.value.filter(log => log.time.startsWith(dateToFilter))
  todayStats.total = filteredLogs.length
  todayStats.success = filteredLogs.filter(l => l.type === 'SUCCESS').length
  todayStats.failure = filteredLogs.filter(l => l.type === 'FAILURE').length
  todayStats.abnormal = filteredLogs.filter(l => l.type === 'ABNORMAL').length
}

onMounted(() => {
  loadLogs()
})
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