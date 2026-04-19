<template>
  <div class="login-page">
    <div class="login-container">
      <div class="left-panel">
        <h1>欢迎回来</h1>
        <p>登录您的账户，开始寻找理想居所</p>
        
        <div class="features">
          <div class="feature-item">
            <div class="icon">
              <el-icon><House /></el-icon>
            </div>
            <div class="text">
              <h4>海量房源</h4>
              <p>真实房源，实地验证</p>
            </div>
          </div>
          
          <div class="feature-item">
            <div class="icon">
              <el-icon><Lock /></el-icon>
            </div>
            <div class="text">
              <h4>安全保障</h4>
              <p>资金托管，交易无忧</p>
            </div>
          </div>
          
          <div class="feature-item">
            <div class="icon">
              <el-icon><Service /></el-icon>
            </div>
            <div class="text">
              <h4>专业服务</h4>
              <p>7x24小时在线客服</p>
            </div>
          </div>
        </div>
      </div>
      
      <div class="right-panel">
        <div class="form-header">
          <h2>账户登录</h2>
          <p>还没有账户？<router-link to="/register">立即注册</router-link></p>
        </div>
        
        <el-form 
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          @submit.prevent="handleLogin"
        >
          <el-form-item prop="username">
            <el-input 
              v-model="loginForm.username"
              placeholder="请输入用户名"
              size="large"
              prefix-icon="User"
            />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input 
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          
          <el-form-item>
            <div class="form-options">
              <el-checkbox v-model="loginForm.remember">记住我</el-checkbox>
              <a href="#" class="forgot-link">忘记密码？</a>
            </div>
          </el-form-item>
          
          <el-form-item>
            <el-button 
              type="primary"
              size="large"
              :loading="loading"
              class="login-btn"
              @click="handleLogin"
            >
              登录
            </el-button>
          </el-form-item>
        </el-form>
        
        <div class="divider">
          <span>其他登录方式</span>
        </div>
        
        <div class="social-login">
          <el-button circle size="large">
            <el-icon><ChatDotRound /></el-icon>
          </el-button>
          <el-button circle size="large">
            <el-icon><Message /></el-icon>
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { login } from '@/api/auth'
import { setToken } from '@/utils/auth'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
  remember: false
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      const response = await login({
        username: loginForm.username,
        password: loginForm.password
      })
      
      console.log('Full response:', response)
      
      if (!response) {
        throw new Error('登录响应为空')
      }
      
      let loginData = null
      
      if (response.data && response.data.token) {
        loginData = response.data
      } else if (response.data && response.data.data && response.data.data.token) {
        loginData = response.data.data
      } else {
        throw new Error('登录数据格式错误')
      }
      
      console.log('Login data:', loginData)
      
      userStore.token = loginData.token
      userStore.userInfo = loginData.user || {}
      setToken(loginData.token)
      
      ElMessage.success('登录成功')
      
      const userType = loginData.user?.userType
      console.log('User type:', userType)
      
      const redirect = route.query.redirect
      let targetPath = redirect
      
      if (!targetPath) {
        if (userType === 'ADMIN') {
          targetPath = '/admin'
        } else if (userType === 'LANDLORD') {
          targetPath = '/landlord'
        } else {
          targetPath = '/tenant'
        }
      }
      
      console.log('Navigating to:', targetPath)
      await router.push(targetPath).then(() => {
        console.log('Navigation successful')
      }).catch((err) => {
        console.error('Navigation failed:', err)
        ElMessage.error('页面跳转失败: ' + err.message)
      })
    } catch (error) {
      console.error('Login error:', error)
      ElMessage.error(error.message || '登录失败')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, $primary-color 0%, #764ba2 100%);
  padding: 20px;
}

.login-container {
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  overflow: hidden;
  width: 900px;
  max-width: 100%;
  min-height: 600px;
  display: flex;
}

.left-panel {
  flex: 1;
  background: linear-gradient(135deg, $primary-color 0%, #764ba2 100%);
  padding: 60px 40px;
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
  
  h1 {
    font-size: 36px;
    margin-bottom: 20px;
    font-weight: 700;
  }
  
  p {
    font-size: 16px;
    line-height: 1.8;
    opacity: 0.9;
    margin-bottom: 40px;
  }
}

.features {
  .feature-item {
    display: flex;
    align-items: center;
    margin-bottom: 25px;
    
    .icon {
      width: 50px;
      height: 50px;
      background: rgba(255, 255, 255, 0.2);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 15px;
      flex-shrink: 0;
      
      .el-icon {
        font-size: 24px;
      }
    }
    
    .text {
      h4 {
        font-size: 16px;
        font-weight: 600;
        margin-bottom: 5px;
      }
      
      p {
        font-size: 14px;
        opacity: 0.8;
        margin-bottom: 0;
      }
    }
  }
}

.right-panel {
  flex: 1;
  padding: 60px 50px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form-header {
  text-align: center;
  margin-bottom: 40px;
  
  h2 {
    font-size: 28px;
    color: $text-primary;
    margin-bottom: 10px;
    font-weight: 600;
  }
  
  p {
    color: $text-secondary;
    font-size: 14px;
    
    a {
      color: $primary-color;
      text-decoration: none;
      font-weight: 500;
      
      &:hover {
        text-decoration: underline;
      }
    }
  }
}

.login-form {
  .form-options {
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .forgot-link {
    color: $primary-color;
    text-decoration: none;
    font-size: 14px;
    
    &:hover {
      text-decoration: underline;
    }
  }
  
  .login-btn {
    width: 100%;
    height: 45px;
    font-size: 16px;
    border-radius: 22px;
    background: linear-gradient(135deg, $primary-color 0%, #764ba2 100%);
    border: none;
    
    &:hover {
      opacity: 0.9;
    }
  }
}

.divider {
  text-align: center;
  margin: 30px 0;
  position: relative;
  
  &::before,
  &::after {
    content: '';
    position: absolute;
    top: 50%;
    width: 40%;
    height: 1px;
    background: $border-color-base;
  }
  
  &::before {
    left: 0;
  }
  
  &::after {
    right: 0;
  }
  
  span {
    background: white;
    padding: 0 15px;
    color: $text-secondary;
    font-size: 13px;
  }
}

.social-login {
  display: flex;
  justify-content: center;
  gap: 20px;
  
  .el-button {
    width: 50px;
    height: 50px;
    border: 1px solid $border-color-base;
    
    &:hover {
      color: $primary-color;
      border-color: $primary-color;
    }
    
    .el-icon {
      font-size: 20px;
    }
  }
}

@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
  }
  
  .left-panel {
    display: none;
  }
  
  .right-panel {
    padding: 40px 30px;
  }
}
</style>
