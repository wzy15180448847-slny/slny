<template>
  <div class="register-page">
    <div class="register-container">
      <div class="left-panel">
        <h1>加入我们</h1>
        <p>注册新账户，开启您的租房之旅</p>
        
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
          <h2>创建账户</h2>
          <p>已有账户？<router-link to="/login">立即登录</router-link></p>
        </div>
        
        <el-form 
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          class="register-form"
          @submit.prevent="handleRegister"
        >
          <el-form-item prop="username">
            <el-input 
              v-model="registerForm.username"
              placeholder="请输入用户名"
              size="large"
              prefix-icon="User"
            />
          </el-form-item>
          
          <el-form-item prop="email">
            <el-input 
              v-model="registerForm.email"
              placeholder="请输入邮箱"
              size="large"
              prefix-icon="Message"
            />
          </el-form-item>
          
          <el-form-item prop="phone">
            <el-input 
              v-model="registerForm.phone"
              placeholder="请输入手机号"
              size="large"
              prefix-icon="Phone"
            />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input 
              v-model="registerForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          
          <el-form-item prop="confirmPassword">
            <el-input 
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请确认密码"
              size="large"
              prefix-icon="Check"
              show-password
              @keyup.enter="handleRegister"
            />
          </el-form-item>
          
          <el-form-item prop="userType">
            <el-radio-group v-model="registerForm.userType">
              <el-radio-button label="TENANT">租客</el-radio-button>
              <el-radio-button label="LANDLORD">房东</el-radio-button>
              <el-radio-button label="AGENT">经纪人</el-radio-button>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item>
            <el-checkbox v-model="registerForm.agreeTerms">我已阅读并同意<a href="#" class="terms-link">服务条款</a>和<a href="#" class="terms-link">隐私政策</a></el-checkbox>
          </el-form-item>
          
          <el-form-item>
            <el-button 
              type="primary"
              size="large"
              :loading="loading"
              class="register-btn"
              @click="handleRegister"
            >
              注册
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  username: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: '',
  userType: 'TENANT',
  agreeTerms: false
})

const registerRules = {
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
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      }, trigger: 'blur' }
  ],
  userType: [
    { required: true, message: '请选择用户类型', trigger: 'change' }
  ],
  agreeTerms: [
    { required: true, message: '请阅读并同意服务条款和隐私政策', trigger: 'change' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      await userStore.register({
        username: registerForm.username,
        email: registerForm.email,
        phone: registerForm.phone,
        password: registerForm.password,
        userType: registerForm.userType
      })
      
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } catch (error) {
      console.error(error)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, $primary-color 0%, #764ba2 100%);
  padding: 20px;
}

.register-container {
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  overflow: hidden;
  width: 900px;
  max-width: 100%;
  min-height: 650px;
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
  padding: 40px 50px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form-header {
  text-align: center;
  margin-bottom: 30px;
  
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

.register-form {
  .el-radio-group {
    width: 100%;
    display: flex;
    justify-content: space-between;
    margin-bottom: 15px;
  }
  
  .terms-link {
    color: $primary-color;
    text-decoration: none;
    
    &:hover {
      text-decoration: underline;
    }
  }
  
  .register-btn {
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

@media (max-width: 768px) {
  .register-container {
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