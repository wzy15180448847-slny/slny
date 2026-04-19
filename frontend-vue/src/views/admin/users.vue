<template>
  <div class="admin-users">
    <div class="page-header">
      <h1>用户管理</h1>
    </div>
    
    <div class="search-bar">
      <el-input v-model="searchForm.keyword" placeholder="搜索用户名或手机号" class="search-input" />
      <el-select v-model="searchForm.userType" placeholder="选择用户类型">
        <el-option label="全部" value="" />
        <el-option label="管理员" value="ADMIN" />
        <el-option label="房东" value="LANDLORD" />
        <el-option label="租客" value="TENANT" />
      </el-select>
      <el-select v-model="searchForm.status" placeholder="选择状态">
        <el-option label="全部" value="" />
        <el-option label="正常" value="ACTIVE" />
        <el-option label="禁用" value="INACTIVE" />
      </el-select>
      <el-button type="primary" @click="search">搜索</el-button>
    </div>
    
    <el-table :data="filteredUsers" border>
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="userType" label="用户类型">
        <template #default="scope">
          <el-tag :type="getTypeTag(scope.row.userType)">{{ getTypeText(scope.row.userType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="creditScore" label="信用分" />
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="scope.row.status === 'ACTIVE' ? 'success' : 'danger'">
            {{ scope.row.status === 'ACTIVE' ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button size="small" @click="viewDetail(scope.row)">查看</el-button>
          <el-button 
            v-if="scope.row.status === 'ACTIVE'" 
            size="small" 
            type="warning" 
            @click="toggleStatus(scope.row)"
          >
            禁用
          </el-button>
          <el-button 
            v-else 
            size="small" 
            type="success" 
            @click="toggleStatus(scope.row)"
          >
            启用
          </el-button>
          <el-button size="small" type="danger" @click="deleteUser(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-dialog title="用户详情" v-model="showDetailDialog" width="500px">
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
          <span class="detail-label">手机号</span>
          <span class="detail-value">{{ selectedUser.phone }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">邮箱</span>
          <span class="detail-value">{{ selectedUser.email }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">用户类型</span>
          <el-tag :type="getTypeTag(selectedUser.userType)">{{ getTypeText(selectedUser.userType) }}</el-tag>
        </div>
        <div class="detail-row">
          <span class="detail-label">信用分</span>
          <span class="detail-value">{{ selectedUser.creditScore }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">状态</span>
          <el-tag :type="selectedUser.status === 'ACTIVE' ? 'success' : 'danger'">
            {{ selectedUser.status === 'ACTIVE' ? '正常' : '禁用' }}
          </el-tag>
        </div>
        <div class="detail-row">
          <span class="detail-label">注册时间</span>
          <span class="detail-value">{{ selectedUser.createTime }}</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchForm = reactive({
  keyword: '',
  userType: '',
  status: ''
})

const users = ref([
  { id: 1, username: 'admin', nickname: '管理员', phone: '138****0001', email: 'admin@test.com', userType: 'ADMIN', creditScore: 100, status: 'ACTIVE', createTime: '2024-01-01 10:00' },
  { id: 2, username: 'user0', nickname: '张房东', phone: '138****0002', email: 'user0@test.com', userType: 'LANDLORD', creditScore: 92, status: 'ACTIVE', createTime: '2024-01-02 10:00' },
  { id: 3, username: 'user1', nickname: '租客A', phone: '138****0003', email: 'user1@test.com', userType: 'TENANT', creditScore: 95, status: 'ACTIVE', createTime: '2024-01-03 10:00' },
  { id: 4, username: 'user2', nickname: '李房东', phone: '138****0004', email: 'user2@test.com', userType: 'LANDLORD', creditScore: 88, status: 'INACTIVE', createTime: '2024-01-04 10:00' },
  { id: 5, username: 'user3', nickname: '租客B', phone: '138****0005', email: 'user3@test.com', userType: 'TENANT', creditScore: 75, status: 'ACTIVE', createTime: '2024-01-05 10:00' }
])

const selectedUser = ref(null)
const showDetailDialog = ref(false)

const filteredUsers = computed(() => {
  return users.value.filter(user => {
    const matchKeyword = !searchForm.keyword || 
      user.username.includes(searchForm.keyword) || 
      user.phone.includes(searchForm.keyword)
    const matchType = !searchForm.userType || user.userType === searchForm.userType
    const matchStatus = !searchForm.status || user.status === searchForm.status
    return matchKeyword && matchType && matchStatus
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

const search = () => {}

const viewDetail = (user) => {
  selectedUser.value = user
  showDetailDialog.value = true
}

const toggleStatus = (user) => {
  user.status = user.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
  ElMessage.success(user.status === 'ACTIVE' ? '用户已启用' : '用户已禁用')
}

const deleteUser = async (user) => {
  try {
    await ElMessageBox.confirm('确定要删除这个用户吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const index = users.value.findIndex(u => u.id === user.id)
    if (index > -1) {
      users.value.splice(index, 1)
    }
    ElMessage.success('用户已删除')
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.admin-users {
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
    width: 250px;
  }
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
  }
}
</style>