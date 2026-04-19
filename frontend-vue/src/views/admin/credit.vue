<template>
  <div class="admin-credit">
    <div class="page-header">
      <h1>信用管理</h1>
    </div>
    
    <div class="credit-overview">
      <div class="overview-card">
        <div class="overview-title">用户信用分布</div>
        <div class="credit-distribution">
          <div class="distribution-item">
            <div class="distribution-bar high" style="width: 35%"></div>
            <span>优秀 (80-100): 35%</span>
          </div>
          <div class="distribution-item">
            <div class="distribution-bar medium" style="width: 45%"></div>
            <span>良好 (60-79): 45%</span>
          </div>
          <div class="distribution-item">
            <div class="distribution-bar low" style="width: 15%"></div>
            <span>一般 (40-59): 15%</span>
          </div>
          <div class="distribution-item">
            <div class="distribution-bar bad" style="width: 5%"></div>
            <span>较差 (&lt;40): 5%</span>
          </div>
        </div>
      </div>
    </div>
    
    <div class="search-bar">
      <el-input v-model="searchForm.keyword" placeholder="搜索用户名" class="search-input" />
      <el-select v-model="searchForm.creditLevel" placeholder="信用等级">
        <el-option label="全部" value="" />
        <el-option label="优秀" value="HIGH" />
        <el-option label="良好" value="MEDIUM" />
        <el-option label="一般" value="LOW" />
        <el-option label="较差" value="BAD" />
      </el-select>
      <el-button type="primary" @click="search">搜索</el-button>
    </div>
    
    <el-table :data="filteredUsers" border>
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="userType" label="用户类型">
        <template #default="scope">
          <el-tag :type="getTypeTag(scope.row.userType)">{{ getTypeText(scope.row.userType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="creditScore" label="信用分">
        <template #default="scope">
          <span :class="getCreditClass(scope.row.creditScore)">{{ scope.row.creditScore }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="creditLevel" label="信用等级">
        <template #default="scope">
          <el-tag :type="getCreditTag(scope.row.creditScore)">{{ getCreditLevel(scope.row.creditScore) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="recentBehavior" label="近期行为" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button size="small" @click="viewDetail(scope.row)">查看详情</el-button>
          <el-button size="small" type="warning" @click="adjustCredit(scope.row)">调整信用分</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-dialog title="用户信用详情" v-model="showDetailDialog" width="500px">
      <div v-if="selectedUser" class="detail-content">
        <div class="detail-row">
          <span class="detail-label">用户名</span>
          <span class="detail-value">{{ selectedUser.username }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">昵称</span>
          <span class="detail-value">{{ selectedUser.nickname }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">用户类型</span>
          <el-tag :type="getTypeTag(selectedUser.userType)">{{ getTypeText(selectedUser.userType) }}</el-tag>
        </div>
        <div class="detail-row">
          <span class="detail-label">当前信用分</span>
          <span class="detail-value credit-score" :class="getCreditClass(selectedUser.creditScore)">
            {{ selectedUser.creditScore }}
          </span>
        </div>
        <div class="detail-row">
          <span class="detail-label">信用等级</span>
          <el-tag :type="getCreditTag(selectedUser.creditScore)">{{ getCreditLevel(selectedUser.creditScore) }}</el-tag>
        </div>
        <div class="detail-section">
          <h4>信用记录</h4>
          <el-table :data="selectedUser.creditHistory" border>
            <el-table-column prop="time" label="时间" />
            <el-table-column prop="change" label="变动">
              <template #default="scope">
                <span :class="scope.row.change > 0 ? 'positive' : 'negative'">
                  {{ scope.row.change > 0 ? '+' : '' }}{{ scope.row.change }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="reason" label="原因" />
          </el-table>
        </div>
      </div>
    </el-dialog>
    
    <el-dialog title="调整信用分" v-model="showAdjustDialog" width="400px">
      <el-form :model="adjustForm" label-width="100px">
        <el-form-item label="调整分数">
          <el-input v-model="adjustForm.score" placeholder="正数为增加，负数为扣除" />
        </el-form-item>
        <el-form-item label="调整原因">
          <el-input type="textarea" v-model="adjustForm.reason" :rows="3" placeholder="请输入调整原因" />
        </el-form-item>
        <div class="current-score">
          当前信用分: <span :class="getCreditClass(selectedUser?.creditScore || 0)">{{ selectedUser?.creditScore || 0 }}</span>
        </div>
        <div class="new-score" v-if="adjustForm.score">
          调整后: <span :class="getCreditClass((selectedUser?.creditScore || 0) + parseInt(adjustForm.score) || 0)">
            {{ (selectedUser?.creditScore || 0) + (parseInt(adjustForm.score) || 0) }}
          </span>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="showAdjustDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmAdjust">确认调整</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'

const searchForm = reactive({
  keyword: '',
  creditLevel: ''
})

const users = ref([
  { id: 1, username: 'user1', nickname: '租客A', userType: 'TENANT', creditScore: 95, recentBehavior: '按时支付租金', creditHistory: [
    { time: '2024-01-10', change: 5, reason: '按时支付租金' },
    { time: '2024-01-05', change: 3, reason: '完成房屋评价' }
  ]},
  { id: 2, username: 'user0', nickname: '张房东', userType: 'LANDLORD', creditScore: 92, recentBehavior: '及时处理报修', creditHistory: [
    { time: '2024-01-12', change: 2, reason: '及时处理报修' },
    { time: '2024-01-08', change: 5, reason: '房源信息真实' }
  ]},
  { id: 3, username: 'user3', nickname: '租客B', userType: 'TENANT', creditScore: 75, recentBehavior: '正常', creditHistory: [
    { time: '2024-01-05', change: -5, reason: '逾期支付租金' }
  ]},
  { id: 4, username: 'user4', nickname: '租客C', userType: 'TENANT', creditScore: 35, recentBehavior: '多次逾期', creditHistory: [
    { time: '2024-01-14', change: -10, reason: '严重逾期' },
    { time: '2024-01-10', change: -5, reason: '逾期支付租金' }
  ]}
])

const selectedUser = ref(null)
const showDetailDialog = ref(false)
const showAdjustDialog = ref(false)

const adjustForm = reactive({
  score: '',
  reason: ''
})

const filteredUsers = computed(() => {
  return users.value.filter(user => {
    const matchKeyword = !searchForm.keyword || user.username.includes(searchForm.keyword)
    const matchLevel = !searchForm.creditLevel || getCreditLevel(user.creditScore) === getLevelText(searchForm.creditLevel)
    return matchKeyword && matchLevel
  })
})

const getTypeTag = (type) => {
  const types = {
    'ADMIN': 'danger',
    'LANDLORD': 'success',
    'TENANT': 'primary'
  }
  return types[type] || 'default'
}

const getTypeText = (type) => {
  const texts = {
    'ADMIN': '管理员',
    'LANDLORD': '房东',
    'TENANT': '租客'
  }
  return texts[type] || type
}

const getCreditClass = (score) => {
  if (score >= 80) return 'credit-high'
  if (score >= 60) return 'credit-medium'
  if (score >= 40) return 'credit-low'
  return 'credit-bad'
}

const getCreditTag = (score) => {
  if (score >= 80) return 'success'
  if (score >= 60) return 'primary'
  if (score >= 40) return 'warning'
  return 'danger'
}

const getCreditLevel = (score) => {
  if (score >= 80) return '优秀'
  if (score >= 60) return '良好'
  if (score >= 40) return '一般'
  return '较差'
}

const getLevelText = (level) => {
  const texts = {
    'HIGH': '优秀',
    'MEDIUM': '良好',
    'LOW': '一般',
    'BAD': '较差'
  }
  return texts[level] || level
}

const search = () => {}

const viewDetail = (user) => {
  selectedUser.value = user
  showDetailDialog.value = true
}

const adjustCredit = (user) => {
  selectedUser.value = user
  showAdjustDialog.value = true
}

const confirmAdjust = () => {
  if (!adjustForm.score || !adjustForm.reason) {
    ElMessage.error('请填写完整信息')
    return
  }
  
  const scoreChange = parseInt(adjustForm.score)
  selectedUser.value.creditScore += scoreChange
  selectedUser.value.creditHistory.unshift({
    time: new Date().toLocaleString(),
    change: scoreChange,
    reason: adjustForm.reason
  })
  
  ElMessage.success('信用分调整成功')
  showAdjustDialog.value = false
  adjustForm.score = ''
  adjustForm.reason = ''
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.admin-credit {
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

.credit-overview {
  margin-bottom: 20px;
}

.overview-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  
  .overview-title {
    font-size: 16px;
    color: $text-primary;
    margin-bottom: 15px;
    font-weight: 600;
  }
}

.credit-distribution {
  .distribution-item {
    margin-bottom: 10px;
    
    .distribution-bar {
      height: 20px;
      border-radius: 4px;
      margin-bottom: 5px;
      
      &.high {
        background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
      }
      
      &.medium {
        background: linear-gradient(135deg, #409eff 0%, #67b8ff 100%);
      }
      
      &.low {
        background: linear-gradient(135deg, #e6a23c 0%, #f0c78a 100%);
      }
      
      &.bad {
        background: linear-gradient(135deg, #f56c6c 0%, #f89898 100%);
      }
    }
    
    span {
      font-size: 13px;
      color: $text-secondary;
    }
  }
}

.search-bar {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  flex-wrap: wrap;
  
  .search-input {
    width: 250px;
  }
}

.credit-high {
  color: #67c23a;
  font-weight: bold;
}

.credit-medium {
  color: #409eff;
  font-weight: bold;
}

.credit-low {
  color: #e6a23c;
  font-weight: bold;
}

.credit-bad {
  color: #f56c6c;
  font-weight: bold;
}

.detail-content {
  .detail-row {
    display: flex;
    justify-content: space-between;
    padding: 12px 0;
    border-bottom: 1px solid $border-color-base;
    
    &:last-child {
      border-bottom: none;
    }
    
    .detail-label {
      color: $text-secondary;
      font-size: 14px;
    }
    
    .detail-value {
      color: $text-primary;
      font-size: 14px;
      font-weight: 500;
    }
    
    .credit-score {
      font-size: 20px;
    }
  }
  
  .detail-section {
    margin-top: 20px;
    
    h4 {
      font-size: 14px;
      color: $text-primary;
      margin-bottom: 10px;
    }
  }
}

.current-score, .new-score {
  font-size: 14px;
  color: $text-secondary;
  margin-top: 10px;
  
  span {
    font-weight: bold;
    font-size: 18px;
  }
}

.positive {
  color: #67c23a;
}

.negative {
  color: #f56c6c;
}
</style>