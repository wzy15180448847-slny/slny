<template>
  <div class="profile-page">
    <div class="profile-header">
      <div class="avatar-section">
        <div class="avatar-wrapper">
          <img 
            v-if="formData.avatar" 
            :src="formData.avatar" 
            class="avatar"
            alt="头像"
          />
          <div v-else class="avatar">
            <User :size="80" />
          </div>
          <div class="avatar-upload-btn" @click="triggerUpload">
            <el-icon><Upload /></el-icon>
            <span>更换头像</span>
          </div>
          <input 
            ref="avatarInput"
            type="file" 
            accept="image/jpeg,image/png,image/gif" 
            class="avatar-input"
            @change="handleAvatarUpload"
          />
        </div>
        <h2>{{ userStore.userInfo?.username || '管理员' }}</h2>
        <p class="role">管理员</p>
      </div>
    </div>
    
    <div class="profile-content">
      <div class="form-section">
        <h3 class="section-title">基本信息</h3>
        <el-form :model="formData" label-width="120px">
          <el-form-item label="用户名">
            <el-input v-model="formData.username" disabled />
          </el-form-item>
          <el-form-item label="真实姓名">
            <el-input v-model="formData.realName" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="formData.phone" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="formData.email" />
          </el-form-item>
          <el-form-item label="创建时间">
            <el-input v-model="formData.createTime" disabled />
          </el-form-item>
        </el-form>
      </div>
      
      <div class="form-section">
        <h3 class="section-title">修改密码</h3>
        <el-form :model="passwordForm" label-width="120px">
          <el-form-item label="原密码" prop="oldPassword">
            <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码" />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请确认新密码" />
          </el-form-item>
        </el-form>
      </div>
      
      <div class="btn-section">
        <el-button type="primary" @click="saveProfile">保存修改</el-button>
        <el-button type="warning" @click="changePassword">修改密码</el-button>
        <el-button @click="resetForm">重置</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { User, Upload } from '@element-plus/icons-vue'
import { uploadFile } from '@/api/file'
import { updatePassword } from '@/api/auth'

const userStore = useUserStore()
const avatarInput = ref(null)

const formData = reactive({
  username: '',
  realName: '',
  phone: '',
  email: '',
  createTime: '',
  avatar: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

onMounted(() => {
  loadUserInfo()
})

const loadUserInfo = () => {
  if (userStore.userInfo) {
    formData.username = userStore.userInfo.username || ''
    formData.realName = userStore.userInfo.realName || ''
    formData.phone = userStore.userInfo.phone || ''
    formData.email = userStore.userInfo.email || ''
    formData.createTime = userStore.userInfo.createTime || ''
    formData.avatar = userStore.userInfo.avatar || ''
  }
}

const triggerUpload = () => {
  avatarInput.value?.click()
}

const handleAvatarUpload = async (event) => {
  const file = event.target.files?.[0]
  if (!file) return
  
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('请选择图片文件')
    return
  }
  
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过2MB')
    return
  }
  
  try {
    const response = await uploadFile(file)
    if (response.data) {
      formData.avatar = response.data
      ElMessage.success('头像上传成功')
    }
  } catch (error) {
    console.error('头像上传失败:', error)
    ElMessage.error('头像上传失败')
  }
  
  event.target.value = ''
}

const saveProfile = async () => {
  try {
    const updateData = {
      realName: formData.realName,
      phone: formData.phone,
      email: formData.email
    }
    
    if (formData.avatar) {
      updateData.avatar = formData.avatar
    }
    
    await userStore.updateProfile(updateData)
    ElMessage.success('保存成功')
  } catch (error) {
    console.error(error)
    ElMessage.error('保存失败')
  }
}

const changePassword = async () => {
  if (!passwordForm.oldPassword) {
    ElMessage.error('请输入原密码')
    return
  }
  if (!passwordForm.newPassword) {
    ElMessage.error('请输入新密码')
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  
  try {
    await updatePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (error) {
    console.error(error)
    ElMessage.error(error.response?.data?.message || '密码修改失败')
  }
}

const resetForm = () => {
  loadUserInfo()
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.profile-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.profile-header {
  text-align: center;
  padding: 30px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  margin-bottom: 20px;
  color: #fff;
  
  .avatar-section {
    .avatar-wrapper {
      position: relative;
      display: inline-block;
      
      .avatar {
        width: 120px;
        height: 120px;
        margin: 0 auto 15px;
        background: rgba(255, 255, 255, 0.2);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        object-fit: cover;
        
        svg {
          color: #fff;
        }
      }
      
      .avatar-upload-btn {
        position: absolute;
        bottom: 5px;
        right: 50%;
        transform: translateX(50%);
        background: rgba(0, 0, 0, 0.6);
        border-radius: 50%;
        width: 36px;
        height: 36px;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        opacity: 0;
        transition: opacity 0.3s ease;
        
        &:hover {
          background: rgba(0, 0, 0, 0.8);
        }
        
        span {
          display: none;
        }
        
        .el-icon {
          color: #fff;
          font-size: 16px;
        }
      }
      
      &:hover .avatar-upload-btn {
        opacity: 1;
      }
      
      .avatar-input {
        display: none;
      }
    }
    
    h2 {
      margin: 0 0 8px;
      font-size: 24px;
    }
    
    .role {
      margin: 0;
      opacity: 0.8;
    }
  }
}

.profile-content {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.form-section {
  margin-bottom: 30px;
  
  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 2px solid $primary-color;
  }
}

.btn-section {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  padding-top: 20px;
  border-top: 1px solid #eee;
  
  button {
    padding: 8px 24px;
  }
}
</style>