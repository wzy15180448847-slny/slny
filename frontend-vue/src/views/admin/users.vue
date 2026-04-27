<template>
  <div class="admin-users">
    <div class="page-header">
      <h1>用户管理</h1>
    </div>
    
    <div class="search-bar">
      <el-input v-model="searchForm.username" placeholder="搜索用户名或手机号" class="search-input" />
      <el-select v-model="searchForm.userType" placeholder="选择用户类型">
        <el-option label="全部" value="" />
        <el-option label="管理员" value="ADMIN" />
        <el-option label="房东" value="LANDLORD" />
        <el-option label="租客" value="TENANT" />
      </el-select>
      <el-select v-model="searchForm.status" placeholder="选择状态">
        <el-option label="全部" value="" />
        <el-option label="正常" value="1" />
        <el-option label="禁用" value="0" />
      </el-select>
      <el-button type="primary" @click="search">搜索</el-button>
    </div>
    
    <el-table :data="users" border>
      <el-table-column prop="username" label="用户名" />
      <el-table-column label="昵称">
        <template #default="scope">
          {{ scope.row.nickname || scope.row.realName || '-' }}
        </template>
      </el-table-column>
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
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" />
      <el-table-column label="操作">
        <template #default="scope">
          <div class="action-buttons">
            <el-button size="small" @click="viewDetail(scope.row)">查看</el-button>
            <el-button 
              v-if="scope.row.status === 1" 
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
          </div>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="pagination-bar">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.current"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pagination.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
      />
    </div>
    
    <el-dialog title="用户详情" v-model="showDetailDialog" width="500px">
      <div v-if="selectedUser" class="detail-content">
        <div class="detail-row">
          <span class="detail-label">用户名</span>
          <span class="detail-value">{{ selectedUser.username }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">昵称</span>
          <span class="detail-value">{{ selectedUser.nickname || '-' }}</span>
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
          <el-tag :type="selectedUser.status === 1 ? 'success' : 'danger'">
            {{ selectedUser.status === 1 ? '正常' : '禁用' }}
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUsers, updateUserStatus, deleteUser as deleteUserApi } from '@/api/admin'

const searchForm = reactive({
  username: '',
  userType: '',
  status: ''
})

const users = ref([])
const selectedUser = ref(null)
const showDetailDialog = ref(false)

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
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

const search = () => {
  pagination.current = 1
  loadUsers()
}

const loadUsers = async () => {
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      username: searchForm.username,
      userType: searchForm.userType,
      status: searchForm.status
    }
    const result = await getUsers(params)
    users.value = result?.records || []
    pagination.total = result?.total || 0
    pagination.current = result?.current || 1
    pagination.size = result?.size || 10
  } catch (error) {
    console.error('加载用户列表失败:', error)
    ElMessage.error('加载用户列表失败')
  }
}

const handleSizeChange = (val) => {
  pagination.size = val
  pagination.current = 1
  loadUsers()
}

const handleCurrentChange = (val) => {
  pagination.current = val
  loadUsers()
}

const viewDetail = (user) => {
  selectedUser.value = user
  showDetailDialog.value = true
}

const toggleStatus = async (user) => {
  try {
    const newStatus = user.status === 1 ? 0 : 1
    await updateUserStatus(user.id, newStatus)
    
    user.status = newStatus
    ElMessage.success(user.status === 1 ? '用户已启用' : '用户已禁用')
  } catch (error) {
    console.error('修改用户状态失败:', error)
    ElMessage.error('修改用户状态失败')
  }
}

const deleteUser = async (user) => {
  try {
    await ElMessageBox.confirm('确定要删除这个用户吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteUserApi(user.id)
    users.value = users.value.filter(u => u.id !== user.id)
    pagination.total--
    ElMessage.success('用户已删除')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除用户失败:', error)
      ElMessage.error('删除用户失败')
    }
  }
}

onMounted(() => {
  loadUsers()
})
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

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 6px;
  align-items: flex-start;
  padding: 0;
  margin: 0;
  
  :deep(.el-button) {
    margin: 2px 0 !important;
    padding: 4px 12px !important;
    line-height: 1.4 !important;
    min-width: 60px;
    text-align: center;
  }
}

.pagination-bar {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
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