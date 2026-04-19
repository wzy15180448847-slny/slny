<template>
  <div class="profile-page">
    <div class="profile-container">
      <div class="profile-header">
        <h2>个人中心</h2>
        <p>管理您的个人信息和账户设置</p>
      </div>
      
      <div class="profile-content">
        <div class="sidebar">
          <el-menu
            :default-active="activeTab"
            class="profile-menu"
            @select="handleMenuSelect"
          >
            <el-menu-item index="basic">
              <el-icon><User /></el-icon>
              <span>基本信息</span>
            </el-menu-item>
            <el-menu-item index="security">
              <el-icon><Lock /></el-icon>
              <span>账户安全</span>
            </el-menu-item>
            <el-menu-item index="notification">
              <el-icon><Bell /></el-icon>
              <span>通知设置</span>
            </el-menu-item>
          </el-menu>
        </div>
        
        <div class="main-content">
          <div v-if="activeTab === 'basic'" class="profile-section">
            <div class="section-header">
              <h3>基本信息</h3>
              <el-button 
                type="primary" 
                size="small"
                @click="toggleEdit"
              >
                {{ isEditing ? '取消' : '编辑' }}
              </el-button>
            </div>
            
            <el-form 
              ref="profileFormRef"
              :model="profileForm"
              :rules="profileRules"
              class="profile-form"
            >
              <div class="form-row">
                <el-form-item prop="username">
                  <el-input 
                    v-model="profileForm.username"
                    placeholder="用户名"
                    :disabled="!isEditing"
                  />
                </el-form-item>
                
                <el-form-item prop="email">
                  <el-input 
                    v-model="profileForm.email"
                    placeholder="邮箱"
                    :disabled="!isEditing"
                  />
                </el-form-item>
              </div>
              
              <div class="form-row">
                <el-form-item prop="phone">
                  <el-input 
                    v-model="profileForm.phone"
                    placeholder="手机号"
                    :disabled="!isEditing"
                  />
                </el-form-item>
                
                <el-form-item prop="nickname">
                  <el-input 
                    v-model="profileForm.nickname"
                    placeholder="昵称"
                    :disabled="!isEditing"
                  />
                </el-form-item>
              </div>
              
              <div class="form-row">
                <el-form-item prop="realName">
                  <el-input 
                    v-model="profileForm.realName"
                    placeholder="真实姓名"
                    :disabled="!isEditing"
                  />
                </el-form-item>
                
                <el-form-item prop="gender">
                  <el-select 
                    v-model="profileForm.gender"
                    placeholder="性别"
                    :disabled="!isEditing"
                  >
                    <el-option label="男" :value="1" />
                    <el-option label="女" :value="2" />
                    <el-option label="保密" :value="0" />
                  </el-select>
                </el-form-item>
              </div>
              
              <div v-if="isEditing" class="form-actions">
                <el-button @click="toggleEdit">取消</el-button>
                <el-button type="primary" @click="saveProfile">保存</el-button>
              </div>
            </el-form>
          </div>
          
          <div v-if="activeTab === 'security'" class="profile-section">
            <div class="section-header">
              <h3>账户安全</h3>
            </div>
            
            <div class="security-list">
              <div class="security-item">
                <div class="security-info">
                  <h4>修改密码</h4>
                  <p>定期修改密码可以提高账户安全性</p>
                </div>
                <el-button type="primary" size="small" @click="showChangePasswordDialog">修改</el-button>
              </div>
              
              <div class="security-item">
                <div class="security-info">
                  <h4>绑定邮箱</h4>
                  <p>{{ profileForm.email || '未绑定' }}</p>
                </div>
                <el-button type="primary" size="small">绑定</el-button>
              </div>
              
              <div class="security-item">
                <div class="security-info">
                  <h4>绑定手机</h4>
                  <p>{{ profileForm.phone || '未绑定' }}</p>
                </div>
                <el-button type="primary" size="small">绑定</el-button>
              </div>
            </div>
          </div>
          
          <div v-if="activeTab === 'notification'" class="profile-section">
            <div class="section-header">
              <h3>通知设置</h3>
            </div>
            
            <div class="notification-list">
              <div class="notification-item">
                <div class="notification-info">
                  <h4>新消息通知</h4>
                  <p>接收系统和其他用户的消息通知</p>
                </div>
                <el-switch v-model="notificationSettings.message" />
              </div>
              
              <div class="notification-item">
                <div class="notification-info">
                  <h4>预约提醒</h4>
                  <p>接收房源预约的提醒通知</p>
                </div>
                <el-switch v-model="notificationSettings.appointment" />
              </div>
              
              <div class="notification-item">
                <div class="notification-info">
                  <h4>房源更新</h4>
                  <p>接收关注房源的更新通知</p>
                </div>
                <el-switch v-model="notificationSettings.houseUpdate" />
              </div>
              
              <div class="notification-item">
                <div class="notification-info">
                  <h4>促销活动</h4>
                  <p>接收平台的促销活动通知</p>
                </div>
                <el-switch v-model="notificationSettings.promotion" />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 修改密码对话框 -->
    <el-dialog
      title="修改密码"
      v-model="changePasswordDialogVisible"
      width="400px"
    >
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
      >
        <el-form-item prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            placeholder="请输入原密码"
          />
        </el-form-item>
        <el-form-item prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码"
          />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请确认新密码"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="changePasswordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="changePassword">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const activeTab = ref('basic')
