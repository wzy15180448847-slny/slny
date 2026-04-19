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
            <div class="distribution-bar high" :style="{ width: creditDistribution.highPercent + '%' }"></div>
            <span>优秀 (80-100): {{ creditDistribution.highPercent }}%</span>
          </div>
          <div class="distribution-item">
            <div class="distribution-bar medium" :style="{ width: creditDistribution.mediumPercent + '%' }"></div>
            <span>良好 (60-79): {{ creditDistribution.mediumPercent }}%</span>
          </div>
          <div class="distribution-item">
            <div class="distribution-bar low" :style="{ width: creditDistribution.lowPercent + '%' }"></div>
            <span>一般 (40-59): {{ creditDistribution.lowPercent }}%</span>
          </div>
          <div class="distribution-item">
            <div class="distribution-bar bad" :style="{ width: creditDistribution.badPercent + '%' }"></div>
            <span>较差 (&lt;40): {{ creditDistribution.badPercent }}%</span>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUsers, updateCreditScore } from '@/api/admin'

const searchForm = reactive({
  keyword: '',
  creditLevel: ''
})

const users = ref([])

const creditDistribution = reactive({
  highPercent: 0,
  mediumPercent: 0,
  lowPercent: 0,
  badPercent: 0
})

const calculateDistribution = () => {
  const total = users.value.length
  if (total === 0) {
    creditDistribution.highPercent = 0
    creditDistribution.mediumPercent = 0
    creditDistribution.lowPercent = 0
    creditDistribution.badPercent = 0
    return
  }
  
  const high = users.value.filter(u => u.creditScore >= 80).length
  const medium = users.value.filter(u => u.creditScore >= 60 && u.creditScore < 80).length
  const low = users.value.filter(u => u.creditScore >= 40 && u.creditScore < 60).length
  const bad = users.value.filter(u => u.creditScore < 40).length
  
  creditDistribution.highPercent = Math.round((high / total) * 100)
  creditDistribution.mediumPercent = Math.round((medium / total) * 100)
  creditDistribution.lowPercent = Math.round((low / total) * 100)
  creditDistribution.badPercent = Math.round((bad / total) * 100)
}

const loadUsers = async () => {
  try {
    const { data } = await getUsers({})
    users.value = (data?.records || []).map(user => ({
      id: user.id,
      username: user.username,
      nickname: user.nickname || user.username,
      userType: user.userType === 1 ? 'LANDLORD' : 'TENANT',
      creditScore: user.creditScore || 0,
      recentBehavior: '',
      creditHistory: []
    }))
    calculateDistribution()
  } catch (error) {
    console.error('加载用户信用数据失败:', error)
    ElMessage.error('加载用户信用数据失败')
  }
}

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

const confirmAdjust = async () => {
  if (!adjustForm.score || !adjustForm.reason) {
    ElMessage.error('请填写完整信息')
    return
  }
  
  try {
    const scoreChange = parseInt(adjustForm.score)
    await updateCreditScore(selectedUser.value.id, { score: scoreChange })
    
    selectedUser.value.creditScore += scoreChange
    selectedUser.value.creditHistory.unshift({
      time: new Date().toLocaleString(),
      change: scoreChange,
      reason: adjustForm.reason
    })
    
    const userIndex = users.value.findIndex(u => u.id === selectedUser.value.id)
    if (userIndex !== -1) {
      users.value[userIndex].creditScore = selectedUser.value.creditScore
    }
    
    calculateDistribution()
    
    ElMessage.success('信用分调整成功')
    showAdjustDialog.value = false
    adjustForm.score = ''
    adjustForm.reason = ''
  } catch (error) {
    console.error('调整信用分失败:', error)
    ElMessage.error('调整信用分失败')
  }
}

onMounted(() => {
  loadUsers()
})
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