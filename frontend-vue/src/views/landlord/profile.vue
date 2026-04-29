<template>
  <div class="landlord-profile">
    <div class="profile-header">
      <div class="avatar-section">
        <div class="avatar-container">
          <el-avatar :size="120" class="profile-avatar" :src="formData.avatar">
            {{ userStore.nickname.charAt(0) }}
          </el-avatar>
          <div class="avatar-overlay" @click="triggerAvatarUpload">
            <el-icon class="upload-icon"><Camera /></el-icon>
            <span>更换头像</span>
          </div>
        </div>
        <input 
          type="file" 
          ref="avatarInput"
          accept="image/*"
          style="display: none"
          @change="handleAvatarChange"
        />
        <h2>{{ userStore.nickname }}</h2>
        <p class="user-type">房东</p>
        <p class="credit-score">信用分: {{ userInfo.creditScore }}</p>
      </div>
    </div>
    
    <div class="profile-content">
      <div class="info-card">
        <h3>基本信息</h3>
        <el-form :model="formData" label-width="100px">
          <el-form-item label="用户名">
            <el-input v-model="formData.username" disabled />
          </el-form-item>
          <el-form-item label="昵称">
            <el-input v-model="formData.nickname" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="formData.phone" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="formData.email" />
          </el-form-item>
          <el-form-item label="身份证号">
            <el-input v-model="formData.idCard" disabled />
          </el-form-item>
          <el-form-item label="注册时间">
            <el-input v-model="formData.registerTime" disabled />
          </el-form-item>
        </el-form>
        <el-button type="primary" @click="saveProfile">保存修改</el-button>
      </div>
      
      <div class="info-card">
        <h3>资质认证</h3>
        <div class="qualification-list">
          <div class="qualification-item">
            <div class="qualification-icon">
              <el-icon><CreditCard /></el-icon>
            </div>
            <div class="qualification-info">
              <h4>实名认证</h4>
              <p>已认证</p>
            </div>
            <el-tag type="success">已完成</el-tag>
          </div>
          <div class="qualification-item">
            <div class="qualification-icon">
              <el-icon><HomeFilled /></el-icon>
            </div>
            <div class="qualification-info">
              <h4>房源资质</h4>
              <p>已上传 {{ userInfo.houseCount }} 套房源</p>
            </div>
            <el-tag type="success">已完成</el-tag>
          </div>
        </div>
      </div>
      
      <div class="info-card">
        <h3>安全设置</h3>
        <div class="security-options">
          <div class="security-item" @click="changePassword">
            <div class="security-icon">
              <el-icon><Key /></el-icon>
            </div>
            <div class="security-info">
              <h4>修改密码</h4>
              <p>定期更换密码保障账户安全</p>
            </div>
            <el-icon class="arrow-icon"><ArrowRight /></el-icon>
          </div>
        </div>
      </div>
    </div>
    
    <el-dialog title="修改密码" v-model="showPasswordDialog" width="400px">
      <el-form :model="passwordForm" label-width="80px">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input type="password" v-model="passwordForm.oldPassword" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input type="password" v-model="passwordForm.newPassword" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input type="password" v-model="passwordForm.confirmPassword" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="submitPassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { Camera } from '@element-plus/icons-vue'
import { updatePassword } from '@/api/auth'
import { uploadFile, getFileUrl } from '@/api/file'
import { getMyHouses } from '@/api/landlord'

const userStore = useUserStore()
const avatarInput = ref(null)

const userInfo = reactive({
  creditScore: 100,
  houseCount: 0
})

const formData = reactive({
  username: '',
  nickname: '',
  phone: '',
  email: '',
  idCard: '',
  registerTime: '',
  avatar: ''
})

const showPasswordDialog = ref(false)

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

onMounted(async () => {
  formData.username = userStore.username
  formData.nickname = userStore.nickname
  formData.phone = userStore.userInfo?.phone || ''
  formData.email = userStore.userInfo?.email || ''
  formData.avatar = userStore.avatar || ''
  formData.idCard = userStore.userInfo?.idCard ? maskIdCard(userStore.userInfo.idCard) : '**** **** **** ****'
  formData.registerTime = userStore.userInfo?.createTime ? formatDate(userStore.userInfo.createTime) : '2024-01-01 10:00:00'
  userInfo.creditScore = userStore.userInfo?.creditScore || 100
  
  await loadHouseCount()
})

const maskIdCard = (idCard) => {
  if (!idCard || idCard.length < 18) return '**** **** **** ****'
  return `${idCard.substring(0, 4)} **** **** ${idCard.substring(14)}`
}