const isEditing = ref(false)
const profileFormRef = ref(null)
const passwordFormRef = ref(null)
const changePasswordDialogVisible = ref(false)

const profileForm = reactive({
  username: '',
  email: '',
  phone: '',
  nickname: '',
  userType: '',
  gender: '',
  realName: ''
})

const profileRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
  ]
}

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      }, trigger: 'blur' }
  ]
}

const notificationSettings = reactive({
  message: true,
  appointment: true,
  houseUpdate: true,
  promotion: false
})

const handleMenuSelect = (key) => {
  activeTab.value = key
}

const toggleEdit = () => {
  isEditing.value = !isEditing.value
  if (!isEditing.value) {
    loadUserProfile()
  }
}

const saveProfile = async () => {
  if (!profileFormRef.value) return
  
  await profileFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    try {
      await userStore.updateProfile(profileForm)
      ElMessage.success('个人信息更新成功')
      isEditing.value = false
    } catch (error) {
      console.error(error)
    }
  })
}

const showChangePasswordDialog = () => {
  changePasswordDialogVisible.value = true
  Object.assign(passwordForm, {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  })
}

const changePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    try {
      await userStore.changePassword({
        oldPassword: passwordForm.oldPassword,
        newPassword: passwordForm.newPassword
      })
      ElMessage.success('密码修改成功')
      changePasswordDialogVisible.value = false
    } catch (error) {
      console.error(error)
    }
  })
}

const loadUserProfile = () => {
  // 从用户存储中加载个人信息
  Object.assign(profileForm, {
    username: userStore.username || '',
    email: userStore.email || '',
    phone: userStore.phone || '',
    nickname: userStore.nickname || '',
    userType: userStore.userType || '',
    gender: userStore.gender || '',
    bio: userStore.bio || ''
  })
}

onMounted(() => {
  loadUserProfile()
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.profile-page {
  padding: 40px 20px;
  min-height: 80vh;
}

.profile-container {
  max-width: 1200px;
  margin: 0 auto;
}

.profile-header {
  text-align: center;
  margin-bottom: 40px;
  
  h2 {
    font-size: 32px;
    color: $text-primary;
    margin-bottom: 10px;
    font-weight: 600;
  }
  
  p {
    color: $text-secondary;
    font-size: 16px;
  }
}

.profile-content {
  display: flex;
  gap: 30px;
}

.sidebar {
  flex: 0 0 250px;
}

.profile-menu {
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  
  .el-menu-item {
    height: 60px;
    line-height: 60px;
    font-size: 15px;
    
    &.is-active {
      background-color: $primary-color;
      color: white;
    }
  }
}

.main-content {
  flex: 1;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 30px;
}

.profile-section {
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;
    
    h3 {
      font-size: 20px;
      color: $text-primary;
      font-weight: 600;
    }
  }
}

.profile-form {
  .form-row {
    display: flex;
    gap: 20px;
    margin-bottom: 20px;
    
    .el-form-item {
      flex: 1;
      
      &.full-width {
        flex: 1 1 100%;
      }
    }
  }
  
  .form-actions {
    display: flex;
    gap: 10px;
    justify-content: flex-end;
    margin-top: 30px;
  }
}

.security-list,
.notification-list {
  .security-item,
  .notification-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px;
    border-bottom: 1px solid $border-color-base;
    
    &:last-child {
      border-bottom: none;
    }
    
    .security-info,
    .notification-info {
      h4 {
        font-size: 16px;
        color: $text-primary;
        margin-bottom: 5px;
        font-weight: 500;
      }
      
      p {
        font-size: 14px;
        color: $text-secondary;
      }
    }
  }
}

@media (max-width: 768px) {
  .profile-content {
    flex-direction: column;
  }
  
  .sidebar {
    flex: 1;
  }
  
  .form-row {
    flex-direction: column;
  }
}
</style>