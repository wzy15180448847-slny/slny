import { defineStore } from 'pinia'
import { login, logout, getUserInfo, updateProfile, updatePassword } from '@/api/auth'
import { getToken, setToken, removeToken } from '@/utils/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    userInfo: null,
    roles: [],
    permissions: []
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    username: (state) => state.userInfo?.username || '',
    nickname: (state) => state.userInfo?.nickname || '',
    userType: (state) => state.userInfo?.userType || '',
    avatar: (state) => state.userInfo?.avatar || '',
    phone: (state) => state.userInfo?.phone || '',
    email: (state) => state.userInfo?.email || '',
    gender: (state) => state.userInfo?.gender || '',
    bio: (state) => state.userInfo?.bio || ''
  },

  actions: {
    async login(loginForm) {
      try {
        const response = await login(loginForm)
        const data = response.data
        console.log('Login response:', response)
        console.log('Login data:', data)
        
        this.token = data.token
        setToken(data.token)
        
        if (data.user) {
          this.userInfo = data.user
          const roles = data.user.roles
          this.roles = typeof roles === 'string' ? (roles ? [roles] : []) : (roles || [])
          const permissions = data.user.permissions
          this.permissions = typeof permissions === 'string' ? (permissions ? [permissions] : []) : (permissions || [])
        } else {
          await this.getUserInfo()
        }
        
        console.log('User type after login:', this.userType)
        console.log('User roles:', this.roles)
        return Promise.resolve(data)
      } catch (error) {
        console.error('Login error:', error)
        return Promise.reject(error)
      }
    },

    async getUserInfo() {
      try {
        const { data } = await getUserInfo()
        this.userInfo = data
        this.roles = data.roles || []
        this.permissions = data.permissions || []
        return Promise.resolve(data)
      } catch (error) {
        return Promise.reject(error)
      }
    },

    async logout() {
      try {
        await logout()
      } finally {
        this.resetState()
      }
    },

    resetState() {
      this.token = ''
      this.userInfo = null
      this.roles = []
      this.permissions = []
      removeToken()
    },

    initializeUser() {
      if (this.token) {
        this.getUserInfo().catch(() => {
          this.resetState()
        })
      }
    },

    async updateProfile(profileData) {
      try {
        const { data } = await updateProfile(profileData)
        this.userInfo = data
        return Promise.resolve(data)
      } catch (error) {
        return Promise.reject(error)
      }
    },

    async changePassword(passwordData) {
      try {
        const { data } = await updatePassword(passwordData)
        return Promise.resolve(data)
      } catch (error) {
        return Promise.reject(error)
      }
    }
  },

  persist: {
    key: 'user-store',
    storage: localStorage,
    paths: ['token']
  }
})