const formatDate = (date) => {
  if (!date) return ''
  return date.replace('T', ' ').substring(0, 19)
}

const loadHouseCount = async () => {
  try {
    const data = await getMyHouses()
    userInfo.houseCount = (data || []).length
  } catch (error) {
    console.error('加载房源数量失败:', error)
  }
}

const triggerAvatarUpload = () => {
  avatarInput.value.click()
}

const handleAvatarChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('头像文件大小不能超过 5MB')
    return
  }

  if (!file.type.startsWith('image/')) {
    ElMessage.error('只支持图片格式')
    return
  }

  try {
    const fileName = await uploadFile(file)
    const avatarUrl = await getFileUrl(fileName)
    formData.avatar = avatarUrl
    ElMessage.success('头像上传成功')
  } catch (error) {
    console.error('头像上传失败:', error)
    ElMessage.error('头像上传失败，请重试')
  } finally {
    if (avatarInput.value) {
      avatarInput.value.value = ''
    }
  }
}

const saveProfile = async () => {
  try {
    await userStore.updateProfile({
      nickname: formData.nickname,
      phone: formData.phone,
      email: formData.email,
      avatar: formData.avatar
    })
    ElMessage.success('个人信息修改成功')
  } catch (error) {
    console.error(error)
    ElMessage.error('保存失败')
  }
}

const changePassword = () => {
  showPasswordDialog.value = true
}

const submitPassword = async () => {
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
    showPasswordDialog.value = false
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (error) {
    console.error(error)
    ElMessage.error(error.response?.data?.message || '密码修改失败')
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.landlord-profile {
  padding: 30px;
  max-width: 800px;
  margin: 0 auto;
}

.profile-header {
  text-align: center;
  margin-bottom: 30px;
  padding: 30px;
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  border-radius: 12px;
  color: white;
  
  .avatar-container {
    position: relative;
    display: inline-block;
    margin-bottom: 15px;
    
    .profile-avatar {
      background: rgba(255, 255, 255, 0.2);
    }
    
    .avatar-overlay {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      border-radius: 50%;
      background: rgba(0, 0, 0, 0.6);
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      opacity: 0;
      transition: opacity 0.3s;
      cursor: pointer;
      
      &:hover {
        opacity: 1;
      }
      
      .upload-icon {
        font-size: 28px;
        margin-bottom: 5px;
      }
      
      span {
        font-size: 12px;
      }
    }
  }
  
  h2 {
    font-size: 24px;
    margin-bottom: 10px;
  }
  
  .user-type {
    background: rgba(255, 255, 255, 0.2);
    padding: 5px 15px;
    border-radius: 20px;
    display: inline-block;
    margin-bottom: 10px;
  }
  
  .credit-score {
    font-size: 14px;
    opacity: 0.9;
  }
}

.profile-content {
  display: grid;
  gap: 20px;
}

.info-card {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  
  h3 {
    font-size: 18px;
    color: $text-primary;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 1px solid $border-color-base;
  }
}

.qualification-list {
  .qualification-item {
    display: flex;
    align-items: center;
    padding: 15px 0;
    border-bottom: 1px solid $border-color-base;
    
    &:last-child {
      border-bottom: none;
    }
  }
  
  .qualification-icon {
    width: 40px;
    height: 40px;
    background: $bg-color;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 15px;
    
    .el-icon {
      font-size: 18px;
      color: $primary-color;
    }
  }
  
  .qualification-info {
    flex: 1;
    
    h4 {
      font-size: 15px;
      color: $text-primary;
      margin-bottom: 5px;
    }
    
    p {
      font-size: 13px;
      color: $text-secondary;
    }
  }
}

.security-options {
  .security-item {
    display: flex;
    align-items: center;
    padding: 15px 0;
    border-bottom: 1px solid $border-color-base;
    cursor: pointer;
    transition: background 0.3s ease;
    
    &:hover {
      background: $bg-color;
    }
    
    &:last-child {
      border-bottom: none;
    }
  }
  
  .security-icon {
    width: 40px;
    height: 40px;
    background: $bg-color;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 15px;
    
    .el-icon {
      font-size: 18px;
      color: $primary-color;
    }
  }
  
  .security-info {
    flex: 1;
    
    h4 {
      font-size: 15px;
      color: $text-primary;
      margin-bottom: 5px;
    }
    
    p {
      font-size: 13px;
      color: $text-secondary;
    }
  }
  
  .arrow-icon {
    color: $text-secondary;
  }
}

.el-form {
  margin-bottom: 20px;
}

.el-button {
  width: 100%;
}
</style>